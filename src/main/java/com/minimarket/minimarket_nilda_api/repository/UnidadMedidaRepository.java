package com.minimarket.minimarket_nilda_api.repository;

import com.minimarket.minimarket_nilda_api.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
    List<UnidadMedida> findByActivoTrue();
}