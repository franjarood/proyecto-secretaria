(() => {
  const el = {};
  const q = (id) => document.getElementById(id);

  // =========================
  // Helpers UI
  // =========================
  function setMsg(txt) {
    if (!el.kMsg) return;
    el.kMsg.textContent = txt || "";
  }

  function escapeHtml(str) {
    return String(str ?? "")
      .replaceAll("&", "&amp;").replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;").replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function isValidEmail(email) {
    if (!email) return false;
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
  }

  function requireKioskoRole(user) {
    const r = String(user?.rol || "").toUpperCase();
    return ["ADMIN", "SECRETARIA", "CONSERJE"].includes(r);
  }

  function tickClock() {
    const now = new Date();
    const hh = String(now.getHours()).padStart(2, "0");
    const mm = String(now.getMinutes()).padStart(2, "0");
    el.kTime.textContent = `${hh}:${mm}`;
    el.kDate.textContent = now.toLocaleDateString("es-ES", {
      weekday: "short", year: "numeric", month: "2-digit", day: "2-digit"
    });
  }

  // =========================
  // Navegación (HOME es referencia)
  // =========================
  function setActiveNav(go) {
    document.querySelectorAll(".k-side-item").forEach(btn => {
      btn.classList.toggle("active", btn.getAttribute("data-go") === go);
    });
  }

  function showOnly(sectionId) {
    // Oculta steps
    [el.stepSacar, el.stepConfirmar, el.stepConsultar].forEach(s => {
      if (s) s.classList.remove("active");
    });

    // Mostrar sólo el que toque
    if (sectionId === "home") {
      document.getElementById("home")?.scrollIntoView({ behavior: "smooth", block: "start" });
      return;
    }

    if (sectionId === "sacar") el.stepSacar?.classList.add("active");
    if (sectionId === "confirmar") el.stepConfirmar?.classList.add("active");
    if (sectionId === "consultar") el.stepConsultar?.classList.add("active");


    if (sectionId === "info") document.getElementById("info")?.scrollIntoView({ behavior: "smooth", block: "start" });

    // Si es step, hacemos scroll al propio step
    if (["sacar", "confirmar", "consultar"].includes(sectionId)) {
      const map = {
        sacar: el.stepSacar,
        confirmar: el.stepConfirmar,
        consultar: el.stepConsultar
      };
      map[sectionId]?.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  }

  function go(sectionId) {
    setActiveNav(sectionId === "home" ? "home" : sectionId);
    showOnly(sectionId);

    // UX: foco
    if (sectionId === "sacar") setTimeout(() => el.tramiteSelect?.focus(), 120);
    if (sectionId === "confirmar") setTimeout(() => el.turnoIdConfirmar?.focus(), 120);
    if (sectionId === "consultar") setTimeout(() => el.turnoIdConsultar?.focus(), 120);
  }

  // =========================
  // Clima + Estado sistema
  // =========================
  async function cargarClima() {
    if (!el.kClima) return false;
    try {
      const clima = await API.get("/clima/actual");
      if (clima?.climaDisponible) {
        el.kClima.textContent = `${clima.icono || "🌤"} ${clima.ciudad || "Vigo"} · ${clima.temperatura ?? "--"}°C`;
      } else {
        el.kClima.textContent = `🌥 ${clima?.ciudad || "Vigo"} · No disponible`;
      }
      return !!clima?.climaDisponible;
    } catch {
      el.kClima.textContent = "🌥 Clima · No disponible";
      return false;
    }
  }

  async function verificarBackend() {
    try {
      await API.get("/tipos-tramite");
      return true;
    } catch {
      return false;
    }
  }

  function setEstadoSistema({ backendOk, climaOk }) {
    if (!el.kEstadoSistema) return;

    if (backendOk && climaOk) el.kEstadoSistema.textContent = "🟢 Sistema: OK";
    else if (backendOk && !climaOk) el.kEstadoSistema.textContent = "🟡 Sistema: OK (clima no)";
    else el.kEstadoSistema.textContent = "🔴 Sistema: sin conexión";
  }

  async function initSystemBadges() {
    const backendOk = await verificarBackend();
    const climaOk = await cargarClima();
    setEstadoSistema({ backendOk, climaOk });

    setInterval(async () => {
      const b = await verificarBackend();
      const c = await cargarClima();
      setEstadoSistema({ backendOk: b, climaOk: c });
    }, 120000);
  }

  // =========================
  // Trámites (select)
  // =========================
  let tramites = [];

  function getSelectedTramite() {
    const id = Number(el.tramiteSelect?.value);
    if (!Number.isFinite(id) || !id) return null;
    return tramites.find(t => Number(t.id) === id) || null;
  }

  async function loadTramites() {
    setMsg("Cargando trámites…");
    const list = await API.get("/tipos-tramite");
    tramites = Array.isArray(list) ? list : [];

    el.tramiteSelect.innerHTML = `<option value="">Selecciona un trámite…</option>`;

    if (!tramites.length) {
      setMsg("No hay trámites disponibles. Crea alguno en Admin.");
      el.tramiteInfo.textContent = "No hay trámites disponibles.";
      return;
    }

    // Filtrar visibles si existe el campo
    const visibles = tramites.filter(t => t.visiblePublicamente !== false);
    const lista = visibles.length ? visibles : tramites;

    lista.forEach(t => {
      const opt = document.createElement("option");
      opt.value = String(t.id);
      opt.textContent = `${t.nombre || "Trámite"} · ${t.duracionEstimada ?? "-"} min`;
      el.tramiteSelect.appendChild(opt);
    });

    setMsg(`Trámites cargados: ${lista.length}`);
    el.tramiteInfo.textContent = "Selecciona un trámite para ver más información.";
  }

  // =========================
  // Ticket UI
  // =========================
  function showTicket(turno, tramiteNombre) {
    el.ticketBox.style.display = "block";
    el.ticketNumero.textContent = String(turno?.numeroTurno ?? "A-???");
    el.ticketId.textContent = String(turno?.id ?? "—");
    el.ticketEstado.textContent = String(turno?.estadoTurno ?? "—");
    el.ticketTramite.textContent = tramiteNombre || "—";
  }

  function resetTicket() {
    el.ticketBox.style.display = "none";
    el.ticketNumero.textContent = "A-000";
    el.ticketId.textContent = "—";
    el.ticketEstado.textContent = "—";
    el.ticketTramite.textContent = "—";
  }

  // =========================
  // Acciones
  // =========================
  async function sacarTurno() {
    const tramite = getSelectedTramite();
    if (!tramite) {
      setMsg("Selecciona un trámite para continuar.");
      return;
    }

    const wantsEmail = el.toggleEmail.checked;
    const email = el.emailContacto.value.trim();

    if (wantsEmail && !isValidEmail(email)) {
      setMsg("Email inválido. Corrígelo o desactiva avisos por email.");
      return;
    }

    el.btnSacarTurno.disabled = true;
    const oldText = el.btnSacarTurno.textContent;
    el.btnSacarTurno.textContent = "Generando…";

    try {
      const now = new Date();
      const fechaCita = now.toISOString().slice(0, 10);
      const horaCita = now.toTimeString().slice(0, 8);

      setMsg("Creando reserva…");

      // OJO: emailContacto es mejora futura (DTO actual no lo guarda)
      const reservaPayload = {
         fechaCita,
          horaCita,
          origenTurno: "KIOSKO",
          tiposTramiteIds: [tramite.id],
          emailContacto: wantsEmail ? email : null
      };

      const reserva = await API.post("/reservas", reservaPayload);

      setMsg("Generando turno…");
      const turno = await API.post(`/turnos/desde-reserva/${reserva.id}`, {});

      showTicket(turno, tramite.nombre);
      el.turnoIdConfirmar.value = turno.id;

      setMsg("Ticket generado ✅");
    } catch (err) {
      setMsg(err.message || "Error al generar ticket");
    } finally {
      el.btnSacarTurno.disabled = false;
      el.btnSacarTurno.textContent = oldText;
    }
  }

  async function confirmarLlegada() {
    const id = Number(el.turnoIdConfirmar.value);
    if (!Number.isFinite(id) || !id) {
      el.resultadoConfirmar.textContent = "ID inválido.";
      setMsg("Introduce un ID válido.");
      return;
    }

    setMsg("Confirmando llegada…");
    try {
      const turno = await API.request(`/turnos/${id}/confirmar`, { method: "PUT" });

      el.resultadoConfirmar.innerHTML = `
        <div><strong>✅ Llegada confirmada</strong></div>
        <div>Turno: <strong>${escapeHtml(turno?.numeroTurno ?? "-")}</strong></div>
        <div>Estado: <strong>${escapeHtml(turno?.estadoTurno ?? "-")}</strong></div>
      `;
      setMsg("Confirmado ✅");
    } catch (err) {
      el.resultadoConfirmar.textContent = err.message || "Error al confirmar";
      setMsg("Error al confirmar");
    }
  }

  async function consultarTurno() {
    const id = Number(el.turnoIdConsultar.value);
    if (!Number.isFinite(id) || !id) {
      el.resultadoConsultar.textContent = "ID inválido.";
      setMsg("Introduce un ID válido.");
      return;
    }

    setMsg("Consultando…");
    try {
      const turno = await API.get(`/turnos/${id}`);

      el.resultadoConsultar.innerHTML = `
        <div><strong>🧾 Estado del turno</strong></div>
        <div>Turno: <strong>${escapeHtml(turno?.numeroTurno ?? "-")}</strong></div>
        <div>Estado: <strong>${escapeHtml(turno?.estadoTurno ?? "-")}</strong></div>
        <div>Origen: <strong>${escapeHtml(turno?.origenTurno ?? "-")}</strong></div>
      `;
      setMsg("Consulta OK ✅");
    } catch (err) {
      el.resultadoConsultar.textContent = err.message || "Error al consultar";
      setMsg("Error al consultar");
    }
  }

  // =========================
  // Bind eventos
  // =========================
  function bind() {
    // Navegación universal por data-go
    document.querySelectorAll("[data-go]").forEach(btn => {
      btn.addEventListener("click", () => {
        const target = btn.getAttribute("data-go");
        if (!target) return;

        // Si es botón lateral o card, activamos nav y sección
        if (["home","sacar","confirmar","consultar","info"].includes(target)) {
          go(target);
        }
      });
    });

    // Asistente (de momento solo scroll a requisitos como "ayuda")
    el.btnAsistenteHome?.addEventListener("click", () => {
      document.getElementById("home")?.scrollIntoView({ behavior: "smooth", block: "start" });
      // opcional: seleccionar un tema por defecto
      if (el.helpText) el.helpText.textContent = "Pulsa una tarjeta para ver recomendaciones.";
    });

    el.toggleEmail?.addEventListener("change", () => {
      el.emailBox.style.display = el.toggleEmail.checked ? "block" : "none";
      if (el.toggleEmail.checked) setTimeout(() => el.emailContacto?.focus(), 100);
    });

    el.tramiteSelect?.addEventListener("change", () => {
      const t = getSelectedTramite();
      if (!t) {
        el.tramiteInfo.textContent = "Selecciona un trámite para ver más información.";
        return;
      }

      const doc = (t.requiereDocumentacion === true)
        ? "📎 Requiere documentación: trae copias o PDF."
        : "✅ Normalmente no requiere documentación.";

      el.tramiteInfo.textContent =
        `📝 ${t.descripcion || "Sin descripción"} · ⏱️ ${t.duracionEstimada ?? "-"} min · ${doc}`;
    });

    el.btnSacarTurno?.addEventListener("click", sacarTurno);
    el.btnConfirmar?.addEventListener("click", confirmarLlegada);
    el.btnConsultar?.addEventListener("click", consultarTurno);

    el.turnoIdConfirmar?.addEventListener("keydown", (e) => {
      if (e.key === "Enter") el.btnConfirmar.click();
    });

    el.turnoIdConsultar?.addEventListener("keydown", (e) => {
      if (e.key === "Enter") el.btnConsultar.click();
    });

    el.btnLimpiar?.addEventListener("click", () => {
      el.tramiteSelect.value = "";
      el.tramiteInfo.textContent = "Selecciona un trámite para ver más información.";

      el.toggleEmail.checked = false;
      el.emailContacto.value = "";
      el.emailBox.style.display = "none";

      el.turnoIdConfirmar.value = "";
      el.turnoIdConsultar.value = "";
      el.resultadoConfirmar.textContent = "—";
      el.resultadoConsultar.textContent = "—";

      resetTicket();
      setMsg("Listo.");
      go("home");
    });

    el.btnImprimirFake?.addEventListener("click", () => {
      setMsg("Impresión (demo): preparado para impresora térmica / QR (mejora futura).");
    });

    const HELP = {
      matricula: `✅ Trae DNI/NIE y documentación académica previa (si aplica).
    📌 Consejo: prepara copias o PDFs para agilizar la atención.
    🕒 Puede requerir revisión de datos y documentos.`,
      docs: `📄 Revisa que los documentos estén firmados y legibles.
    🧷 Si falta algo, te indicarán cómo completarlo.
    💡 Consejo: trae copias y originales si te los solicitan.`,
      cert: `🏷️ Indica el tipo: matrícula, notas, asistencia, etc.
    ⏱️ Algunos certificados pueden tardar: consulta plazos.
    📌 Consejo: confirma si lo necesitas con sello/registro.`,
      info: `ℹ️ Para dudas rápidas el trámite suele ser corto.
    ✅ Si necesitas entregar papeles, usa “Documentación”.
    💡 Si prefieres hacerlo desde casa, usa la versión digital (fase siguiente).`
    };

    el.helpGrid?.addEventListener("click", (e) => {
      const btn = e.target.closest("[data-help]");
      if (!btn) return;
      const key = btn.getAttribute("data-help");
      if (el.helpText) el.helpText.textContent = HELP[key] || "—";
    });
  }

  // =========================
  // Init
  // =========================
  function init() {
    if (!Auth.requireAuth()) return;

    const user = Auth.getCurrentUser();
    if (!requireKioskoRole(user)) {
      alert("Acceso denegado: solo ADMIN/SECRETARIA/CONSERJE.");
      window.location.href = CONFIG.ROUTES.LOGIN;
      return;
    }

    // cache DOM
    el.kTime = q("kTime");
    el.kDate = q("kDate");
    el.kMsg = q("kMsg");
    el.kEstadoSistema = q("kEstadoSistema");
    el.kClima = q("kClima");

    el.btnAsistenteHome = q("btnAsistenteHome");
    el.helpGrid = q("helpGrid");
    el.helpText = q("helpText");

    el.stepSacar = q("stepSacar");
    el.stepConfirmar = q("stepConfirmar");
    el.stepConsultar = q("stepConsultar");

    el.tramiteSelect = q("tramiteSelect");
    el.tramiteInfo = q("tramiteInfo");

    el.toggleEmail = q("toggleEmail");
    el.emailBox = q("emailBox");
    el.emailContacto = q("emailContacto");

    el.btnSacarTurno = q("btnSacarTurno");
    el.btnLimpiar = q("btnLimpiar");

    el.ticketBox = q("ticketBox");
    el.ticketNumero = q("ticketNumero");
    el.ticketId = q("ticketId");
    el.ticketEstado = q("ticketEstado");
    el.ticketTramite = q("ticketTramite");
    el.btnIrConfirmar2 = q("btnIrConfirmar2");
    el.btnImprimirFake = q("btnImprimirFake");

    el.turnoIdConfirmar = q("turnoIdConfirmar");
    el.btnConfirmar = q("btnConfirmar");
    el.resultadoConfirmar = q("resultadoConfirmar");

    el.turnoIdConsultar = q("turnoIdConsultar");
    el.btnConsultar = q("btnConsultar");
    el.resultadoConsultar = q("resultadoConsultar");

    // reloj
    tickClock();
    setInterval(tickClock, 20000);

    // eventos + badges + datos
    bind();
    initSystemBadges().catch(() => {});
    loadTramites().catch(err => setMsg(err.message || "Error cargando trámites"));

    // estado inicial: HOME real
    go("home");
    resetTicket();
  }

  document.addEventListener("DOMContentLoaded", init);
})();