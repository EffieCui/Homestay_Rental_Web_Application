package com.project.staybooking.service;

import com.project.staybooking.model.StayReservedDate;
import com.project.staybooking.model.StayReservedDateKey;
import org.springframework.stereotype.Service;

import com.project.staybooking.repository.ReservationRepository;
import com.project.staybooking.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.staybooking.exception.ReservationCollisionException;
import com.project.staybooking.exception.ReservationNotFoundException;
import com.project.staybooking.model.Reservation;
import com.project.staybooking.model.Stay;
import com.project.staybooking.model.User;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService {

    // 需要修改哪个table，就把哪个repository 用作private field
    private ReservationRepository reservationRepository;
    private StayReservationDateRepository stayReservationDateRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, StayReservationDateRepository stayReservationDateRepository) {
        this.reservationRepository = reservationRepository;
        this.stayReservationDateRepository = stayReservationDateRepository;
    }

    // 四个功能（根据导图）
    public List<Reservation> listByGuest(String username) {
        return reservationRepository.findByGuest(new User.Builder().setUsername(username).build());
    }

    public List<Reservation> listByStay(Long stayId) {
        return reservationRepository.findByStay(new Stay.Builder().setId(stayId).build());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE) // 原子操作（不可拆分），要么date和reservation都成功，要么都失败。
    // 用于修改多个数据库的内容时，可以保证多个table可以一致地被修改
    public void add(Reservation reservation) throws ReservationCollisionException {
        // check if there's collision for date
        Set<Long> stayIds = stayReservationDateRepository.findByIdInAndDateBetween(
                Arrays.asList(reservation.getStay().getId()),
                reservation.getCheckinDate(),
                reservation.getCheckoutDate().minusDays(1)); // check out 当天可以做别人的 check in date
        if (!stayIds.isEmpty()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }

        // save reserved date to mysql
        List<StayReservedDate> reservedDates = new ArrayList<>();
        for (LocalDate date = reservation.getCheckinDate(); date.isBefore(reservation.getCheckoutDate()); date = date.plusDays(1)) {
            reservedDates.add(new StayReservedDate(new StayReservedDateKey(reservation.getStay().getId(), date), reservation.getStay()));
        }
        stayReservationDateRepository.saveAll(reservedDates); // save date 可能会有多条日期，所以要用save all

        // save reservation to mysql
        reservationRepository.save(reservation); //只会有一条信息
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long reservationId, String username) { // 需要guest name让guest删除自己的reservation
        // is the reservation exist
        Reservation reservation = reservationRepository.findByIdAndGuest(reservationId, new User.Builder().setUsername(username).build());
        if (reservation == null) {
            throw new ReservationNotFoundException("Reservation is not available");
        }

        // delete reserved date to mysql
        for (LocalDate date = reservation.getCheckinDate(); date.isBefore(reservation.getCheckoutDate()); date = date.plusDays(1)) {
            stayReservationDateRepository.deleteById(new StayReservedDateKey(reservation.getStay().getId(), date));
        }
        // 或者把要delete的变成一个list，然后delete all（同上）

        // delete reservation to mysql
        reservationRepository.deleteById(reservationId);
    }
}
