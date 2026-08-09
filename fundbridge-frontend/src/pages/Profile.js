import React, { useEffect, useState } from "react";
import api from "../api/axios";
import { formatDateTime } from "../utils/format";

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ fullName: "", phoneNumber: "", address: "" });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const load = async () => {
    setLoading(true);
    try {
      const res = await api.get("/users/profile");
      const data = res.data.data;
      setProfile(data);
      setForm({
        fullName: data.fullName || "",
        phoneNumber: data.phoneNumber || "",
        address: data.address || "",
      });
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load profile.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    setSuccess("");
    try {
      const res = await api.put("/users/profile", form);
      setProfile(res.data.data);
      setSuccess("Profile updated.");
    } catch (err) {
      setError(err?.response?.data?.message || "Could not update profile.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p className="muted center">Loading profile...</p>;
  if (!profile) return <div className="alert alert-error">{error}</div>;

  return (
    <div className="page narrow">
      <div className="card">
        <h2>Your profile</h2>
        <p className="muted small">
          {profile.username} &middot; {profile.email} &middot;{" "}
          {profile.role?.replace("ROLE_", "")}
        </p>
        <p className="muted small">
          Member since {formatDateTime(profile.createdAt)}
        </p>

        <form onSubmit={handleSubmit}>
          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}

          <label>Full name</label>
          <input
            value={form.fullName}
            maxLength={100}
            onChange={(e) => setForm({ ...form, fullName: e.target.value })}
          />

          <label>Phone number</label>
          <input
            value={form.phoneNumber}
            maxLength={15}
            onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
          />

          <label>Address</label>
          <textarea
            rows={3}
            maxLength={500}
            value={form.address}
            onChange={(e) => setForm({ ...form, address: e.target.value })}
          />

          <button className="btn btn-primary btn-block" disabled={saving}>
            {saving ? "Saving..." : "Save changes"}
          </button>
        </form>
      </div>
    </div>
  );
}
