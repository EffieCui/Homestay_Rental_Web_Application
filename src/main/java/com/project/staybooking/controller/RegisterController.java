package com.project.staybooking.controller;

import com.project.staybooking.model.User;
import com.project.staybooking.model.UserRole;
import com.project.staybooking.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController //合并了@Controller 和 @ResponseBody
public class RegisterController {
    private RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    // endpoint for guest registration
    @PostMapping("/register/guest") //annotation to indicate the API supports POST method and maps to the /register/guest path.
    public void addGuest(@RequestBody User user) { //@requestBody: convert the request body from JSON format to a Java object.
        registerService.add(user, UserRole.ROLE_GUEST);
    }

    // endpoint for host registration
    @PostMapping("/register/host")
    public void addHost(@RequestBody User user) {
        registerService.add(user, UserRole.ROLE_HOST);
    }


}
