package com.minimarket.minimarket_nilda_api.service;

import com.minimarket.minimarket_nilda_api.dto.UsuarioRequestDTO;
import com.minimarket.minimarket_nilda_api.entity.Usuario;
import com.minimarket.minimarket_nilda_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Transactional
    public Usuario crear(UsuarioRequestDTO dto) {
        String usernameNormalizado = dto.getUsername().trim();
        if (usuarioRepository.existsByUsername(usernameNormalizado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre().trim());
        usuario.setUsername(usernameNormalizado);
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario existente = obtenerPorId(id);
        String usernameNormalizado = dto.getUsername().trim();

        if (!existente.getUsername().equals(usernameNormalizado)
                && usuarioRepository.existsByUsername(usernameNormalizado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }

        existente.setNombre(dto.getNombre().trim());
        existente.setUsername(usernameNormalizado);
        existente.setRol(dto.getRol());
        if (dto.getActivo() != null) {
            existente.setActivo(dto.getActivo());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existente.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuarioRepository.delete(usuario);
    }
}