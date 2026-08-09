import React, { useState, useEffect } from "react";
import api from "../api/axios";
import { formatDateTime } from "../utils/format";

export default function NotificationsDropdown() {
  const [notifications, setNotifications] = useState([]);
  const [open, setOpen] = useState(false);

  const fetchUnread = async () => {
    try {
      const res = await api.get("/notifications/unread");
      setNotifications(res.data.data || []);
    } catch (err) {
      console.error("Could not fetch notifications", err);
    }
  };

  useEffect(() => {
    fetchUnread();
    // Optional: set up an interval to poll for notifications every 30 seconds
    const interval = setInterval(fetchUnread, 30000);
    return () => clearInterval(interval);
  }, []);

  const markAsRead = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`);
      // Optimistically remove it from the list
      setNotifications(notifications.filter((n) => n.id !== id));
    } catch (err) {
      console.error("Could not mark as read", err);
    }
  };

  return (
    <div className="notifications-wrapper" style={{ position: "relative" }}>
      <button
        className="btn btn-ghost"
        style={{ position: "relative", padding: "6px 10px" }}
        onClick={() => setOpen(!open)}
      >
        🔔
        {notifications.length > 0 && (
          <span
            className="badge badge-danger"
            style={{
              position: "absolute",
              top: "-5px",
              right: "-5px",
              padding: "2px 6px",
              fontSize: "0.65rem",
            }}
          >
            {notifications.length}
          </span>
        )}
      </button>

      {open && (
        <div
          className="card"
          style={{
            position: "absolute",
            top: "100%",
            right: 0,
            width: "320px",
            maxHeight: "400px",
            overflowY: "auto",
            marginTop: "10px",
            padding: "0",
            zIndex: 100,
            boxShadow: "0 10px 25px rgba(0,0,0,0.1)",
          }}
        >
          <div style={{ padding: "12px 16px", borderBottom: "1px solid var(--border)", fontWeight: "bold" }}>
            Notifications
          </div>
          {notifications.length === 0 ? (
            <div style={{ padding: "20px", textAlign: "center", color: "var(--muted)" }}>
              No unread notifications
            </div>
          ) : (
            <ul style={{ listStyle: "none", margin: 0, padding: 0 }}>
              {notifications.map((n) => (
                <li
                  key={n.id}
                  style={{
                    padding: "12px 16px",
                    borderBottom: "1px solid var(--border)",
                    cursor: "pointer",
                  }}
                  onClick={() => markAsRead(n.id)}
                >
                  <div style={{ fontWeight: "600", fontSize: "0.9rem", marginBottom: "4px" }}>
                    {n.title}
                  </div>
                  <div style={{ fontSize: "0.85rem", color: "var(--muted)", marginBottom: "4px" }}>
                    {n.message}
                  </div>
                  <div style={{ fontSize: "0.75rem", color: "var(--muted)" }}>
                    {formatDateTime(n.createdAt)}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}
