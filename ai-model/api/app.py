# ================================================================
#   CDSS Flask API — AI Prediction Service
#   Endpoint: POST /predict
#   Called by: Spring Boot Backend
# ================================================================

from flask import Flask, request, jsonify
from flask_cors import CORS
import pandas as pd
import numpy as np
import pickle
import os
from datetime import datetime

app = Flask(__name__)
CORS(app)

# ================================================================
# Load all model files on startup
# ================================================================
BASE_DIR   = os.path.dirname(os.path.abspath(__file__))
MODELS_DIR = os.path.join(BASE_DIR, '..', 'models')

print("⏳ Loading model files...")

with open(os.path.join(MODELS_DIR, 'best_model.pkl'), 'rb') as f:
    model = pickle.load(f)

with open(os.path.join(MODELS_DIR, 'label_encoder.pkl'), 'rb') as f:
    le = pickle.load(f)

with open(os.path.join(MODELS_DIR, 'symptom_columns.pkl'), 'rb') as f:
    symptom_cols = pickle.load(f)

with open(os.path.join(MODELS_DIR, 'shap_explainer.pkl'), 'rb') as f:
    explainer = pickle.load(f)

print("✅ All model files loaded!")
print(f"   Symptoms : {len(symptom_cols)}")
print(f"   Diseases : {len(le.classes_)}")

# ================================================================
# DEBUG — confirm SHAP output shape on startup
# ================================================================
_test_input = pd.DataFrame([{col: 0 for col in symptom_cols}])
_test_input[symptom_cols[0]] = 1
_shap_test = np.array(explainer.shap_values(_test_input))
print(f"✅ SHAP output shape: {_shap_test.shape}")
print(f"   n_classes={len(le.classes_)}, n_features={len(symptom_cols)}")


# ================================================================
# Helper — extract SHAP values safely regardless of shape
# ================================================================
def extract_shap_for_class(shap_vals, predicted_encoded, n_classes, n_features):
    arr = np.array(shap_vals)

    if arr.ndim == 3 and arr.shape[0] == n_classes:
        # (n_classes, n_samples, n_features)
        return arr[predicted_encoded][0]

    elif arr.ndim == 3 and arr.shape[2] == n_classes:
        # (n_samples, n_features, n_classes)
        return arr[0, :, predicted_encoded]

    elif arr.ndim == 3 and arr.shape[1] == n_classes:
        # (n_samples, n_classes, n_features)
        return arr[0, predicted_encoded, :]

    elif arr.ndim == 2:
        # (n_samples, n_features) — binary or single output
        return arr[0]

    else:
        # Fallback
        return arr.reshape(-1, n_features)[0]


# ================================================================
# Helper — build prediction + SHAP explanation
# ================================================================
def predict_with_explanation(symptom_list: list) -> dict:
    # Build input vector (all zeros)
    input_vector = pd.DataFrame([{col: 0 for col in symptom_cols}])

    valid_symptoms   = []
    invalid_symptoms = []

    for symptom in symptom_list:
        symptom_clean = symptom.strip().lower().replace(' ', '_')
        if symptom_clean in symptom_cols:
            input_vector[symptom_clean] = 1
            valid_symptoms.append(symptom_clean)
        else:
            invalid_symptoms.append(symptom)

    # Predict
    predicted_encoded = int(model.predict(input_vector)[0])
    predicted_disease = le.inverse_transform([predicted_encoded])[0]
    probabilities     = model.predict_proba(input_vector)[0]
    confidence        = float(probabilities[predicted_encoded]) * 100

    # Top 3 alternatives
    top3_indices = np.argsort(probabilities)[::-1][:3]
    alternatives = [
        {
            "disease"    : le.inverse_transform([int(i)])[0],
            "probability": round(float(probabilities[i]) * 100, 2)
        }
        for i in top3_indices
    ]

    # SHAP explanation
    shap_vals    = explainer.shap_values(input_vector)
    symptom_shap = extract_shap_for_class(
        shap_vals,
        predicted_encoded,
        len(le.classes_),
        len(symptom_cols)
    )

    explanation = [
        {
            "symptom"   : symptom_cols[i],
            "shap_value": round(float(symptom_shap[i]), 4),
            "active"    : int(input_vector.iloc[0][symptom_cols[i]])
        }
        for i in np.argsort(np.abs(symptom_shap))[::-1][:10]
    ]

    return {
        "predicted_disease": predicted_disease,
        "confidence"       : round(confidence, 2),
        "valid_symptoms"   : valid_symptoms,
        "invalid_symptoms" : invalid_symptoms,
        "alternatives"     : alternatives,
        "explanation"      : explanation
    }


# ================================================================
# ROUTE 1 — Health Check
# GET /health
# ================================================================
@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status"   : "UP",
        "service"  : "CDSS AI Prediction API",
        "timestamp": datetime.now().isoformat(),
        "model"    : "Random Forest",
        "symptoms" : len(symptom_cols),
        "diseases" : len(le.classes_)
    }), 200


# ================================================================
# ROUTE 2 — Main Prediction Endpoint
# POST /predict
# Body: { "symptoms": ["itching", "skin_rash"] }
# ================================================================
@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()

        if not data:
            return jsonify({
                "error": "Request body is missing or not JSON"
            }), 400

        if 'symptoms' not in data:
            return jsonify({
                "error"  : "Missing 'symptoms' field in request body",
                "example": {"symptoms": ["itching", "skin_rash"]}
            }), 400

        symptoms = data['symptoms']

        if not isinstance(symptoms, list) or len(symptoms) == 0:
            return jsonify({
                "error": "'symptoms' must be a non-empty list"
            }), 400

        result = predict_with_explanation(symptoms)

        response = {
            "status"           : "success",
            "timestamp"        : datetime.now().isoformat(),
            "input_symptoms"   : symptoms,
            "valid_symptoms"   : result['valid_symptoms'],
            "invalid_symptoms" : result['invalid_symptoms'],
            "prediction"       : {
                "disease"     : result['predicted_disease'],
                "confidence"  : result['confidence'],
                "alternatives": result['alternatives']
            },
            "explanation"      : result['explanation']
        }

        return jsonify(response), 200

    except Exception as e:
        return jsonify({
            "status" : "error",
            "message": str(e)
        }), 500


# ================================================================
# ROUTE 3 — Get All Available Symptoms
# GET /symptoms
# ================================================================
@app.route('/symptoms', methods=['GET'])
def get_symptoms():
    return jsonify({
        "status"  : "success",
        "count"   : len(symptom_cols),
        "symptoms": sorted(symptom_cols)
    }), 200


# ================================================================
# ROUTE 4 — Get All Diseases
# GET /diseases
# ================================================================
@app.route('/diseases', methods=['GET'])
def get_diseases():
    return jsonify({
        "status"  : "success",
        "count"   : len(le.classes_),
        "diseases": sorted(le.classes_.tolist())
    }), 200


# ================================================================
# ROUTE 5 — Verify Prediction (for blockchain audit)
# POST /verify
# Body: { "symptoms": [...], "expected_disease": "Fungal infection" }
# ================================================================
@app.route('/verify', methods=['POST'])
def verify():
    try:
        data             = request.get_json()
        symptoms         = data.get('symptoms', [])
        expected_disease = data.get('expected_disease', '')

        result = predict_with_explanation(symptoms)
        match  = result['predicted_disease'] == expected_disease

        return jsonify({
            "status"           : "success",
            "expected_disease" : expected_disease,
            "predicted_disease": result['predicted_disease'],
            "match"            : match,
            "confidence"       : result['confidence']
        }), 200

    except Exception as e:
        return jsonify({
            "status" : "error",
            "message": str(e)
        }), 500


# ================================================================
# Run the app
# ================================================================
if __name__ == '__main__':
    print("\n" + "=" * 50)
    print("   🚀 CDSS Flask API Starting...")
    print("=" * 50)
    print("   URL      : http://localhost:5000")
    print("   Endpoints:")
    print("     GET  /health")
    print("     POST /predict")
    print("     GET  /symptoms")
    print("     GET  /diseases")
    print("     POST /verify")
    print("=" * 50 + "\n")
    app.run(debug=True, host='0.0.0.0', port=5000)