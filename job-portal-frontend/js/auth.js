// ---------- Role toggle (register page only) ----------
const candidateOption = document.getElementById("candidateOption");
const recruiterOption = document.getElementById("recruiterOption");

if (candidateOption && recruiterOption) {
  const options = [candidateOption, recruiterOption];
  options.forEach(opt => {
    opt.addEventListener("click", () => {
      options.forEach(o => o.classList.remove("active"));
      opt.classList.add("active");
    });
  });
}

// ---------- Login ----------
const loginForm = document.getElementById("loginForm");
if (loginForm) {
  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    hideAlert("loginAlert");

    const btn = document.getElementById("loginBtn");
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Logging in...';

    try {
      const data = await apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email: document.getElementById("email").value.trim(),
          password: document.getElementById("password").value,
        }),
      });

      setSession(data.token, {
        id: data.id, name: data.name, email: data.email, role: data.role,
      });
      redirectToDashboard();
    } catch (err) {
      showAlert("loginAlert", err.message);
      btn.disabled = false;
      btn.textContent = "Log in";
    }
  });
}

// ---------- Register ----------
const registerForm = document.getElementById("registerForm");
if (registerForm) {
  registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    hideAlert("registerAlert");

    const btn = document.getElementById("registerBtn");
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Creating account...';

    const role = document.querySelector('input[name="role"]:checked').value;

    try {
      const data = await apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify({
          name: document.getElementById("name").value.trim(),
          email: document.getElementById("email").value.trim(),
          phone: document.getElementById("phone").value.trim(),
          password: document.getElementById("password").value,
          role: role,
        }),
      });

      setSession(data.token, {
        id: data.id, name: data.name, email: data.email, role: data.role,
      });
      redirectToDashboard();
    } catch (err) {
      showAlert("registerAlert", err.message);
      btn.disabled = false;
      btn.textContent = "Create account";
    }
  });
}