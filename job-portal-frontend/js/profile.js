requireLogin();
renderNavbar("profile");

async function loadProfile() {
  try {
    const profile = await apiFetch("/profile");
    document.getElementById("nameInput").value = profile.name;
    document.getElementById("emailInput").value = profile.email;
    document.getElementById("phoneInput").value = profile.phone || "";
    document.getElementById("roleBadge").textContent = profile.role;
    document.getElementById("profileAvatar").textContent = profile.name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
  } catch (err) {
    showAlert("profileAlert", err.message);
  }
}

document.getElementById("profileForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert("profileAlert");
  hideAlert("successAlert");

  const btn = document.getElementById("saveBtn");
  btn.disabled = true;
  btn.innerHTML = '<span class="spinner"></span> Saving...';

  try {
    const updated = await apiFetch("/profile", {
      method: "PUT",
      body: JSON.stringify({
        name: document.getElementById("nameInput").value.trim(),
        phone: document.getElementById("phoneInput").value.trim(),
      }),
    });

    const user = getUser();
    user.name = updated.name;
    setSession(getToken(), user);

    showAlert("successAlert", "Profile updated.");
    renderNavbar("profile");
  } catch (err) {
    showAlert("profileAlert", err.message);
  } finally {
    btn.disabled = false;
    btn.textContent = "Save changes";
  }
});

loadProfile();