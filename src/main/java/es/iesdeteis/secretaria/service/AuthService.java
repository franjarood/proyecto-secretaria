package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.LoginRequestDto;
import es.iesdeteis.secretaria.dto.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequest);
}