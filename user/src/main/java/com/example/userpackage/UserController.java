package com.example.userpackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/add")
    public String addUser(@RequestBody()UserRequestDto userRequestDto){


        return userService.addUser(userRequestDto);
    }

    @GetMapping("/findByUser/{userName}")
    public User getUserByUserName (@PathVariable("userName")String username){
        return userService.getUserByUsername(username);
    }
}
