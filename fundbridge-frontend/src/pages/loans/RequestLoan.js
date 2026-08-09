import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios";

const initialForm = {
  amount: "",
  tenureMonths: "",
  purpose: "",
  monthlyIncome: "",
  creditScore: "",
};

export default function RequestLoan() {
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const payload = {
        amount: Number(form.amount),
        tenureMonths: Number(form.tenureMonths),
        purpose: form.purpose,
        monthlyIncome: form.monthlyIncome ? Number(form.monthlyIncome) : null,
        creditScore: form.creditScore ? Number(form.creditScore) : null,
      };
      await api.post("/loans/request", payload);
      navigate("/loans/my-requests");
    } catch (err) {
      setError(err?.response?.data?.message || "Could not submit loan request.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page narrow">
      <form className="card" onSubmit={handleSubmit}>
        <h2>Request a loan</h2>
        <p className="muted">
          Tell lenders about your loan needs. Lenders will review and can send
          you offers.
        </p>
        {error && <div className="alert alert-error">{error}</div>}

        <label>Amount needed (₹)</label>
        <input
          type="number"
          name="amount"
          min="1000"
          step="0.01"
          value={form.amount}
          onChange={handleChange}
          required
        />

        <label>Tenure (months, 1-60)</label>
        <input
          type="number"
          name="tenureMonths"
          min="1"
          max="60"
          value={form.tenureMonths}
          onChange={handleChange}
          required
        />

        <label>Purpose</label>
        <textarea
          name="purpose"
          rows={3}
          value={form.purpose}
          onChange={handleChange}
          required
        />

        <label>Monthly income (optional)</label>
        <input
          type="number"
          name="monthlyIncome"
          value={form.monthlyIncome}
          onChange={handleChange}
        />

        <label>Credit score (optional)</label>
        <input
          type="number"
          name="creditScore"
          value={form.creditScore}
          onChange={handleChange}
        />

        <button className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? "Submitting..." : "Submit request"}
        </button>
      </form>
    </div>
  );
}
