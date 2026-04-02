import { useState } from "react";
import { addExpense, getCurrency } from "../api";
import { Sparkles } from "lucide-react";

const INCOME_ITEMS  = ["Paypal","Salary","Freelance","Investments","Bonus","Rental Income","Other Income"];
const EXPENSE_ITEMS = ["Grocery","Netflix","Rent","Paypal","Starbucks","Shopping","Transport",
                       "Utilities","Dining Out","Entertainment","Healthcare","Insurance",
                       "Subscriptions","Education","Debt Payments","Gifts & Donations","Travel","Other Expenses"];

export default function AddExpenseScreen({ navigate, isIncome }) {
  const [title, setTitle]   = useState(isIncome ? INCOME_ITEMS[0] : EXPENSE_ITEMS[0]);
  const [amount, setAmount] = useState("");
  const [notes, setNotes]   = useState("");
  const [date, setDate]     = useState(new Date().toISOString().split("T")[0]);
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState("");
  const [aiCategory, setAiCategory] = useState("");

  const handleSubmit = async () => {
    setError("");
    if (!title || !amount) { setError("Please fill in all fields"); return; }
    const parsed = parseFloat(amount);
    if (isNaN(parsed) || parsed <= 0) { setError("Enter a valid amount"); return; }

    setLoading(true);
    try {
      const result = await addExpense({
        title,
        amount: parsed,
        type: isIncome ? "INCOME" : "EXPENSE",
        notes: notes || null,
      });
      if (result.category) setAiCategory(result.category);
      setTimeout(() => navigate("/home"), 1000);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const listItems = isIncome ? INCOME_ITEMS : EXPENSE_ITEMS;

  return (
    <div className="screen">
      {/* Form card — matches DataForm white card */}
      <div style={{ padding: "0 16px" }}>
        <div className="card" style={{ boxShadow: "0 8px 40px rgba(0,0,0,0.12)" }}>

          {/* AI category badge */}
          {aiCategory && (
            <div style={{
              background: "var(--zinc-light)", borderRadius: 8,
              padding: "8px 12px", marginBottom: 16,
              color: "var(--zinc)", fontSize: 13, fontWeight: 600,
              display: "flex", alignItems: "center", gap: 6
            }}>
              <Sparkles size={15} /> AI categorized as: <strong>{aiCategory}</strong>
            </div>
          )}

          <div className="input-group">
            <label className="input-label">Name</label>
            <select
              className="input-field"
              value={title}
              onChange={e => setTitle(e.target.value)}
            >
              {listItems.map(item => (
                <option key={item} value={item}>{item}</option>
              ))}
            </select>
          </div>

          <div className="input-group">
            <label className="input-label">Amount</label>
            <div style={{ position: "relative" }}>
              <span style={{
                position: "absolute", left: 14, top: "50%",
                transform: "translateY(-50%)",
                color: "var(--text-muted)", fontSize: 14, fontWeight: 600
              }}>{getCurrency() || "USD"}</span>
              <input
                className="input-field"
                type="number"
                placeholder="0.00"
                value={amount}
                onChange={e => setAmount(e.target.value)}
                style={{ paddingLeft: 28, fontFamily: "DM Mono, monospace" }}
              />
            </div>
          </div>

          <div className="input-group">
            <label className="input-label">Date</label>
            <input
              className="input-field"
              type="date"
              value={date}
              onChange={e => setDate(e.target.value)}
            />
          </div>

          <div className="input-group">
            <label className="input-label">Notes (optional)</label>
            <input
              className="input-field"
              placeholder="Add a note..."
              value={notes}
              onChange={e => setNotes(e.target.value)}
            />
          </div>

          {error && <p className="error-msg">{error}</p>}

          <div style={{ marginTop: 24 }}>
            <button className="btn" onClick={handleSubmit} disabled={loading}>
              {loading
                ? <><div className="spinner" /> Saving & AI categorizing...</>
                : `Add ${isIncome ? "Income" : "Expense"}`
              }
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
