import { useState, useEffect } from "react";
import {
  getExpenses, deleteExpense, logout,
  formatCurrency
} from "../api";
import { ArrowDownCircle, ArrowUpCircle, Plus, Trash2 } from "lucide-react";
import { getCategoryIcon } from "../components/icons";

export default function HomeScreen({ navigate }) {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading]   = useState(true);
  const [fabOpen, setFabOpen]   = useState(false);

  useEffect(() => { loadExpenses(); }, []);

  const loadExpenses = async () => {
    setLoading(true);
    try {
      const data = await getExpenses();
      setExpenses(data);
    } catch (e) {
      if (e.message === "Session expired") { logout(); navigate("/login"); }
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    await deleteExpense(id);
    loadExpenses();
  };

  const handleLogout = () => { logout(); navigate("/login"); };

  const totalIncome  = expenses.filter(e => e.type === "INCOME").reduce((s, e) => s + e.amount, 0);
  const totalExpense = expenses.filter(e => e.type === "EXPENSE").reduce((s, e) => s + e.amount, 0);
  const balance      = totalIncome - totalExpense;

  const recent = expenses.slice(0, 10);

  return (
    <div className="screen">

      {/* Balance Card — matches CardItem */}
      <div style={{
        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        borderRadius: 16, padding: 20, marginBottom: 20,
        color: "white"
      }}>
        <p style={{ fontSize: 13, fontWeight: 500 }}>Total Balance</p>
        <h1 style={{
          fontSize: 34, fontWeight: 700,
          letterSpacing: -1, margin: "6px 0 16px",
          fontFamily: "DM Mono, monospace"
        }}>
          {formatCurrency(balance)}
        </h1>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          {/* Income row item */}
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <div style={{
              width: 32, height: 32, borderRadius: 8,
              background: "rgba(255,255,255,0.2)",
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: 16
            }}><ArrowUpCircle size={16} /></div>
            <div>
              <p style={{ fontSize: 12 }}>Income</p>
              <p style={{ fontWeight: 700, fontSize: 15, fontFamily: "DM Mono, monospace" }}>
                {formatCurrency(totalIncome)}
              </p>
            </div>
          </div>
          {/* Expense row item */}
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <div style={{
              width: 32, height: 32, borderRadius: 8,
              background: "rgba(255,255,255,0.2)",
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: 16
            }}><ArrowDownCircle size={16} /></div>
            <div>
              <p style={{ fontSize: 12 }}>Expense</p>
              <p style={{ fontWeight: 700, fontSize: 15, fontFamily: "DM Mono, monospace" }}>
                {formatCurrency(totalExpense)}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div style={{ display: "flex", gap: 12, marginBottom: 20 }}>
        <button
          className="btn-secondary"
          onClick={() => navigate("/add-expense", { isIncome: false })}
          style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center", gap: 8 }}
        >
          <Plus size={16} /> Add Expense
        </button>
        <button
          className="btn-secondary"
          onClick={() => navigate("/add-expense", { isIncome: true })}
          style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center", gap: 8 }}
        >
          <ArrowUpCircle size={16} /> Add Income
        </button>
      </div>

      {/* Recent Transactions */}
      <div className="card">
        <h3 style={{ marginBottom: 16 }}>Recent Transactions</h3>
        {loading ? (
          <p>Loading...</p>
        ) : recent.length === 0 ? (
          <p>No transactions yet.</p>
        ) : (
          recent.map(item => <TransactionRow key={item.id} item={item} onDelete={handleDelete} />)
        )}
      </div>
    </div>
  );
}

function TransactionRow({ item, onDelete }) {
  const isIncome = item.type === "INCOME";
  return (
    <div className="transaction-item" style={{ position: "relative" }}>
      <div className="tx-icon" style={{ background: isIncome ? "#e8f9f0" : "#fff0f0" }}>
        {(() => {
          const Icon = getCategoryIcon(item.category);
          return <Icon size={18} />;
        })()}
      </div>
      <div className="tx-info">
        <p className="tx-title">{item.title}</p>
        <p className="tx-date">{item.category} • {item.transactionDate}</p>
      </div>
      <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
        <span className={`tx-amount ${isIncome ? "income" : "expense"}`}>
          {isIncome ? "+" : "-"}{formatCurrency(item.amount)}
        </span>
        <button
          onClick={() => onDelete(item.id)}
          style={{
            background: "none", border: "none", cursor: "pointer",
            color: "var(--text-muted)", fontSize: 16, lineHeight: 1
          }}
        >
          <Trash2 size={16} />
        </button>
      </div>
    </div>
  );
}
