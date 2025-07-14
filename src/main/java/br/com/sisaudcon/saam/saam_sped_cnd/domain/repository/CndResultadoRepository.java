package br.com.sisaudcon.saam.saam_sped_cnd.domain.repository;

import br.com.sisaudcon.saam.saam_sped_cnd.domain.model.CndResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CndResultadoRepository extends JpaRepository<CndResultado, Long> {

    boolean existsByCliente_Id(Integer clienteId);
    List<CndResultado> findByStatusAndSituacaoIsNull(String status);
}