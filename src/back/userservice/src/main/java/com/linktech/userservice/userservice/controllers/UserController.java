package com.linktech.userservice.userservice.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.linktech.userservice.userservice.models.UserModel;
import com.linktech.userservice.userservice.repositories.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;
    
    @PostMapping()
    public UserModel createUser(@RequestBody UserModel user){
        return userRepository.save(user);
    }

    @GetMapping()
    public ResponseEntity<?> getUsers(){
        List<UserModel> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/{id}")
    public Optional<UserModel> getUserById(@PathVariable("id") String id) {
        return userRepository.findById(id); 
    }
    
    @PutMapping(value="/{id}")
    public UserModel updateUser(@PathVariable String id, @RequestBody UserModel user) {
        
        user.setId(id);
        return userRepository.save(user);
    }

    // @GetMapping(value = "/searchByName/{firstName}")
    @GetMapping(value = "/searchByName")
    public List<UserModel> requestMethodName(@RequestParam(required = false, value = "firstName") String firstName,
        @RequestParam(required = false, value = "secondName") String secondName,
        @RequestParam(required = false, value = "firstAndLastName") String firstAndLastName) 
    {
        if(firstName != null && secondName != null)
            return userRepository.findByFirstNameAndSecondName(firstName, secondName);
        if (firstName != null)
            return userRepository.findByFirstName(firstName);
        if (secondName != null)
            return userRepository.findBySecondName(secondName);
        
            return userRepository.findAll();
    }

    @PutMapping(value = "/banUser/{id}")
    public UserModel banUser(@PathVariable("id") String id, @RequestBody UserModel user)
    {
        user.setIsBanned(true);
        user.setId(id);
        return userRepository.save(user);
    }

    @PutMapping(value = "/followInstitution")
    public ResponseEntity followInstitution(@PathVariable("institutionId") String institutionId, @RequestBody UserModel user) 
    {
        UserModel userGotten = userRepository.findById(user.getId()).get();

        if (userGotten != null)
        {
            userGotten.getInstitutionsFollowing().add(institutionId);
            userRepository.save(userGotten);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PutMapping(value = "/unFollowInstitution")
    public ResponseEntity unFollowInstitution(@PathVariable("institutionId") String institutionId, @RequestBody UserModel user) 
    {
        UserModel userGotten = userRepository.findById(user.getId()).get();

        if (userGotten != null)
        {
            userGotten.getInstitutionsFollowing().remove(institutionId);
            userRepository.save(userGotten);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/followUser")
    public ArrayList<String> followUser(@RequestParam("currentUserId") String currentUserId, 
        @RequestParam("followingUserId") String followingUserId)
    {
        UserModel currentUser = userRepository.findById(currentUserId).get();
        UserModel followingUser = userRepository.findById(followingUserId).get();

        ArrayList<String> following = currentUser.getFollowing();
        following.add(followingUserId);
        currentUser.setFollowing(following);
        ArrayList<String> followers = followingUser.getFollowers();
        followers.add(currentUserId);
        followingUser.setFollowers(followers);

        userRepository.save(currentUser);
        userRepository.save(followingUser);

        return followingUser.getFollowing();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") String id)
    {
        userRepository.deleteById(id);
    }
}