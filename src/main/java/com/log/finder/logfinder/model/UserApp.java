package com.log.finder.logfinder.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

public class UserApp extends User {

    private String email;
    private String lastName;
    private String firstName;


    public UserApp(String username, Collection<? extends GrantedAuthority> authorities) {
        super(username.toLowerCase(),"",true,true,true,true, authorities);
    }

    public UserApp(String username){
        this(username, Collections.emptyList());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
