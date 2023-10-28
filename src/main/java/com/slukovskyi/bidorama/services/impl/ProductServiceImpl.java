package com.slukovskyi.bidorama.services.impl;

import com.slukovskyi.bidorama.dtos.ProductRequestDto;
import com.slukovskyi.bidorama.dtos.ProductResponseDto;
import com.slukovskyi.bidorama.mappers.ProductRequestDtoMapper;
import com.slukovskyi.bidorama.mappers.ProductResponseDtoMapper;
import com.slukovskyi.bidorama.models.Category;
import com.slukovskyi.bidorama.models.Product;
import com.slukovskyi.bidorama.models.User;
import com.slukovskyi.bidorama.models.enums.AuctionStatus;
import com.slukovskyi.bidorama.repositories.CategoryRepository;
import com.slukovskyi.bidorama.repositories.ProductRepository;
import com.slukovskyi.bidorama.services.AuctionService;
import com.slukovskyi.bidorama.services.ProductService;
import com.slukovskyi.bidorama.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Value("${upload.path}")
    private String uploadPath;

    private final UserService userService;
    private final AuctionService auctionService;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRequestDtoMapper productRequestDtoMapper;
    private final ProductResponseDtoMapper productResponseDtoMapper;

    @Override
    public ProductResponseDto add(ProductRequestDto productRequestDto) throws IOException, ParseException {
        User currentUser = userService.getCurrentUser();
        Product product = productRequestDtoMapper.productRequestDtoToProduct(productRequestDto);

        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElse(null);
        product.setCategory(category);

        product.setCreationTime(new Timestamp(System.currentTimeMillis()));
        product.setAuthor(userService.getCurrentUser());

        if (!productRequestDto.getImages().isEmpty()) {
            saveProductImages(product, productRequestDto);
        }

        Product savedProduct = productRepository.save(product);

        auctionService.createAuctionForProduct(savedProduct, productRequestDto);
        savedProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        return productResponseDtoMapper.productToProductResponseDto(savedProduct, currentUser);
    }

    private void saveProductImages(Product product, ProductRequestDto productRequestDto) throws IOException {
        for (MultipartFile image : productRequestDto.getImages()) {
            if (!image.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + image.getOriginalFilename();

                image.transferTo(new File(uploadPath + "/" + resultFilename));

                product.getImageFilenames().add(resultFilename);
            }
        }
    }

    @Override
    public ProductResponseDto update(ProductRequestDto productRequestDto) throws IOException, ParseException {
        User currentUser = userService.getCurrentUser();
        Product product = productRepository.findById(productRequestDto.getId()).orElse(null);

        if (product == null) {
            return null;
        }

        product.setName(productRequestDto.getName());

        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElse(null);
        product.setCategory(category);

        product.setDescription(productRequestDto.getDescription());

        if (productRequestDto.getImagesWasChanged() && !productRequestDto.getImages().isEmpty()) {
            saveProductImages(product, productRequestDto);
        }

        Product savedProduct = productRepository.save(product);

        auctionService.updateAuctionForProduct(savedProduct, productRequestDto);
        savedProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        return productResponseDtoMapper.productToProductResponseDto(savedProduct, currentUser);
    }

    @Override
    public List<ProductResponseDto> getAll() {
        User currentUser = userService.getCurrentUser();
        List<Product> products = productRepository.findAll();
        return products.stream().map(product ->
                productResponseDtoMapper.productToProductResponseDto(product, currentUser)).toList();
    }

    @Override
    public List<ProductResponseDto> getAllByCategoryId(Long categoryId) {
        User currentUser = userService.getCurrentUser();
        List<Product> products = productRepository.getAllByCategory_Id(categoryId);
        return products.stream().map(product ->
                productResponseDtoMapper.productToProductResponseDto(product, currentUser)).toList();
    }

    @Override
    public List<ProductResponseDto> getAllByCurrentUser() {
        User currentUser = userService.getCurrentUser();
        List<Product> products = productRepository.getAllByAuthor(currentUser);
        return products.stream().map(product ->
                productResponseDtoMapper.productToProductResponseDto(product, currentUser)).toList();
    }

    @Override
    public List<ProductResponseDto> getAllRegisteredByCurrentUser() {
        User currentUser = userService.getCurrentUser();
        List<Product> products = productRepository.findAll();
        List<Product> filteredProducts = products.stream()
                .filter(product -> product.getAuction().getRegisteredUsers().contains(currentUser)).toList();

        return filteredProducts.stream().map(product ->
                productResponseDtoMapper.productToProductResponseDto(product, currentUser)).toList();
    }

    @Override
    public ProductResponseDto getById(Long id) {
        User currentUser = userService.getCurrentUser();
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return productResponseDtoMapper.productToProductResponseDto(product, currentUser);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null && product.getAuction().getStatus().equals(AuctionStatus.CREATED)) {
            productRepository.delete(product);
        }
    }

}
