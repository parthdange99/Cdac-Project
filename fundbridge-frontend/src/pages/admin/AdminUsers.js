import React, { useEffect, useState } from "react";
import api from "../../api/axios";
import { formatDateTime } from "../../utils/format";

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [busyId, setBusyId] = useState(null);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get("/users/admin/all");
      setUsers(res.data.data || []);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load users.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const deactivate = async (id) => {
    if (!window.confirm("Deactivate this user?")) return;
    setBusyId(id);
    try {
      await api.patch(`/users/admin/${id}/deactivate`);
      load();
    } catch (err) {
      alert(err?.response?.data?.message || "Could not deactivate user.");
    } finally {
      setBusyId(null);
    }
  };

  if (loading) return <p className="muted center">Loading users...</p>;

  return (
    <div className="page">
      <h1>Admin &middot; Users</h1>
      {error && <div className="alert alert-error">{error}</div>}
      <table className="table">
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Full name</th>
            <th>Role</th>
            <th>Status</th>
            <th>Joined</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.username}</td>
              <td>{u.email}</td>
              <td>{u.fullName}</td>
              <td>{u.role?.replace("ROLE_", "")}</td>
              <td>
                <span className={u.active ? "badge badge-success" : "badge badge-danger"}>
                  {u.active ? "Active" : "Inactive"}
                </span>
              </td>
              <td>{formatDateTime(u.createdAt)}</td>
              <td>
                {u.active && (
                  <button
                    className="btn btn-danger-ghost btn-sm"
                    disabled={busyId === u.id}
                    onClick={() => deactivate(u.id)}
                  >
                    Deactivate
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
