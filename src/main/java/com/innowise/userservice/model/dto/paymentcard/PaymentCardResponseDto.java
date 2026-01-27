package com.innowise.userservice.model.dto.paymentcard;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardResponseDto {

    private Long id;
    private String number;
    private boolean active;
    private Long userId;

    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }
}
