import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { formatMoney, statusClass } from "../utils/format";

export default function MyCampaigns() {
  const [campaigns, setCampaigns] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [editForm, setEditForm] = useState({});
  const [savingId, setSavingId] = useState(null);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/campaigns/my");
      setCampaigns(res.data.data || []);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load your campaigns.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const startEdit = (c) => {
    setEditingId(c.id);
    setEditForm({
      title: c.title,
      description: c.description,
      goalAmount: c.goalAmount,
      endDate: c.endDate,
      imageUrl: c.imageUrl || "",
      category: c.category || "",
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditForm({});
  };

  const saveEdit = async (id) => {
    setSavingId(id);
    try {
      await api.put(`/campaigns/${id}`, {
        ...editForm,
        goalAmount: Number(editForm.goalAmount),
      });
      setEditingId(null);
      load();
    } catch (err) {
      alert(err?.response?.data?.message || "Could not update campaign.");
    } finally {
      setSavingId(null);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this campaign? This cannot be undone.")) return;
    try {
      await api.delete(`/campaigns/${id}`);
      load();
    } catch (err) {
      alert(err?.response?.data?.message || "Could not delete campaign.");
    }
  };

  if (loading) return <p className="muted center">Loading...</p>;

  return (
    <div className="page">
      <div className="page-header">
        <h1>My campaigns</h1>
        <Link to="/campaigns/new" className="btn btn-primary">
          + New campaign
        </Link>
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      {campaigns.length === 0 ? (
        <p className="muted">You haven't created any campaigns yet.</p>
      ) : (
        <div className="table-list">
          {campaigns.map((c) => (
            <div key={c.id} className="card list-row">
              {editingId === c.id ? (
                <div className="edit-form">
                  <label>Title</label>
                  <input
                    value={editForm.title}
                    onChange={(e) =>
                      setEditForm({ ...editForm, title: e.target.value })
                    }
                  />
                  <label>Description</label>
                  <textarea
                    rows={3}
                    value={editForm.description}
                    onChange={(e) =>
                      setEditForm({ ...editForm, description: e.target.value })
                    }
                  />
                  <div className="grid-2">
                    <div>
                      <label>Goal amount (₹)</label>
                      <input
                        type="number"
                        value={editForm.goalAmount}
                        onChange={(e) =>
                          setEditForm({ ...editForm, goalAmount: e.target.value })
                        }
                      />
                    </div>
                    <div>
                      <label>End date</label>
                      <input
                        type="date"
                        value={editForm.endDate}
                        onChange={(e) =>
                          setEditForm({ ...editForm, endDate: e.target.value })
                        }
                      />
                    </div>
                  </div>
                  <label>Category</label>
                  <input
                    value={editForm.category}
                    onChange={(e) =>
                      setEditForm({ ...editForm, category: e.target.value })
                    }
                  />
                  <label>Image URL</label>
                  <input
                    value={editForm.imageUrl}
                    onChange={(e) =>
                      setEditForm({ ...editForm, imageUrl: e.target.value })
                    }
                  />
                  <div className="row-actions">
                    <button
                      className="btn btn-primary"
                      onClick={() => saveEdit(c.id)}
                      disabled={savingId === c.id}
                    >
                      {savingId === c.id ? "Saving..." : "Save"}
                    </button>
                    <button className="btn btn-ghost" onClick={cancelEdit}>
                      Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <div className="list-row-main">
                    <Link to={`/campaigns/${c.id}`}>
                      <h3>{c.title}</h3>
                    </Link>
                    <p className="muted small">
                      {formatMoney(c.raisedAmount)} raised of{" "}
                      {formatMoney(c.goalAmount)} &middot; ends {c.endDate}
                    </p>
                  </div>
                  <div className="list-row-actions">
                    <span className={statusClass(c.status)}>{c.status}</span>
                    <button className="btn btn-ghost" onClick={() => startEdit(c)}>
                      Edit
                    </button>
                    <button
                      className="btn btn-danger-ghost"
                      onClick={() => handleDelete(c.id)}
                    >
                      Delete
                    </button>
                  </div>
                </>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
