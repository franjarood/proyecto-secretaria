const API_URL = "http://localhost:9001";

console.log("JS cargado correctamente");

document.addEventListener("DOMContentLoaded", () => {
    cargarTiposTramite();

    const formReserva = document.getElementById("formReserva");
    formReserva.addEventListener("submit", crearReserva);
});

function cargarTiposTramite() {
    fetch(`${API_URL}/tipos-tramite`)
        .then(response => response.json())
        .then(tipos => {
            const select = document.getElementById("tipoTramite");
            select.innerHTML = '<option value="">Selecciona un trámite</option>';

            tipos.forEach(tipo => {
                const option = document.createElement("option");
                option.value = tipo.id;
                option.textContent = tipo.nombre;
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error("Error al cargar tipos de trámite:", error);
        });
}

function crearReserva(event) {
    event.preventDefault();

    const reserva = {
        nombre: document.getElementById("nombre").value,
        apellidos: document.getElementById("apellidos").value,
        email: document.getElementById("email").value,
        fechaCita: document.getElementById("fechaCita").value,
        horaCita: document.getElementById("horaCita").value,
        tipoTramiteId: document.getElementById("tipoTramite").value,
        observaciones: document.getElementById("observaciones").value
    };

    fetch(`${API_URL}/reservas`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Basic " + btoa("admin@test.com:1234")
        },
        body: JSON.stringify(reserva)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("No se pudo crear la reserva");
            }

            return response.json();
        })
        .then(data => {
            const mensaje = document.getElementById("mensajeReserva");
            mensaje.textContent = "Reserva creada correctamente.";
            mensaje.className = "mensaje correcto";

            document.getElementById("formReserva").reset();
        })
        .catch(error => {
            const mensaje = document.getElementById("mensajeReserva");
            mensaje.textContent = "Error al crear la reserva. Revisa los datos.";
            mensaje.className = "mensaje error";

            console.error(error);
        });
}

function volverInicio() {
    window.location.href = "../index.html";
}