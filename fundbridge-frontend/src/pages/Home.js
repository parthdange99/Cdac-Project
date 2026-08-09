import React, { useEffect, useState } from "react";
import api from "../api/axios";
import CampaignCard from "../components/CampaignCard";

export default function Home() {
  const [campaigns, setCampaigns] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const size = 9;

  const load = async (pageNum = 0, searchTerm = "") => {
    setLoading(true);
    setError("");
    try {
      const url = searchTerm
        ? "/campaigns/public/search"
        : "/campaigns/public/all";
      const params = searchTerm
        ? { keyword: searchTerm, page: pageNum, size }
        : { page: pageNum, size };
      const res = await api.get(url, { params });
      const data = res.data.data;
      setCampaigns(data.content || []);
      setTotalPages(data.totalPages || 0);
      setPage(pageNum);
    } catch (err) {
      setError(
        err?.response?.data?.message || "Could not load campaigns right now."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(0, "");
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    load(0, keyword.trim());
  };

  return (
    <div className="page">
      <section className="hero">
        <h1>Fund what matters. Borrow what you need.</h1>
        <p className="muted">
          Discover active campaigns and support causes you care about, or
          browse peer-to-peer loan requests as a lender.
        </p>
        <form className="search-bar" onSubmit={handleSearch}>
          <input
            placeholder="Search campaigns by keyword..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <button className="btn btn-primary" type="submit">
            Search
          </button>
        </form>
      </section>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <p className="muted center">Loading campaigns...</p>
      ) : campaigns.length === 0 ? (
        <p className="muted center">No campaigns found.</p>
      ) : (
        <div className="grid grid-3">
          {campaigns.map((c) => (
            <CampaignCard key={c.id} campaign={c} />
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            className="btn btn-ghost"
            disabled={page === 0}
            onClick={() => load(page - 1, keyword.trim())}
          >
            Previous
          </button>
          <span className="muted">
            Page {page + 1} of {totalPages}
          </span>
          <button
            className="btn btn-ghost"
            disabled={page + 1 >= totalPages}
            onClick={() => load(page + 1, keyword.trim())}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
