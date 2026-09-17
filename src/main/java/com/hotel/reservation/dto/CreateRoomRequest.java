package com.hotel.reservation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRoomRequest {

    @NotBlank(message = "Oda numarası boş bırakılamaz")
    private String roomNumber;

    @NotBlank(message = "Oda tipi boş bırakılamaz (örn: DELUXE, STANDART)")
    private String roomType;

    @NotNull(message = "Gecelik ücret belirtilmelidir")
    @DecimalMin(value = "0.0", inclusive = false, message = "Ücret 0'dan büyük olmalıdır")
    private BigDecimal pricePerNight;
}