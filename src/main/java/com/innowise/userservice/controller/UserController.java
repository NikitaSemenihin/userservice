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
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final UserMapper userMapper;
    private final PaymentCardMapper cardMapper;

    public UserController(UserService service, UserMapper userMapper, PaymentCardMapper cardMapper) {
        this.service = service;
        this.userMapper = userMapper;
        this.cardMapper = cardMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        User user = service.createUser(userMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getUsers(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String surname,
                                                          Pageable pageable) {
        Specification<User> specification = Specification.allOf(
                UserSpecification.hasName(name), UserSpecification.hasSurname(surname)
        );

        Page<UserResponseDto> page = service.findUsers(specification, pageable).map(userMapper::toDto);

        return ResponseEntity.ok(page);
    }

    @PostMapping("/{id}/cards")
    public ResponseEntity<PaymentCardResponseDto> addCard(@PathVariable Long id,
                                                          @Valid @RequestBody PaymentCardCreateDto dto) {
        PaymentCard card = service.addCard(id, cardMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(cardMapper.toDto(card));
    }
}
