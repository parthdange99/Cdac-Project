import React, { useEffect, useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import { formatMoney, formatDateTime, statusClass } from "../utils/format";
import { loadRazorpayScript } from "../utils/loadRazorpay";

export default function CampaignDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [campaign, setCampaign] = useState(null);
  const [donations, setDonations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");
  const [anonymous, setAnonymous] = useState(false);
  const [donating, setDonating] = useState(false);
  const [donateError, setDonateError] = useState("");
  const [donateSuccess, setDonateSuccess] = useState("");
  const [suggestingAi, setSuggestingAi] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [campaignRes, donationsRes] = await Promise.all([
        api.get(`/campaigns/public/${id}`),
        api.get(`/donations/campaign/${id}`),
      ]);
      setCampaign(campaignRes.data.data);
      setDonations(donationsRes.data.data || []);
    } catch (err) {
      setError(err?.response?.data?.message || "Campaign not found.");
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  const handleSuggestMessage = async () => {
    setDonateError("");
    setSuggestingAi(true);
    try {
      const res = await api.post("/ai/suggest-donation-message", {
        donorName: anonymous ? "Anonymous" : (user?.username || "Anonymous"),
        campaignTitle: campaign.title,
      });
      setMessage(res.data.data.message);
    } catch (err) {
      setDonateError("Could not generate AI message. Please try again.");
    } finally {
      setSuggestingAi(false);
    }
  };

  const handleDonate = async (e) => {
    e.preventDefault();
    setDonateError("");
    setDonateSuccess("");

    if (!user) {
      navigate("/login");
      return;
    }

    setDonating(true);
    try {
      // 1. Initiate donation record on our backend
      const donationRes = await api.post(`/donations/campaign/${id}`, {
        amount: Number(amount),
        message,
        anonymous,
      });
      const donation = donationRes.data.data;

      // 2. Create a Razorpay order
      const orderRes = await api.post("/payments/create-order", null, {
        params: {
          amount: Number(amount),
          description: `Donation to ${campaign?.title || "campaign"}`,
        },
      });
      const order = orderRes.data.data;

      // 3. Load Razorpay checkout and open it
      const loaded = await loadRazorpayScript();
      if (!loaded || !window.Razorpay) {
        setDonateError(
          "Could not load the payment gateway. Please check your connection and try again."
        );
        setDonating(false);
        return;
      }

      const options = {
        key: order.keyId,
        amount: order.amount,
        currency: order.currency,
        name: "FundBridge",
        description: order.description,
        order_id: order.orderId,
        handler: async function (response) {
          try {
            await api.patch(`/donations/${donation.id}/confirm`, null, {
              params: {
                razorpayPaymentId: response.razorpay_payment_id,
                razorpayOrderId: response.razorpay_order_id || order.orderId,
              },
            });
            setDonateSuccess("Thank you! Your donation was confirmed.");
            setAmount("");
            setMessage("");
            setAnonymous(false);
            load();
          } catch (err) {
            setDonateError(
              err?.response?.data?.message ||
                "Payment succeeded but confirmation failed. Contact support."
            );
          } finally {
            setDonating(false);
          }
        },
        modal: {
          ondismiss: function () {
            setDonating(false);
          },
        },
        prefill: {
          name: user.username,
          email: user.email,
        },
        theme: { color: "#4f46e5" },
      };

      const rzp = new window.Razorpay(options);
      rzp.open();
    } catch (err) {
      setDonateError(err?.response?.data?.message || "Could not start donation.");
      setDonating(false);
    }
  };

  if (loading) return <p className="muted center">Loading campaign...</p>;
  if (error) return <div className="alert alert-error">{error}</div>;
  if (!campaign) return null;

  const goal = Number(campaign.goalAmount || 0);
  const raised = Number(campaign.raisedAmount || 0);
  const pct = goal > 0 ? Math.min(100, Math.round((raised / goal) * 100)) : 0;

  return (
    <div className="page">
      <div className="detail-layout">
        <div>
          <div className="detail-image">
            {campaign.imageUrl ? (
              <img src={campaign.imageUrl} alt={campaign.title} />
            ) : (
              <div className="campaign-card-placeholder large">
                {campaign.title?.[0] || "F"}
              </div>
            )}
          </div>
          <div className="detail-header">
            {campaign.category && <span className="tag">{campaign.category}</span>}
            <span className={statusClass(campaign.status)}>{campaign.status}</span>
          </div>
          <h1>{campaign.title}</h1>
          <p className="muted">
            by {campaign.creatorName || "Anonymous"} &middot; ends{" "}
            {campaign.endDate}
          </p>
          <p className="body-text">{campaign.description}</p>

          <h3>Recent donations</h3>
          {donations.length === 0 ? (
            <p className="muted">No donations yet. Be the first to support!</p>
          ) : (
            <ul className="donation-list">
              {donations.map((d) => (
                <li key={d.id} className="donation-item">
                  <div>
                    <strong>{d.donorName || "Anonymous"}</strong>
                    <span className="muted"> &middot; {formatDateTime(d.donatedAt)}</span>
                    {d.message && <p className="muted small">"{d.message}"</p>}
                  </div>
                  <div className="donation-amount">{formatMoney(d.amount)}</div>
                </li>
              ))}
            </ul>
          )}
        </div>

        <aside className="card donate-card">
          <div className="progress-bar large">
            <div className="progress-fill" style={{ width: `${pct}%` }} />
          </div>
          <div className="donate-stats">
            <strong>{formatMoney(raised)}</strong>
            <span className="muted"> raised of {formatMoney(goal)}</span>
          </div>
          <p className="muted small">{pct}% funded</p>

          <form onSubmit={handleDonate}>
            {donateError && <div className="alert alert-error">{donateError}</div>}
            {donateSuccess && (
              <div className="alert alert-success">{donateSuccess}</div>
            )}
            <label>Amount (₹)</label>
            <input
              type="number"
              min="1"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
              placeholder="500"
            />
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginTop: "1rem" }}>
              <label style={{ margin: 0 }}>Message (optional)</label>
              <button 
                type="button" 
                onClick={handleSuggestMessage} 
                disabled={suggestingAi}
                style={{ 
                  background: "linear-gradient(90deg, #a855f7, #ec4899)", 
                  color: "white", 
                  border: "none", 
                  padding: "2px 8px", 
                  borderRadius: "15px",
                  cursor: "pointer",
                  fontSize: "0.75rem"
                }}
              >
                {suggestingAi ? "✨ Generating..." : "✨ Suggest Message"}
              </button>
            </div>
            <textarea
              rows={2}
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Wishing you the best!"
            />
            <label className="checkbox-row">
              <input
                type="checkbox"
                checked={anonymous}
                onChange={(e) => setAnonymous(e.target.checked)}
              />
              Donate anonymously
            </label>
            <button className="btn btn-primary btn-block" disabled={donating}>
              {donating ? "Processing..." : "Donate now"}
            </button>
            {!user && (
              <p className="muted small center">
                You'll need to log in to complete a donation.
              </p>
            )}
          </form>
        </aside>
      </div>
    </div>
  );
}
