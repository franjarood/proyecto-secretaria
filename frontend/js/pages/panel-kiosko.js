(() => {
  const el = {};
  const q = (id) => document.getElementById(id);

  const HELP = {
    matricula: `✅ Trae DNI/NIE, documentación académica previa (si aplica) y justificante de pago si corresponde.
📌 Consejo: prepara copias o PDFs para agilizar la atención.`,
    docs: `📄 Entrega de documentación: revisa que los documentos estén firmados y legibles.
💡 Si falta algo, te indicarán cómo completarlo y podrás volver con prioridad (mejora futura).`,
    cert: `🏷️ Certificados: indica el tipo (matrícula, notas, asistencia).
⏱️ Algunos certificados pueden tardar; consulta si hay recogida posterior.`,
    info: `ℹ️ Información general: para dudas rápidas, el trámite suele ser corto.
✅ Si tu consulta requiere documentos, usa “Documentación”.`
  };

  function setMsg(txt, type="info"){
    if (!el.kMsg) return;
    el.kMsg.textContent = txt || "";
    el.kMsg.style.opacity = "1";

    // En el kiosko claro, colores más coherentes
    el.kMsg.style.color =
      type === "ok" ? "#0f766e" :
      type === "error" ? "#b91c1c" :
      "#55708b";
  }

  function escapeHtml(str){
    return String(str ?? "")
      .replaceAll("&","&amp;").replaceAll("<","&lt;")
      .replaceAll(">","&gt;").replaceAll('"',"&quot;")
      .replaceAll("'","&#039;");
  }

  function requireKioskoRole(user){
    const r = String(user?.rol || "").toUpperCase();
    return ["ADMIN", "SECRETARIA", "CONSERJE"].includes(r);
  }

  function tickClock(){
    const now = new Date();
    const hh = String(now.getHours()).padStart(2,"0");
    const mm = String(now.getMinutes()).padStart(2,"0");
    el.kTime.textContent = `${hh}:${mm}`;
    el.kDate.textContent = now.toLocaleDateString("es-ES", {
      weekday:"short", year:"numeric", month:"2-digit", day:"2-digit"
    });
  }

  // Kiosko = evitar scroll global
  function setActiveStep(step){
    el.stepSacar.classList.remove("active");
    el.stepConfirmar.classList.remove("active");
    el.stepConsultar.classList.remove("active");
    step.classList.add("active");

    // BOOM UX: foco directo en inputs según modo
    if (step === el.stepConfirmar) setTimeout(() => el.turnoIdConfirmar?.focus(), 60);
    if (step === el.stepConsultar) setTimeout(() => el.turnoIdConsultar?.focus(), 60);
  }

  function getSelectedTramite(){
    const sel = document.querySelector("input[name='tramite']:checked");
    if (!sel) return null;
    const id = Number(sel.value);
    return tramites.find(t => Number(t.id) === id) || null;
  }

  function isValidEmail(email){
    if (!email) return false;
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());
  }

  // BOOM: flash visual del ticket
  function flashTicket(){
    if (!el.ticketBox) return;
    el.ticketBox.style.transform = "scale(1.01)";
    el.ticketBox.style.transition = "transform .18s ease, box-shadow .18s ease";
    el.ticketBox.style.boxShadow = "0 24px 60px rgba(14,165,233,.22)";
    setTimeout(() => {
      el.ticketBox.style.transform = "scale(1)";
      el.ticketBox.style.boxShadow = "";
    }, 220);
  }

  async function copyToClipboard(text){
    try{
      if (navigator.clipboard?.writeText) {
        await navigator.clipboard.writeText(String(text));
        return true;
      }
    }catch{}
    return false;
  }

  // =========================
  // CLIMA + ESTADO DEL SISTEMA
  // =========================
  async function cargarClima(){
    if (!el.kClima) return;
    try{
      const clima = await API.get("/clima/actual");
      if (clima?.climaDisponible) {
        el.kClima.textContent = `${clima.icono || "🌤"} ${clima.ciudad || "Vigo"} · ${clima.temperatura ?? "--"}°C`;
      } else {
        el.kClima.textContent = `🌥 ${clima?.ciudad || "Vigo"} · No disponible`;
      }
      return !!clima?.climaDisponible;
    }catch{
      el.kClima.textContent = "🌥 Clima · No disponible";
      return false;
    }
  }

  async function verificarBackend(){
    // Ping simple: usa un endpoint que sabes que responde si hay sesión
    try{
      await API.get("/tipos-tramite");
      return true;
    }catch{
      return false;
    }
  }

  function setEstadoSistema({ backendOk, climaOk }){
    if (!el.kEstadoSistema) return;

    if (backendOk && climaOk) {
      el.kEstadoSistema.textContent = "🟢 Sistema: OK";
    } else if (backendOk && !climaOk) {
      el.kEstadoSistema.textContent = "🟡 Sistema: OK (clima no)";
    } else {
      el.kEstadoSistema.textContent = "🔴 Sistema: sin conexión";
    }
  }

  // =========================
  // TRÁMITES
  // =========================
  let tramites = [];

  async function loadTramites(){
    setMsg("Cargando trámites...");
    const list = await API.get("/tipos-tramite");
    tramites = Array.isArray(list) ? list : [];

    if (!tramites.length){
      el.tramitesBox.innerHTML = `<div class="k-empty">No hay trámites. Crea alguno en Admin.</div>`;
      setMsg("No hay trámites disponibles.", "error");
      return;
    }

    el.tramitesBox.innerHTML = tramites.map(t => `
      <label class="k-tramite">
        <input type="radio" name="tramite" value="${t.id}">
        <div>
          <div class="k-tramite-name">${escapeHtml(t.nombre || "Trámite")}</div>
          <div class="k-tramite-desc">${escapeHtml(t.descripcion || "")}</div>
        </div>
      </label>
    `).join("");

    setMsg(`Trámites cargados: ${tramites.length}`, "ok");
  }

  // =========================
  // TICKET
  // =========================
  function showTicket(turno, tramiteNombre){
    el.ticketBox.style.display = "grid";
    el.ticketNumero.textContent = String(turno?.numeroTurno ?? "A-???");
    el.ticketId.textContent = String(turno?.id ?? "—");
    el.ticketEstado.textContent = String(turno?.estadoTurno ?? "—");
    el.ticketTramite.textContent = tramiteNombre || "—";
    flashTicket();
  }

  function resetTicket(){
    el.ticketBox.style.display = "none";
    el.ticketNumero.textContent = "A-000";
    el.ticketId.textContent = "—";
    el.ticketEstado.textContent = "—";
    el.ticketTramite.textContent = "—";
  }

  // =========================
  // ACCIONES
  // =========================
  async function sacarTurno(){
    const tramite = getSelectedTramite();
    if (!tramite){
      setMsg("Selecciona un trámite para continuar.", "error");
      return;
    }

    const wantsEmail = el.toggleEmail.checked;
    const email = el.emailContacto.value.trim();

    if (wantsEmail && !isValidEmail(email)){
      setMsg("Email inválido. Corrígelo o desactiva avisos por email.", "error");
      return;
    }

    // Evitar doble click
    el.btnSacarTurno.disabled = true;
    const oldText = el.btnSacarTurno.textContent;
    el.btnSacarTurno.textContent = "Generando...";

    try{
      const now = new Date();
      const fechaCita = now.toISOString().slice(0,10);
      const horaCita = now.toTimeString().slice(0,8);

      setMsg("Creando reserva (KIOSKO)...");

      // HOOK: si tu backend difiere lo ajustamos en Network
      const reservaPayload = {
        fechaCita,
        horaCita,
        origenTurno: "KIOSKO",
        tiposTramiteIds: [tramite.id],
        emailContacto: wantsEmail ? email : null
      };

      const reserva = await API.post("/reservas", reservaPayload);

      setMsg("Generando turno...");
      const turno = await API.post(`/turnos/desde-reserva/${reserva.id}`, {});

      showTicket(turno, tramite.nombre);
      el.turnoIdConfirmar.value = turno.id;

      // BOOM extra: copia ID al portapapeles (si se puede)
      const copied = await copyToClipboard(turno.id);
      setMsg(copied ? "Ticket generado ✅ (ID copiado)" : "Ticket generado ✅", "ok");

    }catch(err){
      setMsg(err.message || "Error al generar ticket", "error");
      throw err;
    }finally{
      el.btnSacarTurno.disabled = false;
      el.btnSacarTurno.textContent = oldText;
    }
  }

  async function confirmarLlegada(){
    const id = Number(el.turnoIdConfirmar.value);
    if (!Number.isFinite(id)){
      el.resultadoConfirmar.textContent = "ID inválido.";
      setMsg("Introduce un ID válido.", "error");
      return;
    }

    setMsg("Confirmando llegada...");
    const turno = await API.request(`/turnos/${id}/confirmar`, { method:"PUT" });

    el.resultadoConfirmar.innerHTML = `
      <div><strong>✅ Llegada confirmada</strong></div>
      <div>Turno: <strong>${escapeHtml(turno?.numeroTurno ?? "-")}</strong></div>
      <div>Estado: <strong>${escapeHtml(turno?.estadoTurno ?? "-")}</strong></div>
    `;

    setMsg("Confirmado ✅", "ok");
  }

  async function consultarTurno(){
    const id = Number(el.turnoIdConsultar.value);
    if (!Number.isFinite(id)){
      el.resultadoConsultar.textContent = "ID inválido.";
      setMsg("Introduce un ID válido.", "error");
      return;
    }

    setMsg("Consultando turno...");
    const turno = await API.get(`/turnos/${id}`);

    el.resultadoConsultar.innerHTML = `
      <div><strong>🔎 Estado del turno</strong></div>
      <div>Turno: <strong>${escapeHtml(turno?.numeroTurno ?? "-")}</strong></div>
      <div>Estado: <strong>${escapeHtml(turno?.estadoTurno ?? "-")}</strong></div>
      <div>Origen: <strong>${escapeHtml(turno?.origenTurno ?? "-")}</strong></div>
    `;

    setMsg("Consulta OK ✅", "ok");
  }

  // =========================
  // EVENTOS
  // =========================
  function bind(){
    el.btnIrSacar.addEventListener("click", () => setActiveStep(el.stepSacar));
    el.btnIrConfirmar.addEventListener("click", () => setActiveStep(el.stepConfirmar));
    el.btnIrConsultar.addEventListener("click", () => setActiveStep(el.stepConsultar));
    el.btnIrConfirmar2.addEventListener("click", () => setActiveStep(el.stepConfirmar));

    el.btnLimpiar.addEventListener("click", () => {
      document.querySelectorAll("input[name='tramite']").forEach(r => r.checked = false);
      el.toggleEmail.checked = false;
      el.emailContacto.value = "";
      el.emailBox.style.display = "none";
      el.turnoIdConfirmar.value = "";
      el.turnoIdConsultar.value = "";
      el.resultadoConfirmar.textContent = "—";
      el.resultadoConsultar.textContent = "—";
      resetTicket();
      setMsg("Listo.");
    });

    el.toggleEmail.addEventListener("change", () => {
      el.emailBox.style.display = el.toggleEmail.checked ? "block" : "none";
      if (el.toggleEmail.checked) setTimeout(() => el.emailContacto?.focus(), 60);
    });

    el.btnSacarTurno.addEventListener("click", () => {
      sacarTurno().catch(err => setMsg(err.message || "Error al sacar turno", "error"));
    });

    el.btnConfirmar.addEventListener("click", () => {
      confirmarLlegada().catch(err => setMsg(err.message || "Error al confirmar", "error"));
    });

    el.btnConsultar.addEventListener("click", () => {
      consultarTurno().catch(err => setMsg(err.message || "Error al consultar", "error"));
    });

    el.btnVolverSacar1.addEventListener("click", () => setActiveStep(el.stepSacar));
    el.btnVolverSacar2.addEventListener("click", () => setActiveStep(el.stepSacar));

    // Enter para confirmar/consultar
    el.turnoIdConfirmar.addEventListener("keydown", (e) => {
      if (e.key === "Enter") el.btnConfirmar.click();
    });
    el.turnoIdConsultar.addEventListener("keydown", (e) => {
      if (e.key === "Enter") el.btnConsultar.click();
    });

    el.helpGrid.addEventListener("click", (e) => {
      const card = e.target.closest("[data-help]");
      if (!card) return;
      const key = card.getAttribute("data-help");
      el.helpText.textContent = HELP[key] || "—";
    });

    el.btnImprimirFake.addEventListener("click", () => {
      setMsg("Impresión (demo): listo para impresora térmica / QR en mejora futura.", "ok");
    });

    el.btnFullscreen.addEventListener("click", async () => {
      try{
        if (!document.fullscreenElement) await document.documentElement.requestFullscreen();
        else await document.exitFullscreen();
      }catch{
        setMsg("Pantalla completa no disponible en este navegador.", "error");
      }
    });
  }

  // =========================
  // INIT
  // =========================
  async function initSystemBadges(){
    const backendOk = await verificarBackend();
    const climaOk = await cargarClima();
    setEstadoSistema({ backendOk, climaOk });

    // refresco suave cada 2 min (clima/estado)
    setInterval(async () => {
      const b = await verificarBackend();
      const c = await cargarClima();
      setEstadoSistema({ backendOk: b, climaOk: c });
    }, 120000);
  }

  function init(){
    if (!Auth.requireAuth()) return;

    const user = Auth.getCurrentUser();
    if (!requireKioskoRole(user)){
      alert("Acceso denegado: solo ADMIN/SECRETARIA/CONSERJE.");
      window.location.href = CONFIG.ROUTES.LOGIN;
      return;
    }

    // cache
    el.kTime = q("kTime");
    el.kDate = q("kDate");
    el.kMsg = q("kMsg");
    el.kEstadoSistema = q("kEstadoSistema");
    el.kClima = q("kClima"); // 👈 añade el div en HTML

    el.btnFullscreen = q("btnFullscreen");
    el.btnIrSacar = q("btnIrSacar");
    el.btnIrConfirmar = q("btnIrConfirmar");
    el.btnIrConsultar = q("btnIrConsultar");

    el.stepSacar = q("stepSacar");
    el.stepConfirmar = q("stepConfirmar");
    el.stepConsultar = q("stepConsultar");

    el.tramitesBox = q("tramitesBox");
    if (!el.tramitesBox) {
      console.error("Falta #tramitesBox en panel-kiosko.html");
      setMsg("Error UI: falta el contenedor de trámites (#tramitesBox).", "error");
      return;
    }
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
    el.btnVolverSacar1 = q("btnVolverSacar1");
    el.resultadoConfirmar = q("resultadoConfirmar");

    el.turnoIdConsultar = q("turnoIdConsultar");
    el.btnConsultar = q("btnConsultar");
    el.btnVolverSacar2 = q("btnVolverSacar2");
    el.resultadoConsultar = q("resultadoConsultar");

    el.helpGrid = q("helpGrid");
    el.helpText = q("helpText");

    // start
    tickClock();
    setInterval(tickClock, 1000 * 20);

    bind();
    initSystemBadges().catch(() => {}); // no bloquea la UI

    loadTramites().catch(err => setMsg(err.message || "Error cargando trámites", "error"));
    setActiveStep(el.stepSacar);

    // Placeholder de “llamadas”
    q("liveActual").textContent = "A-023";
    q("liveEstado").textContent = "En atención · Mostrador 2";
  }

  document.addEventListener("DOMContentLoaded", init);
})();