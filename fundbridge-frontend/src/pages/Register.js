import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const initialForm = {
  username: "",
  email: "",
  password: "",
  fullName: "",
  phoneNumber: "",
  address: "",
};

export default function Register() {
  const { register, loading } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    const res = await register(form);
    if (res.success) {
      navigate("/");
    } else {
      setError(res.message);
    }
  };

  return (
    <div className="auth-page">
      <form className="card auth-card" onSubmit={handleSubmit}>
        <h2>Create your account</h2>
        <p className="muted">Join FundBridge to donate, borrow, or lend</p>
        {error && <div className="alert alert-error">{error}</div>}

        <label>Username</label>
        <input
          name="username"
          value={form.username}
          onChange={handleChange}
          required
          minLength={3}
          maxLength={50}
        />

        <label>Full name</label>
        <input
          name="fullName"
          value={form.fullName}
          onChange={handleChange}
          required
        />

        <label>Email</label>
        <input
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          required
        />

        <label>Password</label>
        <input
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          required
          minLength={6}
        />

        <label>Phone number (optional)</label>
        <input
          name="phoneNumber"
          value={form.phoneNumber}
          onChange={handleChange}
        />

        <label>Address (optional)</label>
        <input name="address" value={form.address} onChange={handleChange} />

        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? "Creating account..." : "Sign up"}
        </button>
        <p className="muted small center">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </form>
    </div>
  );
}
