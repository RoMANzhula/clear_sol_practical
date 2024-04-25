package org.romanzhula.clear_sol_practical.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.romanzhula.clear_sol_practical.dto.UserDTO;
import org.romanzhula.clear_sol_practical.json_responsies.RegistrationResponse;
import org.romanzhula.clear_sol_practical.models.User;
import org.romanzhula.clear_sol_practical.repositories.UserRepository;
import org.romanzhula.clear_sol_practical.services.UserService;
import org.romanzhula.clear_sol_practical.utils.UtilsController;
import org.romanzhula.clear_sol_practical.validations.UserCreateMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Example with validation marker
     * with exception handler to BadRequest
    */
    @Validated({UserCreateMarker.class})
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> createUser(
            @ModelAttribute("userDTO")
            @Valid
            @RequestBody UserDTO userDTO
    ) {
        userService.registerNewUser(userDTO);

        return ResponseEntity
                .ok()
                .body(new RegistrationResponse("User created successfully"))
        ;
    }

    /**
     * Example with BindingResult
     * with wrapping binding errors to string VS field
     */
    @PatchMapping("/update-field/{userId}")
    public ResponseEntity<?> updateUserField(
            @PathVariable Long userId,
            @Valid
            @RequestBody UserDTO userDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation error! Check your input update data.");

            Map<String, String> errors = UtilsController.getBindingErrors(bindingResult);

            return ResponseEntity
                    .badRequest()
                    .body(errors)
            ;
        }

        userService.updateUserByField(userId, userDTO);

        return ResponseEntity
                .ok()
                .body("User's field(s) updated successfully")
        ;

    }

    @PutMapping("/whole-update/{userId}")
    public ResponseEntity<?> updateWholeUser(
        @PathVariable Long userId,
        @Valid
        @RequestBody UserDTO userDTO,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation error! Check all input fields data.");

            return ResponseEntity
                    .badRequest()
                    .body("Validation error! Check all input fields data.")
            ;
        }

        userService.updateWholeUser(userDTO, userId);

        return ResponseEntity
                .ok()
                .body("User with id: " + userId + " was updated whole successfully!")
        ;
    }

    @DeleteMapping("/remove-user/{userId}")
    public ResponseEntity<?> removeUserById(
            @PathVariable Long userId
    ) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);

            return ResponseEntity
                    .ok()
                    .body("User deleted successfully!")
            ;
        }

        logger.error("ERROR: User with id {} NOT FOUND!", userId);
        return ResponseEntity
                .notFound()
                .build()
        ;
    }

    @GetMapping("/birthday-range")
    public ResponseEntity<?> getUsersByBirthdayRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate to
    ) {
        if (from.isAfter(to)) {
            return ResponseEntity
                    .badRequest()
                    .body("Date From should be less than date To.")
            ;
        }

        List<User> users = userRepository.findByBirthDateBetween(from, to);
        return ResponseEntity
                .ok()
                .body(users)
        ;
    }

    @ModelAttribute("userDTO")
    public UserDTO userDTO() {
        return new UserDTO();
    }

}
