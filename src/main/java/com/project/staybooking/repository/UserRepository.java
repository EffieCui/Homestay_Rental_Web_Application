package com.project.staybooking.repository;

import com.project.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//对user table进行增删改查
//之前用hibernate时，增删改查用session factory链接数据库，对数据库改变
//现在用spring framework里的spring jpa。增删改查不用手动实现。default implementation
//只需要定义一个interface extends jpa repository，这个框架在启动时会根据primary key自动implement改变

@Repository
public interface UserRepository extends JpaRepository<User, String> { //<table, primary key>

}