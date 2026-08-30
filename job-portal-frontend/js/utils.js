function escapeHtml(str) {
  if (str === null || str === undefined) return "";
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

function formatJobType(type) {
  const map = { FULL_TIME: "Full-time", PART_TIME: "Part-time", INTERNSHIP: "Internship", CONTRACT: "Contract" };
  return map[type] || type;
}

function formatDate(dateStr) {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
}

function timeAgo(dateStr) {
  const diffDays = Math.floor((Date.now() - new Date(dateStr).getTime()) / (1000 * 60 * 60 * 24));
  if (diffDays <= 0) return "Today";
  if (diffDays === 1) return "Yesterday";
  if (diffDays < 7) return diffDays + " days ago";
  if (diffDays < 30) return Math.floor(diffDays / 7) + "w ago";
  return formatDate(dateStr);
}

function formatSalary(amount) {
  if (!amount) return "Not disclosed";
  return "₹" + Number(amount).toLocaleString("en-IN");
}

function statusBadgeClass(status) {
  const map = { APPLIED: "badge-brand", SHORTLISTED: "badge-accent", SELECTED: "badge-accent", REJECTED: "badge-rose" };
  return map[status] || "badge-neutral";
}

function spineClass(jobType) {
  const map = { PART_TIME: "spine-accent", INTERNSHIP: "spine-amber", CONTRACT: "spine-rose" };
  return map[jobType] || "";
}