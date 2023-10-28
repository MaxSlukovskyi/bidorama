package com.slukovskyi.bidorama.mappers;

import com.slukovskyi.bidorama.dtos.BidResponseDto;
import com.slukovskyi.bidorama.models.Bid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Mapper(componentModel = "spring", uses = UserResponseDtoMapper.class)
public interface BidResponseDtoMapper {

    @Mapping(source = "creationTime", target = "creationTime", qualifiedByName = "timestampToString")
    BidResponseDto bidToBidResponseDto(Bid bid);

    @Mapping(source = "creationTime", target = "creationTime", qualifiedByName = "stringToTimestamp")
    Bid bidResponseDtoToBid(BidResponseDto bidResponseDto);

    @Named("stringToTimestamp")
    static Timestamp stringToTimestamp(String value) {
        return Timestamp.valueOf(value);
    }

    @Named("timestampToString")
    static String timestampToString(Timestamp value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
        return value == null ? "" : simpleDateFormat.format(value);
    }
}
