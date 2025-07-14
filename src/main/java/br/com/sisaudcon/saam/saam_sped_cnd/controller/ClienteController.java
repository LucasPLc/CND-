package br.com.sisaudcon.saam.saam_sped_cnd.controller;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.exception.ClienteNotFoundException;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Cliente;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.repository.ClienteRepository;
import br.com.sisaudcon.saam.saam_sped_cnd.domain.service.RegistroClienteService;
import br.com.sisaudcon.saam.saam_sped_cnd.dto.ClienteDTO;
import br.com.sisaudcon.saam.saam_sped_cnd.mapper.ClienteMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("clientes")
public class ClienteController {

    private final RegistroClienteService registroClienteService;
    private final ClienteRepository clienteRepository;

    @GetMapping
    public List<ClienteDTO> listar() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(ClienteMapper::toDTO)
                .toList();
    }
    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> buscar(@PathVariable Integer clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado para o ID informado."));
        return ResponseEntity.ok(ClienteMapper.toDTO(cliente));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDTO cadastrar(@RequestBody @Valid ClienteDTO clienteDTO) {
        Cliente cliente = ClienteMapper.toEntity(clienteDTO);
        Cliente clienteSalvo = registroClienteService.salvarClienteComEmpresa(cliente);
        return ClienteMapper.toDTO(clienteSalvo);
    }

    @PutMapping("/{clienteId}")
    public ClienteDTO atualizar(@PathVariable Integer clienteId,
                                @RequestBody @Valid ClienteDTO clienteDTO) {
        // se não existir, lança a exceção
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNotFoundException("Cliente não encontrado para o ID informado.");
        }
        Cliente cliente = ClienteMapper.toEntity(clienteDTO);
        cliente.setId(clienteId);
        Cliente atualizado = registroClienteService.salvarClienteComEmpresa(cliente);
        return ClienteMapper.toDTO(atualizado);
    }
    @DeleteMapping("/{clienteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Integer clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNotFoundException("Cliente não encontrado para o ID informado.");
        }
        registroClienteService.excluir(clienteId);
    }


}