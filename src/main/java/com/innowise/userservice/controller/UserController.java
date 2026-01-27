package com.innowise.userservice.controller;

import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.user.UserCreateDto;
import com.innowise.userservice.model.dto.user.UserResponseDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.specification.UserSpecification;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final UserMapper userMapper;
    private final PaymentCardMapper cardMapper;

    public UserController(UserService service, UserMapper userMapper, PaymentCardMapper cardMapper) {
        this.service = service;
        this.userMapper = userMapper;
        this.cardMapper = cardMapper;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        UserResponseDto responseDto = service.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.findUser(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> findUsers(@RequestParam(required = false) String name,
                                                           @RequestParam(required = false) String surname,
                                                           Pageable pageable) {
        Specification<User> specification = Specification.allOf(
                UserSpecification.hasName(name), UserSpecification.hasSurname(surname)
        );

        Page<UserResponseDto> page = service.findUsersWithSpecification(specification, pageable);

        return ResponseEntity.ok(page);
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateDto dto) {
        UserResponseDto responseDto = service.updateUser(id, dto);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        service.updateUserStatus(id, active);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }

    @PostMapping("/{userId}/cards")
    public ResponseEntity<PaymentCardResponseDto> addCard(@PathVariable Long userId,
                                                          @Valid @RequestBody PaymentCardCreateDto dto) {
        PaymentCardResponseDto paymentCardResponseDto = service.addCard(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardResponseDto);
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<PaymentCardResponseDto>> findUserCards(@PathVariable Long userId) {
        List<PaymentCardResponseDto> cards =
                service.findUserCards(userId);
        return ResponseEntity.ok(cards);
    }
}
