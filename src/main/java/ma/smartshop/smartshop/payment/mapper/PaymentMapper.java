package ma.smartshop.smartshop.payment.mapper;

import ma.smartshop.smartshop.entity.Payment;
import ma.smartshop.smartshop.payment.dto.PaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponseDto toResponseDto(Payment payment);
}