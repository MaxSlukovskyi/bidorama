package com.slukovskyi.bidorama.mappers;

import com.slukovskyi.bidorama.dtos.ProductResponseDto;
import com.slukovskyi.bidorama.models.Product;
import com.slukovskyi.bidorama.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Mapper(uses = {CategoryResponseDtoMapper.class, UserResponseDtoMapper.class,
        AuctionResponseDtoMapper.class, BidResponseDtoMapper.class}, componentModel = "spring")
public interface ProductResponseDtoMapper {

    AuctionResponseDtoMapper auctionResponseDtoMapper = Mappers.getMapper(AuctionResponseDtoMapper.class);

    @Mapping(source = "product.id", target = "id")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.creationTime", target = "creationTime", qualifiedByName = "getCreationTime")
    @Mapping(target = "auction", expression = "java(auctionResponseDtoMapper.auctionToAuctionResponseDto(product.getAuction(), currentUser))")
    ProductResponseDto productToProductResponseDto(Product product, User currentUser);

    @Named("getCreationTime")
    static String getCreationTime(Timestamp value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        return value == null ? "" : simpleDateFormat.format(value);
    }

}
