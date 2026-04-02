import { useEffect, useState } from "react";
import {
  getCurrency,
  getMonthlyBudget,
  getProfile,
  getUserEmail,
  getUserName,
  updateBudget,
  updateCurrency,
  updatePassword,
  updateProfile,
} from "../api";
export default function ProfileScreen() {
  const [name, setName] = useState(getUserName());
  const [budget, setBudget] = useState(getMonthlyBudget());
  const [currency, setCurrency] = useState(getCurrency());
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const currencyOptions = ["USD", "EUR", "GBP", "INR", "JPY", "CAD", "AUD", "SGD"];

  useEffect(() => {
    const load = async () => {
      try {
        const profile = await getProfile();
        setName(profile.name);
        setBudget(profile.monthlyBudget || 0);
        setCurrency(profile.currency || "USD");
      } catch {
        // Keep localStorage fallback if fetch fails
      }
    };
    load();
  }, []);

  const handleUpdateProfile = async () => {
    setError("");
    setSuccess("");
    if (!name.trim()) { setError("Name cannot be empty"); return; }
    setLoading(true);
    try {
      await updateProfile(name.trim());
      setSuccess("Profile updated successfully!");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateBudget = async () => {
    setError("");
    setSuccess("");
    if (budget < 0) { setError("Budget cannot be negative"); return; }
    setLoading(true);
    try {
      await updateBudget(budget);
      setSuccess("Budget updated successfully!");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateCurrency = async () => {
    setError("");
    setSuccess("");
    if (!currency.trim()) { setError("Currency cannot be empty"); return; }
    setLoading(true);
    try {
      await updateCurrency(currency.trim());
      setSuccess("Currency updated successfully!");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdatePassword = async () => {
    setError("");
    setSuccess("");
    if (!currentPassword || !newPassword || !confirmPassword) {
      setError("All password fields are required");
      return;
    }
    if (newPassword !== confirmPassword) {
      setError("New passwords do not match");
      return;
    }
    if (newPassword.length < 6) {
      setError("New password must be at least 6 characters");
      return;
    }
    setLoading(true);
    try {
      await updatePassword(currentPassword, newPassword);
      setSuccess("Password updated successfully!");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="screen">
      <div style={{ maxWidth: "600px", margin: "0 auto" }}>
        <h2 style={{ marginBottom: "24px" }}>Profile Settings</h2>

        {error && <p className="error-msg" style={{ marginBottom: "16px" }}>{error}</p>}
        {success && <p style={{ color: "var(--green)", marginBottom: "16px" }}>{success}</p>}

        {/* Profile Info */}
        <div className="card" style={{ marginBottom: "20px" }}>
          <h3 style={{ marginBottom: "16px" }}>Profile Information</h3>
          <div className="input-group">
            <label className="input-label">Email</label>
            <input
              className="input-field"
              type="email"
              value={getUserEmail()}
              disabled
            />
          </div>
          <div className="input-group">
            <label className="input-label">Name</label>
            <input
              className="input-field"
              value={name}
              onChange={e => setName(e.target.value)}
            />
          </div>
          <button className="btn" onClick={handleUpdateProfile} disabled={loading}>
            {loading ? <div className="spinner" /> : "Update Profile"}
          </button>
        </div>

        {/* Budget */}
        <div className="card" style={{ marginBottom: "20px" }}>
          <h3 style={{ marginBottom: "16px" }}>Monthly Budget</h3>
          <div className="input-group">
            <label className="input-label">Budget Amount</label>
            <input
              className="input-field"
              type="number"
              value={budget}
              onChange={e => setBudget(parseFloat(e.target.value) || 0)}
              min="0"
              step="0.01"
            />
          </div>
          <button className="btn" onClick={handleUpdateBudget} disabled={loading}>
            {loading ? <div className="spinner" /> : "Update Budget"}
          </button>
        </div>

        {/* Currency */}
        <div className="card" style={{ marginBottom: "20px" }}>
          <h3 style={{ marginBottom: "16px" }}>Currency</h3>
          <div className="input-group">
            <label className="input-label">Currency</label>
            <select
              className="input-field"
              value={currency}
              onChange={e => setCurrency(e.target.value)}
            >
              {currencyOptions.map((c) => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>
          </div>
          <button className="btn" onClick={handleUpdateCurrency} disabled={loading}>
            {loading ? <div className="spinner" /> : "Update Currency"}
          </button>
        </div>

        {/* Password */}
        <div className="card">
          <h3 style={{ marginBottom: "16px" }}>Change Password</h3>
          <div className="input-group">
            <label className="input-label">Current Password</label>
            <input
              className="input-field"
              type="password"
              value={currentPassword}
              onChange={e => setCurrentPassword(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label className="input-label">New Password</label>
            <input
              className="input-field"
              type="password"
              value={newPassword}
              onChange={e => setNewPassword(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label className="input-label">Confirm New Password</label>
            <input
              className="input-field"
              type="password"
              value={confirmPassword}
              onChange={e => setConfirmPassword(e.target.value)}
            />
          </div>
          <button className="btn" onClick={handleUpdatePassword} disabled={loading}>
            {loading ? <div className="spinner" /> : "Update Password"}
          </button>
        </div>
      </div>
    </div>
  );
}
