package com.project.auth_profile_email_exception_app.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {


    @GetMapping("/hello")
    public ResponseEntity<String> check(){
        return ResponseEntity.ok("Hello...!!");

    }

   }
