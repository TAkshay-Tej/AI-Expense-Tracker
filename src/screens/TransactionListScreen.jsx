import { useState, useEffect } from "react";
import { getExpenses, deleteExpense, formatCurrency } from "../api";
import { Filter, Trash2 } from "lucide-react";
import { getCategoryIcon } from "../components/icons";

export default function TransactionListScreen({ navigate }) {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading]   = useState(true);
  const [filterType, setFilterType]   = useState("All");
  const [showFilter, setShowFilter]   = useState(false);

  useEffect(() => {
    getExpenses().then(setExpenses).finally(() => setLoading(false));
  }, []);

  const handleDelete = async (id) => {
    await deleteExpense(id);
    setExpenses(prev => prev.filter(e => e.id !== id));
  };

  const filtered = expenses.filter(e => {
    if (filterType === "Expense") return e.type === "EXPENSE";
    if (filterType === "Income")  return e.type === "INCOME";
    return true;
  });

  return (
    <div className="screen">
      {/* Filter */}
      <div style={{ marginBottom: 16, display: "flex", justifyContent: "flex-end" }}>
        <div style={{ position: "relative" }}>
          <button
            onClick={() => setShowFilter(!showFilter)}
            className="btn-secondary"
            style={{ padding: "8px 12px", fontSize: 14 }}
          >
            <Filter size={15} /> Filter: {filterType}
          </button>
          {showFilter && (
            <div style={{
              position: "absolute", top: "100%", right: 0, zIndex: 10,
              background: "white", borderRadius: 10, padding: 4, boxShadow: "var(--shadow)",
              minWidth: 120
            }}>
              {["All", "Expense", "Income"].map(type => (
                <button
                  key={type}
                  onClick={() => { setFilterType(type); setShowFilter(false); }}
                  style={{
                    display: "block", width: "100%", textAlign: "left",
                    padding: "10px 14px", border: "none", background: "none",
                    borderRadius: 8, cursor: "pointer", fontFamily: "DM Sans, sans-serif",
                    fontSize: 14, fontWeight: filterType === type ? 600 : 400,
                    color: filterType === type ? "var(--zinc)" : "var(--text)",
                    backgroundColor: filterType === type ? "var(--zinc-light)" : "transparent"
                  }}
                >
                  {type}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {loading ? (
        <div style={{ display: "flex", justifyContent: "center", padding: 60 }}>
          <div className="spinner" style={{ borderTopColor: "var(--zinc)", borderColor: "var(--border)", width: 32, height: 32 }} />
        </div>
      ) : filtered.length === 0 ? (
        <div className="card" style={{ textAlign: "center", padding: 40, color: "var(--text-muted)" }}>
          <p style={{ fontSize: 32, marginBottom: 8 }}>-</p>
          <p>No transactions found</p>
        </div>
      ) : (
        <div className="card">
          {filtered.map(item => {
            const isIncome = item.type === "INCOME";
            return (
              <div key={item.id} className="transaction-item">
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
                    onClick={() => handleDelete(item.id)}
                    style={{ background: "none", border: "none", cursor: "pointer", fontSize: 16 }}
                  ><Trash2 size={16} /></button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
