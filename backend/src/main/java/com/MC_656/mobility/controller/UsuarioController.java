package com.mc_656.mobility.controller;

import com.mc_656.mobility.dto.CadastroUsuarioRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cadastro")
public class UsuarioController {

    // Simulando base para duplicidade
    private static final Map<String, String> emailsExistentes = new HashMap<>();
    private static final Map<String, String> telefonesExistentes = new HashMap<>();

    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@Valid @RequestBody CadastroUsuarioRequest request, BindingResult result) {

        // Validações automáticas de anotação
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().get(0).getDefaultMessage());
        }

        // Validar senhas iguais
        if (!request.getSenha().equals(request.getConfirmacaoSenha())) {
            return ResponseEntity.badRequest().body("Senha e confirmação não coincidem");
        }

        // Validar formato senha (letras e números)
        if (!request.getSenha().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            return ResponseEntity.badRequest().body("Senha deve conter letras e números");
        }

        // Verificar duplicidade (simulada)
        if (emailsExistentes.containsKey(request.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("errorCode", "EMAIL_DUPLICADO");
            response.put("message", "Email já cadastrado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (telefonesExistentes.containsKey(request.getTelefone())) {
            Map<String, String> response = new HashMap<>();
            response.put("errorCode", "TELEFONE_DUPLICADO");
            response.put("message", "Telefone já cadastrado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Criar hash da senha
        String senhaHash = BCrypt.hashpw(request.getSenha(), BCrypt.gensalt());

        // Simular salvar no banco (adicionar ao Map)
        emailsExistentes.put(request.getEmail(), senhaHash);
        telefonesExistentes.put(request.getTelefone(), request.getEmail());

        // Simular geração de token de sessão (pode usar JWT futuramente)
        String tokenSimulado = "token-sessao-exemplo-" + System.currentTimeMillis();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuário criado com sucesso");
        response.put("token", tokenSimulado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
