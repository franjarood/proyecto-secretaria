// ==========================================================
// PANEL ALUMNO PREMIUM
// Archivo puente para conectar el diseño premium con backend,
// botones, clima, mapa, ubicación y check-in.
// ==========================================================

const PanelAlumnoPremium = {

    API_BASE: (window.CONFIG && window.CONFIG.API_BASE_URL)
        ? window.CONFIG.API_BASE_URL
        : "http://localhost:9001",

    usuario: null,
    dashboard: null,
    ubicacionAlumno: null,
    proximoTurnoId: null,
    mapaInicializado: false,
    toastTimeout: null,

    CENTRO: {
        // Fuente única: CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO (definido en js/config.js)
        lat: (window.CONFIG && window.CONFIG.GOOGLE_MAPS && window.CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO)
            ? window.CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.lat
            : 0,
        lng: (window.CONFIG && window.CONFIG.GOOGLE_MAPS && window.CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO)
            ? window.CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.lng
            : 0,
        nombre: (window.CONFIG && window.CONFIG.GOOGLE_MAPS && window.CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO && window.CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.nombre)
            ? window.CONFIG.GOOGLE_MAPS.CENTRO_EDUCATIVO.nombre
            : "Centro educativo",
        radioCheckinMetros: (window.CONFIG && window.CONFIG.GOOGLE_MAPS && window.CONFIG.GOOGLE_MAPS.RADIO_CHECKIN_METROS)
            ? Number(window.CONFIG.GOOGLE_MAPS.RADIO_CHECKIN_METROS)
            : 500
    },

    async init() {
        console.log("✅ Panel alumno premium JS cargado correctamente");
        console.log("Inicializando panel alumno premium...");

        this.cargarConfigCentro();
        this.initReloj();
        this.initBotones();
        this.initAccesosRapidos();
        this.initMenuLateral();

        await this.cargarDatosBackend();
        await this.cargarClima();

        await this.cargarGoogleMapsSiHayKey();

        console.log("✅ Panel alumno premium inicializado");
    },

    // ======================================================
    // CONFIG
    // ======================================================

    cargarConfigCentro() {
        if (!window.CONFIG || !window.CONFIG.GOOGLE_MAPS) {
            console.warn("CONFIG.GOOGLE_MAPS no disponible. Se usará centro por defecto.");
            return;
        }

        const configMaps = window.CONFIG.GOOGLE_MAPS;

        if (configMaps.CENTRO_EDUCATIVO) {
            this.CENTRO.lat = configMaps.CENTRO_EDUCATIVO.lat || this.CENTRO.lat;
            this.CENTRO.lng = configMaps.CENTRO_EDUCATIVO.lng || this.CENTRO.lng;
            this.CENTRO.nombre = configMaps.CENTRO_EDUCATIVO.nombre || this.CENTRO.nombre;
        }

        this.CENTRO.radioCheckinMetros =
            configMaps.RADIO_CHECKIN_METROS || this.CENTRO.radioCheckinMetros;
    },

    // ======================================================
    // API / AUTH
    // ======================================================

    getAuthHeaders() {
        const headers = {
            "Content-Type": "application/json"
        };

        // Si existe Auth del proyecto, intentamos usarlo
        try {
            if (window.Auth && typeof Auth.getSession === "function") {
                const session = Auth.getSession();

                if (session && session.email && session.password) {
                    headers.Authorization = "Basic " + btoa(session.email + ":" + session.password);
                    return headers;
                }

                if (session && session.authHeader) {
                    headers.Authorization = session.authHeader;
                    return headers;
                }
            }
        } catch (error) {
            console.warn("No se pudo leer sesión desde Auth:", error);
        }

        // Fallbacks por si el login guarda datos en sessionStorage
        const email =
            sessionStorage.getItem("email") ||
            sessionStorage.getItem("usuarioEmail") ||
            sessionStorage.getItem("userEmail");

        const password =
            sessionStorage.getItem("password") ||
            sessionStorage.getItem("usuarioPassword") ||
            sessionStorage.getItem("userPassword");

        const authHeader =
            sessionStorage.getItem("authHeader") ||
            sessionStorage.getItem("authorization");

        if (authHeader) {
            headers.Authorization = authHeader.startsWith("Basic ")
                ? authHeader
                : "Basic " + authHeader;
        } else if (email && password) {
            headers.Authorization = "Basic " + btoa(email + ":" + password);
        }

        return headers;
    },

    async get(path) {
        if (window.API && typeof API.get === "function") {
            return await API.get(path);
        }

        const response = await fetch(this.API_BASE + path, {
            method: "GET",
            headers: this.getAuthHeaders()
        });

        if (!response.ok) {
            const text = await response.text().catch(() => "");
            throw new Error(`GET ${path} -> ${response.status} ${text}`);
        }

        return await response.json();
    },

    async post(path, body) {
        if (window.API && typeof API.post === "function") {
            return await API.post(path, body);
        }

        const response = await fetch(this.API_BASE + path, {
            method: "POST",
            headers: this.getAuthHeaders(),
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            const text = await response.text().catch(() => "");
            throw new Error(`POST ${path} -> ${response.status} ${text}`);
        }

        return await response.json().catch(() => null);
    },

    // ======================================================
    // BACKEND
    // ======================================================

    async cargarDatosBackend() {
        try {
            console.log("Cargando /usuarios/me...");
            const usuario = await this.get("/usuarios/me");
            this.usuario = usuario;

            console.log("Usuario cargado:", usuario);

            const usuarioId =
                usuario.id ||
                usuario.usuarioId ||
                usuario.idUsuario;

            const nombre =
                usuario.nombreUsuario ||
                usuario.nombre ||
                usuario.username ||
                usuario.email ||
                "Alumno";

            this.pintarNombre(nombre);

            if (!usuarioId) {
                console.warn("No se encontró usuarioId en /usuarios/me");
                this.mostrarToast("No se pudo identificar el usuario.");
                return;
            }

            console.log(`Cargando /dashboard/alumno/${usuarioId}...`);
            const dashboard = await this.get(`/dashboard/alumno/${usuarioId}`);
            this.dashboard = dashboard;

            console.log("Dashboard alumno:", dashboard);
            this.renderDashboard(dashboard);

            console.log(`Cargando /asistente/usuario/${usuarioId}...`);
            const recomendaciones = await this.get(`/asistente/usuario/${usuarioId}`)
                .catch(error => {
                    console.warn("No se pudo cargar asistente:", error);
                    return [];
                });

            this.renderAsistente(Array.isArray(recomendaciones) ? recomendaciones : []);

        } catch (error) {
            console.error("❌ Error cargando backend:", error);
            this.mostrarToast("No se pudo cargar el panel. Revisa backend, login o consola.");
            this.renderBackendFallback();
        }
    },

    pintarNombre(nombre) {
        this.setText("hero-nombre-usuario", nombre);
        this.setText("sidebar-user-name", nombre);
        this.setText("topbar-user-name", nombre);

        const inicial = nombre.trim().charAt(0).toUpperCase() || "A";
        document.querySelectorAll(".avatar").forEach(avatar => {
            avatar.textContent = inicial;
        });
    },

    renderDashboard(data) {
        if (!data) return;

        const turnosActivos = Array.isArray(data.turnosActivos) ? data.turnosActivos : [];
        const documentosPendientes = Array.isArray(data.documentosPendientes) ? data.documentosPendientes : [];
        const notificaciones = Number(data.notificacionesNoLeidas || 0);
        const estadoPrematricula = data.estadoPrematricula || "No iniciada";
        const progreso = this.calcularProgresoPrematricula(estadoPrematricula);

        const proximoTurno = data.proximoTurno || data.proximaReserva || null;
        this.proximoTurnoId = this.obtenerIdTurno(proximoTurno);

        this.setText("stat-turnos", turnosActivos.length);
        this.setText("stat-documentos", documentosPendientes.length);
        this.setText("stat-notificaciones", notificaciones);
        this.setText("stat-prematricula", progreso + "%");
        this.setText("prematricula-label", estadoPrematricula);
        this.setText("progress-percent", progreso + "%");

        this.setBar("bar-turnos", turnosActivos.length, 5);
        this.setBar("bar-documentos", documentosPendientes.length, 5);
        this.setBar("bar-notificaciones", notificaciones, 10);
        this.setBar("bar-prematricula", progreso, 100);
        this.setCircleProgress(progreso);

        this.renderProximoTurno(proximoTurno);
        this.renderTurnosActivos(turnosActivos);
        this.renderDocumentosPendientes(documentosPendientes);

        this.actualizarBotonCheckin();
    },

    renderBackendFallback() {
        this.setText("stat-turnos", "--");
        this.setText("stat-documentos", "--");
        this.setText("stat-notificaciones", "--");
        this.setText("stat-prematricula", "--");

        const asistente = document.getElementById("asistente-list");
        if (asistente) {
            asistente.innerHTML = `
                <div class="assistant-item">
                    <span>⚠️</span>
                    <div>
                        <strong>No se pudo cargar el panel</strong>
                        <p>Comprueba backend, login o consola.</p>
                    </div>
                    <b>›</b>
                </div>
            `;
        }
    },

    obtenerIdTurno(turno) {
        if (!turno) return null;

        return turno.id ||
            turno.turnoId ||
            turno.idTurno ||
            turno.idReserva ||
            null;
    },

    // ======================================================
    // CLIMA
    // ======================================================

    async cargarClima() {
        try {
            console.log("Cargando /clima/actual...");
            const clima = await this.get("/clima/actual");

            console.log("Clima:", clima);

            if (!clima || clima.climaDisponible === false) {
                this.renderClimaFallback();
                return;
            }

            const temp = clima.temperatura !== null && clima.temperatura !== undefined
                ? Math.round(Number(clima.temperatura)) + "°C"
                : "--°C";

            this.setText("weather-icon", clima.icono || "🌤️");
            this.setText("weather-temp", temp);
            this.setText("weather-desc", clima.descripcion || "Tiempo actual");

        } catch (error) {
            console.warn("Clima no disponible:", error);
            this.renderClimaFallback();
        }
    },

    renderClimaFallback() {
        this.setText("weather-icon", "✨");
        this.setText("weather-temp", "--°C");
        this.setText("weather-desc", "Clima no disponible");
    },

    // ======================================================
    // MAPA
    // ======================================================

    async cargarGoogleMapsSiHayKey() {
        const mapa = document.getElementById("mapa-centro");
        if (!mapa) return;

        const apiKey = window.CONFIG &&
            window.CONFIG.GOOGLE_MAPS &&
            window.CONFIG.GOOGLE_MAPS.API_KEY;

        if (!apiKey || apiKey.includes("TU_API_KEY") || apiKey.trim() === "") {
            console.warn("Google Maps API key no configurada.");
            this.renderMapaFallback("Configura tu API key de Google Maps en config.local.js.");
            return;
        }

        if (window.google && window.google.maps) {
            this.inicializarMapa();
            return;
        }

        try {
            await new Promise((resolve, reject) => {
                const script = document.createElement("script");
                script.src = `https://maps.googleapis.com/maps/api/js?key=${encodeURIComponent(apiKey)}&libraries=places`;
                script.async = true;
                script.defer = true;

                script.onload = resolve;
                script.onerror = reject;

                document.head.appendChild(script);
            });

            console.log("✅ Google Maps API cargada correctamente");
            this.inicializarMapa();

        } catch (error) {
            console.error("❌ Error cargando Google Maps:", error);
            this.renderMapaFallback("Google Maps no pudo cargarse. Revisa la API key, APIs habilitadas y restricciones.");
        }
    },

    inicializarMapa() {
        const mapa = document.getElementById("mapa-centro");
        if (!mapa) return;

        try {
            if (window.MapaCentro && typeof window.MapaCentro.inicializar === "function") {
                window.MapaCentro.inicializar("mapa-centro");
                this.mapaInicializado = true;
                console.log("✅ MapaCentro inicializado desde mapa.js");
                return;
            }

            // Fallback si mapa.js no existe o no expone MapaCentro
            const centro = {
                lat: this.CENTRO.lat,
                lng: this.CENTRO.lng
            };

            this.googleMap = new google.maps.Map(mapa, {
                center: centro,
                zoom: 15,
                disableDefaultUI: false,
                mapTypeControl: false,
                streetViewControl: false,
                fullscreenControl: true
            });

            this.marcadorCentro = new google.maps.Marker({
                position: centro,
                map: this.googleMap,
                title: this.CENTRO.nombre
            });

            this.circuloRadio = new google.maps.Circle({
                strokeColor: "#34d399",
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillColor: "#34d399",
                fillOpacity: 0.18,
                map: this.googleMap,
                center: centro,
                radius: this.CENTRO.radioCheckinMetros
            });

            this.directionsService = new google.maps.DirectionsService();
            this.directionsRenderer = new google.maps.DirectionsRenderer({
                map: this.googleMap,
                suppressMarkers: false
            });

            this.mapaInicializado = true;
            console.log("✅ Mapa Google inicializado con fallback interno");

        } catch (error) {
            console.error("❌ Error inicializando mapa:", error);
            this.renderMapaFallback("No se pudo inicializar el mapa.");
        }
    },

    renderMapaFallback(mensaje) {
        const mapa = document.getElementById("mapa-centro");
        if (!mapa) return;

        mapa.innerHTML = `
            <div>
                <strong>🗺️ Mapa no disponible</strong>
                <p>${mensaje || "Puedes seguir usando la ubicación para calcular distancia."}</p>
            </div>
        `;
    },

    // ======================================================
    // BOTONES
    // ======================================================

    initBotones() {
        this.onClick("btn-reservar-turno", () => {
            console.log("Click reservar turno");
            this.mostrarToast("Próximamente podrás reservar turno desde aquí.");
        });

        this.onClick("btn-confirmar-llegada", () => {
            console.log("Click confirmar llegada hero");
            document.querySelector(".map-card")?.scrollIntoView({
                behavior: "smooth",
                block: "center"
            });

            const btnUbicacion = document.getElementById("btn-usar-ubicacion");
            if (btnUbicacion) {
                btnUbicacion.click();
            }
        });

        this.onClick("btn-usar-ubicacion", () => {
            console.log("Click usar ubicación");
            this.usarMiUbicacion();
        });

        this.onClick("btn-checkin", () => {
            console.log("Click check-in");
            this.confirmarLlegada();
        });

        this.onClick("btn-logout", () => {
            sessionStorage.clear();
            window.location.href = "login.html";
        });

        this.onClick("btn-personalizar-accesos", () => {
            this.mostrarToast("Personalización próximamente disponible.");
        });
    },

    initAccesosRapidos() {
        document.querySelectorAll(".quick-action").forEach(btn => {
            btn.addEventListener("click", () => {
                const action = btn.dataset.action;
                console.log("Click acceso rápido:", action);
                this.gestionarAccesoRapido(action);
            });
        });
    },

    gestionarAccesoRapido(action) {
        switch (action) {
            case "turnos":
                this.mostrarToast("Turnos: próximamente podrás reservar y consultar tus citas.");
                break;

            case "checkin":
                document.querySelector(".map-card")?.scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                });
                document.getElementById("btn-usar-ubicacion")?.click();
                break;

            case "matricula":
                this.mostrarToast("Matrícula: seguimiento próximamente disponible.");
                break;

            case "documentos":
                document.getElementById("documentos-pendientes-list")?.scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                });
                break;

            case "foro":
                this.mostrarToast("Foro académico próximamente disponible.");
                break;

            case "eventos":
                this.mostrarToast("Eventos próximamente disponible.");
                break;

            default:
                this.mostrarToast("Funcionalidad próximamente disponible.");
        }
    },

    initMenuLateral() {
        document.querySelectorAll(".menu-item").forEach(btn => {
            if (btn.classList.contains("active")) return;

            btn.addEventListener("click", () => {
                const action = btn.dataset.action;
                console.log("Click menú:", action);

                if (action === "checkin" || action === "mapa") {
                    document.querySelector(".map-card")?.scrollIntoView({
                        behavior: "smooth",
                        block: "center"
                    });
                    return;
                }

                if (action === "asistente") {
                    document.querySelector(".assistant-card")?.scrollIntoView({
                        behavior: "smooth",
                        block: "center"
                    });
                    return;
                }

                this.mostrarToast("Próximamente disponible.");
            });
        });

        document.querySelectorAll(".topbar-actions button").forEach(btn => {
            btn.addEventListener("click", () => {
                this.mostrarToast("Acción próximamente disponible.");
            });
        });

        document.querySelectorAll("[data-action='retos'], [data-action='calendario']").forEach(btn => {
            btn.addEventListener("click", () => {
                this.mostrarToast("Próximamente disponible.");
            });
        });
    },

    onClick(id, callback) {
        const element = document.getElementById(id);

        if (!element) {
            console.warn("No existe elemento con id:", id);
            return;
        }

        element.addEventListener("click", callback);
    },

    // ======================================================
    // UBICACIÓN / CHECK-IN
    // ======================================================

    async usarMiUbicacion() {
        if (!navigator.geolocation) {
            this.mostrarToast("Tu navegador no permite geolocalización.");
            return;
        }

        const btn = document.getElementById("btn-usar-ubicacion");
        const textoOriginal = btn ? btn.textContent : "";

        try {
            if (btn) {
                btn.disabled = true;
                btn.textContent = "Calculando...";
            }

            const position = await new Promise((resolve, reject) => {
                navigator.geolocation.getCurrentPosition(resolve, reject, {
                    enableHighAccuracy: true,
                    timeout: 12000,
                    maximumAge: 0
                });
            });

            const lat = position.coords.latitude;
            const lng = position.coords.longitude;

            this.ubicacionAlumno = { lat, lng };

            const distancia = this.calcularDistanciaMetros(
                lat,
                lng,
                this.CENTRO.lat,
                this.CENTRO.lng
            );

            console.log("Ubicación alumno:", this.ubicacionAlumno);
            console.log("Distancia al centro:", distancia);

            this.renderDistancia(distancia);
            this.actualizarMapaConUbicacion(lat, lng, distancia);
            this.actualizarBotonCheckin();

        } catch (error) {
            console.warn("No se pudo obtener ubicación:", error);
            this.mostrarToast("No se pudo obtener tu ubicación.");
            this.setHtml("distancia-centro", "No podemos calcular la distancia sin permiso de ubicación.");
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.textContent = textoOriginal || "Usar ubicación";
            }
        }
    },

    actualizarMapaConUbicacion(lat, lng, distancia) {
        if (window.MapaCentro && typeof window.MapaCentro.actualizarUbicacionAlumno === "function") {
            window.MapaCentro.actualizarUbicacionAlumno(lat, lng, distancia);
            return;
        }

        if (!this.googleMap || !window.google || !window.google.maps) {
            return;
        }

        const alumno = { lat, lng };
        const centro = {
            lat: this.CENTRO.lat,
            lng: this.CENTRO.lng
        };

        if (this.marcadorAlumno) {
            this.marcadorAlumno.setMap(null);
        }

        this.marcadorAlumno = new google.maps.Marker({
            position: alumno,
            map: this.googleMap,
            title: "Tu ubicación"
        });

        const bounds = new google.maps.LatLngBounds();
        bounds.extend(alumno);
        bounds.extend(centro);
        this.googleMap.fitBounds(bounds);

        if (this.directionsService && this.directionsRenderer) {
            this.directionsService.route(
                {
                    origin: alumno,
                    destination: centro,
                    travelMode: google.maps.TravelMode.WALKING
                },
                (result, status) => {
                    if (status === "OK") {
                        this.directionsRenderer.setDirections(result);
                    } else {
                        console.warn("No se pudo calcular ruta:", status);
                    }
                }
            );
        }
    },

    renderDistancia(distanciaMetros) {
        const distanciaTexto = distanciaMetros >= 1000
            ? (distanciaMetros / 1000).toFixed(2).replace(".", ",") + " km"
            : Math.round(distanciaMetros) + " m";

        const dentroRadio = distanciaMetros <= this.CENTRO.radioCheckinMetros;

        this.setHtml(
            "distancia-centro",
            dentroRadio
                ? `
                    <strong>📍 Estás a ${distanciaTexto} del centro.</strong><br>
                    ✅ Estás dentro del radio permitido. Ya puedes confirmar tu llegada.
                `
                : `
                    <strong>📍 Estás a ${distanciaTexto} del centro.</strong><br>
                    ⚠️ Estás fuera del radio permitido. Acércate al centro para poder hacer check-in.
                `
        );
    },

    actualizarBotonCheckin() {
        const btn = document.getElementById("btn-checkin");
        if (!btn) return;

        if (!this.proximoTurnoId) {
            btn.disabled = true;
            btn.textContent = "No hay turno para check-in";
            return;
        }

        if (!this.ubicacionAlumno) {
            btn.disabled = true;
            btn.textContent = "Activa tu ubicación primero";
            return;
        }

        btn.disabled = false;
        btn.textContent = "Confirmar llegada";
    },

    async confirmarLlegada() {
        if (!this.proximoTurnoId) {
            this.mostrarToast("No tienes un turno válido para hacer check-in.");
            return;
        }

        if (!this.ubicacionAlumno) {
            this.mostrarToast("Primero debes usar tu ubicación.");
            return;
        }

        try {
            await this.post(`/turnos/${this.proximoTurnoId}/checkin-geo`, {
                latitud: this.ubicacionAlumno.lat,
                longitud: this.ubicacionAlumno.lng
            });

            this.mostrarToast("Check-in realizado correctamente.");
            await this.cargarDatosBackend();

        } catch (error) {
            console.error("Error haciendo check-in:", error);
            this.mostrarToast("No se pudo confirmar la llegada. Revisa distancia o turno.");
        }
    },

    calcularDistanciaMetros(lat1, lon1, lat2, lon2) {
        const R = 6371000;
        const toRad = value => value * Math.PI / 180;

        const dLat = toRad(lat2 - lat1);
        const dLon = toRad(lon2 - lon1);

        const a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRad(lat1)) *
            Math.cos(toRad(lat2)) *
            Math.sin(dLon / 2) *
            Math.sin(dLon / 2);

        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    },

    // ======================================================
    // RENDERS
    // ======================================================

    renderProximoTurno(turno) {
        const container = document.getElementById("proximo-turno-container");
        if (!container) return;

        if (!turno) {
            container.innerHTML = `
                <div class="empty-state">
                    <strong>No tienes ningún turno próximo.</strong>
                    <p>Cuando reserves uno, aparecerá aquí.</p>
                </div>
            `;
            return;
        }

        const numero = turno.numeroTurno || turno.codigo || turno.id || turno.idReserva || "--";
        const estado = turno.estadoTurno || turno.estado || "Pendiente";
        const fecha = this.formatearFecha(turno.fechaCita || turno.fecha || turno.fechaHora);

        container.innerHTML = `
            <div class="turno-destacado">
                <strong>Turno ${numero}</strong>
                <p>${fecha}</p>
                <span>${estado}</span>
            </div>
        `;
    },

    renderTurnosActivos(turnos) {
        const container = document.getElementById("turnos-activos-list");
        if (!container) return;

        if (!Array.isArray(turnos) || turnos.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <strong>No tienes turnos activos.</strong>
                    <p>Reserva un turno cuando necesites atención en secretaría.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = turnos.map(turno => `
            <div class="list-item-premium">
                <span>📅</span>
                <div>
                    <strong>Turno ${turno.numeroTurno || turno.id || "--"}</strong>
                    <p>${this.formatearFecha(turno.fechaCita || turno.fechaHora || turno.fecha)}</p>
                </div>
                <b>${turno.estadoTurno || turno.estado || "Activo"}</b>
            </div>
        `).join("");
    },

    renderDocumentosPendientes(documentos) {
        const container = document.getElementById("documentos-pendientes-list");
        if (!container) return;

        if (!Array.isArray(documentos) || documentos.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <strong>No tienes documentos pendientes.</strong>
                    <p>Todo está al día.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = documentos.map(doc => `
            <div class="list-item-premium">
                <span>📄</span>
                <div>
                    <strong>${this.escapeHtml(doc.nombreDocumento || doc.nombre || "Documento")}</strong>
                    <p>${this.escapeHtml(doc.estadoDocumento || doc.estado || "Pendiente")}</p>
                </div>
                <b>Pendiente</b>
            </div>
        `).join("");
    },

    renderAsistente(recomendaciones) {
        const container = document.getElementById("asistente-list");
        if (!container) return;

        if (!Array.isArray(recomendaciones) || recomendaciones.length === 0) {
            container.innerHTML = `
                <div class="assistant-item">
                    <span>✅</span>
                    <div>
                        <strong>Todo al día</strong>
                        <p>No tienes recomendaciones pendientes.</p>
                    </div>
                    <b>›</b>
                </div>
            `;
            return;
        }

        container.innerHTML = recomendaciones.slice(0, 4).map(rec => `
            <div class="assistant-item">
                <span>${this.iconoRecomendacion(rec.tipo)}</span>
                <div>
                    <strong>${this.escapeHtml(rec.titulo || "Recomendación")}</strong>
                    <p>${this.escapeHtml(rec.mensaje || "")}</p>
                </div>
                <b>›</b>
            </div>
        `).join("");
    },

    iconoRecomendacion(tipo) {
        const t = String(tipo || "").toUpperCase();

        if (t.includes("URGENTE")) return "🚨";
        if (t.includes("AVISO")) return "⚠️";
        if (t.includes("EXITO") || t.includes("ÉXITO")) return "✅";
        if (t.includes("INFO")) return "💡";

        return "🤖";
    },

    initReloj() {
        const update = () => {
            const now = new Date();

            this.setText("current-time", now.toLocaleTimeString("es-ES", {
                hour: "2-digit",
                minute: "2-digit"
            }));

            const fecha = now.toLocaleDateString("es-ES", {
                weekday: "long",
                day: "numeric",
                month: "long"
            });

            this.setText("current-date", this.capitalize(fecha));
        };

        update();
        setInterval(update, 60000);
    },

    // ======================================================
    // HELPERS
    // ======================================================

    calcularProgresoPrematricula(estado) {
        const e = String(estado || "").toLowerCase();

        if (e.includes("validada") || e.includes("completada") || e.includes("finalizada")) return 100;
        if (e.includes("validación") || e.includes("validacion") || e.includes("revisión") || e.includes("revision")) return 75;
        if (e.includes("proceso") || e.includes("pendiente")) return 50;
        if (e.includes("iniciada") || e.includes("creada")) return 25;

        return 0;
    },

    setBar(id, value, max) {
        const element = document.getElementById(id);
        if (!element) return;

        const percent = Math.min(100, Math.round(((Number(value) || 0) / (Number(max) || 1)) * 100));
        element.style.width = percent + "%";
    },

    setCircleProgress(percent) {
        const circle = document.getElementById("circle-progress");
        if (!circle) return;

        const p = Math.max(0, Math.min(100, Number(percent) || 0));

        circle.style.background = `
            radial-gradient(circle at center, #eef4ff 58%, transparent 59%),
            conic-gradient(#3fcf91 0 ${p}%, #d8e0ef ${p}% 100%)
        `;
    },

    setText(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    },

    setHtml(id, value) {
        const element = document.getElementById(id);
        if (element) {
            element.innerHTML = value;
        }
    },

    formatearFecha(value) {
        if (!value) return "Fecha no disponible";

        const date = new Date(value);

        if (isNaN(date.getTime())) {
            return String(value);
        }

        return date.toLocaleString("es-ES", {
            day: "2-digit",
            month: "short",
            hour: "2-digit",
            minute: "2-digit"
        });
    },

    capitalize(text) {
        if (!text) return "";
        return text.charAt(0).toUpperCase() + text.slice(1);
    },

    escapeHtml(text) {
        return String(text)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    },

    mostrarToast(mensaje) {
        let toast = document.getElementById("premium-toast");

        if (!toast) {
            toast = document.createElement("div");
            toast.id = "premium-toast";
            toast.style.position = "fixed";
            toast.style.right = "28px";
            toast.style.bottom = "28px";
            toast.style.padding = "15px 20px";
            toast.style.borderRadius = "16px";
            toast.style.background = "rgba(20, 31, 55, 0.94)";
            toast.style.color = "white";
            toast.style.boxShadow = "0 18px 45px rgba(0,0,0,.28)";
            toast.style.zIndex = "9999";
            toast.style.backdropFilter = "blur(16px)";
            toast.style.transition = "opacity .25s ease, transform .25s ease";
            document.body.appendChild(toast);
        }

        toast.textContent = mensaje;
        toast.style.opacity = "1";
        toast.style.transform = "translateY(0)";

        clearTimeout(this.toastTimeout);

        this.toastTimeout = setTimeout(() => {
            toast.style.opacity = "0";
            toast.style.transform = "translateY(10px)";
        }, 2600);
    }
};

window.PanelAlumnoPremium = PanelAlumnoPremium;

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () => PanelAlumnoPremium.init());
} else {
    PanelAlumnoPremium.init();
}