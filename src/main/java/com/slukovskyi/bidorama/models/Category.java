package com.slukovskyi.bidorama.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Category {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name")
    private String name;

    @Column(name = "category_image_filename")
    private String imageFilename;

    @OneToMany(mappedBy="category")
    private List<Product> products;

}
