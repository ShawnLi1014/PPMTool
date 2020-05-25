package com.tianlun.ppmtool.services.impl;

import com.tianlun.ppmtool.domain.User;
import com.tianlun.ppmtool.exceptions.UsernameAlreadyExistsException;
import com.tianlun.ppmtool.repositories.UserRepository;
import com.tianlun.ppmtool.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User newUser) {


        try {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            // User name has to be unique
            // Make sure that password and confirmpassword match
            // We dont persist or show the confirmpassword
            return userRepository.save(newUser);
        } catch (Exception ex) {
            throw new UsernameAlreadyExistsException("Username '" + newUser.getUsername() + "' already exists");
        }
    }
}
