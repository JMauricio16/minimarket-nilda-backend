package com.minimarket.minimarket_nilda_api.repository;

import com.minimarket.minimarket_nilda_api.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}