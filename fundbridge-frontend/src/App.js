import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import PrivateRoute from "./components/PrivateRoute";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import CampaignDetail from "./pages/CampaignDetail";
import CreateCampaign from "./pages/CreateCampaign";
import MyCampaigns from "./pages/MyCampaigns";
import Profile from "./pages/Profile";
import MyDonations from "./pages/MyDonations";

import RequestLoan from "./pages/loans/RequestLoan";
import MyLoanRequests from "./pages/loans/MyLoanRequests";
import PendingLoanRequests from "./pages/loans/PendingLoanRequests";
import LoanDetail from "./pages/loans/LoanDetail";
import RepaymentSchedule from "./pages/loans/RepaymentSchedule";

import AdminUsers from "./pages/admin/AdminUsers";
import AdminCampaigns from "./pages/admin/AdminCampaigns";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        <main className="app-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/campaigns/:id" element={<CampaignDetail />} />

            <Route
              path="/campaigns/new"
              element={
                <PrivateRoute>
                  <CreateCampaign />
                </PrivateRoute>
              }
            />
            <Route
              path="/campaigns/my"
              element={
                <PrivateRoute>
                  <MyCampaigns />
                </PrivateRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <PrivateRoute>
                  <Profile />
                </PrivateRoute>
              }
            />
            <Route
              path="/donations/my"
              element={
                <PrivateRoute>
                  <MyDonations />
                </PrivateRoute>
              }
            />

            <Route
              path="/loans/request"
              element={
                <PrivateRoute>
                  <RequestLoan />
                </PrivateRoute>
              }
            />
            <Route
              path="/loans/my-requests"
              element={
                <PrivateRoute>
                  <MyLoanRequests />
                </PrivateRoute>
              }
            />
            <Route
              path="/loans/pending"
              element={
                <PrivateRoute>
                  <PendingLoanRequests />
                </PrivateRoute>
              }
            />
            <Route
              path="/loans/:id"
              element={
                <PrivateRoute>
                  <LoanDetail />
                </PrivateRoute>
              }
            />
            <Route
              path="/loans/:id/repayment-schedule"
              element={
                <PrivateRoute>
                  <RepaymentSchedule />
                </PrivateRoute>
              }
            />

            <Route
              path="/admin/users"
              element={
                <PrivateRoute adminOnly>
                  <AdminUsers />
                </PrivateRoute>
              }
            />
            <Route
              path="/admin/campaigns"
              element={
                <PrivateRoute adminOnly>
                  <AdminCampaigns />
                </PrivateRoute>
              }
            />

            <Route path="*" element={<Home />} />
          </Routes>
        </main>
      </AuthProvider>
    </BrowserRouter>
  );
}
