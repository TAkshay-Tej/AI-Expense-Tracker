import { useState, useEffect, useMemo } from "react";
import { getExpenses, formatCurrency } from "../api";
import { getCategoryIcon } from "../components/icons";
import { PieChart } from "lucide-react";

const CHART_COLORS = [
  "#2F7E79",
  "#1f5e5a",
  "#4a9d94",
  "#6bb5b0",
  "#8fc9c4",
  "#b5ddd9",
];

function buildConicGradient(entries, total) {
  if (total <= 0 || entries.length === 0) return "conic-gradient(var(--border) 0deg 360deg)";
  let acc = 0;
  const parts = entries.map(([_, amt], i) => {
    const startDeg = (acc / total) * 360;
    acc += amt;
    const endDeg = (acc / total) * 360;
    return `${CHART_COLORS[i % CHART_COLORS.length]} ${startDeg}deg ${endDeg}deg`;
  });
  return `conic-gradient(${parts.join(", ")})`;
}

export default function StatsScreen() {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getExpenses().then(setExpenses).finally(() => setLoading(false));
  }, []);

  const expenseOnly = expenses.filter((e) => e.type === "EXPENSE");
  const incomeOnly = expenses.filter((e) => e.type === "INCOME");

  const totalExpense = expenseOnly.reduce((s, e) => s + e.amount, 0);
  const totalIncome = incomeOnly.reduce((s, e) => s + e.amount, 0);
  const balance = totalIncome - totalExpense;

  const topExpenses = [...expenseOnly].sort((a, b) => b.amount - a.amount).slice(0, 5);

  const byDate = expenseOnly.reduce((acc, e) => {
    const date = e.transactionDate?.substring(0, 10) || "Unknown";
    acc[date] = (acc[date] || 0) + e.amount;
    return acc;
  }, {});

  const chartData = Object.entries(byDate)
    .sort(([a], [b]) => a.localeCompare(b))
    .slice(-7);

  const maxChart = Math.max(...chartData.map(([, v]) => v), 1);

  const categoryPie = useMemo(() => {
    const byCat = expenseOnly.reduce((acc, e) => {
      const cat = (e.category && String(e.category).trim()) || "Uncategorized";
      acc[cat] = (acc[cat] || 0) + e.amount;
      return acc;
    }, {});
    const sorted = Object.entries(byCat).sort((a, b) => b[1] - a[1]);
    if (sorted.length <= 6) {
      return { segments: sorted, total: totalExpense };
    }
    const top = sorted.slice(0, 5);
    const rest = sorted.slice(5).reduce((s, [, v]) => s + v, 0);
    if (rest > 0) top.push(["Other", rest]);
    return { segments: top, total: totalExpense };
  }, [expenseOnly, totalExpense]);

  const donutGradient = buildConicGradient(categoryPie.segments, categoryPie.total);

  return (
    <div className="screen">
      {loading ? (
        <div style={{ display: "flex", justifyContent: "center", padding: 60 }}>
          <div
            className="spinner"
            style={{
              borderTopColor: "var(--zinc)",
              borderColor: "var(--border)",
              width: 32,
              height: 32,
            }}
          />
        </div>
      ) : (
        <>
          <div className="stats-grid">
            <div className="stats-kpi">
              <div className="stats-kpi__label">Total income</div>
              <div className="stats-kpi__value income">{formatCurrency(totalIncome)}</div>
            </div>
            <div className="stats-kpi">
              <div className="stats-kpi__label">Total expenses</div>
              <div className="stats-kpi__value expense">{formatCurrency(totalExpense)}</div>
            </div>
            <div className="stats-kpi">
              <div className="stats-kpi__label">Net balance</div>
              <div className="stats-kpi__value balance">{formatCurrency(balance)}</div>
            </div>
          </div>

          <div className="stats-charts-row">
            <div className="card">
              <p className="stats-section-title">Spending by day (last 7 days with data)</p>
              {chartData.length === 0 ? (
                <p style={{ color: "var(--text-muted)", textAlign: "center", padding: 24 }}>
                  Add expenses to see a bar chart.
                </p>
              ) : (
                <div className="stats-bar-wrap">
                  {chartData.map(([date, amount]) => (
                    <div key={date} className="stats-bar-col">
                      <div className="stats-bar-value">{formatCurrency(amount)}</div>
                      <div className="stats-bar-track">
                        <div
                          className="stats-bar-fill"
                          style={{
                            height: `${Math.max(8, (amount / maxChart) * 100)}%`,
                          }}
                          title={`${date}: ${formatCurrency(amount)}`}
                        />
                      </div>
                      <div className="stats-bar-label">{date.substring(5)}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="card">
              <p className="stats-section-title" style={{ display: "flex", alignItems: "center", gap: 8 }}>
                <PieChart size={18} strokeWidth={2} />
                Spending by category
              </p>
              {categoryPie.total <= 0 || categoryPie.segments.length === 0 ? (
                <p style={{ color: "var(--text-muted)", textAlign: "center", padding: 24 }}>
                  Add expenses to see a category breakdown.
                </p>
              ) : (
                <div className="stats-donut-wrap">
                  <div
                    className="stats-donut"
                    style={{ background: donutGradient }}
                    role="img"
                    aria-label="Category spending donut chart"
                  >
                    <div className="stats-donut__hole" />
                  </div>
                  <div className="stats-legend">
                    {categoryPie.segments.map(([name, amt], i) => (
                      <div key={name} className="stats-legend__row">
                        <span
                          className="stats-legend__swatch"
                          style={{ background: CHART_COLORS[i % CHART_COLORS.length] }}
                        />
                        <span className="stats-legend__name" title={name}>
                          {name}
                        </span>
                        <span className="stats-legend__pct">
                          {((amt / categoryPie.total) * 100).toFixed(0)}% · {formatCurrency(amt)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className="card">
            <p style={{ fontSize: 16, fontWeight: 700, marginBottom: 12 }}>Top spending</p>
            {topExpenses.length === 0 ? (
              <p style={{ color: "var(--text-muted)", textAlign: "center", padding: 20 }}>
                No expenses yet
              </p>
            ) : (
              topExpenses.map((item) => (
                <div key={item.id} className="transaction-item">
                  <div className="tx-icon" style={{ background: "#fff0f0" }}>
                    {(() => {
                      const Icon = getCategoryIcon(item.category);
                      return <Icon size={18} />;
                    })()}
                  </div>
                  <div className="tx-info">
                    <p className="tx-title">{item.title}</p>
                    <p className="tx-date">
                      {item.category} • {item.transactionDate}
                    </p>
                  </div>
                  <span className="tx-amount expense">-{formatCurrency(item.amount)}</span>
                </div>
              ))
            )}
          </div>
        </>
      )}
    </div>
  );
}
