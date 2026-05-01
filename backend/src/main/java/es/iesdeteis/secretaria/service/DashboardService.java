package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.DashboardAdminDTO;
import es.iesdeteis.secretaria.dto.DashboardAlumnoDTO;
import es.iesdeteis.secretaria.dto.DashboardKioskoDTO;
import es.iesdeteis.secretaria.dto.DashboardSecretariaDTO;

public interface DashboardService {

    DashboardAlumnoDTO obtenerDashboardAlumno(Long usuarioId);

    DashboardSecretariaDTO obtenerDashboardSecretaria();

    DashboardAdminDTO obtenerDashboardAdmin();

    DashboardKioskoDTO obtenerDashboardKiosko();
}

