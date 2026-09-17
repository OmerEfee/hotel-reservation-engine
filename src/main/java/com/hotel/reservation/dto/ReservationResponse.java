package com.hotel.reservation.dto;

import com.hotel.reservation.model.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ReservationResponse {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String customerName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalPrice;
    private ReservationStatus status;
}