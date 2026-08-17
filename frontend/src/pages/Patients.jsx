import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

export default function Patients() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [patients, setPatients]   = useState([]);
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState('');
  const [showForm, setShowForm]   = useState(false);
  const [search, setSearch]       = useState('');

  // New-patient form state
  const [form, setForm] = useState({
    fullName: '', dateOfBirth: '', gender: 'MALE',
    nic: '', phone: '', address: '', bloodGroup: '',
  });
  const [saving, setSaving]       = useState(false);
  const [formError, setFormError] = useState('');

  // Load patients on mount
  useEffect(() => {
    fetchPatients();
  }, []);

  const fetchPatients = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/patients');
      setPatients(res.data);
    } catch (err) {
      setError('Failed to load patients.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleFormChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleCreate = async () => {
    setFormError('');
    if (!form.fullName.trim()) {
      setFormError('Full name is required.');
      return;
    }
    setSaving(true);
    try {
      // Only send dateOfBirth if provided (backend expects null or valid date)
      const payload = { ...form };
      if (!payload.dateOfBirth) delete payload.dateOfBirth;

      await api.post('/patients', payload);
      setForm({
        fullName: '', dateOfBirth: '', gender: 'MALE',
        nic: '', phone: '', address: '', bloodGroup: '',
      });
      setShowForm(false);
      fetchPatients();
    } catch (err) {
      setFormError(
        err.response?.data?.message || 'Failed to create patient.'
      );
    } finally {
      setSaving(false);
    }
  };

  // Client-side filter for the search box
  const filtered = patients.filter((p) =>
    p.fullName.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-slate-100">
      {/* Top bar */}
      <header className="bg-white shadow-sm">
        <div className="max-w-6xl mx-auto px-6 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-xl font-bold text-slate-800">CDSS Portal</h1>
            <p className="text-xs text-slate-500">
              {user?.fullName || user?.username} · {user?.role}
            </p>
          </div>
          <div className="flex gap-2">
            <button
              onClick={() => navigate('/dashboard')}
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700 transition"
            >
              Dashboard
            </button>
            <button
              onClick={handleLogout}
              className="rounded-lg bg-slate-200 px-4 py-2 text-sm text-slate-700 hover:bg-slate-300 transition"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-8">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-slate-800">Patients</h2>
          <button
            onClick={() => { setShowForm(!showForm); setFormError(''); }}
            className="rounded-lg bg-blue-600 px-4 py-2 text-sm text-white font-medium hover:bg-blue-700 transition"
          >
            {showForm ? 'Cancel' : '+ New Patient'}
          </button>
        </div>

        {/* New patient form */}
        {showForm && (
          <div className="bg-white rounded-2xl shadow p-6 mb-6">
            <h3 className="text-lg font-semibold text-slate-800 mb-4">
              Add New Patient
            </h3>

            {formError && (
              <div className="mb-4 rounded-lg bg-red-50 border border-red-200 px-4 py-2 text-sm text-red-700">
                {formError}
              </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Full Name *
                </label>
                <input
                  name="fullName" value={form.fullName} onChange={handleFormChange}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="John Doe"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Date of Birth
                </label>
                <input
                  type="date" name="dateOfBirth" value={form.dateOfBirth} onChange={handleFormChange}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Gender
                </label>
                <select
                  name="gender" value={form.gender} onChange={handleFormChange}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  NIC
                </label>
                <input
                  name="nic" value={form.nic} onChange={handleFormChange}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="991234567V"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Phone
                </label>
                <input
                  name="phone" value={form.phone} onChange={handleFormChange}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="0771234567"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Blood Group
                </label>
                <input
                  name="bloodGroup" value={form.bloodGroup} onChange={handleFormChange}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="O+"
                />
              </div>
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Address
                </label>
                <input
                  name="address" value={form.address} onChange={handleFormChange}
                  className="w-full rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="123 Main St, Colombo"
                />
              </div>
            </div>

            <div className="mt-4 flex justify-end">
              <button
                onClick={handleCreate}
                disabled={saving}
                className="rounded-lg bg-green-600 px-5 py-2 text-white font-medium hover:bg-green-700 disabled:opacity-60 transition"
              >
                {saving ? 'Saving…' : 'Save Patient'}
              </button>
            </div>
          </div>
        )}

        {/* Search */}
        <div className="mb-4">
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full max-w-sm rounded-lg border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="🔍 Search by name…"
          />
        </div>

        {/* Patient list */}
        <div className="bg-white rounded-2xl shadow overflow-hidden">
          {loading ? (
            <div className="p-8 text-center text-slate-500">Loading patients…</div>
          ) : error ? (
            <div className="p-8 text-center text-red-600">{error}</div>
          ) : filtered.length === 0 ? (
            <div className="p-8 text-center text-slate-500">
              No patients yet. Click "+ New Patient" to add one.
            </div>
          ) : (
            <table className="w-full text-sm">
              <thead className="bg-slate-50 text-slate-600 text-left">
                <tr>
                  <th className="px-6 py-3 font-medium">Name</th>
                  <th className="px-6 py-3 font-medium">Gender</th>
                  <th className="px-6 py-3 font-medium">NIC</th>
                  <th className="px-6 py-3 font-medium">Phone</th>
                  <th className="px-6 py-3 font-medium">Blood</th>
                  <th className="px-6 py-3 font-medium text-right">Action</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((p) => (
                  <tr key={p.id} className="border-t border-slate-100 hover:bg-slate-50">
                    <td className="px-6 py-3 font-medium text-slate-800">{p.fullName}</td>
                    <td className="px-6 py-3 text-slate-600">{p.gender}</td>
                    <td className="px-6 py-3 text-slate-600">{p.nic || '—'}</td>
                    <td className="px-6 py-3 text-slate-600">{p.phone || '—'}</td>
                    <td className="px-6 py-3 text-slate-600">{p.bloodGroup || '—'}</td>
                    <td className="px-6 py-3 text-right">
                      <button
                        onClick={() => navigate(`/diagnose/${p.id}`)}
                        className="rounded-lg bg-blue-600 px-3 py-1.5 text-xs text-white hover:bg-blue-700 transition"
                      >
                        Diagnose →
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  );
}