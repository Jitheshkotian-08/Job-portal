requireRole("RECRUITER");
renderNavbar("myjobs");

const jobId = new URLSearchParams(window.location.search).get("jobId");
if (!jobId) window.location.href = "recruiter-jobs.html";

const STATUS_OPTIONS = ["APPLIED", "SHORTLISTED", "SELECTED", "REJECTED"];

async function loadApplicants() {
  const listEl = document.getElementById("applicantList");
  listEl.innerHTML = Array(3).fill('<div class="skeleton" style="height:80px; margin-bottom:12px;"></div>').join("");

  try {
    const [job, applicants] = await Promise.all([
      apiFetch("/jobs/" + jobId),
      apiFetch(`/recruiter/jobs/${jobId}/applications`),
    ]);

    document.getElementById("jobTitle").textContent = job.title;
    applicants.sort((a, b) => new Date(b.appliedDate) - new Date(a.appliedDate));
    renderApplicants(applicants);
  } catch (err) {
    listEl.innerHTML = `<div class="empty-state"><h3>Something went wrong</h3><p>${escapeHtml(err.message)}</p></div>`;
  }
}

function renderApplicants(applicants) {
  const listEl = document.getElementById("applicantList");

  if (applicants.length === 0) {
    listEl.innerHTML = "";
    document.getElementById("emptyState").style.display = "block";
    return;
  }

  listEl.innerHTML = applicants.map(app => `
    <div class="card applicant-row" id="app-${app.id}">
      <div class="applicant-info">
        <h3>${escapeHtml(app.candidateName)}</h3>
        <div class="applicant-email">${escapeHtml(app.candidateEmail)} · Applied ${timeAgo(app.appliedDate)}</div>
      </div>
      <div class="applicant-actions">
        <a href="${SERVER_BASE_URL}${app.resumePath}" target="_blank" class="btn btn-outline btn-sm">View resume</a>
        <select class="status-select" id="status-${app.id}">
          ${STATUS_OPTIONS.map(s => `<option value="${s}" ${s === app.status ? "selected" : ""}>${s}</option>`).join("")}
        </select>
        <button class="btn btn-primary btn-sm" onclick="updateStatus(${app.id})">Update</button>
      </div>
    </div>
  `).join("");
}

async function updateStatus(appId) {
  const select = document.getElementById("status-" + appId);
  const newStatus = select.value;
  const btn = select.nextElementSibling;

  const originalText = btn.textContent;
  btn.disabled = true;
  btn.textContent = "Saving...";

  try {
    await apiFetch(`/recruiter/applications/${appId}/status`, {
      method: "PUT",
      body: JSON.stringify({ status: newStatus }),
    });
    btn.textContent = "✓ Updated";
    setTimeout(() => { btn.textContent = originalText; btn.disabled = false; }, 1500);
  } catch (err) {
    alert("Couldn't update status: " + err.message);
    btn.disabled = false;
    btn.textContent = originalText;
  }
}

loadApplicants();