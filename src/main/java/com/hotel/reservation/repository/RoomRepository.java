package com.hotel.reservation.repository;

import com.hotel.reservation.model.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Normal okuma: Sadece odayı görüntülemek için
    Optional<Room> findByRoomNumber(String roomNumber);

    // MÜLAKAT KOZU: Pessimistic Write Lock
    // Rezervasyon işlemi başladığında bu odaya ait satırı kilitler.
    // Diğer işlemler kilit kalkana kadar (transaction bitene kadar) beklemek zorunda kalır.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdWithLock(@Param("id") Long id);
}