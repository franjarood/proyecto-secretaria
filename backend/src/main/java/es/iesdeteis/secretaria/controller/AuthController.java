package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.LoginRequestDto;
import es.iesdeteis.secretaria.dto.LoginResponseDto;
import es.iesdeteis.secretaria.dto.RegisterRequestDTO;
import es.iesdeteis.secretaria.dto.RegisterResponseDTO;
import es.iesdeteis.secretaria.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // ATRIBUTOS

    private final AuthService authService;


    // CONSTRUCTOR

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // REGISTRO PÚBLICO

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        return authService.register(registerRequest);
    }


    // LOGIN

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) {
        return authService.login(loginRequest);
    }
}