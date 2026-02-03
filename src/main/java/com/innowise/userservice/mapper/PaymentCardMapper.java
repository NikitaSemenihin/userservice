package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.model.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "holder", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentCard toEntity(PaymentCardCreateDto dto);

    @Mapping(source = "user.id", target = "userId")
    PaymentCardResponseDto toDto(PaymentCard card);
}
