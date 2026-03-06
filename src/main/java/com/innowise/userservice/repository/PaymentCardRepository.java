package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>,
        JpaSpecificationExecutor<PaymentCard> {

    List<PaymentCard> findAllByUserIdAndActiveTrue(Long userId);

    Optional<PaymentCard> findByIdAndActiveTrue(Long cardId);

    Page<PaymentCard> findAllByUserIdAndActiveTrue(Long userId, Pageable pageable);

    @Modifying
    @Query(
            value = "update PaymentCard c set c.active = :active where c.id = :id"
    )
    int updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);

}
