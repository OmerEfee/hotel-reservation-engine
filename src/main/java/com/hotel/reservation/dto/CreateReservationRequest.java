package com.hotel.reservation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateReservationRequest {

    @NotNull(message = "Oda ID belirtilmelidir")
    private Long roomId;

    @NotBlank(message = "Müşteri adı boş bırakılamaz")
    private String customerName;

    @NotNull(message = "Giriş tarihi belirtilmelidir")
    @FutureOrPresent(message = "Giriş tarihi bugünden önce olamaz")
    private LocalDate checkInDate;

    @NotNull(message = "Çıkış tarihi belirtilmelidir")
    private LocalDate checkOutDate;
}