import React from "react";
import { Link } from "react-router-dom";
import { formatMoney, statusClass } from "../utils/format";

export default function CampaignCard({ campaign }) {
  const goal = Number(campaign.goalAmount || 0);
  const raised = Number(campaign.raisedAmount || 0);
  const pct = goal > 0 ? Math.min(100, Math.round((raised / goal) * 100)) : 0;

  return (
    <Link to={`/campaigns/${campaign.id}`} className="card campaign-card">
      <div className="campaign-card-image">
        {campaign.imageUrl ? (
          <img src={campaign.imageUrl} alt={campaign.title} />
        ) : (
          <div className="campaign-card-placeholder">{campaign.title?.[0] || "F"}</div>
        )}
        <span className={statusClass(campaign.status)}>{campaign.status}</span>
      </div>
      <div className="campaign-card-body">
        {campaign.category && <span className="tag">{campaign.category}</span>}
        <h3>{campaign.title}</h3>
        <p className="muted line-clamp-2">{campaign.description}</p>
        <div className="progress-bar">
          <div className="progress-fill" style={{ width: `${pct}%` }} />
        </div>
        <div className="campaign-card-stats">
          <span>
            <strong>{formatMoney(raised)}</strong> raised
          </span>
          <span className="muted">of {formatMoney(goal)}</span>
        </div>
        <div className="muted small">by {campaign.creatorName || "Anonymous"}</div>
      </div>
    </Link>
  );
}
