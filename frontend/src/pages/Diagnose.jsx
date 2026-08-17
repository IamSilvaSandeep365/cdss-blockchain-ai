import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import ShapChart from '../components/ShapChart';

export default function Diagnose() {
  const { patientId } = useParams();
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const [patient, setPatient]       = useState(null);
  const [catalog, setCatalog]       = useState([]);
  const [loadingCatalog, setLoadingCatalog] = useState(true);

  const [selected, setSelected]     = useState([]);   // evidence codes
  const [search, setSearch]         = useState('');
  const [age, setAge]               = useState('');
  const [sex, setSex]               = useState('');

  const [predicting, setPredicting] = useState(false);
  const [result, setResult]         = useState(null);
  const [error, setError]           = useState('');

  const [medication, setMedication] = useState('');
  const [notes, setNotes]           = useState('');
  const [saving, setSaving]         = useState(false);
  const [savedPrescription, setSavedPrescription] = useState(null);

  // Load patient + evidence catalog on mount
  useEffect(() => {
    loadPatient();
    loadCatalog();
  }, [patientId]);

  const loadPatient = async () => {
    try {
      const res = await api.get(`/patients/${patientId}`);
      setPatient(res.data);
      // Pre-fill sex from patient record if available
      if (res.data.gender === 'MALE') setSex('M');
      else if (res.data.gender === 'FEMALE') setSex('F');
    } catch {
      setError('Failed to load patient.');
    }
  };

  const loadCatalog = async () => {
    setLoadingCatalog(true);
    try {
      // Flask catalog endpoint (through Spring Boot proxy if you have one,
      // else directly). Here we call Flask via the backend's known URL.
      const res = await fetch('http://localhost:5000/evidences');
      const data = await res.json();
      setCatalog(data.evidences || []);
    } catch {
      setError('Failed to load evidence catalog. Is the Flask service running?');
    } finally {
      setLoadingCatalog(false);
    }
  };

  const toggleEvidence = (code) => {
    setSelected((prev) =>
      prev.includes(code) ? prev.filter((c) => c !== code) : [...prev, code]
    );
  };

  const handlePredict = async () => {
    setError('');
    setResult(null);
    setSavedPrescription(null);

    if (selected.length === 0) {
      setError('Please select at least one symptom or antecedent.');
      return;
    }
    if (!age || !sex) {
      setError('Please enter age and sex.');
      return;
    }

    setPredicting(true);
    try {
      // We predict by creating a prescription (which calls Flask + returns AI result)
      const res = await api.post('/prescriptions', {
        patientId: Number(patientId),
        evidences: selected,
        age: Number(age),
        sex,
        prescribedMedication: medication,
        notes,
      });
      setResult(res.data.aiPrediction);
      setSavedPrescription(res.data.prescription);
    } catch (err) {
      setError(
        err.response?.data?.message || 'Prediction failed. Check services are running.'
      );
    } finally {
      setPredicting(false);
    }
  };

  // Filter catalog by search term
  const filteredCatalog = catalog.filter((e) =>
    e.label.toLowerCase().includes(search.toLowerCase())
  );
  const symptoms   = filteredCatalog.filter((e) => e.group === 'Symptoms');
  const antecedents = filteredCatalog.filter((e) => e.group === 'Antecedents');

  const renderEvidence = (e) => (
    <label
      key={e.code}
      className="flex items-start gap-2 py-1.5 px-2 rounded hover:bg-slate-50 cursor-pointer text-sm"
    >
      <input
        type="checkbox"
        checked={selected.includes(e.code)}
        onChange={() => toggleEvidence(e.code)}
        className="mt-0.5"
      />
      <span className="text-slate-700">{e.label}</span>
    </label>
  );

  return (
    <div className="min-h-screen bg-slate-100">
      {/* Top bar */}
      <header className="bg-white shadow-sm">
        <div className="max-w-6xl mx-auto px-6 py-4 flex justify-between items-center">
          <div>
            <button
              onClick={() => navigate('/patients')}
              className="text-sm text-blue-600 hover:underline mb-1"
            >
              ← Back to Patients
            </button>
            <h1 className="text-xl font-bold text-slate-800">
              Diagnose: {patient?.fullName || '…'}
            </h1>
          </div>
          <button
            onClick={() => { logout(); navigate('/login'); }}
            className="rounded-lg bg-slate-200 px-4 py-2 text-sm text-slate-700 hover:bg-slate-300 transition"
          >
            Logout
          </button>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-8 grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* LEFT: evidence selection */}
        <div className="bg-white rounded-2xl shadow p-6">
          <h2 className="text-lg font-semibold text-slate-800 mb-1">
            Select Findings
          </h2>
          <p className="text-xs text-slate-500 mb-4">
            💡 Tip: this model simulates early assessment — around 4 key findings
            works best.
          </p>

          {/* Age + Sex */}
          <div className="grid grid-cols-2 gap-3 mb-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Age</label>
              <input
                type="number" value={age} onChange={(e) => setAge(e.target.value)}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="35"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Sex</label>
              <select
                value={sex} onChange={(e) => setSex(e.target.value)}
                className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Select…</option>
                <option value="M">Male</option>
                <option value="F">Female</option>
              </select>
            </div>
          </div>

          {/* Selected count */}
          <div className="mb-3 text-sm text-slate-600">
            Selected: <span className="font-semibold">{selected.length}</span>
            {selected.length > 0 && (
              <button
                onClick={() => setSelected([])}
                className="ml-3 text-xs text-red-600 hover:underline"
              >
                Clear all
              </button>
            )}
          </div>

          {/* Search */}
          <input
            value={search} onChange={(e) => setSearch(e.target.value)}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="🔍 Search findings…"
          />

          {loadingCatalog ? (
            <div className="text-center text-slate-500 py-8">Loading catalog…</div>
          ) : (
            <div className="max-h-96 overflow-y-auto pr-2">
              {symptoms.length > 0 && (
                <>
                  <h3 className="text-xs font-semibold text-slate-500 uppercase mt-2 mb-1">
                    Symptoms
                  </h3>
                  {symptoms.map(renderEvidence)}
                </>
              )}
              {antecedents.length > 0 && (
                <>
                  <h3 className="text-xs font-semibold text-slate-500 uppercase mt-4 mb-1">
                    Antecedents (History)
                  </h3>
                  {antecedents.map(renderEvidence)}
                </>
              )}
            </div>
          )}
        </div>

        {/* RIGHT: prediction + results */}
        <div className="space-y-6">
          {/* Prescription details + predict button */}
          <div className="bg-white rounded-2xl shadow p-6">
            <h2 className="text-lg font-semibold text-slate-800 mb-4">
              Prescription
            </h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Medication
                </label>
                <input
                  value={medication} onChange={(e) => setMedication(e.target.value)}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="e.g. Nasal corticosteroid spray"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Notes
                </label>
                <textarea
                  value={notes} onChange={(e) => setNotes(e.target.value)}
                  rows={2}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Follow up in 2 weeks"
                />
              </div>
            </div>

            {error && (
              <div className="mt-4 rounded-lg bg-red-50 border border-red-200 px-4 py-2 text-sm text-red-700">
                {error}
              </div>
            )}

            <button
              onClick={handlePredict}
              disabled={predicting}
              className="mt-4 w-full rounded-lg bg-blue-600 py-2.5 text-white font-medium hover:bg-blue-700 disabled:opacity-60 transition"
            >
              {predicting ? 'Analyzing…' : '🧠 Run AI Prediction'}
            </button>
          </div>

          {/* Results */}
          {result && (
            <div className="bg-white rounded-2xl shadow p-6">
              <h2 className="text-lg font-semibold text-slate-800 mb-4">
                AI Prediction
              </h2>

              {/* Main prediction */}
              <div className="rounded-xl bg-blue-50 border border-blue-200 p-4 mb-4">
                <div className="text-sm text-slate-600">Predicted Diagnosis</div>
                <div className="text-2xl font-bold text-blue-800">
                  {result.predictedDisease}
                </div>
                <div className="text-sm text-slate-600 mt-1">
                  Confidence:{' '}
                  <span className="font-semibold">{result.confidence}%</span>
                </div>
              </div>

              {/* Alternatives */}
              <div className="mb-4">
                <h3 className="text-sm font-semibold text-slate-700 mb-2">
                  Differential Diagnosis
                </h3>
                <div className="space-y-1">
                  {result.alternatives?.map((alt, i) => (
                    <div key={i} className="flex justify-between text-sm">
                      <span className="text-slate-700">{alt.disease}</span>
                      <span className="text-slate-500">{alt.probability}%</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* SHAP chart */}
              <div>
                <h3 className="text-sm font-semibold text-slate-700 mb-2">
                  Why this prediction? (SHAP)
                </h3>
                <ShapChart explanation={result.explanation} />
                <p className="text-xs text-slate-400 mt-2">
                  Green = pushed toward diagnosis · Red = pushed against
                </p>
              </div>

              {savedPrescription && (
                <div className="mt-4 rounded-lg bg-green-50 border border-green-200 px-4 py-3 text-sm text-green-700">
                  ✅ Prescription #{savedPrescription.id} saved.
                  <button
                    onClick={() => navigate(`/verify/${savedPrescription.id}`)}
                    className="ml-2 text-blue-600 hover:underline"
                  >
                    Go to blockchain →
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}