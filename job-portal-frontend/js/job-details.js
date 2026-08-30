requireRole("CANDIDATE");
renderNavbar("jobs");

const jobId = new URLSearchParams(window.location.search).get("id");
if (!jobId) window.location.href = "jobs.html";

let alreadyApplied = false;

async function loadJob() {
  try {
    const [job, myApplications] = await Promise.all([
      apiFetch("/jobs/" + jobId),
      apiFetch("/candidate/applications"),
    ]);

    alreadyApplied = myApplications.some(app => String(app.jobId) === String(jobId));
    renderJob(job);
  } catch (err) {
    document.getElementById("loadingState").textContent = "Couldn't load this job: " + err.message;
  }
}

function renderJob(job) {
  document.getElementById("loadingState").style.display = "none";
  document.getElementById("jobContent").style.display = "grid";

  document.getElementById("jobTypeBadge").textContent = formatJobType(job.jobType);
  document.getElementById("jobTitle").textContent = job.title;
  document.getElementById("jobLocation").textContent = "📍 " + job.location;
  document.getElementById("jobPosted").textContent = "🕒 Posted " + timeAgo(job.postedDate);
  document.getElementById("jobDescription").textContent = job.description;
  document.getElementById("jobRecruiter").textContent = job.postedByName;
  document.getElementById("jobSalary").textContent = formatSalary(job.salary);
  document.getElementById("jobDeadline").textContent = formatDate(job.applicationDeadline);

  const tags = (job.skillsRequired || "").split(",").filter(s => s.trim());
  document.getElementById("jobTags").innerHTML = tags.map(s => `<span class="tag">${escapeHtml(s.trim())}</span>`).join("");

  const applyForm = document.getElementById("applyForm");
  const alreadyAppliedState = document.getElementById("alreadyAppliedState");
  const applyBtn = document.getElementById("applyBtn");

  if (alreadyApplied) {
    applyForm.style.display = "none";
    alreadyAppliedState.style.display = "block";
  } else if (job.status !== "ACTIVE") {
    applyForm.style.display = "none";
    showAlert("applyAlert", "This job is no longer accepting applications.");
  } else {
    applyBtn.onclick = () => submitApplication(job.id);
  }
}

async function submitApplication(jobId) {
  hideAlert("applyAlert");
  const fileInput = document.getElementById("resumeInput");

  if (!fileInput.files.length) {
    showAlert("applyAlert", "Please select a resume file to upload.");
    return;
  }

  const applyBtn = document.getElementById("applyBtn");
  applyBtn.disabled = true;
  applyBtn.innerHTML = '<span class="spinner"></span> Submitting...';

  const formData = new FormData();
  formData.append("resume", fileInput.files[0]);

  try {
    await apiFetch("/candidate/applications/" + jobId, { method: "POST", body: formData });
    document.getElementById("applyForm").style.display = "none";
    document.getElementById("alreadyAppliedState").style.display = "block";
  } catch (err) {
    showAlert("applyAlert", err.message);
    applyBtn.disabled = false;
    applyBtn.textContent = "Apply now";
  }
}

loadJob();