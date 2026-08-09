import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

const initialForm = {
  title: "",
  description: "",
  goalAmount: "",
  endDate: "",
  imageUrl: "",
  category: "",
};

export default function CreateCampaign() {
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
        ...form,
        goalAmount: Number(form.goalAmount),
      };
      const res = await api.post("/campaigns", payload);
      const created = res.data.data;
      navigate(`/campaigns/${created.id}`);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not create campaign.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page narrow">
      <form className="card" onSubmit={handleSubmit}>
        <h2>Start a campaign</h2>
        <p className="muted">
          Your campaign will be submitted for review before it goes live.
        </p>
        {error && <div className="alert alert-error">{error}</div>}

        <label>Title</label>
        <input
          name="title"
          maxLength={200}
          value={form.title}
          onChange={handleChange}
          required
        />

        <label>Description</label>
        <textarea
          name="description"
          rows={5}
          value={form.description}
          onChange={handleChange}
          required
        />

        <label>Goal amount (₹)</label>
        <input
          type="number"
          name="goalAmount"
          min="100"
          step="0.01"
          value={form.goalAmount}
          onChange={handleChange}
          required
        />

        <label>End date</label>
        <input
          type="date"
          name="endDate"
          value={form.endDate}
          onChange={handleChange}
          required
        />

        <label>Category (optional)</label>
        <input name="category" value={form.category} onChange={handleChange} />

        <label>Image URL (optional)</label>
        <input name="imageUrl" value={form.imageUrl} onChange={handleChange} />

        <button className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? "Submitting..." : "Submit campaign"}
        </button>
      </form>
    </div>
  );
}
