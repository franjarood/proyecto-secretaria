const PanelAdmin = (() => {

  let user = null;

  // ===== CICLOS =====
  let ciclos = [];
  let filtro = "";
  let editId = null;

  // ===== TRÁMITES =====
  let tramites = [];
  let tramiteEditId = null;
  let tramiteFiltro = "";

  let chartActividadLinea = null;
  let chartActividadTop = null;
  let chartUsuarios7d = null;

  let historialCache = [];
  let acciones7dCache = 0;

  // ===== USUARIOS =====
  let usuarios = [];
  let usuariosFiltro = "";
  let usuariosRolFiltro = "";
  let usuariosPage = 1;
  const usuariosPageSize = 10;

  const ROLES = ["USUARIO", "ALUMNO", "SECRETARIA", "CONSERJE", "ADMIN", "PROFESOR"];

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

  function setUsuariosMsg(txt, type = "info") {
    if (!el.usuariosMsg) return;
    el.usuariosMsg.textContent = txt || "";
    el.usuariosMsg.className = "admin-hint text-secondary";
    if (type === "ok") el.usuariosMsg.style.color = "var(--color-success-dark)";
    else if (type === "error") el.usuariosMsg.style.color = "var(--color-error-dark)";
    else el.usuariosMsg.style.color = "var(--text-secondary)";
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

  // =========================
  // USUARIOS
  // =========================

  function formatFecha(value) {
    if (!value) return "-";
    try {
      const d = new Date(value);
      if (isNaN(d.getTime())) return String(value);
      return d.toLocaleString("es-ES", {
        year: "numeric", month: "2-digit", day: "2-digit",
        hour: "2-digit", minute: "2-digit"
      });
    } catch {
      return String(value);
    }
  }

  function usuariosAplicarFiltros() {
    const qText = (usuariosFiltro || "").trim().toLowerCase();
    const rol = (usuariosRolFiltro || "").trim().toUpperCase();

    let list = [...usuarios];

    if (rol) list = list.filter(u => String(u.rol || "").toUpperCase() === rol);

    if (qText) {
      list = list.filter(u => {
        const hay = [
          u.id, u.nombre, u.apellidos, u.email, u.dni, u.rol
        ].map(v => (v ?? "").toString().toLowerCase()).join(" ");
        return hay.includes(qText);
      });
    }

    return list;
  }

  function renderUsuarios() {
    if (!el.usuariosTbody) return;

    const list = usuariosAplicarFiltros();
    const total = list.length;

    const totalPages = Math.max(1, Math.ceil(total / usuariosPageSize));
    if (usuariosPage > totalPages) usuariosPage = totalPages;
    if (usuariosPage < 1) usuariosPage = 1;

    const start = (usuariosPage - 1) * usuariosPageSize;
    const pageItems = list.slice(start, start + usuariosPageSize);

    if (!pageItems.length) {
      el.usuariosTbody.innerHTML = `<tr><td colspan="7" class="text-secondary">No hay usuarios para mostrar.</td></tr>`;
    } else {
      el.usuariosTbody.innerHTML = pageItems.map(u => {
        const rolActual = String(u.rol || "").toUpperCase();
        const creado = formatFecha(u.creadoEn);
        const nombreFull = `${u.nombre || ""} ${u.apellidos || ""}`.trim();

        const options = ROLES.map(r => `
          <option value="${r}" ${r === rolActual ? "selected" : ""}>${r}</option>
        `).join("");

        return `
          <tr data-user-id="${u.id}">
            <td>${u.id}</td>
            <td><strong>${escapeHtml(nombreFull || "-")}</strong></td>
            <td>${escapeHtml(u.email || "-")}</td>
            <td>${escapeHtml(u.dni || "-")}</td>
            <td>
              <select class="admin-input usuarios-role" data-user-id="${u.id}" data-original="${rolActual}">
                ${options}
              </select>
            </td>
            <td>${escapeHtml(creado)}</td>
            <td>
              <button class="btn btn-primary btn-sm btn-aplicar-rol" data-user-id="${u.id}" disabled>Aplicar</button>
            </td>
          </tr>
        `;
      }).join("");
    }

    if (el.usuariosPagerMeta) {
      const from = total ? (start + 1) : 0;
      const to = Math.min(total, start + usuariosPageSize);
      el.usuariosPagerMeta.innerHTML = `Mostrando <strong>${from}</strong>–<strong>${to}</strong> de <strong>${total}</strong>`;
    }

    if (el.btnUsuariosPrev) el.btnUsuariosPrev.disabled = usuariosPage <= 1;
    if (el.btnUsuariosNext) el.btnUsuariosNext.disabled = usuariosPage >= totalPages;
  }

  async function loadUsuarios() {
    setUsuariosMsg("Cargando usuarios...");
    try {
      usuarios = await API.get("/usuarios");
      if (!Array.isArray(usuarios)) usuarios = [];
      setUsuariosMsg(`Usuarios cargados: ${usuarios.length}`, "ok");
      usuariosPage = 1;
      renderUsuarios();
      renderRolesWidget();
      const altas7d = renderChartUsuarios7d();
      renderMiniKpisActividad(acciones7dCache, altas7d, usuarios.length);
    } catch (err) {
      setUsuariosMsg("Error cargando usuarios.", "error");
      throw err;
    }
  }

  async function cambiarRolUsuario(userId, nuevoRol) {
    const endpoint = `/usuarios/${userId}/cambiar-rol`;
    const payload = { rol: nuevoRol };

    return API.request(endpoint, {
      method: "PATCH",
      body: JSON.stringify(payload)
    });
  }

  function colorForRol(rol) {
    // Cohérente, sin inventar colores raros:
    // Reutilizamos 6 tonos suaves (CSS inline) solo en dots/segments.
    // Si prefieres, luego lo pasamos a variables.
    const map = {
      ADMIN: "#7c3aed",
      SECRETARIA: "#2563eb",
      CONSERJE: "#16a34a",
      PROFESOR: "#0ea5e9",
      ALUMNO: "#f59e0b",
      USUARIO: "#64748b"
    };
    return map[String(rol || "").toUpperCase()] || "#64748b";
  }

  function renderRolesWidget() {
    if (!el.rolesBar || !el.rolesLegend) return;

    const counts = {};
    (usuarios || []).forEach(u => {
      const r = String(u.rol || "DESCONOCIDO").toUpperCase();
      counts[r] = (counts[r] || 0) + 1;
    });

    const entries = Object.entries(counts).sort((a,b) => b[1]-a[1]);
    const total = entries.reduce((acc, [,v]) => acc + v, 0) || 1;

    // Barra apilada
    el.rolesBar.innerHTML = entries.map(([rol, n]) => {
      const pct = (n / total) * 100;
      return `<div class="roles-seg" style="width:${pct}%;background:${colorForRol(rol)}"></div>`;
    }).join("");

    // Leyenda
    el.rolesLegend.innerHTML = entries.map(([rol, n]) => {
      const pct = Math.round((n / total) * 100);
      return `
        <div class="roles-item">
          <span class="roles-dot" style="background:${colorForRol(rol)}"></span>
          <span><strong>${escapeHtml(rol)}</strong> · ${n} (${pct}%)</span>
        </div>
      `;
    }).join("");
  }

  // =========================
  // LOADERS EXISTENTES
  // =========================

  async function loadDashboard() {
    const dto = await API.getDashboardAdmin();

    renderStats(dto.tarjetas || []);
    renderQuickAccess(dto.accesosRapidos || []);

    const estado = (dto.estadoGeneralSistema || "OK").toUpperCase();
    el.estadoGeneralBadge.textContent = estado;

    el.estadoGeneralBadge.className = "badge " + (estado === "OK" ? "badge-success" : "badge-warning");

    renderQuickMini();
  }

  async function loadCiclos() {
    setMsg("Cargando ciclos...");
    ciclos = await API.get("/ciclos");
    setMsg(`Ciclos cargados: ${ciclos.length}`, "ok");
    renderCiclos();
  }

  async function loadActivity() {
    const list = await API.get("/historial");
    const ordenado = (list || [])
      .slice()
      .sort((a, b) => String(b.fechaHora).localeCompare(String(a.fechaHora)));

    const top = ordenado.slice(0, 8);

    historialCache = ordenado.slice(0, 200);
    acciones7dCache = renderChartActividadLinea() || 0;
    renderChartActividadTop();

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
    el.dotBackend.className = "system-dot";
    el.txtBackend.textContent = "Conectado";

    try {
      await API.get("/ciclos");
      el.dotDB.className = "system-dot";
      el.txtDB.textContent = "OK";
    } catch {
      el.dotDB.className = "system-dot bad";
      el.txtDB.textContent = "Error";
    }

    try {
      const clima = await API.get("/clima/actual");
      const ok = !!clima?.climaDisponible;
      el.dotClima.className = ok ? "system-dot" : "system-dot warn";
      el.txtClima.textContent = ok ? "Disponible" : "No disponible";
    } catch {
      el.dotClima.className = "system-dot warn";
      el.txtClima.textContent = "No disponible";
    }

    renderEstadoSistemaMini();
  }

  // =========================
  // MODAL CICLOS
  // =========================

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
    await checkSystem();
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
    await checkSystem();
  }

  async function desactivar(id) {
    if (!confirm("¿Desactivar este ciclo?")) return;
    await API.request(`/ciclos/${id}/desactivar`, { method: "PATCH" });
    toast("Ciclo desactivado", `ID ${id}`);
    await loadCiclos();
    await checkSystem();
  }

  async function activar(id) {
    const ciclo = ciclos.find(c => c.id === id);
    if (!ciclo) return;

    const payload = { ...ciclo, activo: true };
    await API.put(`/ciclos/${id}`, payload);
    toast("Ciclo activado", `ID ${id}`);
    await loadCiclos();
    await checkSystem();
  }

  function bind() {
    // Buscar desde el input del header:
    // - filtra ciclos
    // - y también usuarios (mismo texto)
    el.searchInput.addEventListener("input", () => {
      filtro = el.searchInput.value;
      usuariosFiltro = el.searchInput.value;
      tramiteFiltro = el.searchInput.value;
      renderTramites();
      renderCiclos();
      usuariosPage = 1;
      renderUsuarios();
    });

    // CICLOS
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

    // USUARIOS: buscador + filtro rol + recargar
    if (el.usuariosSearch) {
      el.usuariosSearch.addEventListener("input", () => {
        usuariosFiltro = el.usuariosSearch.value;
        usuariosPage = 1;
        renderUsuarios();
      });
    }

    if (el.usuariosRolFilter) {
      el.usuariosRolFilter.addEventListener("change", () => {
        usuariosRolFiltro = el.usuariosRolFilter.value;
        usuariosPage = 1;
        renderUsuarios();
      });
    }

    if (el.btnRefreshUsuarios) {
      el.btnRefreshUsuarios.addEventListener("click", () => {
        loadUsuarios().catch(err => toast("Error", err.message));
      });
    }

    // Paginación
    if (el.btnUsuariosPrev) {
      el.btnUsuariosPrev.addEventListener("click", () => {
        usuariosPage = Math.max(1, usuariosPage - 1);
        renderUsuarios();
      });
    }

    if (el.btnUsuariosNext) {
      el.btnUsuariosNext.addEventListener("click", () => {
        usuariosPage = usuariosPage + 1;
        renderUsuarios();
      });
    }

    // Delegación de eventos para select/botón aplicar (tabla usuarios)
    if (el.usuariosTbody) {
      el.usuariosTbody.addEventListener("change", (e) => {
        const sel = e.target.closest("select.usuarios-role");
        if (!sel) return;

        const row = sel.closest("tr");
        const btn = row?.querySelector(".btn-aplicar-rol");
        if (!btn) return;

        const original = String(sel.getAttribute("data-original") || "").toUpperCase();
        const current = String(sel.value || "").toUpperCase();

        btn.disabled = (original === current);

        if (original !== current) row.classList.add("usuarios-row-changed");
        else row.classList.remove("usuarios-row-changed");
      });

      el.usuariosTbody.addEventListener("click", (e) => {
        const btn = e.target.closest("button.btn-aplicar-rol");
        if (!btn) return;

        const userId = btn.getAttribute("data-user-id");
        const row = btn.closest("tr");
        const sel = row?.querySelector("select.usuarios-role");
        if (!sel) return;

        const original = String(sel.getAttribute("data-original") || "").toUpperCase();
        const nuevoRol = String(sel.value || "").toUpperCase();

        if (nuevoRol === original) return;

        (async () => {
          // Confirmación solo si subes a ADMIN (para evitar liadas)
          if (nuevoRol === "ADMIN") {
            const ok = confirm("Vas a asignar rol ADMIN. ¿Seguro?");
            if (!ok) {
              sel.value = original;
              btn.disabled = true;
              row.classList.remove("usuarios-row-changed");
              return;
            }
          }

          btn.disabled = true;
          const oldText = btn.textContent;
          btn.textContent = "Aplicando...";

          try {
            const updated = await cambiarRolUsuario(userId, nuevoRol);

            // Actualizamos en memoria
            const idx = usuarios.findIndex(u => String(u.id) === String(userId));
            if (idx >= 0) usuarios[idx] = updated;

            // Marcamos original como el nuevo rol
            const rolFinal = String(updated?.rol || nuevoRol).toUpperCase();
            sel.setAttribute("data-original", rolFinal);
            sel.value = rolFinal;

            row.classList.remove("usuarios-row-changed");
            toast("Rol actualizado ✅", `Usuario #${userId} → ${rolFinal}`);

            // Re-render para que filtros/paginación se mantengan coherentes
            renderUsuarios();

          } catch (err) {
            toast("Error", err.message || "No se pudo cambiar el rol");
            // Revertimos visualmente si falla
            sel.value = original;
            btn.disabled = true;
            row.classList.remove("usuarios-row-changed");
          } finally {
            btn.textContent = oldText;
          }
        })().catch(err => toast("Error", err.message));
      });
    }

    // TRÁMITES (blindado)
    if (el.formTramite) {
      el.formTramite.addEventListener("submit", (e) => {
        guardarTramite(e).catch(err => toast("Error", err.message));
      });
    }

    el.btnCancelarEdicionTramite?.addEventListener("click", resetTramiteForm);

    el.btnRefreshTramites?.addEventListener("click", () => {
      loadTramites().catch(err => toast("Error", err.message));
    });

    // clicks tabla trámites
    if (el.tramitesTbody) {
      el.tramitesTbody.addEventListener("click", (e) => {
        const btn = e.target.closest("button[data-action]");
        if (!btn) return;

        const id = Number(btn.dataset.id);
        const action = btn.dataset.action;

        (async () => {
          if (action === "edit-tramite") {
            const t = tramites.find(x => x.id === id);
            if (!t) return;

            tramiteEditId = id;
            el.tramiteNombre.value = t.nombre || "";
            el.tramiteDescripcion.value = t.descripcion || "";
            el.tramiteDuracion.value = t.duracionEstimada ?? 10;
            el.tramiteRequiereDoc.value = String(!!t.requiereDocumentacion);
            el.tramiteDestacado.value = String(!!t.destacado);
            el.tramiteVisible.value = String(!!t.visiblePublicamente);

            el.btnGuardarTramite.textContent = "💾 Guardar";
            el.btnCancelarEdicionTramite.style.display = "inline-flex";
            toast("Edición", `Editando trámite ID ${id}`);
          }

          if (action === "del-tramite") {
            return borrarTramite(id);
          }
        })().catch(err => toast("Error", err.message));
      });
    }
  }

  function boolBadge(value, yes="Sí", no="No") {
    return value ? `<span class="badge badge-success">${yes}</span>` : `<span class="badge badge-error">${no}</span>`;
  }

  function setTramitesMsg(txt, type = "info") {
    if (!el.tramitesMsg) return;
    el.tramitesMsg.textContent = txt || "";
    el.tramitesMsg.className = "admin-hint text-secondary";
    if (type === "ok") el.tramitesMsg.style.color = "var(--color-success-dark)";
    else if (type === "error") el.tramitesMsg.style.color = "var(--color-error-dark)";
    else el.tramitesMsg.style.color = "var(--text-secondary)";
  }

  function renderTramites() {
    const f = (tramiteFiltro || "").trim().toLowerCase();

    const lista = !f ? tramites : tramites.filter(t =>
      (t.nombre || "").toLowerCase().includes(f) ||
      (t.descripcion || "").toLowerCase().includes(f)
    );

    if (!lista.length) {
      el.tramitesTbody.innerHTML = `<tr><td colspan="8" class="text-secondary">No hay trámites.</td></tr>`;
      return;
    }

    el.tramitesTbody.innerHTML = lista.map(t => `
      <tr>
        <td>${t.id}</td>
        <td><strong>${escapeHtml(t.nombre)}</strong></td>
        <td>${escapeHtml(t.descripcion || "")}</td>
        <td>${escapeHtml(String(t.duracionEstimada ?? "-"))} min</td>
        <td>${boolBadge(!!t.requiereDocumentacion, "Sí", "No")}</td>
        <td>${boolBadge(!!t.destacado, "Sí", "No")}</td>
        <td>${boolBadge(!!t.visiblePublicamente, "Sí", "No")}</td>
        <td>
          <div class="actions">
            <button class="btn btn-secondary btn-sm" data-action="edit-tramite" data-id="${t.id}">✏️ Editar</button>
            <button class="btn btn-danger btn-sm" data-action="del-tramite" data-id="${t.id}">🗑️ Borrar</button>
          </div>
        </td>
      </tr>
    `).join("");
  }

  async function loadTramites() {
    setTramitesMsg("Cargando trámites...");
    tramites = await API.get("/tipos-tramite");
    if (!Array.isArray(tramites)) tramites = [];
    setTramitesMsg(`Trámites cargados: ${tramites.length}`, "ok");
    renderTramites();
  }

  function resetTramiteForm() {
    tramiteEditId = null;
    el.formTramite.reset();
    el.btnGuardarTramite.textContent = "➕ Crear";
    el.btnCancelarEdicionTramite.style.display = "none";
  }

  async function guardarTramite(e) {
    e.preventDefault();

    const payload = {
      nombre: el.tramiteNombre.value.trim(),
      descripcion: el.tramiteDescripcion.value.trim(),
      duracionEstimada: Number(el.tramiteDuracion.value),
      requiereDocumentacion: el.tramiteRequiereDoc.value === "true",
      destacado: el.tramiteDestacado.value === "true",
      visiblePublicamente: el.tramiteVisible.value === "true"
    };

    if (!payload.nombre || !payload.descripcion) {
      toast("Faltan datos", "Nombre y descripción son obligatorios.");
      return;
    }
    if (!Number.isFinite(payload.duracionEstimada) || payload.duracionEstimada <= 0) {
      toast("Duración inválida", "Pon una duración > 0.");
      return;
    }

    if (tramiteEditId) {
      await API.put(`/tipos-tramite/${tramiteEditId}`, { id: tramiteEditId, ...payload });
      toast("Trámite actualizado ✅", `ID ${tramiteEditId}`);
    } else {
      await API.post("/tipos-tramite", payload);
      toast("Trámite creado ✅", payload.nombre);
    }

    resetTramiteForm();
    await loadTramites();
  }

  async function borrarTramite(id) {
    if (!confirm("¿Borrar este trámite?")) return;
    await API.delete(`/tipos-tramite/${id}`);
    toast("Trámite borrado", `ID ${id}`);
    await loadTramites();
  }

  function ensureChartJs() { return typeof Chart !== "undefined"; }

  function isoDay(dateStr) {
    if (!dateStr) return null;
    return String(dateStr).slice(0, 10);
  }

  function lastNDaysLabels(n = 7) {
    const out = [];
    const now = new Date();
    for (let i = n - 1; i >= 0; i--) {
      const d = new Date(now);
      d.setDate(now.getDate() - i);
      out.push(d.toISOString().slice(0, 10));
    }
    return out;
  }

  function renderMiniKpisActividad(acciones7d, altas7d, totalUsuarios) {
    if (!el.miniKpisActividad) return;
    el.miniKpisActividad.innerHTML = `
      <div class="mini-kpi"><div class="label">Acciones (7d)</div><div class="value">${acciones7d}</div></div>
      <div class="mini-kpi"><div class="label">Altas (7d)</div><div class="value">${altas7d}</div></div>
      <div class="mini-kpi"><div class="label">Usuarios totales</div><div class="value">${totalUsuarios}</div></div>
    `;
  }

  function entidadKey(h) {
    const raw = (h?.entidadAfectada || "").toString().trim();
    return raw ? raw.toUpperCase() : "OTROS";
  }

  function renderChartActividadLinea() {
    if (!ensureChartJs() || !el.chartActividadLinea) return 0;

    const labels = lastNDaysLabels(7);
    const counts = Object.fromEntries(labels.map(d => [d, 0]));

    for (const h of historialCache) {
      const d = isoDay(h.fechaHora);
      if (d && counts[d] !== undefined) counts[d]++;
    }

    const data = labels.map(d => counts[d]);

    if (chartActividadLinea) chartActividadLinea.destroy();
    chartActividadLinea = new Chart(el.chartActividadLinea, {
      type: "line",
      data: {
        labels: labels.map(x => x.slice(5)),
        datasets: [{ data, tension: 0.35, fill: false, borderWidth: 2, pointRadius: 3 }]
      },
      options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });

    const total = data.reduce((a,b)=>a+b,0);
    el.chartActividadLineaMsg && (el.chartActividadLineaMsg.textContent = `Total acciones (7d): ${total}`);
    return total;
  }

  function renderChartActividadTop() {
    if (!ensureChartJs() || !el.chartActividadTop) return;

    const counts = {};
    for (const h of historialCache) {
      const k = entidadKey(h);
      counts[k] = (counts[k] || 0) + 1;
    }

    const entries = Object.entries(counts).sort((a,b)=>b[1]-a[1]).slice(0, 8);
    const labels = entries.map(([k]) => k);
    const data = entries.map(([,v]) => v);

    if (chartActividadTop) chartActividadTop.destroy();
    chartActividadTop = new Chart(el.chartActividadTop, {
      type: "bar",
      data: { labels, datasets: [{ data, borderWidth: 1 }] },
      options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });

    el.chartActividadTopMsg && (el.chartActividadTopMsg.textContent = `Top entidades (basado en ${historialCache.length} registros)`);
  }

  function renderChartUsuarios7d() {
    if (!ensureChartJs() || !el.chartUsuarios7d) return 0;

    if (!Array.isArray(usuarios) || usuarios.length === 0) {
      el.chartUsuarios7dMsg && (el.chartUsuarios7dMsg.textContent = "Sin usuarios.");
      return 0;
    }

    const labels = lastNDaysLabels(7);
    const counts = Object.fromEntries(labels.map(d => [d, 0]));

    for (const u of usuarios) {
      const d = isoDay(u.creadoEn);
      if (d && counts[d] !== undefined) counts[d]++;
    }

    const data = labels.map(d => counts[d]);

    if (chartUsuarios7d) chartUsuarios7d.destroy();
    chartUsuarios7d = new Chart(el.chartUsuarios7d, {
      type: "bar",
      data: { labels: labels.map(x => x.slice(5)), datasets: [{ data, borderWidth: 1 }] },
      options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });

    const total = data.reduce((a,b)=>a+b,0);
    el.chartUsuarios7dMsg && (el.chartUsuarios7dMsg.textContent = `Altas (7d): ${total}`);
    return total;
  }

  function renderEstadoSistemaMini() {
    if (!el.estadoSistemaMini) return;
    el.estadoSistemaMini.innerHTML = `
      <div class="system-list">
        <div class="system-item"><div class="system-left"><strong>Backend</strong></div><span class="text-secondary">${escapeHtml(el.txtBackend?.textContent || "—")}</span></div>
        <div class="system-item"><div class="system-left"><strong>Base de datos</strong></div><span class="text-secondary">${escapeHtml(el.txtDB?.textContent || "—")}</span></div>
        <div class="system-item"><div class="system-left"><strong>Clima</strong></div><span class="text-secondary">${escapeHtml(el.txtClima?.textContent || "—")}</span></div>
      </div>
    `;
    if (el.estadoGeneralBadge2 && el.estadoGeneralBadge) {
      el.estadoGeneralBadge2.textContent = el.estadoGeneralBadge.textContent;
      el.estadoGeneralBadge2.className = el.estadoGeneralBadge.className;
    }
  }

  function renderQuickMini() {
    if (!el.adminQuickGridMini || !el.adminQuickGrid) return;
    const items = Array.from(el.adminQuickGrid.querySelectorAll(".card-quick-access")).slice(0, 4);
    el.adminQuickGridMini.innerHTML = "";
    items.forEach(n => el.adminQuickGridMini.appendChild(n.cloneNode(true)));
  }

  async function initData() {
    await loadDashboard();
    await loadCiclos();
    await loadTramites().catch(() => {});
    await loadUsuarios().catch(() => {});
    await loadActivity();
    await checkSystem();
  }

  function cache() {
    el.searchInput = q("searchInput");

    el.adminStatsGrid = q("adminStatsGrid");
    el.adminQuickGrid = q("adminQuickGrid");

    el.btnRefreshAdmin = q("btnRefreshAdmin");

    // CICLOS
    el.formCiclo = q("formCiclo");
    el.cicloNombre = q("cicloNombre");
    el.cicloCodigo = q("cicloCodigo");
    el.cicloFamilia = q("cicloFamilia");
    el.cicloGrado = q("cicloGrado");
    el.ciclosTbody = q("ciclosTbody");
    el.ciclosMsg = q("ciclosMsg");

    // USUARIOS
    el.usuariosMsg = q("usuariosMsg");
    el.usuariosSearch = q("usuariosSearch");
    el.usuariosRolFilter = q("usuariosRolFilter");
    el.btnRefreshUsuarios = q("btnRefreshUsuarios");
    el.usuariosTbody = q("usuariosTbody");
    el.usuariosPagerMeta = q("usuariosPagerMeta");
    el.btnUsuariosPrev = q("btnUsuariosPrev");
    el.btnUsuariosNext = q("btnUsuariosNext");

    el.rolesBar = q("rolesBar");
    el.rolesLegend = q("rolesLegend");

    // ACTIVIDAD
    el.adminActivityFeed = q("adminActivityFeed");

    // SISTEMA
    el.estadoGeneralBadge = q("estadoGeneralBadge");
    el.dotBackend = q("dotBackend");
    el.txtBackend = q("txtBackend");
    el.dotDB = q("dotDB");
    el.txtDB = q("txtDB");
    el.dotClima = q("dotClima");
    el.txtClima = q("txtClima");

    // TOAST
    el.toast = q("toast");
    el.toastTitle = q("toastTitle");
    el.toastMsg = q("toastMsg");

    // MODAL CICLOS
    el.modalOverlay = q("modalOverlay");
    el.modalCiclo = q("modalCiclo");
    el.btnCerrarModal = q("btnCerrarModal");
    el.formEditarCiclo = q("formEditarCiclo");
    el.editNombre = q("editNombre");
    el.editCodigo = q("editCodigo");
    el.editFamilia = q("editFamilia");
    el.editGrado = q("editGrado");

    // TRÁMITES
    el.tramitesMsg = q("tramitesMsg");
    el.btnRefreshTramites = q("btnRefreshTramites");
    el.formTramite = q("formTramite");
    el.tramiteNombre = q("tramiteNombre");
    el.tramiteDescripcion = q("tramiteDescripcion");
    el.tramiteDuracion = q("tramiteDuracion");
    el.tramiteRequiereDoc = q("tramiteRequiereDoc");
    el.tramiteDestacado = q("tramiteDestacado");
    el.tramiteVisible = q("tramiteVisible");
    el.btnGuardarTramite = q("btnGuardarTramite");
    el.btnCancelarEdicionTramite = q("btnCancelarEdicionTramite");
    el.tramitesTbody = q("tramitesTbody");

    el.chartActividadLinea = q("chartActividadLinea");
    el.chartActividadLineaMsg = q("chartActividadLineaMsg");
    el.chartActividadTop = q("chartActividadTop");
    el.chartActividadTopMsg = q("chartActividadTopMsg");
    el.chartUsuarios7d = q("chartUsuarios7d");
    el.chartUsuarios7dMsg = q("chartUsuarios7dMsg");

    el.estadoGeneralBadge2 = q("estadoGeneralBadge2");
    el.estadoSistemaMini = q("estadoSistemaMini");
    el.adminQuickGridMini = q("adminQuickGridMini");
    el.miniKpisActividad = q("miniKpisActividad");

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