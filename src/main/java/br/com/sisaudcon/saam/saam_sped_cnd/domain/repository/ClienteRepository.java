package br.com.sisaudcon.saam.saam_sped_cnd.domain.repository;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByCnpj(String cnpj);
    Optional<Cliente> findByCnpjAndEmpresa_IdEmpresa(String cnpj, String idEmpresa);
    Optional<Cliente> findByCnpjAndEmpresa_IdEmpresaAndIdNot(String cnpj, String idEmpresa, Integer id);
}