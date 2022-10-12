package com.project.staybooking.service;

import com.project.staybooking.exception.StayDeleteException;
import com.project.staybooking.exception.StayNotExistException;
import com.project.staybooking.model.*;
import com.project.staybooking.repository.LocationRepository;
import com.project.staybooking.repository.ReservationRepository;
import com.project.staybooking.repository.StayRepository;
import com.project.staybooking.repository.StayReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StayService {
    private StayRepository stayRepository; //支持数据库增删改查

    private LocationRepository locationRepository;

    private ReservationRepository reservationRepository;
    private ImageStorageService imageStorageService;

    private GeoCodingService geoCodingService;

    private StayReservationDateRepository stayReservationDateRepository;


    @Autowired
    public StayService(StayRepository stayRepository,
                       LocationRepository locationRepository,
                       ReservationRepository reservationRepository,
                       ImageStorageService imageStorageService,
                       GeoCodingService geoCodingService,
                       StayReservationDateRepository stayReservationDateRepository) {
        this.stayRepository = stayRepository;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
        this.imageStorageService = imageStorageService;
        this.geoCodingService = geoCodingService;
    }

        public List<Stay> listByUser(String username) { //给一个user能返回所有相关的值
        return stayRepository.findByHost(new User.Builder().setUsername(username).build()); //user constructor是空，必须通过builder来call method
    }

    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }

    public void add(Stay stay) {
        stayRepository.save(stay);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE) // 原子操作：涉及多个table时，成功都成功，失败都失败 （stay删掉，date，image，reservation都删掉）
    public void delete(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }

        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }


        stayRepository.deleteById(stayId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(Stay stay, MultipartFile[] images) {
        // upload file, return urls (并行上传，同时操作)
        List<String> mediaLinks = Arrays.stream(images).parallel().map(image -> imageStorageService.save(image)).collect(Collectors.toList());
        // Arrays.stream的好处：可以体现parallel，并行操作上传，速度快。但顺序不一定
        //效率低但清楚的写法：（范型操作，速度慢）
        // List<String> imageUrls = new ArrayList<>();
        // for(MultipartFile image : images){
        //      String url = imageStorageService.sava(image);
        //      imageUrls.add(url);
        //}

        // create stayimage objects, save to db
        List<StayImage> stayImages = new ArrayList<>();
        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }
        stay.setImages(stayImages);

        stayRepository.save(stay);

        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress());
        locationRepository.save(location);
    }


}
