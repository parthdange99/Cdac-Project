import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../api/axios";
import { formatMoney, statusClass } from "../../utils/format";

export default function MyLoanRequests() {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [busyOfferId, setBusyOfferId] = useState(null);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/loans/my-requests");
      setLoans(res.data.data || []);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load loan requests.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const respondToOffer = async (offerId, action) => {
    setBusyOfferId(offerId);
    try {
      await api.patch(`/loans/offers/${offerId}/${action}`);
      load();
    } catch (err) {
      alert(err?.response?.data?.message || `Could not ${action} offer.`);
    } finally {
      setBusyOfferId(null);
    }
  };

  if (loading) return <p className="muted center">Loading...</p>;

  return (
    <div className="page">
      <div className="page-header">
        <h1>My loan requests</h1>
        <Link to="/loans/request" className="btn btn-primary">
          + New request
        </Link>
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      {loans.length === 0 ? (
        <p className="muted">You haven't requested any loans yet.</p>
      ) : (
        <div className="table-list">
          {loans.map((loan) => (
            <div key={loan.id} className="card list-row-stack">
              <div className="list-row">
                <div className="list-row-main">
                  <h3>{formatMoney(loan.amount)} &middot; {loan.tenureMonths} months</h3>
                  <p className="muted small">{loan.purpose}</p>
                </div>
                <div className="list-row-actions">
                  <span className={statusClass(loan.status)}>{loan.status}</span>
                  <Link
                    className="btn btn-ghost"
                    to={`/loans/${loan.id}/repayment-schedule`}
                  >
                    Repayment schedule
                  </Link>
                </div>
              </div>

              {loan.loanOffers && loan.loanOffers.length > 0 && (
                <div className="offers-list">
                  <h4>Offers received</h4>
                  {loan.loanOffers.map((offer) => (
                    <div key={offer.id} className="offer-row">
                      <div>
                        <strong>{formatMoney(offer.offeredAmount)}</strong> at{" "}
                        {offer.offeredInterestRate}% from {offer.lenderName}
                        {offer.message && (
                          <p className="muted small">"{offer.message}"</p>
                        )}
                      </div>
                      <div className="row-actions">
                        <span className={statusClass(offer.offerStatus)}>
                          {offer.offerStatus}
                        </span>
                        {offer.offerStatus === "PENDING" && (
                          <>
                            <button
                              className="btn btn-primary btn-sm"
                              disabled={busyOfferId === offer.id}
                              onClick={() => respondToOffer(offer.id, "accept")}
                            >
                              Accept
                            </button>
                            <button
                              className="btn btn-danger-ghost btn-sm"
                              disabled={busyOfferId === offer.id}
                              onClick={() => respondToOffer(offer.id, "reject")}
                            >
                              Reject
                            </button>
                          </>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
