import React, { createContext, useContext, useEffect, useState } from "react";
import api from "../api/axios";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem("fb_user");
    return stored ? JSON.parse(stored) : null;
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      localStorage.setItem("fb_user", JSON.stringify(user));
    } else {
      localStorage.removeItem("fb_user");
    }
  }, [user]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const res = await api.post("/auth/login", { email, password });
      const data = res.data.data; // AuthResponse
      localStorage.setItem("fb_token", data.token);
      const loggedInUser = {
        userId: data.userId,
        email: data.email,
        username: data.username,
        role: data.role,
      };
      setUser(loggedInUser);
      return { success: true };
    } catch (err) {
      return {
        success: false,
        message: err?.response?.data?.message || "Login failed",
      };
    } finally {
      setLoading(false);
    }
  };

  const register = async (payload) => {
    setLoading(true);
    try {
      const res = await api.post("/auth/register", payload);
      const data = res.data.data;
      localStorage.setItem("fb_token", data.token);
      const loggedInUser = {
        userId: data.userId,
        email: data.email,
        username: data.username,
        role: data.role,
      };
      setUser(loggedInUser);
      return { success: true };
    } catch (err) {
      return {
        success: false,
        message: err?.response?.data?.message || "Registration failed",
      };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("fb_token");
    localStorage.removeItem("fb_user");
    setUser(null);
  };

  const isAdmin = user?.role === "ROLE_ADMIN";

  return (
    <AuthContext.Provider
      value={{ user, loading, login, register, logout, isAdmin }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
