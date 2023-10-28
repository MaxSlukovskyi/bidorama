package com.slukovskyi.bidorama.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String name;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "product_image_filenames", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "product_image_filename", nullable = false)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> imageFilenames = new ArrayList<>();

    @Column(name = "product_description")
    private String description;

    @Column(name = "product_creation_time")
    private Timestamp creationTime;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User author;

    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)
    private Category category;

    @OneToOne(mappedBy = "product")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Auction auction;
}
