requireRole("CANDIDATE");
renderNavbar("dashboard");

const user = getUser();
document.getElementById("userName").textContent = user.name.split(" ")[0];
const hour = new Date().getHours();
document.getElementById("greetingEyebrow").textContent = hour < 12 ? "Good morning" : hour < 18 ? "Good afternoon" : "Good evening";

async function loadRecent() {
  const listEl = document.getElementById("recentList");
  try {
    const apps = await apiFetch("/candidate/applications");
    apps.sort((a, b) => new Date(b.appliedDate) - new Date(a.appliedDate));
    const recent = apps.slice(0, 3);

    if (recent.length === 0) {
      document.getElementById("emptyState").style.display = "block";
      return;
    }

    listEl.innerHTML = recent.map(app => `
      <div class="card application-row">
        <div class="app-info">
          <h3><a href="job-details.html?id=${app.jobId}" style="color:var(--ink);">${escapeHtml(app.jobTitle)}</a></h3>
          <span class="text-small text-muted">Applied ${timeAgo(app.appliedDate)}</span>
        </div>
        <span class="badge ${statusBadgeClass(app.status)}">${app.status}</span>
      </div>
    `).join("");
  } catch (err) {
    listEl.innerHTML = `<p class="text-muted">Couldn't load recent applications.</p>`;
  }
}

loadRecent();