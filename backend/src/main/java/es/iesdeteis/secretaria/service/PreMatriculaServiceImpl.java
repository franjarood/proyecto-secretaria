package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.exception.PreMatriculaDuplicadaException;
import es.iesdeteis.secretaria.exception.PreMatriculaNoEncontradaException;
import es.iesdeteis.secretaria.exception.UsuarioNoEncontradoException;
import es.iesdeteis.secretaria.model.*;
import es.iesdeteis.secretaria.repository.PreMatriculaRepository;
import es.iesdeteis.secretaria.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreMatriculaServiceImpl implements PreMatriculaService {

    // =========================
    // ATRIBUTOS
    // =========================

    private final PreMatriculaRepository preMatriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;


    // =========================
    // CONSTRUCTOR
    // =========================

    public PreMatriculaServiceImpl(PreMatriculaRepository preMatriculaRepository,
                                   UsuarioRepository usuarioRepository,
                                   NotificacionService notificacionService) {
        this.preMatriculaRepository = preMatriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }


    // =========================
    // MÉTODOS PRINCIPALES
    // =========================

    @Override
    public List<PreMatricula> findAll() {
        return preMatriculaRepository.findAll();
    }

    @Override
    public Optional<PreMatricula> findById(Long id) {
        return preMatriculaRepository.findById(id);
    }

    @Override
    public PreMatricula save(PreMatricula preMatricula) {

        // Validación: evitar duplicados por DNI
        if (preMatriculaRepository.findByDniAlumno(preMatricula.getDniAlumno()).isPresent()) {
            throw new PreMatriculaDuplicadaException("Ya existe una prematrícula con ese DNI");
        }

        // 🔐 Obtener usuario autenticado
        Usuario usuario = obtenerUsuarioAutenticado();

        // Asociar usuario
        preMatricula.setUsuario(usuario);

        PreMatricula preMatriculaGuardada = preMatriculaRepository.save(preMatricula);

        // 🔔 Notificación
        notificacionService.crearNotificacionInterna(
                "Prematrícula creada",
                "Tu prematrícula se ha registrado correctamente.",
                TipoNotificacion.PREMATRICULA_CREADA,
                "PREMATRICULA_" + preMatriculaGuardada.getId(),
                "/prematriculas",
                usuario
        );

        return preMatriculaGuardada;
    }

    @Override
    public PreMatricula update(Long id, PreMatricula preMatriculaActualizada) {

        PreMatricula preMatricula = preMatriculaRepository.findById(id)
                .orElseThrow(() -> new PreMatriculaNoEncontradaException("PreMatricula no encontrada"));

        preMatricula.setNombreAlumno(preMatriculaActualizada.getNombreAlumno());
        preMatricula.setApellidosAlumno(preMatriculaActualizada.getApellidosAlumno());
        preMatricula.setDniAlumno(preMatriculaActualizada.getDniAlumno());
        preMatricula.setEmailAlumno(preMatriculaActualizada.getEmailAlumno());
        preMatricula.setTelefonoAlumno(preMatriculaActualizada.getTelefonoAlumno());
        preMatricula.setCicloSolicitado(preMatriculaActualizada.getCicloSolicitado());
        preMatricula.setCursoSolicitado(preMatriculaActualizada.getCursoSolicitado());
        preMatricula.setModalidad(preMatriculaActualizada.getModalidad());
        preMatricula.setObservaciones(preMatriculaActualizada.getObservaciones());

        return preMatriculaRepository.save(preMatricula);
    }

    @Override
    public void deleteById(Long id) {

        if (!preMatriculaRepository.existsById(id)) {
            throw new PreMatriculaNoEncontradaException("PreMatricula no encontrada");
        }

        preMatriculaRepository.deleteById(id);
    }

    @Override
    public PreMatricula cambiarEstado(Long id, String estado) {

        PreMatricula preMatricula = preMatriculaRepository.findById(id)
                .orElseThrow(() -> new PreMatriculaNoEncontradaException("PreMatricula no encontrada"));

        EstadoPreMatricula nuevoEstado = EstadoPreMatricula.valueOf(estado.toUpperCase());

        preMatricula.setEstado(nuevoEstado);

        PreMatricula guardada = preMatriculaRepository.save(preMatricula);

        // 🔔 Notificación según estado REAL
        if (nuevoEstado == EstadoPreMatricula.EN_REVISION) {

            notificacionService.crearNotificacionInterna(
                    "Prematrícula en revisión",
                    "Tu prematrícula está siendo revisada por el centro.",
                    TipoNotificacion.PREMATRICULA_EN_REVISION,
                    "PREMATRICULA_" + guardada.getId(),
                    "/prematriculas",
                    guardada.getUsuario()
            );

        } else if (nuevoEstado == EstadoPreMatricula.VALIDADA) {

            notificacionService.crearNotificacionInterna(
                    "Prematrícula validada",
                    "Tu prematrícula ha sido validada correctamente.",
                    TipoNotificacion.PREMATRICULA_ACEPTADA, // esto está bien en el enum de notificaciones
                    "PREMATRICULA_" + guardada.getId(),
                    "/prematriculas",
                    guardada.getUsuario()
            );

            // 🎓 Convertir USUARIO → ALUMNO al validar matrícula
            convertirUsuarioEnAlumnoSiProcede(guardada);

        } else if (nuevoEstado == EstadoPreMatricula.RECHAZADA) {

            notificacionService.crearNotificacionInterna(
                    "Prematrícula rechazada",
                    "Tu prematrícula ha sido rechazada. Revisa la información indicada.",
                    TipoNotificacion.PREMATRICULA_RECHAZADA,
                    "PREMATRICULA_" + guardada.getId(),
                    "/prematriculas",
                    guardada.getUsuario()
            );
        }

        return guardada;
    }


    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    private void convertirUsuarioEnAlumnoSiProcede(PreMatricula matricula) {
        Usuario usuario = matricula.getUsuario();

        if (usuario != null && usuario.getRol() == RolUsuario.USUARIO) {
            usuario.setRol(RolUsuario.ALUMNO);
            usuarioRepository.save(usuario);
        }
    }
}