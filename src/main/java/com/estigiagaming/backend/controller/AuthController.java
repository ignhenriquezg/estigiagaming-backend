package com.estigiagaming.backend.controller;

import com.estigiagaming.backend.model.Usuario;
import com.estigiagaming.backend.repository.UsuarioRepository;
import com.estigiagaming.backend.security.JwtUtil;
import com.estigiagaming.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Inicio de sesión y registro con emisión de token JWT")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository,
                          UsuarioService usuarioService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String clave = credenciales.get("clave");

        if (correo == null || clave == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debe ingresar correo y clave."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoIgnoreCase(correo.trim());
        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(clave, usuarioOpt.get().getClave())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Correo o contraseña incorrectos."));
        }

        Usuario usuario = usuarioOpt.get();
        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "usuario", usuario
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo cliente y obtener token JWT")
    public ResponseEntity<?> register(@RequestBody Usuario nuevoUsuario) {
        try {
            nuevoUsuario.setRol("CLIENTE");
            Usuario guardado = usuarioService.registrar(nuevoUsuario);
            String token = jwtUtil.generarToken(guardado.getCorreo(), guardado.getRol());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "token", token,
                    "usuario", guardado
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}