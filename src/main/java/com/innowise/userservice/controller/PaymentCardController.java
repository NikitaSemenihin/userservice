package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.StatusDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.service.AccessPolicyService;
import com.innowise.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AccessPolicyService accessPolicyService;

    public PaymentCardController(UserService service, AccessPolicyService accessPolicyService) {
        this.service = service;
        this.accessPolicyService = accessPolicyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> findCardById(HttpServletRequest request, @PathVariable Long id) {
        accessPolicyService.requireCardOwnerOrAdmin(request, id);
        return ResponseEntity.ok(service.findCard(id));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponseDto>> findCards(HttpServletRequest request, Pageable pageable) {
        var context = accessPolicyService.requireContext(request);
        Page<PaymentCardResponseDto> page;
        if (context.isAdmin()) {
            page = service.findActiveCards(pageable);
        } else {
            accessPolicyService.requireUserOrAdmin(request);
            Long requesterId = context.userId();
            page = service.findActiveCardsByUserId(requesterId, pageable);
        }
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> updateCard(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardCreateDto dto
    ) {
        accessPolicyService.requireCardOwnerOrAdmin(request, id);
        PaymentCardResponseDto responseDto = service.updateCard(id, dto);
        return ResponseEntity.ok().body(responseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCardStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody StatusDto dto
    ) {
        accessPolicyService.requireAdmin(request);
        service.updateCardStatus(id, dto.isStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(HttpServletRequest request, @PathVariable Long id) {
        accessPolicyService.requireCardOwnerOrAdmin(request, id);
        service.deleteCard(id);
    }
}
