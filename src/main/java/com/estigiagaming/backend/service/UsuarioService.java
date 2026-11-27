package com.estigiagaming.backend.service;

import com.estigiagaming.backend.model.Usuario;
import com.estigiagaming.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        if (usuario.getRun() != null && usuarioRepository.existsByRun(usuario.getRun())) {
            throw new RuntimeException("El RUN ya está registrado.");
        }
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("CLIENTE");
        } else {
            usuario.setRol(normalizarRol(usuario.getRol()));
        }
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public String normalizarRol(String rol) {
        String limpio = rol.trim().toUpperCase();
        if (limpio.startsWith("ADMIN")) return "ADMINISTRADOR";
        if (limpio.startsWith("VEND")) return "VENDEDOR";
        return "CLIENTE";
    }
}