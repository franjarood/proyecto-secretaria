package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.DashboardAdminDTO;
import es.iesdeteis.secretaria.dto.DashboardAlumnoDTO;
import es.iesdeteis.secretaria.dto.DashboardKioskoDTO;
import es.iesdeteis.secretaria.dto.DashboardSecretariaDTO;
import es.iesdeteis.secretaria.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE', 'ALUMNO')")
    @GetMapping("/alumno/{usuarioId}")
    public DashboardAlumnoDTO dashboardAlumno(@PathVariable Long usuarioId) {
        return dashboardService.obtenerDashboardAlumno(usuarioId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'PROFESOR')")
    @GetMapping("/secretaria")
    public DashboardSecretariaDTO dashboardSecretaria() {
        return dashboardService.obtenerDashboardSecretaria();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public DashboardAdminDTO dashboardAdmin() {
        return dashboardService.obtenerDashboardAdmin();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARIA', 'CONSERJE')")
    @GetMapping("/kiosko")
    public DashboardKioskoDTO dashboardKiosko() {
        return dashboardService.obtenerDashboardKiosko();
    }
}

