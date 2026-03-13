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
import java.util.Map;
import java.util.Set;

public interface UserService {

    UserResponseDto createUser(UserCreateDto dto);

    UserResponseDto findUser(Long id);

    UserResponseDto findUserByEmail(String email);

    Map<String, UserResponseDto> findUsersByEmails(Set<String> emails);

    Page<UserResponseDto> findUsersWithSpecification(Specification<User> specification, Pageable pageable);

    UserResponseDto updateUser(Long id, UserCreateDto dto);

    void updateUserStatus(Long id, boolean active);

    void deleteUser(Long id);

    void deleteUserHard(Long id);

    PaymentCardResponseDto addCard(Long userId, PaymentCardCreateDto dto);

    PaymentCardResponseDto findCard(Long cardId);

    Page<PaymentCardResponseDto> findActiveCards(Pageable pageable);

    Page<PaymentCardResponseDto> findActiveCardsByUserId(Long userId, Pageable pageable);

    List<PaymentCardResponseDto> findUserCards(Long userId);

    PaymentCardResponseDto updateCard(Long id, PaymentCardCreateDto dto);

    void updateCardStatus(Long cardId, boolean active);

    void deleteCard(Long cardId);
}
