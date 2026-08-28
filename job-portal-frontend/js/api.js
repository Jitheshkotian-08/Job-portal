const API_BASE_URL = "http://localhost:8080/api";

// ---------- Token / user storage ----------
function setSession(token, user) {
  localStorage.setItem("jp_token", token);
  localStorage.setItem("jp_user", JSON.stringify(user));
}

function getToken() {
  return localStorage.getItem("jp_token");
}

function getUser() {
  const raw = localStorage.getItem("jp_user");
  return raw ? JSON.parse(raw) : null;
}

function clearSession() {
  localStorage.removeItem("jp_token");
  localStorage.removeItem("jp_user");
}

function isLoggedIn() {
  return !!getToken();
}

function logout() {
  clearSession();
  window.location.href = "login.html";
}

function redirectToDashboard() {
  const user = getUser();
  if (!user) { window.location.href = "login.html"; return; }
  window.location.href = user.role === "RECRUITER"
    ? "recruiter-dashboard.html"
    : "candidate-dashboard.html";
}

// Guards for protected pages — call at top of page's <script>
function requireRole(role) {
  const user = getUser();
  if (!isLoggedIn() || !user || user.role !== role) {
    window.location.href = "login.html";
  }
}

// ---------- Core fetch wrapper ----------
async function apiFetch(endpoint, options = {}) {
  const headers = options.body instanceof FormData
    ? {}
    : { "Content-Type": "application/json" };

  const token = getToken();
  if (token) headers["Authorization"] = "Bearer " + token;

  const response = await fetch(API_BASE_URL + endpoint, {
    ...options,
    headers: { ...headers, ...(options.headers || {}) },
  });

  let data = null;
  const text = await response.text();
  if (text) {
    try { data = JSON.parse(text); } catch (e) { data = text; }
  }

  if (!response.ok) {
    const message = (data && data.message) || (data && data.errors) || "Something went wrong";
    throw new Error(typeof message === "object" ? Object.values(message).join(", ") : message);
  }

  return data;
}

function showAlert(elementId, message) {
  const el = document.getElementById(elementId);
  el.textContent = message;
  el.classList.add("show");
}

function hideAlert(elementId) {
  document.getElementById(elementId).classList.remove("show");
}