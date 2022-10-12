package com.project.staybooking.repository;

import com.project.staybooking.model.Stay;
import com.project.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> { //jpa repository自动实现crud repository里的功能, <model(table), type of primary key>
    //find by id (primary key) is implemented by jpa repository by default

    // 框架默认只对primary key进行搜索。如果不是对primary key进行查找，要写出来
    // 省略access modifier
    List<Stay> findByHost(User user); //findByXxx命名符合spring creation quary 命名规则，自动实现，不需要自己实现method
    // select * from Stay ...

    Stay findByIdAndHost(Long id, User host); //access control, 只有stay所对应的owner才能返回stay信息

    // 入住人数比客户要找的多
    List<Stay> findByIdInAndGuestNumberGreaterThanEqual(List<Long> ids, int guestNumber);

}