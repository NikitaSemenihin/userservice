package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.user.UserCreateDto;
import com.innowise.userservice.model.dto.user.UserResponseDto;
import com.innowise.userservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto findUser(Long id);

    Page<UserResponseDto> findUsersWithSpecification(Specification<User> specification, Pageable pageable);

    UserResponseDto updateUser(Long id, UserCreateDto dto);

    void updateUserStatus(Long id, boolean active);

    void deleteUser(Long id);

    PaymentCardResponseDto addCard(Long userId, PaymentCardCreateDto dto);

    PaymentCardResponseDto findCard(Long cardId);

    List<PaymentCardResponseDto> findUserCards(Long userId);

    void updateCardStatus(Long cardId, boolean active);

    void deleteCard(Long cardId);
}
