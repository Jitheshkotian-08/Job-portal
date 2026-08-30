requireRole("CANDIDATE");
renderNavbar("applications");

async function loadApplications() {
  const listEl = document.getElementById("appList");
  listEl.innerHTML = Array(3).fill('<div class="skeleton" style="height:80px; margin-bottom:12px;"></div>').join("");

  try {
    const apps = await apiFetch("/candidate/applications");
    apps.sort((a, b) => new Date(b.appliedDate) - new Date(a.appliedDate));
    renderApplications(apps);
  } catch (err) {
    listEl.innerHTML = `<div class="empty-state"><h3>Something went wrong</h3><p>${escapeHtml(err.message)}</p></div>`;
  }
}

function renderApplications(apps) {
  const listEl = document.getElementById("appList");

  if (apps.length === 0) {
    listEl.innerHTML = "";
    document.getElementById("emptyState").style.display = "block";
    return;
  }

  listEl.innerHTML = apps.map(app => `
    <div class="card application-row">
      <div class="app-info">
        <h3><a href="job-details.html?id=${app.jobId}" style="color:var(--ink);">${escapeHtml(app.jobTitle)}</a></h3>
        <span class="text-small text-muted">Applied ${timeAgo(app.appliedDate)}</span>
      </div>
      <div class="app-actions">
        <a href="${SERVER_BASE_URL}${app.resumePath}" target="_blank" class="btn btn-outline btn-sm">View resume</a>
        <span class="badge ${statusBadgeClass(app.status)}">${app.status}</span>
      </div>
    </div>
  `).join("");
}

loadApplications();