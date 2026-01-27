package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {

    private final UserService service;

    public PaymentCardController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> findCardById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findCard(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateCardStatus(@PathVariable Long id, @RequestParam boolean active) {
        service.updateCardStatus(id, active);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long id) {
        service.deleteCard(id);
    }


}
