package com.innowise.userservice.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "payment_cards",
        indexes = {
                @Index(name = "idx_payment_cards_user_id", columnList = "user_id")
        }
)
public class PaymentCard extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String number;
    private String holder;

    private LocalDate expirationDate;

    private boolean active;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getNumber() {
        return number;
    }

    public String getHolder() {
        return holder;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
