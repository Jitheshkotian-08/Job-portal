requireRole("RECRUITER");
renderNavbar("myjobs");

async function loadJobs() {
  const listEl = document.getElementById("jobList");
  listEl.innerHTML = Array(3).fill('<div class="skeleton" style="height:80px; margin-bottom:12px;"></div>').join("");

  try {
    const jobs = await apiFetch("/recruiter/jobs");
    jobs.sort((a, b) => new Date(b.postedDate) - new Date(a.postedDate));
    renderJobs(jobs);
  } catch (err) {
    listEl.innerHTML = `<div class="empty-state"><h3>Something went wrong</h3><p>${escapeHtml(err.message)}</p></div>`;
  }
}

function renderJobs(jobs) {
  const listEl = document.getElementById("jobList");

  if (jobs.length === 0) {
    listEl.innerHTML = "";
    document.getElementById("emptyState").style.display = "block";
    return;
  }

  listEl.innerHTML = jobs.map(job => `
    <div class="card job-row" id="job-${job.id}">
      <div class="job-row-info">
        <h3>${escapeHtml(job.title)}</h3>
        <div class="job-row-meta">
          <span class="badge ${job.status === 'ACTIVE' ? 'badge-accent' : 'badge-neutral'}">${job.status}</span>
          <span>📍 ${escapeHtml(job.location)}</span>
          <span>·</span>
          <span>${formatJobType(job.jobType)}</span>
          <span>·</span>
          <span>Posted ${timeAgo(job.postedDate)}</span>
        </div>
      </div>
      <div class="job-row-actions">
        <a href="job-applicants.html?jobId=${job.id}" class="btn btn-outline btn-sm">View applicants</a>
        <a href="post-job.html?id=${job.id}" class="btn btn-ghost btn-sm">Edit</a>
        <button class="btn btn-danger btn-sm" onclick="deleteJob(${job.id})">Delete</button>
      </div>
    </div>
  `).join("");
}

async function deleteJob(jobId) {
  if (!confirm("Delete this job posting? This can't be undone.")) return;

  try {
    await apiFetch("/recruiter/jobs/" + jobId, { method: "DELETE" });
    document.getElementById("job-" + jobId).remove();
  } catch (err) {
    alert("Couldn't delete this job: " + err.message);
  }
}

loadJobs();