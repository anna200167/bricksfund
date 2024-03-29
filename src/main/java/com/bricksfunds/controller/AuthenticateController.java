package com.bricksfunds.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.bricksfunds.config.JwtUtill;
import com.bricksfunds.entity.JwtRequest;
import com.bricksfunds.entity.JwtResponse;
import com.bricksfunds.entity.User;
import com.bricksfunds.service.UserDetailServiceImpl;

import java.security.Principal;

@RestController
public class AuthenticateController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    @Autowired
    private JwtUtill jwtUtils;

    //generate token
    @PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        try {
            authenticate(jwtRequest.getUsername(), jwtRequest.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("User not found ");
        }

        //authenticate
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token = this.jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }


    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER DISABLED " + e.getMessage());
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid Credentials " + e.getMessage());
        }
    }

    //return the details of current user
    @GetMapping("/current-user")
    public User getCurrentUser(Principal principal) {
    	System.out.println(principal.getName()+"Hatib");
        return ((User) this.userDetailsService.loadUserByUsername(principal.getName()));
    }
    
    @GetMapping("/getUserId")
    public long getCurrentUserId(Principal principal) {
    	User user= (User) this.userDetailsService.loadUserByUsername(principal.getName());
    	System.out.println(principal.getName()+"Hatib");
        return user.getId();
    }
}