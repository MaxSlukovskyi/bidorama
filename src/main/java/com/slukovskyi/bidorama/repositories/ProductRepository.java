package com.slukovskyi.bidorama.repositories;

import com.slukovskyi.bidorama.models.Product;
import com.slukovskyi.bidorama.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> getAllByCategory_Id(Long categoryId);

    List<Product> getAllByAuthor(User user);

}
