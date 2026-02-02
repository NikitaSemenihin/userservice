package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.StatusDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponseDto>> findCards(Pageable pageable) {
        Page<PaymentCardResponseDto> page = service.findActiveCards(pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> updateCard(@PathVariable Long id,
                                                             @Valid @RequestBody PaymentCardCreateDto dto) {
        PaymentCardResponseDto responseDto = service.updateCard(id, dto);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCardStatus(@PathVariable Long id, @RequestBody StatusDto dto) {
        service.updateCardStatus(id, dto.isStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long id) {
        service.deleteCard(id);
    }


}
