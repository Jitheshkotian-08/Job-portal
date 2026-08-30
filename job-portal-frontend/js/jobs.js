requireRole("CANDIDATE");
renderNavbar("jobs");

let currentPage = 0;
const pageSize = 9;

async function loadJobs() {
  const keyword = document.getElementById("keywordInput").value.trim();
  const location = document.getElementById("locationInput").value.trim();
  const jobType = document.getElementById("jobTypeInput").value;

  const grid = document.getElementById("jobGrid");
  grid.innerHTML = Array(6).fill('<div class="skeleton" style="height:180px;"></div>').join("");
  document.getElementById("emptyState").style.display = "none";

  const params = new URLSearchParams({ page: currentPage, size: pageSize, sortBy: "postedDate", direction: "desc" });
  if (keyword) params.append("keyword", keyword);
  if (location) params.append("location", location);
  if (jobType) params.append("jobType", jobType);

  try {
    const data = await apiFetch("/jobs/search?" + params.toString());
    renderJobs(data);
  } catch (err) {
    grid.innerHTML = `<div class="empty-state"><h3>Something went wrong</h3><p>${escapeHtml(err.message)}</p></div>`;
  }
}

function renderJobs(page) {
  const grid = document.getElementById("jobGrid");
  document.getElementById("resultsCount").textContent = page.totalElements + " role" + (page.totalElements === 1 ? "" : "s") + " found";

  if (page.content.length === 0) {
    grid.innerHTML = "";
    document.getElementById("emptyState").style.display = "block";
    document.getElementById("pagination").innerHTML = "";
    return;
  }

  document.getElementById("emptyState").style.display = "none";

  grid.innerHTML = page.content.map(job => `
    <div class="card card-spine ${spineClass(job.jobType)} job-card" onclick="window.location.href='job-details.html?id=${job.id}'">
      <div>
        <h3>${escapeHtml(job.title)}</h3>
        <div class="job-recruiter">${escapeHtml(job.postedByName)}</div>
      </div>
      <div class="job-meta">
        <span class="job-meta-item">📍 ${escapeHtml(job.location)}</span>
        <span class="job-meta-item">🕒 ${timeAgo(job.postedDate)}</span>
      </div>
      <div class="job-tags">
        <span class="badge badge-neutral">${formatJobType(job.jobType)}</span>
        ${(job.skillsRequired || "").split(",").slice(0, 3).map(s => s.trim() ? `<span class="tag">${escapeHtml(s.trim())}</span>` : "").join("")}
      </div>
      <div class="job-card-footer">
        <span class="job-salary">${formatSalary(job.salary)}</span>
        <span class="text-small" style="color:var(--brand); font-weight:600;">View details →</span>
      </div>
    </div>
  `).join("");

  renderPagination(page);
}

function renderPagination(page) {
  const el = document.getElementById("pagination");
  if (page.totalPages <= 1) { el.innerHTML = ""; return; }

  let html = `<button ${page.first ? "disabled" : ""} onclick="goToPage(${currentPage - 1})">‹</button>`;
  for (let i = 0; i < page.totalPages; i++) {
    html += `<button class="${i === currentPage ? "active" : ""}" onclick="goToPage(${i})">${i + 1}</button>`;
  }
  html += `<button ${page.last ? "disabled" : ""} onclick="goToPage(${currentPage + 1})">›</button>`;
  el.innerHTML = html;
}

function goToPage(page) {
  currentPage = page;
  loadJobs();
  window.scrollTo({ top: 0, behavior: "smooth" });
}

document.getElementById("searchBtn").addEventListener("click", () => { currentPage = 0; loadJobs(); });
document.getElementById("keywordInput").addEventListener("keypress", (e) => { if (e.key === "Enter") { currentPage = 0; loadJobs(); } });
document.getElementById("locationInput").addEventListener("keypress", (e) => { if (e.key === "Enter") { currentPage = 0; loadJobs(); } });
document.getElementById("jobTypeInput").addEventListener("change", () => { currentPage = 0; loadJobs(); });

loadJobs();