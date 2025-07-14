package br.com.sisaudcon.saam.saam_sped_cnd.exceptionhandler;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleClienteNotFoundException(ClienteNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(EmpresaNotFoundException.class)
    public ResponseEntity<Object> handleEmpresaNotFound(EmpresaNotFoundException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", "Empresa não encontrada no SAAM para o ID informado.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put("error", error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Falta de fk_empresa (campo obrigatório) retorna 400 BAD REQUEST
    @ExceptionHandler(EmpresaVinculoObrigatorioException.class)
    public ResponseEntity<Object> handleEmpresaVinculoObrigatorio(EmpresaVinculoObrigatorioException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Exclusão proibida por resultados vinculados 400 Bad Request
    @ExceptionHandler(ClienteVinculadoResultadoException.class)
    public ResponseEntity<Object> handleClienteVinculadoResultado(ClienteVinculadoResultadoException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Violação de chave única ou integridade referencial.");

        return new ResponseEntity<>(body, HttpStatus.CONFLICT); // 409 Conflict
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,String>> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Map<String,String> body = new HashMap<>();
        body.put("error", "Campo booleano inválido ou ausente.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }
    // captura tudo que não foi tratado e devolve um 500:
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleAllUncaught(Exception ex) {
        Map<String,String> body = new HashMap<>();
        body.put("error", "Erro interno no servidor. Tente novamente mais tarde.");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
    @ExceptionHandler(ResultadoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResultadoNotFound(ResultadoNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    @ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<Map<String,String>> handleDuplicado(
            ClienteDuplicadoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<Map<String,String>> handleAcessoNegado(AcessoNegadoException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acesso negado. Cliente sem autorização ativa."));
    }
    @ExceptionHandler(ClienteIdInvalidoException.class)
    public ResponseEntity<Map<String,String>> handleIdInvalido(ClienteIdInvalidoException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    // 403 Forbidden — status ≠ 1
    @ExceptionHandler(ClienteNaoAutorizadoException.class)
    public ResponseEntity<Map<String,String>> handleNaoAutorizado(ClienteNaoAutorizadoException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    // 503 Service Unavailable — timeout, 5xx ou falha de comunicação
    @ExceptionHandler(ServicoValidacaoIndisponivelException.class)
    public ResponseEntity<Map<String,String>> handleIndisponivel(ServicoValidacaoIndisponivelException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", ex.getMessage()));
    }

    // 500 Internal Server Error — demais falhas
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<Map<String,String>> handleInternal(InternalServerErrorException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro interno no servidor. Tente novamente mais tarde."));
    }

}