import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../api/axios";
import { formatMoney, statusClass } from "../../utils/format";

export default function PendingLoanRequests() {
  const [loans, setLoans] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const size = 10;

  const load = async (pageNum = 0) => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/loans/pending", {
        params: { page: pageNum, size },
      });
      const data = res.data.data;
      setLoans(data.content || []);
      setTotalPages(data.totalPages || 0);
      setPage(pageNum);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load loan requests.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(0);
  }, []);

  if (loading) return <p className="muted center">Loading...</p>;

  return (
    <div className="page">
      <h1>Loan requests seeking lenders</h1>
      {error && <div className="alert alert-error">{error}</div>}
      {loans.length === 0 ? (
        <p className="muted">No pending loan requests right now.</p>
      ) : (
        <div className="table-list">
          {loans.map((loan) => (
            <Link
              key={loan.id}
              to={`/loans/${loan.id}`}
              className="card list-row"
            >
              <div className="list-row-main">
                <h3>
                  {formatMoney(loan.amount)} &middot; {loan.tenureMonths} months
                </h3>
                <p className="muted small">{loan.purpose}</p>
                <p className="muted small">
                  Requested by {loan.borrowerName}
                  {loan.creditScore ? ` · Credit score ${loan.creditScore}` : ""}
                </p>
              </div>
              <div className="list-row-actions">
                <span className={statusClass(loan.status)}>{loan.status}</span>
              </div>
            </Link>
          ))}
        </div>
      )}
      {totalPages > 1 && (
        <div className="pagination">
          <button
            className="btn btn-ghost"
            disabled={page === 0}
            onClick={() => load(page - 1)}
          >
            Previous
          </button>
          <span className="muted">
            Page {page + 1} of {totalPages}
          </span>
          <button
            className="btn btn-ghost"
            disabled={page + 1 >= totalPages}
            onClick={() => load(page + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
