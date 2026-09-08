function renderNavbar(activeKey) {
  const user = getUser();
  if (!user) return;

  const isRecruiter = user.role === "RECRUITER";

  const links = isRecruiter
    ? [
        { key: "dashboard", href: "recruiter-dashboard.html", label: "Dashboard" },
        { key: "myjobs", href: "recruiter-jobs.html", label: "My Jobs" },
        { key: "postjob", href: "post-job.html", label: "Post a Job" },
        { key: "profile", href: "profile.html", label: "Profile" },
      ]
    : [
        { key: "dashboard", href: "candidate-dashboard.html", label: "Dashboard" },
        { key: "jobs", href: "jobs.html", label: "Find Jobs" },
        { key: "applications", href: "my-applications.html", label: "My Applications" },
        { key: "profile", href: "profile.html", label: "Profile" },
      ];

  const linksHtml = links.map(link =>
    `<a href="${link.href}" class="navbar-link ${activeKey === link.key ? "active" : ""}">${link.label}</a>`
  ).join("");

  const initials = user.name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();

  document.getElementById("navbar").innerHTML = `
    <div class="container">
      <a href="${isRecruiter ? "recruiter-dashboard.html" : "candidate-dashboard.html"}" class="navbar-brand">Sourced.</a>
      <button class="navbar-toggle" id="navToggle" aria-label="Toggle menu">☰</button>
      <div class="navbar-links" id="navLinks">
        ${linksHtml}
        <div class="navbar-user">
          <span class="badge badge-neutral">${initials}</span>
          <span class="text-small text-muted">${escapeHtml(user.name)}</span>
          <button class="btn btn-ghost btn-sm" onclick="logout()">Log out</button>
        </div>
      </div>
    </div>
  `;

  document.getElementById("navToggle").addEventListener("click", () => {
    document.getElementById("navLinks").classList.toggle("open");
  });
}