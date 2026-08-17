import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';

export default function Verify() {
  const { prescriptionId } = useParams();
  const navigate = useNavigate();
  const { logout } = useAuth();

  const [prescription, setPrescription] = useState(null);
  const [loading, setLoading]   = useState(true);
  const [storing, setStoring]   = useState(false);
  const [verifying, setVerifying] = useState(false);
  const [storeResult, setStoreResult]   = useState(null);
  const [verifyResult, setVerifyResult] = useState(null);
  const [error, setError]       = useState('');

  useEffect(() => {
    loadPrescription();
  }, [prescriptionId]);

  const loadPrescription = async () => {
    setLoading(true);
    try {
      const res = await api.get(`/prescriptions/${prescriptionId}`);
      setPrescription(res.data);
    } catch {
      setError('Failed to load prescription.');
    } finally {
      setLoading(false);
    }
  };

  const handleStore = async () => {
    setError('');
    setStoring(true);
    setStoreResult(null);
    try {
      const res = await api.post(`/prescriptions/${prescriptionId}/blockchain`);
      setStoreResult(res.data);
      loadPrescription(); // refresh to show tx hash
    } catch (err) {
      setError(err.response?.data?.message || 'Blockchain storage failed.');
    } finally {
      setStoring(false);
    }
  };

  const handleVerify = async () => {
    setError('');
    setVerifying(true);
    setVerifyResult(null);
    try {
      const res = await api.get(`/prescriptions/${prescriptionId}/verify`);
      setVerifyResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Verification failed.');
    } finally {
      setVerifying(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100">
      <header className="bg-white shadow-sm">
        <div className="max-w-4xl mx-auto px-6 py-4 flex justify-between items-center">
          <div>
            <button
              onClick={() => navigate('/patients')}
              className="text-sm text-blue-600 hover:underline mb-1"
            >
              ← Back to Patients
            </button>
            <h1 className="text-xl font-bold text-slate-800">
              Blockchain Verification
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

      <main className="max-w-4xl mx-auto px-6 py-8">
        {loading ? (
          <div className="text-center text-slate-500 py-8">Loading…</div>
        ) : prescription ? (
          <div className="space-y-6">
            {/* Prescription summary */}
            <div className="bg-white rounded-2xl shadow p-6">
              <h2 className="text-lg font-semibold text-slate-800 mb-4">
                Prescription #{prescription.id}
              </h2>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <div className="text-slate-500">Predicted Disease</div>
                  <div className="font-medium text-slate-800">
                    {prescription.predictedDisease}
                  </div>
                </div>
                <div>
                  <div className="text-slate-500">Confidence</div>
                  <div className="font-medium text-slate-800">
                    {prescription.confidenceScore}%
                  </div>
                </div>
                <div>
                  <div className="text-slate-500">Medication</div>
                  <div className="font-medium text-slate-800">
                    {prescription.prescribedMedication || '—'}
                  </div>
                </div>
                <div>
                  <div className="text-slate-500">Status</div>
                  <div className="font-medium text-slate-800">
                    {prescription.status}
                  </div>
                </div>
              </div>

              {/* Record hash */}
              <div className="mt-4">
                <div className="text-slate-500 text-sm">Record Hash (SHA-256)</div>
                <div className="font-mono text-xs break-all bg-slate-50 rounded p-2 mt-1">
                  {prescription.recordHash}
                </div>
              </div>

              {prescription.blockchainTxHash && (
                <div className="mt-3">
                  <div className="text-slate-500 text-sm">Blockchain TX Hash</div>
                  <div className="font-mono text-xs break-all bg-slate-50 rounded p-2 mt-1">
                    {prescription.blockchainTxHash}
                  </div>
                </div>
              )}
            </div>

            {error && (
              <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            {/* Actions */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Store */}
              <div className="bg-white rounded-2xl shadow p-6">
                <h3 className="font-semibold text-slate-800 mb-2">
                  1. Anchor on Blockchain
                </h3>
                <p className="text-sm text-slate-500 mb-4">
                  Store the prescription's hash immutably on Ethereum.
                </p>
                <button
                  onClick={handleStore}
                  disabled={storing}
                  className="w-full rounded-lg bg-indigo-600 py-2.5 text-white font-medium hover:bg-indigo-700 disabled:opacity-60 transition"
                >
                  {storing ? 'Storing…' : '⛓️ Store on Blockchain'}
                </button>
                {storeResult && (
                  <div className="mt-3 rounded-lg bg-green-50 border border-green-200 px-3 py-2 text-sm text-green-700">
                    ✅ {storeResult.message}
                  </div>
                )}
              </div>

              {/* Verify */}
              <div className="bg-white rounded-2xl shadow p-6">
                <h3 className="font-semibold text-slate-800 mb-2">
                  2. Verify Integrity
                </h3>
                <p className="text-sm text-slate-500 mb-4">
                  Re-check the record against the blockchain.
                </p>
                <button
                  onClick={handleVerify}
                  disabled={verifying}
                  className="w-full rounded-lg bg-blue-600 py-2.5 text-white font-medium hover:bg-blue-700 disabled:opacity-60 transition"
                >
                  {verifying ? 'Verifying…' : '🔍 Verify on Blockchain'}
                </button>
                {verifyResult && (
                  <div
                    className={`mt-3 rounded-lg px-3 py-2 text-sm border ${
                      verifyResult.blockchainMatch
                        ? 'bg-green-50 border-green-200 text-green-700'
                        : 'bg-red-50 border-red-200 text-red-700'
                    }`}
                  >
                    {verifyResult.blockchainMatch
                      ? '✅ VERIFIED — record matches blockchain'
                      : '❌ TAMPERED — record does not match'}
                  </div>
                )}
              </div>
            </div>
          </div>
        ) : (
          <div className="text-center text-red-600 py-8">
            Prescription not found.
          </div>
        )}
      </main>
    </div>
  );
}