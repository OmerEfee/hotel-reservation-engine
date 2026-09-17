package com.hotel.reservation.repository;

import com.hotel.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRoomId(Long roomId);

    // Tarih Çakışması Kontrolü (Overlap Check):
    // Yeni istenen check-in tarihi, mevcut rezervasyonun check-out tarihinden ÖNCE ise
    // VE yeni istenen check-out tarihi, mevcut rezervasyonun check-in tarihinden SONRA ise ÇAKIŞMA VARDIR.
    // İptal edilmiş (CANCELLED) rezervasyonlar hesaba katılmaz.
    @Query("SELECT COUNT(res) > 0 FROM Reservation res " +
           "WHERE res.room.id = :roomId " +
           "AND res.status = 'CONFIRMED' " +
           "AND (:newCheckIn < res.checkOutDate AND :newCheckOut > res.checkInDate)")
    boolean existsOverlappingReservation(
            @Param("roomId") Long roomId,
            @Param("newCheckIn") LocalDate newCheckIn,
            @Param("newCheckOut") LocalDate newCheckOut
    );
}