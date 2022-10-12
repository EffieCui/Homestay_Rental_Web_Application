package com.project.staybooking.repository;

import java.util.List;

// LocationRepository 是自带的用来增删改查，update elastic search数据库
// CustomLocationRepository是我们需要的逻辑(search by 半径)
// searchByDistance需要自己实现

public interface CustomLocationRepository {
    // 不能写在location repo里，因为elastic search不能帮我们implement
    // spring 不知道这个逻辑，所以我们要先自己定义好，后面去implement
    List<Long> searchByDistance(double lat, double lon, String distance);
}
