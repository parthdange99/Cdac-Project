import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { formatMoney, formatDateTime, statusClass } from "../utils/format";

export default function MyDonations() {
  const [donations, setDonations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/donations/my")
      .then((res) => setDonations(res.data.data || []))
      .catch((err) =>
        setError(err?.response?.data?.message || "Could not load donations.")
      )
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="muted center">Loading...</p>;

  return (
    <div className="page">
      <h1>My donations</h1>
      {error && <div className="alert alert-error">{error}</div>}
      {donations.length === 0 ? (
        <p className="muted">You haven't made any donations yet.</p>
      ) : (
        <div className="table-list">
          {donations.map((d) => (
            <div key={d.id} className="card list-row">
              <div className="list-row-main">
                <Link to={`/campaigns/${d.campaignId}`}>
                  <h3>{d.campaignTitle}</h3>
                </Link>
                <p className="muted small">{formatDateTime(d.donatedAt)}</p>
                {d.message && <p className="muted small">"{d.message}"</p>}
              </div>
              <div className="list-row-actions">
                <span className={statusClass(d.paymentStatus)}>
                  {d.paymentStatus}
                </span>
                <strong>{formatMoney(d.amount)}</strong>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
