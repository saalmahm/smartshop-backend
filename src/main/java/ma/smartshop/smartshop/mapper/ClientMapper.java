package ma.smartshop.smartshop.client.mapper;

import ma.smartshop.smartshop.client.dto.ClientRequestDto;
import ma.smartshop.smartshop.client.dto.ClientResponseDto;
import ma.smartshop.smartshop.client.dto.ClientProfileDto;
import ma.smartshop.smartshop.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toEntity(ClientRequestDto dto);

    ClientResponseDto toResponseDto(Client entity);

    void updateEntityFromDto(ClientRequestDto dto, @MappingTarget Client entity);

    ClientProfileDto toProfileDto(Client entity);
}