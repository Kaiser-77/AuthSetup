package com.project.auth_profile_email_exception_app.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.auth_profile_email_exception_app.auth.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Profile {
    @Id
    private Long id;

//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private User user;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonIgnore
    private User user;

    private String name;
    private int age;
    private String bio;
    private int following = 0;
    private int followers = 0;
    private String profilePic;




    public String getUserName() {
        return user.getUserName(); // Retrieve username from User
    }

    // Getters and Setters
}
