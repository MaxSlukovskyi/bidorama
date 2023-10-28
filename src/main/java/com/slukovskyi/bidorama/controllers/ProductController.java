package com.slukovskyi.bidorama.controllers;

import com.slukovskyi.bidorama.dtos.ProductRequestDto;
import com.slukovskyi.bidorama.dtos.ProductResponseDto;
import com.slukovskyi.bidorama.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("add")
    public ResponseEntity<ProductResponseDto> add(@RequestParam("name") String name,
                                                  @RequestParam("category") Long categoryId,
                                                   @RequestParam("description") String description,
                                                   @RequestParam("images") List<MultipartFile> images,
                                                   @RequestParam("startTime") String startTime,
                                                   @RequestParam("endTime") String endTime,
                                                   @RequestParam("startBid") Double startBid,
                                                   @RequestParam("minimalStep") Double minimalStep) throws IOException, ParseException {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setName(name);
        productRequestDto.setCategoryId(categoryId);
        productRequestDto.setDescription(description);
        productRequestDto.setImages(images);
        productRequestDto.setStartTime(startTime);
        productRequestDto.setEndTime(endTime);
        productRequestDto.setStartBid(startBid);
        productRequestDto.setMinimalStep(minimalStep);
        ProductResponseDto productResponseDto = productService.add(productRequestDto);
        if (productResponseDto == null) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(productResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("update")
    public ResponseEntity<ProductResponseDto> update(@RequestParam("id") Long id,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("category") Long categoryId,
                                                     @RequestParam("description") String description,
                                                     @RequestParam("imagesWasChanged") Boolean imagesWasChanged,
                                                     @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam("endTime") String endTime,
                                                     @RequestParam("startBid") Double startBid,
                                                     @RequestParam("minimalStep") Double minimalStep) throws IOException, ParseException {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setId(id);
        productRequestDto.setName(name);
        productRequestDto.setCategoryId(categoryId);
        productRequestDto.setDescription(description);
        productRequestDto.setImagesWasChanged(imagesWasChanged);
        productRequestDto.setImages(images);
        productRequestDto.setStartTime(startTime);
        productRequestDto.setEndTime(endTime);
        productRequestDto.setStartBid(startBid);
        productRequestDto.setMinimalStep(minimalStep);
        ProductResponseDto productResponseDto = productService.update(productRequestDto);
        if (productResponseDto == null) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(productResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        List<ProductResponseDto> products = productService.getAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("category/{id}")
    public ResponseEntity<List<ProductResponseDto>> getAllByCategoryId(@PathVariable(value = "id") Long categoryId) {
        List<ProductResponseDto> products = productService.getAllByCategoryId(categoryId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("all/author")
    public ResponseEntity<List<ProductResponseDto>> getAllByCurrentUser() {
        List<ProductResponseDto> products = productService.getAllByCurrentUser();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("all/registered/user")
    public ResponseEntity<List<ProductResponseDto>> getAllRegisteredByCurrentUser() {
        List<ProductResponseDto> products = productService.getAllRegisteredByCurrentUser();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable(value = "id") Long id) {
        ProductResponseDto product = productService.getById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<ProductResponseDto> delete(@PathVariable(value = "id") Long id) {
        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
