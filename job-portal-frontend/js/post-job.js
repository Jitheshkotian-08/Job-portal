requireRole("RECRUITER");
renderNavbar("postjob");

const jobId = new URLSearchParams(window.location.search).get("id");
const isEditMode = !!jobId;

// Prevent picking a deadline in the past
document.getElementById("deadlineInput").min = new Date().toISOString().split("T")[0];

if (isEditMode) {
  document.getElementById("pageTitle").textContent = "Edit job";
  document.getElementById("submitBtn").textContent = "Save changes";
  loadExistingJob();
}

async function loadExistingJob() {
  try {
    const job = await apiFetch("/jobs/" + jobId);
    document.getElementById("titleInput").value = job.title;
    document.getElementById("descriptionInput").value = job.description;
    document.getElementById("skillsInput").value = job.skillsRequired || "";
    document.getElementById("locationInput").value = job.location;
    document.getElementById("jobTypeInput").value = job.jobType;
    document.getElementById("salaryInput").value = job.salary || "";
    document.getElementById("deadlineInput").value = job.applicationDeadline;
  } catch (err) {
    showAlert("formAlert", "Couldn't load this job: " + err.message);
  }
}

document.getElementById("jobForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  hideAlert("formAlert");

  const btn = document.getElementById("submitBtn");
  btn.disabled = true;
  btn.innerHTML = '<span class="spinner"></span> Saving...';

  const payload = {
    title: document.getElementById("titleInput").value.trim(),
    description: document.getElementById("descriptionInput").value.trim(),
    skillsRequired: document.getElementById("skillsInput").value.trim(),
    location: document.getElementById("locationInput").value.trim(),
    salary: document.getElementById("salaryInput").value ? Number(document.getElementById("salaryInput").value) : null,
    jobType: document.getElementById("jobTypeInput").value,
    applicationDeadline: document.getElementById("deadlineInput").value,
  };

  try {
    if (isEditMode) {
      await apiFetch("/recruiter/jobs/" + jobId, { method: "PUT", body: JSON.stringify(payload) });
    } else {
      await apiFetch("/recruiter/jobs", { method: "POST", body: JSON.stringify(payload) });
    }
    window.location.href = "recruiter-jobs.html";
  } catch (err) {
    showAlert("formAlert", err.message);
    btn.disabled = false;
    btn.textContent = isEditMode ? "Save changes" : "Post job";
  }
});