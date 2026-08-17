# ================================================================
#   CDSS Flask API — DDXPlus / XGBoost version
#   Exposes all 270 features (binary + categorical + demographics)
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

BASE_DIR   = os.path.dirname(os.path.abspath(__file__))
MODELS_DIR = os.path.join(BASE_DIR, '..', 'models')

def _load(name):
    with open(os.path.join(MODELS_DIR, name), 'rb') as f:
        return pickle.load(f)

print("⏳ Loading model files...")
model            = _load('best_model.pkl')
le               = _load('label_encoder.pkl')
feature_cols     = _load('symptom_columns.pkl')
explainer        = _load('shap_explainer.pkl')
feature_metadata = _load('feature_metadata.pkl')
disease_name_map = _load('disease_name_map.pkl')
print(f"✅ Loaded! {len(feature_cols)} features, {len(le.classes_)} diseases")


# ================================================================
# Core — build input vector + predict + explain
# ================================================================
def build_input(evidences, age, sex):
    vec = pd.DataFrame([{c: 0 for c in feature_cols}])

    if 'AGE' in feature_cols and age is not None:
        vec['AGE'] = age
    if sex == 'M' and 'SEX_M' in feature_cols:
        vec['SEX_M'] = 1
    elif sex == 'F' and 'SEX_F' in feature_cols:
        vec['SEX_F'] = 1

    valid, invalid = [], []
    for e in evidences:
        if e in feature_cols:
            vec[e] = 1
            valid.append(e)
        else:
            invalid.append(e)
    return vec, valid, invalid


def predict_core(evidences, age, sex):
    vec, valid, invalid = build_input(evidences, age, sex)

    enc         = int(model.predict(vec)[0])
    raw_disease = le.inverse_transform([enc])[0]
    disease     = disease_name_map.get(raw_disease, raw_disease)   # friendly name
    proba       = model.predict_proba(vec)[0]
    conf        = round(float(proba[enc]) * 100, 2)

    top3 = np.argsort(proba)[::-1][:3]
    alternatives = [
        {"disease": disease_name_map.get(
                        le.inverse_transform([int(i)])[0],
                        le.inverse_transform([int(i)])[0]),
         "probability": round(float(proba[i]) * 100, 2)}
        for i in top3
    ]

    # SHAP for this prediction  (XGBoost: shape = samples, features, classes)
    sv = np.array(explainer.shap_values(vec))[0][:, enc]

    explanation = []
    for i in np.argsort(np.abs(sv))[::-1][:10]:
        feat = feature_cols[i]
        if vec.iloc[0][feat] == 1 or feat == 'AGE':
            meta = feature_metadata.get(feat, {})
            explanation.append({
                "feature"   : feat,
                "label"     : meta.get('label', feat),
                "shap_value": round(float(sv[i]), 4)
            })

    return {
        "predicted_disease": disease,
        "confidence"       : conf,
        "valid_evidences"  : valid,
        "invalid_evidences": invalid,
        "alternatives"     : alternatives,
        "explanation"      : explanation
    }


# ================================================================
# ROUTE 1 — Health
# ================================================================
@app.route('/health', methods=['GET'])
def health():
    return jsonify({
        "status"   : "UP",
        "service"  : "CDSS AI Prediction API (DDXPlus)",
        "model"    : "XGBoost",
        "features" : len(feature_cols),
        "diseases" : len(le.classes_),
        "timestamp": datetime.now().isoformat()
    }), 200


# ================================================================
# ROUTE 2 — Predict
# Body: { "evidences": ["E_91","E_54_@_V_181"], "age": 35, "sex": "F" }
# ================================================================
@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "Request body missing or not JSON"}), 400

        # Accept "evidences" (new) or "symptoms" (legacy alias)
        evidences = data.get('evidences', data.get('symptoms'))
        if not isinstance(evidences, list) or len(evidences) == 0:
            return jsonify({
                "error": "'evidences' must be a non-empty list",
                "example": {"evidences": ["E_91", "E_54_@_V_181"],
                            "age": 35, "sex": "F"}
            }), 400

        age = data.get('age')
        sex = data.get('sex')

        result = predict_core(evidences, age, sex)

        return jsonify({
            "status"          : "success",
            "timestamp"       : datetime.now().isoformat(),
            "input"           : {"evidences": evidences, "age": age, "sex": sex},
            "valid_evidences" : result['valid_evidences'],
            "invalid_evidences": result['invalid_evidences'],
            "prediction"      : {
                "disease"     : result['predicted_disease'],
                "confidence"  : result['confidence'],
                "alternatives": result['alternatives']
            },
            "explanation"     : result['explanation']
        }), 200

    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500


# ================================================================
# ROUTE 3 — Evidence catalog (frontend builds its picker from this)
# GET /evidences
# ================================================================
@app.route('/evidences', methods=['GET'])
def get_evidences():
    catalog = [feature_metadata[f] for f in feature_cols]
    return jsonify({
        "status"  : "success",
        "count"   : len(catalog),
        "evidences": catalog
    }), 200


# ================================================================
# ROUTE 4 — Diseases
# ================================================================
@app.route('/diseases', methods=['GET'])
def get_diseases():
    # Apply friendly names here too, so the diseases list reads nicely
    friendly = sorted(
        disease_name_map.get(d, d) for d in le.classes_.tolist()
    )
    return jsonify({
        "status"  : "success",
        "count"   : len(le.classes_),
        "diseases": friendly
    }), 200


# ================================================================
# ROUTE 5 — Verify (blockchain audit support)
# ================================================================
@app.route('/verify', methods=['POST'])
def verify():
    try:
        data = request.get_json()
        evidences = data.get('evidences', data.get('symptoms', []))
        expected  = data.get('expected_disease', '')
        result    = predict_core(evidences, data.get('age'), data.get('sex'))
        return jsonify({
            "status"           : "success",
            "expected_disease" : expected,
            "predicted_disease": result['predicted_disease'],
            "match"            : result['predicted_disease'] == expected,
            "confidence"       : result['confidence']
        }), 200
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500


if __name__ == '__main__':
    print("\n🚀 CDSS Flask API (DDXPlus) — http://localhost:5000")
    print("   GET  /health    GET  /evidences   GET  /diseases")
    print("   POST /predict   POST /verify\n")
    app.run(debug=True, host='0.0.0.0', port=5000)