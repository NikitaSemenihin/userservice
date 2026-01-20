package com.innowise.userservice.service;

import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User findUser(Long id);

    Page<User> findUsers(Specification<User> specification, Pageable pageable);

    User updateUser(Long id, User user);

    void updateUserStatus(Long id, boolean active);

    PaymentCard addCard(Long userId, PaymentCard card);

    List<PaymentCard> getUserCards(Long userId);

    void updateCardStatus(Long cardId, boolean active);
}
