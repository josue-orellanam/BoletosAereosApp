package com.api.boletos.controller;

import com.api.boletos.dto.AuthRequest;
import com.api.boletos.model.Usuario;
import com.api.boletos.repository.UsuarioRepository;
import com.api.boletos.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Ruta para registrar un nuevo usuario en la base de datos
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        // Verificamos si el correo ya existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: El correo ya está registrado");
        }

        // Creamos el usuario y encriptamos la contraseña
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok("Usuario registrado con éxito en la base de datos");
    }

    // Ruta para iniciar sesión y obtener el Token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Buscamos al usuario por correo
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparamos la contraseña enviada con la encriptada en la base de datos
            if (passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                // Si todo es correcto, generamos el Token
                String token = jwtUtil.generateToken(usuario.getEmail());

                // Preparamos la respuesta en formato JSON
                Map<String, String> response = new HashMap<>();
                response.put("token", token);

                return ResponseEntity.ok(response);
            }
        }

        // Si falla el correo o la contraseña
        return ResponseEntity.status(401).body("Error: Credenciales incorrectas");
    }
}