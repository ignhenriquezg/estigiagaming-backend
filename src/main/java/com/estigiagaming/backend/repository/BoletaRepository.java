package com.estigiagaming.backend.repository;

import com.estigiagaming.backend.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {
}