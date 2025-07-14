package br.com.sisaudcon.saam.saam_sped_cnd.domain.repository;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    Optional<Empresa> findByIdEmpresa(String idEmpresa);
}
