package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.LoginRequestDto;
import es.iesdeteis.secretaria.dto.LoginResponseDto;
import es.iesdeteis.secretaria.dto.RegisterRequestDTO;
import es.iesdeteis.secretaria.dto.RegisterResponseDTO;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequest);

    RegisterResponseDTO register(RegisterRequestDTO registerRequest);
}