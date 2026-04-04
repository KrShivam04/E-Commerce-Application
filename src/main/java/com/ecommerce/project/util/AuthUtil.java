package com.ecommerce.project.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ecommerce.project.Repository.UserRepository;
import com.ecommerce.project.model.User;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    /**
     * Returns email of the currently authenticated user.
     *
     * @return logged-in user's email
     */
    public String loggedInEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getEmail();
    }

    /**
     * Returns user id of the currently authenticated user.
     *
     * @return logged-in user's id
     */
    public Long loggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));

        return user.getUserId();
    }

    /**
     * Returns the complete user entity of the currently authenticated user.
     *
     * @return logged-in user
     */
    public User loggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
        return user;

    }


}
