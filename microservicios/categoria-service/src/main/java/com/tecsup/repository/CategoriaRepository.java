package com.tecsup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
