export default function BottomNav({ active, navigate }) {
  return (
    <div className="bottom-nav">
      <button
        className={`nav-item ${active === "home" ? "active" : ""}`}
        onClick={() => navigate("home")}
      >
        <svg viewBox="0 0 24 24" fill={active === "home" ? "currentColor" : "none"} stroke="currentColor" strokeWidth="2">
          <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" strokeLinecap="round" strokeLinejoin="round"/>
          <polyline points="9 22 9 12 15 12 15 22" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
        Home
      </button>
      <button
        className={`nav-item ${active === "stats" ? "active" : ""}`}
        onClick={() => navigate("stats")}
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <line x1="18" y1="20" x2="18" y2="10" strokeLinecap="round"/>
          <line x1="12" y1="20" x2="12" y2="4"  strokeLinecap="round"/>
          <line x1="6"  y1="20" x2="6"  y2="14" strokeLinecap="round"/>
        </svg>
        Stats
      </button>
    </div>
  );
}
