package com.project.staybooking.repository;

import com.project.staybooking.model.Reservation;
import com.project.staybooking.model.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.staybooking.model.Stay;
import com.project.staybooking.model.User;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 添加jpa不自带的功能
    List<Reservation> findByGuest(User guest);

    List<Reservation> findByStay(Stay stay);

    Reservation findByIdAndGuest(Long id, User guest); // for deletion
    // 有了reservation就不能轻易删除stay了。如果还有没完成的不能删除

    // check active reservations before deleting a stay.
    // 满足 naming convention，自动实现
    List<Reservation> findByStayAndCheckoutDateAfter(Stay stay, LocalDate date);


}
