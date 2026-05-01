package es.iesdeteis.secretaria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Turno no encontrado
    @ExceptionHandler(TurnoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleTurnoNoEncontrado(TurnoNoEncontradoException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Turno no encontrado");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Reserva no encontrada
    @ExceptionHandler(ReservaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleReservaNoEncontrada(ReservaNoEncontradaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Reserva no encontrada");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Usuario no encontrado
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Usuario no encontrado");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Usuario duplicado
    @ExceptionHandler(UsuarioDuplicadoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleUsuarioDuplicado(UsuarioDuplicadoException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Usuario duplicado");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Error genérico
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneral(Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Error interno");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Error de validación
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, Object> error = new HashMap<>();
        Map<String, String> detalles = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                detalles.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Error de validación");
        error.put("detalles", detalles);

        return error;
    }

    @ExceptionHandler(ReservaYaProcesadaException.class)
    public ResponseEntity<Map<String, Object>> handleReservaYaProcesada(ReservaYaProcesadaException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Reserva ya procesada");
        response.put("mensaje", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Estado de turno inválido
    @ExceptionHandler(EstadoTurnoInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleEstadoTurnoInvalido(EstadoTurnoInvalidoException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Estado de turno inválido");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Incidencia no encontrada
    @ExceptionHandler(IncidenciaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleIncidenciaNoEncontrada(IncidenciaNoEncontradaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Incidencia no encontrada");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Historial no encontrado
    @ExceptionHandler(HistorialAccionNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleHistorialNoEncontrado(HistorialAccionNoEncontradaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Historial no encontrado");
        error.put("mensaje", ex.getMessage());

        return error;
    }


    // PreMatricula no encontrada
    @ExceptionHandler(PreMatriculaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handlePreMatriculaNoEncontrada(PreMatriculaNoEncontradaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "PreMatricula no encontrada");
        error.put("status", 404);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // PreMatricula duplicada
    @ExceptionHandler(PreMatriculaDuplicadaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handlePreMatriculaDuplicada(PreMatriculaDuplicadaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "PreMatricula duplicada");
        error.put("status", 400);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // Documento no encontrado
    @ExceptionHandler(DocumentoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleDocumentoNoEncontrado(DocumentoNoEncontradoException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "Documento no encontrado");
        error.put("status", 404);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // Documento no pertenece al usuario
    @ExceptionHandler(DocumentoNoPerteneceUsuarioException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleDocumentoNoPerteneceUsuario(DocumentoNoPerteneceUsuarioException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "Acceso denegado al documento");
        error.put("status", 403);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // Documento no revisable
    @ExceptionHandler(DocumentoNoRevisableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleDocumentoNoRevisable(DocumentoNoRevisableException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "Documento no se puede revisar");
        error.put("status", 400);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // Notificación no encontrada
    @ExceptionHandler(NotificacionNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotificacionNoEncontrada(NotificacionNoEncontradaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "Notificación no encontrada");
        error.put("status", 404);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // Notificación no pertenece al usuario
    @ExceptionHandler(NotificacionNoPerteneceUsuarioException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleNotificacionNoPerteneceUsuario(NotificacionNoPerteneceUsuarioException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "Acceso denegado a la notificación");
        error.put("status", 403);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }


    // Ventana de confirmación inválida
    @ExceptionHandler(VentanaConfirmacionInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleVentanaConfirmacionInvalida(VentanaConfirmacionInvalidaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        error.put("error", "Ventana de confirmación inválida");
        error.put("status", 400);
        error.put("timestamp", LocalDateTime.now());

        return error;
    }

    // Usuario fuera del centro (check-in geo)
    @ExceptionHandler(UsuarioFueraDelCentroException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleUsuarioFueraDelCentro(UsuarioFueraDelCentroException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("error", "Usuario fuera del centro");
        error.put("mensaje", ex.getMessage());

        return error;
    }

    // Ubicación no válida (precisión mala o datos inválidos)
    @ExceptionHandler(UbicacionNoValidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleUbicacionNoValida(UbicacionNoValidaException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Ubicación no válida");
        error.put("mensaje", ex.getMessage());

        return error;
    }

}