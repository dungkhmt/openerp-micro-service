package com.hust.baseweb.controller;

import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRegisterController {

    UserService userService;

    /**
     * It takes a userId as a path variable, finds the PersonModel object in the database, and returns
     * it as a response
     *
     * @param userId The userId is the user's login id.
     * @return A ResponseEntity object.
     */
    @GetMapping("/get-user-detail/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable String userId) {
        log.info("getUserDetail userId = " + userId);
        PersonModel p = userService.findPersonByUserLoginId(userId);
        log.info("getUserDetail, found personModel {}", p);
        return ResponseEntity.ok().body(p);
    }
}
