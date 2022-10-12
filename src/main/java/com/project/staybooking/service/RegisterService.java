package com.project.staybooking.service;

import com.project.staybooking.exception.UserAlreadyExistException;
import com.project.staybooking.model.Authority;
import com.project.staybooking.model.UserRole;
import com.project.staybooking.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.project.staybooking.repository.AuthorityRepository;
import com.project.staybooking.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

@Service
public class RegisterService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;

    //dependency injection
    @Autowired
    public RegisterService(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // insert new user and authority records to the database.
    @Transactional(isolation = Isolation.SERIALIZABLE) //make the insert operations to user and authority tables will succeed together, or fail together.
    public void add(User user, UserRole role) throws UserAlreadyExistException {
        //duplication check
        if (userRepository.existsById(user.getUsername())) {
            throw new UserAlreadyExistException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));//encryption
        user.setEnabled(true); //encryption
        userRepository.save(user);
        authorityRepository.save(new Authority(user.getUsername(), role.name()));
    }
}

