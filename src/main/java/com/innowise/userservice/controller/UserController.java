package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.StatusDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.user.UserCreateDto;
import com.innowise.userservice.model.dto.user.UserResponseDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.specification.UserSpecification;
import com.innowise.userservice.service.AccessPolicyService;
import com.innowise.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;
    private final AccessPolicyService accessPolicyService;

    public UserController(UserService service, AccessPolicyService accessPolicyService) {
        this.service = service;
        this.accessPolicyService = accessPolicyService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDto> createUser(
            HttpServletRequest request,
            @Valid @RequestBody UserCreateDto dto
    ) {
        accessPolicyService.requireService(request, "authservice");
        UserResponseDto responseDto = service.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findUser(HttpServletRequest request, @PathVariable Long id) {
        accessPolicyService.requireSelfOrAdmin(request, id);
        return ResponseEntity.ok(service.findUser(id));
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponseDto> findUserInternal(HttpServletRequest request, @PathVariable Long id) {
        accessPolicyService.requireService(request, "orderservice");
        return ResponseEntity.ok(service.findUser(id));
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserResponseDto> findUserByEmail(HttpServletRequest request, @RequestParam @Email String email) {
        accessPolicyService.requireService(request, "orderservice");
        return ResponseEntity.ok(service.findUserByEmail(email));
    }

    @PostMapping("/emails")
    public ResponseEntity<Map<String, UserResponseDto>> findUsersByEmails(
            HttpServletRequest request,
            @Valid @RequestBody Set<@Email String> emails
    ) {
        accessPolicyService.requireService(request, "orderservice");
        Map<String, UserResponseDto> result = service.findUsersByEmails(emails);
        return ResponseEntity.ok(result);
    }

    @GetMapping(params = "!email")
    public ResponseEntity<Page<UserResponseDto>> findUsers(
            HttpServletRequest request,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable
    ) {
        accessPolicyService.requireAdmin(request);
        Specification<User> specification = Specification.allOf(
                UserSpecification.hasName(name), UserSpecification.hasSurname(surname)
        );

        Page<UserResponseDto> page = service.findUsersWithSpecification(specification, pageable);

        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody UserCreateDto dto
    ) {
        accessPolicyService.requireSelfOrAdmin(request, id);
        UserResponseDto responseDto = service.updateUser(id, dto);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUserStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody StatusDto dto
    ) {
        accessPolicyService.requireAdmin(request);
        service.updateUserStatus(id, dto.isStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(HttpServletRequest request, @PathVariable Long id) {
        accessPolicyService.requireSelfOrAdmin(request, id);
        service.deleteUser(id);
    }

    @DeleteMapping("/internal/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserInternal(HttpServletRequest request, @PathVariable Long id) {
        accessPolicyService.requireService(request, "authservice");
        service.deleteUserHard(id);
    }

    @PostMapping("/{userId}/cards")
    public ResponseEntity<PaymentCardResponseDto> addCard(
            HttpServletRequest request,
            @PathVariable Long userId,
            @Valid @RequestBody PaymentCardCreateDto dto
    ) {
        accessPolicyService.requireSelfOrAdmin(request, userId);
        PaymentCardResponseDto paymentCardResponseDto = service.addCard(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardResponseDto);
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<PaymentCardResponseDto>> findUserCards(
            HttpServletRequest request,
            @PathVariable Long userId
    ) {
        accessPolicyService.requireSelfOrAdmin(request, userId);
        List<PaymentCardResponseDto> cards = service.findUserCards(userId);
        return ResponseEntity.ok(cards);
    }
}
