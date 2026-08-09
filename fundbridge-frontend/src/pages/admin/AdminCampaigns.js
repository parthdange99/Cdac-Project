import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../api/axios";
import { formatMoney, statusClass } from "../../utils/format";

const STATUS_OPTIONS = ["PENDING_REVIEW", "ACTIVE", "COMPLETED", "CANCELLED"];

export default function AdminCampaigns() {
  const [statusFilter, setStatusFilter] = useState("PENDING_REVIEW");
  const [campaigns, setCampaigns] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [busyId, setBusyId] = useState(null);
  const size = 10;

  const load = async (status = statusFilter, pageNum = 0) => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/campaigns/admin/all", {
        params: { status, page: pageNum, size },
      });
      const data = res.data.data;
      setCampaigns(data.content || []);
      setTotalPages(data.totalPages || 0);
      setPage(pageNum);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load campaigns.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(statusFilter, 0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [statusFilter]);

  const updateStatus = async (id, status) => {
    setBusyId(id);
    try {
      await api.patch(`/campaigns/${id}/status`, null, { params: { status } });
      load(statusFilter, page);
    } catch (err) {
      alert(err?.response?.data?.message || "Could not update status.");
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>Admin &middot; Campaigns</h1>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          {STATUS_OPTIONS.map((s) => (
            <option key={s} value={s}>
              {s.replace("_", " ")}
            </option>
          ))}
        </select>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <p className="muted center">Loading...</p>
      ) : campaigns.length === 0 ? (
        <p className="muted">No campaigns with status "{statusFilter}".</p>
      ) : (
        <div className="table-list">
          {campaigns.map((c) => (
            <div key={c.id} className="card list-row">
              <div className="list-row-main">
                <Link to={`/campaigns/${c.id}`}>
                  <h3>{c.title}</h3>
                </Link>
                <p className="muted small">
                  {formatMoney(c.raisedAmount)} of {formatMoney(c.goalAmount)} by{" "}
                  {c.creatorName}
                </p>
              </div>
              <div className="list-row-actions">
                <span className={statusClass(c.status)}>{c.status}</span>

                {c.status === "PENDING_REVIEW" ? (
                  <>
                    <button
                      className="btn btn-primary btn-sm"
                      disabled={busyId === c.id}
                      onClick={() => updateStatus(c.id, "ACTIVE")}
                    >
                      Approve
                    </button>
                    <button
                      className="btn btn-danger-ghost btn-sm"
                      disabled={busyId === c.id}
                      onClick={() => updateStatus(c.id, "CANCELLED")}
                    >
                      Reject
                    </button>
                  </>
                ) : (
                  <select
                    defaultValue=""
                    disabled={busyId === c.id}
                    onChange={(e) => {
                      if (e.target.value) updateStatus(c.id, e.target.value);
                    }}
                  >
                    <option value="" disabled>
                      Change status...
                    </option>
                    {STATUS_OPTIONS.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </select>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            className="btn btn-ghost"
            disabled={page === 0}
            onClick={() => load(statusFilter, page - 1)}
          >
            Previous
          </button>
          <span className="muted">
            Page {page + 1} of {totalPages}
          </span>
          <button
            className="btn btn-ghost"
            disabled={page + 1 >= totalPages}
            onClick={() => load(statusFilter, page + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
