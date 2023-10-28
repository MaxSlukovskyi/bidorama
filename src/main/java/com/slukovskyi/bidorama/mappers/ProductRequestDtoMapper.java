package com.slukovskyi.bidorama.mappers;

import com.slukovskyi.bidorama.dtos.ProductRequestDto;
import com.slukovskyi.bidorama.models.Auction;
import com.slukovskyi.bidorama.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface ProductRequestDtoMapper {

    Product productRequestDtoToProduct(ProductRequestDto productRequestDto);

    @Mapping(source = "startTime", target = "startTime", qualifiedByName = "getStartTime")
    @Mapping(source = "endTime", target = "endTime", qualifiedByName = "getEndTime")
    @Mapping(target = "id", ignore = true)
    Auction productRequestDtoToAuction(ProductRequestDto productRequestDto);

    @Mapping(source = "productRequestDto.startTime", target = "startTime", qualifiedByName = "getStartTime")
    @Mapping(source = "productRequestDto.endTime", target = "endTime", qualifiedByName = "getEndTime")
    @Mapping(target = "id", ignore = true)
    Auction updateAuctionFromProductRequestDto(@MappingTarget Auction auction, ProductRequestDto productRequestDto);

    @Named("getStartTime")
    static Timestamp getStartTime(String startTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String startTimeReplaced = startTime.replace('T', ' ');
        Date parseStartTime = dateFormat.parse(startTimeReplaced);
        return new Timestamp(parseStartTime.getTime());
    }

    @Named("getEndTime")
    static Timestamp getEndTime(String endTime) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String endTimeReplaced = endTime.replace('T', ' ');
        Date parseEndTime = dateFormat.parse(endTimeReplaced);
        Timestamp parsedEndTime = new java.sql.Timestamp(parseEndTime.getTime());
        return new Timestamp(parsedEndTime.getTime());
    }
}
