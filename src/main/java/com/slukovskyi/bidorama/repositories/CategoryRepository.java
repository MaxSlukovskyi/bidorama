package com.slukovskyi.bidorama.repositories;

import com.slukovskyi.bidorama.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
