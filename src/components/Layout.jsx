import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { getUserName, logout } from "../api";
import {
  ChartColumn,
  CircleUserRound,
  LogOut,
  Menu,
  ReceiptText,
  Sparkles,
  Wallet,
} from "lucide-react";

export default function Layout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  const navItems = [
    { path: "/home", label: "Home", icon: Wallet },
    { path: "/stats", label: "Stats", icon: ChartColumn },
    { path: "/transactions", label: "Transactions", icon: ReceiptText },
    { path: "/insights", label: "AI Insights", icon: Sparkles },
    { path: "/profile", label: "Profile", icon: CircleUserRound },
  ];

  const handleLogout = () => {
    logout();
    window.location.reload(); // or navigate to login
  };

  const getPageTitle = () => {
    const item = navItems.find(item => item.path === location.pathname);
    return item ? item.label : "Dashboard";
  };

  return (
    <div className="layout">
      {/* Sidebar */}
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <h1>Expense Tracker</h1>
        </div>
        <nav className="sidebar-nav">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`nav-link ${location.pathname === item.path ? "active" : ""}`}
                onClick={() => setSidebarOpen(false)}
              >
                <Icon size={18} className="icon" />
                {item.label}
              </Link>
            );
          })}
        </nav>
      </aside>

      {/* Main content */}
      <div className="main-content">
        {/* Header */}
        <header className="header">
          <button className="hamburger" onClick={() => setSidebarOpen(!sidebarOpen)}>
            <Menu size={20} />
          </button>
          <h2>{getPageTitle()}</h2>
          <div style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', gap: 12 }}>
            <span style={{ fontSize: 14, color: 'var(--text-muted)' }}>Hello, {getUserName()}</span>
            <button onClick={handleLogout} style={{
              background: 'none',
              border: 'none',
              color: 'var(--text-muted)',
              cursor: 'pointer',
              fontSize: 14,
              display: "inline-flex",
              alignItems: "center",
              gap: 6
            }}>
              <LogOut size={15} />
              Logout
            </button>
          </div>
        </header>

        {/* Page content */}
        <main className="content">
          {children}
        </main>
      </div>

      {/* Overlay for mobile */}
      {sidebarOpen && <div className="overlay" onClick={() => setSidebarOpen(false)}></div>}
    </div>
  );
}
