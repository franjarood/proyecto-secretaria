package es.iesdeteis.secretaria.controller;

import es.iesdeteis.secretaria.dto.LoginRequestDto;
import es.iesdeteis.secretaria.dto.LoginResponseDto;
import es.iesdeteis.secretaria.service.AuthService;
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


    // LOGIN

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) {
        return authService.login(loginRequest);
    }
}