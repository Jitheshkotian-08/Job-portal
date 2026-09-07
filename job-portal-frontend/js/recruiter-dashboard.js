requireRole("RECRUITER");
renderNavbar("dashboard");

const user = getUser();
document.getElementById("userName").textContent = user.name.split(" ")[0];
const hour = new Date().getHours();
document.getElementById("greetingEyebrow").textContent = hour < 12 ? "Good morning" : hour < 18 ? "Good afternoon" : "Good evening";

async function loadDashboard() {
  const statsGrid = document.getElementById("statsGrid");
  statsGrid.innerHTML = Array(4).fill('<div class="skeleton" style="height:100px;"></div>').join("");

  try {
    const jobs = await apiFetch("/recruiter/jobs");

    if (jobs.length === 0) {
      statsGrid.innerHTML = "";
      document.getElementById("emptyState").style.display = "block";
      return;
    }

    // Fetch applicants for every job in parallel to build aggregate stats
    const applicationLists = await Promise.all(
      jobs.map(job => apiFetch(`/recruiter/jobs/${job.id}/applications`).catch(() => []))
    );

    const allApplications = applicationLists.flat();
    const activeJobs = jobs.filter(j => j.status === "ACTIVE").length;
    const pending = allApplications.filter(a => a.status === "APPLIED").length;

    statsGrid.innerHTML = `
      <div class="card stat-card">
        <div class="stat-icon">💼</div>
        <div class="stat-value">${jobs.length}</div>
        <div class="stat-label">Jobs posted</div>
      </div>
      <div class="card stat-card">
        <div class="stat-icon">🟢</div>
        <div class="stat-value">${activeJobs}</div>
        <div class="stat-label">Active listings</div>
      </div>
      <div class="card stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-value">${allApplications.length}</div>
        <div class="stat-label">Total applicants</div>
      </div>
      <div class="card stat-card">
        <div class="stat-icon">⏳</div>
        <div class="stat-value">${pending}</div>
        <div class="stat-label">Awaiting review</div>
      </div>
    `;

    renderRecentJobs(jobs, applicationLists);
  } catch (err) {
    statsGrid.innerHTML = `<div class="empty-state"><h3>Something went wrong</h3><p>${escapeHtml(err.message)}</p></div>`;
  }
}

function renderRecentJobs(jobs, applicationLists) {
  const sorted = [...jobs].sort((a, b) => new Date(b.postedDate) - new Date(a.postedDate)).slice(0, 5);

  document.getElementById("recentJobs").innerHTML = sorted.map(job => {
    const count = applicationLists[jobs.indexOf(job)]?.length || 0;
    return `
      <div class="card job-row">
        <div class="job-row-info">
          <h3>${escapeHtml(job.title)}</h3>
          <div class="job-row-meta">
            <span>📍 ${escapeHtml(job.location)}</span>
            <span>·</span>
            <span>${formatJobType(job.jobType)}</span>
            <span>·</span>
            <span>${count} applicant${count === 1 ? "" : "s"}</span>
          </div>
        </div>
        <div class="job-row-actions">
          <a href="job-applicants.html?jobId=${job.id}" class="btn btn-outline btn-sm">View applicants</a>
        </div>
      </div>
    `;
  }).join("");
}

loadDashboard();