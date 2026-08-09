import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import NotificationsDropdown from "./NotificationsDropdown";

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand">
          Fund<span>Bridge</span>
        </Link>
        <nav className="nav-links">
          <Link to="/">Campaigns</Link>
          {user && (
            <>
              <Link to="/campaigns/my">My Campaigns</Link>
              <Link to="/campaigns/new">Start Campaign</Link>
              <Link to="/donations/my">My Donations</Link>
              <Link to="/loans/request">Request Loan</Link>
              <Link to="/loans/my-requests">My Loans</Link>
              <Link to="/loans/pending">Lend</Link>
              {isAdmin && <Link to="/admin/users">Admin Users</Link>}
              {isAdmin && <Link to="/admin/campaigns">Admin Campaigns</Link>}
            </>
          )}
        </nav>
        <div className="nav-auth">
          {user ? (
            <>
              <NotificationsDropdown />
              <Link to="/profile" className="nav-user">
                {user.username}
              </Link>
              <button className="btn btn-ghost" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-ghost">
                Login
              </Link>
              <Link to="/register" className="btn btn-primary">
                Sign up
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
