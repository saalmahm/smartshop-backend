package ma.smartshop.smartshop.mapper;

import ma.smartshop.smartshop.entity.Payment;
import ma.smartshop.smartshop.dto.payment.PaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponseDto toResponseDto(Payment payment);
}