package com.hotel.reservation.service;

import com.hotel.reservation.dto.CreateReservationRequest;
import com.hotel.reservation.dto.CreateRoomRequest;
import com.hotel.reservation.dto.ReservationResponse;
import com.hotel.reservation.exception.InvalidDateRangeException;
import com.hotel.reservation.exception.RoomAlreadyBookedException;
import com.hotel.reservation.exception.RoomNotFoundException;
import com.hotel.reservation.model.Reservation;
import com.hotel.reservation.model.ReservationStatus;
import com.hotel.reservation.model.Room;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    // Oda Tanımlama
    @Transactional
    public Room createRoom(CreateRoomRequest request) {
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .pricePerNight(request.getPricePerNight())
                .build();
        return roomRepository.save(room);
    }

    // Tüm Odaları Listeleme
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // MÜLAKATIN ODAK NOKTASI: Pessimistic Lock İle Rezervasyon Oluşturma
    @Transactional
    public ReservationResponse makeReservation(CreateReservationRequest request) {
        // 1. Kural: Çıkış tarihi giriş tarihinden sonra olmalı
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new InvalidDateRangeException("Çıkış tarihi, giriş tarihinden en az bir gün sonra olmalıdır.");
        }

        // 2. Kural: Odayı veritabanında satır bazlı KİLİTLEYEREK getir (PESSIMISTIC_WRITE)
        // Bu transaction tamamlanana kadar diğer eşzamanlı istekler bu satırı bekler.
        Room room = roomRepository.findByIdWithLock(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Belirtilen ID'ye sahip oda bulunamadı: " + request.getRoomId()));

        // 3. Kural: Tarih çakışması (Overlap) var mı?
        boolean isOverbooked = reservationRepository.existsOverlappingReservation(
                room.getId(),
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        if (isOverbooked) {
            throw new RoomAlreadyBookedException("Oda (" + room.getRoomNumber() + ") seçilen tarih aralığında zaten doludur.");
        }

        // 4. Gün sayısını ve toplam tutarı hesapla
        long stayDays = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(stayDays));

        // 5. Kaydı hazırla ve kaydet
        Reservation reservation = Reservation.builder()
                .room(room)
                .customerName(request.getCustomerName())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(ReservationStatus.CONFIRMED)
                .build();

        Reservation saved = reservationRepository.save(reservation);

        // 6. Response DTO'ya dönüştür
        return mapToResponse(saved);
    }

    // Rezervasyon İptal Etme
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervasyon bulunamadı: " + reservationId));

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    // Odanın Rezervasyon Geçmişi
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByRoom(Long roomId) {
        return reservationRepository.findByRoomId(roomId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .roomId(reservation.getRoom().getId())
                .roomNumber(reservation.getRoom().getRoomNumber())
                .customerName(reservation.getCustomerName())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .build();
    }
}
