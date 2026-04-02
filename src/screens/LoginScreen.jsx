import { useState } from "react";
import { login, register } from "../api";

export default function LoginScreen({ navigate }) {
  const [isRegister, setIsRegister] = useState(false);
  const [name, setName]             = useState("");
  const [email, setEmail]           = useState("");
  const [password, setPassword]     = useState("");
  const [showPass, setShowPass]     = useState(false);
  const [loading, setLoading]       = useState(false);
  const [error, setError]           = useState("");

  const handleSubmit = async () => {
    setError("");
    if (!email || !password || (isRegister && !name)) {
      setError("Please fill in all fields"); return;
    }
    if (password.length < 6) {
      setError("Password must be at least 6 characters"); return;
    }
    setLoading(true);
    try {
      if (isRegister) await register(name, email, password);
      else            await login(email, password);
      navigate("/home");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const toggle = () => {
    setIsRegister(!isRegister);
    setError(""); setName(""); setEmail(""); setPassword("");
  };

  return (
    <div style={{
      minHeight: "100vh",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      background: "var(--bg)",
      padding: "20px"
    }}>
      <div style={{ width: "100%", maxWidth: "400px" }}>
        <div style={{ textAlign: "center", marginBottom: "32px" }}>
          <h1 style={{ fontSize: "28px", fontWeight: 700, color: "var(--zinc)", marginBottom: "8px" }}>
            Expense Tracker
          </h1>
          <p style={{ color: "var(--text-muted)", fontSize: "14px" }}>
            {isRegister ? "Start tracking smarter" : "Welcome Back"}
          </p>
        </div>

        <div className="card" style={{ boxShadow: "0 8px 40px rgba(0,0,0,0.12)" }}>

          {isRegister && (
            <div className="input-group">
              <label className="input-label">Name</label>
              <input
                className="input-field"
                placeholder="Your full name"
                value={name}
                onChange={e => setName(e.target.value)}
              />
            </div>
          )}

          <div className="input-group">
            <label className="input-label">Email</label>
            <input
              className="input-field"
              type="email"
              placeholder="you@email.com"
              value={email}
              onChange={e => setEmail(e.target.value)}
            />
          </div>

          <div className="input-group">
            <label className="input-label">Password</label>
            <div style={{ position: "relative" }}>
              <input
                className="input-field"
                type={showPass ? "text" : "password"}
                placeholder="Min. 6 characters"
                value={password}
                onChange={e => setPassword(e.target.value)}
                style={{ paddingRight: 60 }}
                onKeyDown={e => e.key === "Enter" && handleSubmit()}
              />
              <button
                onClick={() => setShowPass(!showPass)}
                style={{
                  position: "absolute", right: 12, top: "50%",
                  transform: "translateY(-50%)",
                  background: "none", border: "none",
                  color: "var(--zinc)", fontSize: 12,
                  fontWeight: 600, cursor: "pointer",
                  fontFamily: "DM Sans, sans-serif"
                }}
              >
                {showPass ? "Hide" : "Show"}
              </button>
            </div>
          </div>

          {error && <p className="error-msg">{error}</p>}

          <div style={{ marginTop: 24 }}>
            <button className="btn" onClick={handleSubmit} disabled={loading}>
              {loading ? <div className="spinner" /> : (isRegister ? "Create Account" : "Sign In")}
            </button>
          </div>

          <div style={{ display: "flex", justifyContent: "center", marginTop: 16, gap: 4 }}>
            <span style={{ fontSize: 13, color: "var(--text-muted)" }}>
              {isRegister ? "Already have an account?" : "Don't have an account?"}
            </span>
            <button
              onClick={toggle}
              style={{
                fontSize: 13, fontWeight: 600, color: "var(--zinc)",
                background: "none", border: "none", cursor: "pointer",
                fontFamily: "DM Sans, sans-serif"
              }}
            >
              {isRegister ? " Sign In" : " Register"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
