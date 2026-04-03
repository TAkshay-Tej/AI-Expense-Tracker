const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

const getToken  = ()      => localStorage.getItem("jwt_token");
const getBearer = ()      => `Bearer ${getToken()}`;
const saveAuth  = (data)  => {
  localStorage.setItem("jwt_token",  data.token);
  localStorage.setItem("user_name",  data.name);
  localStorage.setItem("user_email", data.email);
  localStorage.setItem("currency", data.currency || "USD");
  localStorage.setItem("monthly_budget", data.monthlyBudget ?? 0);
};

// ── Auth ──────────────────────────────────────────────────

export const register = async (name, email, password) => {
  const res = await fetch(`${BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Registration failed");
  saveAuth(data);
  return data;
};

export const login = async (email, password) => {
  const res = await fetch(`${BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Login failed");
  saveAuth(data);
  return data;
};

export const logout = () => {
  localStorage.removeItem("jwt_token");
  localStorage.removeItem("user_name");
  localStorage.removeItem("user_email");
  localStorage.removeItem("currency");
  localStorage.removeItem("monthly_budget");
};

// ── Expenses ──────────────────────────────────────────────

export const getExpenses = async () => {
  const res = await fetch(`${BASE_URL}/api/expenses`, {
    headers: { Authorization: getBearer() },
  });
  if (res.status === 403) { logout(); throw new Error("Session expired"); }
  return res.json();
};

export const addExpense = async ({ title, amount, type, notes }) => {
  const res = await fetch(`${BASE_URL}/api/expenses`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: getBearer(),
    },
    body: JSON.stringify({ title, amount, type, notes, category: null }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to add");
  return data;
};

export const deleteExpense = async (id) => {
  await fetch(`${BASE_URL}/api/expenses/${id}`, {
    method: "DELETE",
    headers: { Authorization: getBearer() },
  });
};

// ── AI Insights ───────────────────────────────────────────

export const getInsights = async () => {
  const res = await fetch(`${BASE_URL}/api/expenses/insights`, {
    headers: { Authorization: getBearer() },
  });
  return res.json();
};

export const getProfile = async () => {
  const res = await fetch(`${BASE_URL}/api/auth/profile`, {
    headers: { Authorization: getBearer() },
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to load profile");
  localStorage.setItem("user_name", data.name);
  localStorage.setItem("user_email", data.email);
  localStorage.setItem("currency", data.currency || "USD");
  localStorage.setItem("monthly_budget", data.monthlyBudget ?? 0);
  return data;
};

// ── Helpers ───────────────────────────────────────────────

export const getUserName  = () => localStorage.getItem("user_name")  || "User";
export const getUserEmail = () => localStorage.getItem("user_email") || "";
export const getMonthlyBudget = () => parseFloat(localStorage.getItem("monthly_budget")) || 0;
export const getCurrency = () => localStorage.getItem("currency") || "";

export const getCategoryEmoji = (category) => {
  const map = {
    food: "🍔", dining: "🍽️", starbucks: "☕", grocery: "🛒",
    transport: "🚗", travel: "✈️", shopping: "🛍️",
    salary: "💰", income: "💵", paypal: "💳", freelance: "💻",
    entertainment: "🎬", netflix: "📺", subscriptions: "📱",
    health: "💊", healthcare: "🏥", utilities: "💡",
    education: "📚", rent: "🏠", insurance: "🛡️",
  };
  const key = (category || "").toLowerCase();
  return Object.entries(map).find(([k]) => key.includes(k))?.[1] || "💸";
};

export const formatCurrency = (amount) => {
  const currency = getCurrency().toUpperCase() || "USD";
  try {
    return new Intl.NumberFormat("en-US", { style: "currency", currency }).format(amount || 0);
  } catch {
    return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(amount || 0);
  }
};

// ── Profile ───────────────────────────────────────────────

export const updateProfile = async (name) => {
  const res = await fetch(`${BASE_URL}/api/auth/profile`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: getBearer(),
    },
    body: JSON.stringify({ name }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to update profile");
  localStorage.setItem("user_name", data.name);
  return data;
};

export const updateBudget = async (monthlyBudget) => {
  const res = await fetch(`${BASE_URL}/api/auth/budget`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: getBearer(),
    },
    body: JSON.stringify({ monthlyBudget }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to update budget");
  localStorage.setItem("monthly_budget", data.monthlyBudget);
  return data;
};

export const updateCurrency = async (currency) => {
  const res = await fetch(`${BASE_URL}/api/auth/currency`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: getBearer(),
    },
    body: JSON.stringify({ currency }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to update currency");
  localStorage.setItem("currency", data.currency);
  return data;
};

export const updatePassword = async (currentPassword, newPassword) => {
  const res = await fetch(`${BASE_URL}/api/auth/password`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: getBearer(),
    },
    body: JSON.stringify({ currentPassword, newPassword }),
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || "Failed to update password");
  return data;
};
