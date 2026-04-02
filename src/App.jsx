import { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, useNavigate } from "react-router-dom";
import LoginScreen from "./screens/LoginScreen";
import HomeScreen from "./screens/HomeScreen";
import AddExpenseScreen from "./screens/AddExpenseScreen";
import StatsScreen from "./screens/StatsScreen";
import TransactionListScreen from "./screens/TransactionListScreen";
import InsightsScreen from "./screens/InsightsScreen";
import ProfileScreen from "./screens/ProfileScreen";
import Layout from "./components/Layout";
import { getProfile } from "./api";
import "./App.css";

function AppContent() {
  const [isIncome, setIsIncome] = useState(false);
  const navigate = useNavigate();
  const token = localStorage.getItem("jwt_token");

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    getProfile().catch(() => {
      localStorage.removeItem("jwt_token");
      navigate("/login");
    });
  }, [token, navigate]);

  const handleNavigate = (to, params = {}) => {
    if (params.isIncome !== undefined) setIsIncome(params.isIncome);
    navigate(to);
  };

  if (!token) {
    return <LoginScreen navigate={handleNavigate} />;
  }

  return (
    <Layout>
      <Routes>
        <Route path="/home" element={<HomeScreen navigate={handleNavigate} />} />
        <Route path="/add-expense" element={<AddExpenseScreen navigate={handleNavigate} isIncome={isIncome} />} />
        <Route path="/stats" element={<StatsScreen navigate={handleNavigate} />} />
        <Route path="/transactions" element={<TransactionListScreen navigate={handleNavigate} />} />
        <Route path="/insights" element={<InsightsScreen navigate={handleNavigate} />} />
        <Route path="/profile" element={<ProfileScreen navigate={handleNavigate} />} />
      </Routes>
    </Layout>
  );
}

export default function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}
