import React, { useCallback, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../api/axios";
import { formatMoney, statusClass } from "../../utils/format";

export default function LoanDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loan, setLoan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [offerAmount, setOfferAmount] = useState("");
  const [offerRate, setOfferRate] = useState("");
  const [offerMessage, setOfferMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [offerError, setOfferError] = useState("");
  const [offerSuccess, setOfferSuccess] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get(`/loans/${id}`);
      setLoan(res.data.data);
    } catch (err) {
      setError(err?.response?.data?.message || "Loan request not found.");
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  const handleSubmitOffer = async (e) => {
    e.preventDefault();
    setOfferError("");
    setOfferSuccess("");
    setSubmitting(true);
    try {
      await api.post(`/loans/${id}/offer`, {
        offeredAmount: Number(offerAmount),
        offeredInterestRate: Number(offerRate),
        message: offerMessage,
      });
      setOfferSuccess("Offer submitted successfully!");
      setOfferAmount("");
      setOfferRate("");
      setOfferMessage("");
    } catch (err) {
      setOfferError(err?.response?.data?.message || "Could not submit offer.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <p className="muted center">Loading...</p>;
  if (error) return <div className="alert alert-error">{error}</div>;
  if (!loan) return null;

  return (
    <div className="page">
      <button className="btn btn-ghost" onClick={() => navigate(-1)}>
        &larr; Back
      </button>
      <div className="detail-layout">
        <div>
          <div className="detail-header">
            <span className={statusClass(loan.status)}>{loan.status}</span>
          </div>
          <h1>{formatMoney(loan.amount)} requested</h1>
          <p className="muted">
            Borrower: {loan.borrowerName} &middot; Tenure: {loan.tenureMonths}{" "}
            months
          </p>
          {loan.creditScore && (
            <p className="muted">Credit score: {loan.creditScore}</p>
          )}
          {loan.monthlyIncome && (
            <p className="muted">
              Monthly income: {formatMoney(loan.monthlyIncome)}
            </p>
          )}
          <h3>Purpose</h3>
          <p className="body-text">{loan.purpose}</p>
        </div>

        <aside className="card donate-card">
          <h3>Submit a loan offer</h3>
          <form onSubmit={handleSubmitOffer}>
            {offerError && <div className="alert alert-error">{offerError}</div>}
            {offerSuccess && (
              <div className="alert alert-success">{offerSuccess}</div>
            )}
            <label>Offered amount (₹)</label>
            <input
              type="number"
              min="1000"
              step="0.01"
              value={offerAmount}
              onChange={(e) => setOfferAmount(e.target.value)}
              required
            />
            <label>Interest rate (% per annum, max 36%)</label>
            <input
              type="number"
              min="0"
              max="36"
              step="0.1"
              value={offerRate}
              onChange={(e) => setOfferRate(e.target.value)}
              required
            />
            <label>Message (optional)</label>
            <textarea
              rows={2}
              value={offerMessage}
              onChange={(e) => setOfferMessage(e.target.value)}
            />
            <button className="btn btn-primary btn-block" disabled={submitting}>
              {submitting ? "Submitting..." : "Submit offer"}
            </button>
          </form>
        </aside>
      </div>
    </div>
  );
}
