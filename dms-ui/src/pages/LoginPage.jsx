import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useApp } from "../context/AppContext";
import { api } from "../utils/api";
import { FileText, Lock, Mail, AlertCircle } from "lucide-react";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { dispatch } = useApp();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    if (!email || !password) { setError("Please fill in all fields"); return; }
    setLoading(true); setError("");
    try {
      const users = await api.get("/users");
      const user = users.find(u => u.email === email && u.password === password);
      if (!user) { setError("Invalid email or password"); return; }
      if (user.status === "suspended") { setError("Account suspended. Contact admin."); return; }
      dispatch({ type: "SET_USER", payload: user });
      navigate(user.role === "admin" ? "/admin/users" : "/documents");
    } catch {
      setError("Connection error. Make sure json-server is running.");
    } finally { setLoading(false); }
  }

  return (
    <div style={{ minHeight:"100vh", display:"flex", alignItems:"center", justifyContent:"center", background:"var(--bg)", padding:"24px" }}>
      <div style={{ width:"100%", maxWidth:"420px" }}>
        <div style={{ textAlign:"center", marginBottom:"48px" }}>
          <div style={{ width:64, height:64, background:"var(--accent)", borderRadius:16, display:"flex", alignItems:"center", justifyContent:"center", margin:"0 auto 16px" }}>
            <FileText size={32} color="white" />
          </div>
          <h1 style={{ fontSize:28, marginBottom:8 }}>Document Management</h1>
          <p style={{ color:"var(--text2)" }}>Sign in to your account</p>
        </div>

        <div className="card">
          {error && (
            <div style={{ background:"rgba(239,68,68,0.1)", border:"1px solid rgba(239,68,68,0.3)", borderRadius:8, padding:"12px 16px", marginBottom:20, display:"flex", alignItems:"center", gap:8, color:"var(--danger)", fontSize:14 }}>
              <AlertCircle size={16} />{error}
            </div>
          )}
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Email Address</label>
              <div style={{ position:"relative" }}>
                <Mail size={16} style={{ position:"absolute", left:12, top:"50%", transform:"translateY(-50%)", color:"var(--text2)" }} />
                <input className="input" style={{ paddingLeft:38 }} type="email" placeholder="you@dms.com" value={email} onChange={e=>setEmail(e.target.value)} data-testid="email-input" />
              </div>
            </div>
            <div className="form-group">
              <label>Password</label>
              <div style={{ position:"relative" }}>
                <Lock size={16} style={{ position:"absolute", left:12, top:"50%", transform:"translateY(-50%)", color:"var(--text2)" }} />
                <input className="input" style={{ paddingLeft:38 }} type="password" placeholder="••••••••" value={password} onChange={e=>setPassword(e.target.value)} data-testid="password-input" />
              </div>
            </div>
            <button className="btn btn-primary" style={{ width:"100%", justifyContent:"center", padding:"12px", marginTop:8 }} type="submit" disabled={loading} data-testid="login-btn">
              {loading ? "Signing in..." : "Sign In"}
            </button>
          </form>

        </div>
      </div>
    </div>
  );
}
