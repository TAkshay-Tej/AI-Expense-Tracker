import { useState, useEffect } from "react";
import { getInsights, formatCurrency } from "../api";
import { Lightbulb, PieChart, Trophy, Wallet } from "lucide-react";

export default function InsightsScreen({ navigate }) {
  const [insight, setInsight] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState("");

  useEffect(() => { loadInsights(); }, []);

  const loadInsights = async () => {
    setLoading(true); setError("");
    try {
      const data = await getInsights();
      setInsight(data);
    } catch (e) {
      setError("Could not load insights");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="screen">
      {loading ? (
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", padding: 60, gap: 16 }}>
          <div className="spinner" style={{ borderTopColor: "var(--zinc)", borderColor: "var(--border)", width: 32, height: 32 }} />
          <p style={{ color: "var(--text-muted)", fontSize: 14 }}>Analyzing your spending...</p>
        </div>
      ) : error ? (
        <div className="card" style={{ textAlign: "center", padding: 40 }}>
          <p style={{ color: "var(--text-muted)", marginBottom: 16 }}>{error}</p>
          <button className="btn" onClick={loadInsights} style={{ width: "auto", padding: "10px 24px" }}>
            Try Again
          </button>
        </div>
      ) : insight && (
        <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>

          {/* Stat cards row — matches InsightStatCard */}
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
            <StatCard
              icon={Wallet}
              label="Total Spent"
              value={formatCurrency(insight.totalExpenses)}
              valueColor="var(--red)"
            />
            <StatCard
              icon={PieChart}
              label="Total Income"
              value={formatCurrency(insight.totalIncome)}
              valueColor="var(--green)"
            />
          </div>

          {/* Top Category — matches InsightCard contentIsLarge */}
          <InsightCard title="Top Spending Category" icon={Trophy}>
            <p style={{ fontSize: 22, fontWeight: 700, color: "var(--zinc)" }}>
              {insight.topSpendingCategory}
            </p>
          </InsightCard>

          {/* Summary */}
          <InsightCard title="Spending Summary" icon={PieChart}>
            <p style={{ fontSize: 14, color: "#333", lineHeight: 1.6 }}>
              {insight.summary}
            </p>
          </InsightCard>

          {/* Advice — highlighted card */}
          <InsightCard
            title="💡 Smart Advice"
            icon={Lightbulb}
            style={{ background: "var(--zinc-light)", border: "1px solid rgba(47,126,121,0.2)" }}
          >
            <p style={{ fontSize: 14, color: "#333", lineHeight: 1.6 }}>
              {insight.advice}
            </p>
          </InsightCard>
        </div>
      )}
    </div>
  );
}

function StatCard({ icon: Icon, label, value, valueColor }) {
  return (
    <div className="card">
      <div style={{ marginBottom: 8 }}><Icon size={20} /></div>
      <p style={{ fontSize: 12, color: "var(--text-muted)", marginBottom: 4 }}>{label}</p>
      <p style={{ fontSize: 18, fontWeight: 700, color: valueColor, fontFamily: "DM Mono, monospace" }}>
        {value}
      </p>
    </div>
  );
}

function InsightCard({ title, icon: Icon, children, style = {} }) {
  return (
    <div className="card" style={style}>
      <p style={{ fontSize: 15, fontWeight: 600, marginBottom: 10, display: "flex", gap: 8, alignItems: "center" }}>
        {Icon ? <Icon size={16} /> : null}
        {title}
      </p>
      {children}
    </div>
  );
}
