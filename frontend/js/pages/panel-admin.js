const PanelAdmin = (() => {

  let user = null;
  let ciclos = [];
  let filtro = "";

  let editId = null;

  const el = {};
  const q = (id) => document.getElementById(id);

  function toast(title, msg) {
    el.toastTitle.textContent = title;
    el.toastMsg.textContent = msg;
    el.toast.classList.add("show");
    setTimeout(() => el.toast.classList.remove("show"), 3500);
  }

  function setMsg(txt, type = "info") {
    if (!el.ciclosMsg) return;
    el.ciclosMsg.textContent = txt || "";
    el.ciclosMsg.className = "admin-hint text-secondary";
    if (type === "ok") el.ciclosMsg.style.color = "var(--color-success-dark)";
    else if (type === "error") el.ciclosMsg.style.color = "var(--color-error-dark)";
    else el.ciclosMsg.style.color = "var(--text-secondary)";
  }

  function escapeHtml(str) {
    return String(str ?? "")
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function iconForTarjeta(titulo = "") {
    const t = titulo.toLowerCase();
    if (t.includes("usuario")) return { emoji: "👥", color: "blue" };
    if (t.includes("alumno")) return { emoji: "🎓", color: "purple" };
    if (t.includes("emplead") || t.includes("profesor")) return { emoji: "🧑‍🏫", color: "green" };
    if (t.includes("turno")) return { emoji: "⏱️", color: "orange" };
    if (t.includes("incid")) return { emoji: "🚨", color: "red" };
    if (t.includes("docu")) return { emoji: "📄", color: "purple" };
    return { emoji: "📊", color: "blue" };
  }

  function renderStats(tarjetas = []) {
    el.adminStatsGrid.innerHTML = "";

    // Si backend devuelve 6 tarjetas, se verán como grid premium
    tarjetas.forEach((t) => {
      const icon = iconForTarjeta(t.titulo);
      const card = document.createElement("div");
      card.className = "card card-stat no-hover";

      card.innerHTML = `
        <div class="card-stat-header">
          <div class="card-stat-icon-wrapper ${icon.color}">
            <span style="font-size:20px">${icon.emoji}</span>
          </div>
        </div>
        <div class="card-stat-content">
          <div class="card-stat-label">${escapeHtml(t.titulo)}</div>
          <div class="card-stat-value" data-target="${escapeHtml(t.valor)}">0</div>
          <div class="card-stat-label">${escapeHtml(t.descripcion)}</div>
        </div>
      `;

      el.adminStatsGrid.appendChild(card);
    });

    // animación contadores (solo si son números)
    el.adminStatsGrid.querySelectorAll(".card-stat-value").forEach((node) => {
      const targetStr = node.getAttribute("data-target");
      const target = Number(targetStr);

      if (!Number.isFinite(target)) {
        node.textContent = targetStr;
        return;
      }

      let current = 0;
      const step = Math.max(1, Math.floor(target / 30));
      const timer = setInterval(() => {
        current += step;
        if (current >= target) {
          node.textContent = String(target);
          clearInterval(timer);
        } else {
          node.textContent = String(current);
        }
      }, 18);
    });
  }

  function iconForAcceso(a = "") {
    if (a.includes("USUARIOS")) return "👥";
    if (a.includes("TIPOS_TRAMITE")) return "📋";
    if (a.includes("HISTORIAL")) return "🧾";
    if (a.includes("CONFIG")) return "⚙️";
    return "✨";
  }

  function targetForAcceso(a = "") {
    if (a.includes("USUARIOS")) return "#usuarios";
    if (a.includes("TIPOS_TRAMITE")) return "#catalogos";
    if (a.includes("HISTORIAL")) return "#auditoria";
    if (a.includes("CONFIG")) return "#sistema";
    return "#dashboard";
  }

  function renderQuickAccess(accesos = []) {
    el.adminQuickGrid.innerHTML = "";

    accesos.forEach((a) => {
      const card = document.createElement("div");
      card.className = "card card-quick-access";
      card.setAttribute("data-target", targetForAcceso(a.accion || ""));

      card.innerHTML = `
        <div class="card-quick-access-icon" style="background: var(--accent-light); color: var(--accent-start);">
          ${iconForAcceso(a.accion || "")}
        </div>
        <div class="card-quick-access-content">
          <div class="card-quick-access-title">${escapeHtml(a.titulo)}</div>
          <div class="card-quick-access-subtitle">${escapeHtml(a.descripcion)}</div>
        </div>
      `;

      el.adminQuickGrid.appendChild(card);
    });

    el.adminQuickGrid.querySelectorAll(".card-quick-access").forEach((c) => {
      c.addEventListener("click", () => {
        const target = c.getAttribute("data-target");
        if (target) {
          document.querySelector(target)?.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      });
    });
  }

  function badgeEstado(activo) {
    return activo
      ? `<span class="badge badge-success">Activo</span>`
      : `<span class="badge badge-error">Inactivo</span>`;
  }

  function renderCiclos() {
    const f = filtro.trim().toLowerCase();

    const lista = !f ? ciclos : ciclos.filter(c =>
      (c.nombre || "").toLowerCase().includes(f) ||
      (c.codigo || "").toLowerCase().includes(f) ||
      (c.familiaProfesional || "").toLowerCase().includes(f)
    );

    if (!lista.length) {
      el.ciclosTbody.innerHTML = `
        <tr><td colspan="5" class="text-secondary">No hay resultados.</td></tr>
      `;
      return;
    }

    el.ciclosTbody.innerHTML = lista.map(c => `
      <tr>
        <td>${c.id}</td>
        <td><strong>${escapeHtml(c.nombre)}</strong><div class="text-muted" style="font-size:.85rem">${escapeHtml(c.familiaProfesional || "")}</div></td>
        <td>${escapeHtml(c.codigo || "")}</td>
        <td>${badgeEstado(!!c.activo)}</td>
        <td>
          <div class="actions">
            <button class="btn btn-secondary btn-sm" data-action="edit" data-id="${c.id}">✏️ Editar</button>
            ${c.activo
              ? `<button class="btn btn-danger btn-sm" data-action="off" data-id="${c.id}">⛔ Desactivar</button>`
              : `<button class="btn btn-success btn-sm" data-action="on" data-id="${c.id}">✅ Activar</button>`
            }
          </div>
        </td>
      </tr>
    `).join("");
  }

  async function loadDashboard() {
    const dto = await API.getDashboardAdmin();

    renderStats(dto.tarjetas || []);
    renderQuickAccess(dto.accesosRapidos || []);

    const estado = (dto.estadoGeneralSistema || "OK").toUpperCase();
    el.estadoGeneralBadge.textContent = estado;

    // OK -> verde, si no -> warning
    el.estadoGeneralBadge.className = "badge " + (estado === "OK" ? "badge-success" : "badge-warning");
  }

  async function loadCiclos() {
    setMsg("Cargando ciclos...");
    ciclos = await API.get("/ciclos");
    setMsg(`Ciclos cargados: ${ciclos.length}`, "ok");
    renderCiclos();
  }

  async function loadActivity() {
    // Puede devolver muchos: mostramos los últimos 8
    const list = await API.get("/historial");
    const ordenado = (list || [])
      .slice()
      .sort((a, b) => String(b.fechaHora).localeCompare(String(a.fechaHora)));

    const top = ordenado.slice(0, 8);

    if (!top.length) {
      el.adminActivityFeed.innerHTML = `<div class="text-secondary">Sin actividad registrada.</div>`;
      return;
    }

    el.adminActivityFeed.innerHTML = top.map(h => {
      const fecha = (h.fechaHora || "").replace("T", " ").slice(0, 16);
      return `
        <div class="activity-item">
          <div class="activity-dot"></div>
          <div class="activity-main">
            <div class="activity-title">${escapeHtml(h.accion || "Acción")}</div>
            <div class="activity-desc">${escapeHtml(h.descripcion || "")}</div>
            <div class="activity-meta">
              <span>🧩 ${escapeHtml(h.entidadAfectada || "-")}</span>
              <span>🆔 ${h.idEntidad ?? "-"}</span>
              <span>🕒 ${escapeHtml(fecha)}</span>
            </div>
          </div>
        </div>
      `;
    }).join("");
  }

  async function checkSystem() {
    // Backend OK si dashboard cargó
    el.dotBackend.className = "system-dot";
    el.txtBackend.textContent = "Conectado";

    // DB: si /ciclos responde
    try {
      await API.get("/ciclos");
      el.dotDB.className = "system-dot";
      el.txtDB.textContent = "OK";
    } catch {
      el.dotDB.className = "system-dot bad";
      el.txtDB.textContent = "Error";
    }

    // Clima: si /clima/actual responde y climaDisponible=true
    try {
      const clima = await API.get("/clima/actual");
      const ok = !!clima?.climaDisponible;
      el.dotClima.className = ok ? "system-dot" : "system-dot warn";
      el.txtClima.textContent = ok ? "Disponible" : "No disponible";
    } catch {
      el.dotClima.className = "system-dot warn";
      el.txtClima.textContent = "No disponible";
    }
  }

  function openModal(ciclo) {
    editId = ciclo.id;

    el.editNombre.value = ciclo.nombre || "";
    el.editCodigo.value = ciclo.codigo || "";
    el.editFamilia.value = ciclo.familiaProfesional || "";
    el.editGrado.value = ciclo.grado || "";

    el.modalOverlay.classList.remove("hidden");
    el.modalCiclo.classList.remove("hidden");
  }

  function closeModal() {
    editId = null;
    el.modalOverlay.classList.add("hidden");
    el.modalCiclo.classList.add("hidden");
  }

  async function crearCiclo(e) {
    e.preventDefault();

    const payload = {
      nombre: el.cicloNombre.value.trim(),
      codigo: el.cicloCodigo.value.trim() || null,
      familiaProfesional: el.cicloFamilia.value.trim() || null,
      grado: el.cicloGrado.value.trim() || null,
      activo: true
    };

    if (!payload.nombre) {
      toast("Falta nombre", "El nombre es obligatorio.");
      return;
    }

    await API.post("/ciclos", payload);
    toast("Ciclo creado ✅", payload.nombre);

    el.formCiclo.reset();
    await loadCiclos();
  }

  async function guardarEdicion(e) {
    e.preventDefault();
    if (!editId) return;

    const ciclo = ciclos.find(c => c.id === editId);
    if (!ciclo) return;

    const payload = {
      ...ciclo,
      nombre: el.editNombre.value.trim(),
      codigo: el.editCodigo.value.trim() || null,
      familiaProfesional: el.editFamilia.value.trim() || null,
      grado: el.editGrado.value.trim() || null
    };

    if (!payload.nombre) {
      toast("Falta nombre", "El nombre es obligatorio.");
      return;
    }

    await API.put(`/ciclos/${editId}`, payload);
    toast("Ciclo actualizado ✨", `ID ${editId}`);

    closeModal();
    await loadCiclos();
  }

  async function desactivar(id) {
    if (!confirm("¿Desactivar este ciclo?")) return;
    await API.request(`/ciclos/${id}/desactivar`, { method: "PATCH" });
    toast("Ciclo desactivado", `ID ${id}`);
    await loadCiclos();
  }

  async function activar(id) {
    const ciclo = ciclos.find(c => c.id === id);
    if (!ciclo) return;

    const payload = { ...ciclo, activo: true };
    await API.put(`/ciclos/${id}`, payload);
    toast("Ciclo activado", `ID ${id}`);
    await loadCiclos();
  }

  function bind() {
    // Buscar desde el input del header
    el.searchInput.addEventListener("input", () => {
      filtro = el.searchInput.value;
      renderCiclos();
    });

    el.formCiclo.addEventListener("submit", (e) => {
      crearCiclo(e).catch(err => toast("Error", err.message));
    });

    el.ciclosTbody.addEventListener("click", (e) => {
      const btn = e.target.closest("button[data-action]");
      if (!btn) return;

      const id = Number(btn.dataset.id);
      const action = btn.dataset.action;

      (async () => {
        if (action === "edit") {
          const ciclo = ciclos.find(c => c.id === id);
          if (ciclo) openModal(ciclo);
        }
        if (action === "off") return desactivar(id);
        if (action === "on") return activar(id);
      })().catch(err => toast("Error", err.message));
    });

    el.btnCerrarModal.addEventListener("click", closeModal);
    el.modalOverlay.addEventListener("click", closeModal);
    el.formEditarCiclo.addEventListener("submit", (e) => {
      guardarEdicion(e).catch(err => toast("Error", err.message));
    });

    el.btnRefreshAdmin.addEventListener("click", () => {
      initData().catch(err => toast("Error", err.message));
    });
  }

  async function initData() {
    await loadDashboard();
    await loadCiclos();
    await loadActivity();
    await checkSystem();
  }

  function cache() {
    el.searchInput = q("searchInput");

    el.adminStatsGrid = q("adminStatsGrid");
    el.adminQuickGrid = q("adminQuickGrid");

    el.btnRefreshAdmin = q("btnRefreshAdmin");

    el.formCiclo = q("formCiclo");
    el.cicloNombre = q("cicloNombre");
    el.cicloCodigo = q("cicloCodigo");
    el.cicloFamilia = q("cicloFamilia");
    el.cicloGrado = q("cicloGrado");
    el.ciclosTbody = q("ciclosTbody");
    el.ciclosMsg = q("ciclosMsg");

    el.adminActivityFeed = q("adminActivityFeed");

    el.estadoGeneralBadge = q("estadoGeneralBadge");
    el.dotBackend = q("dotBackend");
    el.txtBackend = q("txtBackend");
    el.dotDB = q("dotDB");
    el.txtDB = q("txtDB");
    el.dotClima = q("dotClima");
    el.txtClima = q("txtClima");

    el.toast = q("toast");
    el.toastTitle = q("toastTitle");
    el.toastMsg = q("toastMsg");

    el.modalOverlay = q("modalOverlay");
    el.modalCiclo = q("modalCiclo");
    el.btnCerrarModal = q("btnCerrarModal");
    el.formEditarCiclo = q("formEditarCiclo");
    el.editNombre = q("editNombre");
    el.editCodigo = q("editCodigo");
    el.editFamilia = q("editFamilia");
    el.editGrado = q("editGrado");
  }

  function init() {
    if (!Auth.requireAuth()) return;

    user = Auth.getCurrentUser();
    if (!user || user.rol !== "ADMIN") {
      alert("Acceso denegado: solo ADMIN.");
      window.location.href = CONFIG.ROUTES.LOGIN;
      return;
    }

    cache();
    bind();

    initData().catch(err => toast("Error", err.message));
  }

  return { init };
})();

window.PanelAdmin = PanelAdmin;