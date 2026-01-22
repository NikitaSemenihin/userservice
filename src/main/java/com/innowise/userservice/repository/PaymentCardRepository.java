package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>,
        JpaSpecificationExecutor<PaymentCard> {
    @Query("select c from PaymentCard c where c.user.id = :userId")
    List<PaymentCard> findAllByUserId(Long userId);

    @Modifying
    @Query(
            value = "update payment_cards set active = :active where id = :id",
            nativeQuery = true
    )
    void updateActiveStatus(Long id, boolean active);
}
