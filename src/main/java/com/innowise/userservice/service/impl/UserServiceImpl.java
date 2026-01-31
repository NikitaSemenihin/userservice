package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.CardLimitExceedException;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.user.UserCreateDto;
import com.innowise.userservice.model.dto.user.UserResponseDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PaymentCardRepository cardRepository;
    private final UserMapper userMapper;
    private final PaymentCardMapper cardMapper;

    private static final short MAXIMUM_NUMBER_OF_CARDS = 5;

    public UserServiceImpl(UserRepository userRepository, PaymentCardRepository cardRepository, UserMapper userMapper, PaymentCardMapper cardMapper) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.userMapper = userMapper;
        this.cardMapper = cardMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#result.id")
    public UserResponseDto createUser(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        user.setActive(true);

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    @Cacheable(
            value = "users",
            key = "#id",
            unless = "#result == null"
    )
    public UserResponseDto findUser(Long id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %d Not Found", id)));

        return userMapper.toDto(user);
    }

    @Override
    public Page<UserResponseDto> findUsersWithSpecification(Specification<User> specification, Pageable pageable) {

        Specification<User> activeSpecification =
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.isTrue(root.get("active")));

        Specification<User> finalSpecification =
                specification == null
                        ? activeSpecification
                        : specification.and(activeSpecification);
        return userRepository.findAll(finalSpecification, pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponseDto updateUser(Long id, UserCreateDto dto) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id: %d not found".formatted(id))
                ));

        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setBirthDate(dto.getBirthDate());
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void updateUserStatus(Long id, boolean active) {
        int updated = userRepository.updateActiveStatus(id, active);

        if (updated == 0) {
            throw new UserNotFoundException(String.format("User with id: %d not found", id));
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id: %d not found", id)
                ));

        user.setActive(false);

        user.getPaymentCards()
                .forEach(card -> card.setActive(false));
    }

    @Override
    @Transactional
    @CacheEvict(value = "userCards", key = "#userId")
    public PaymentCardResponseDto addCard(Long userId, PaymentCardCreateDto dto) {
        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id: %d not found", userId)
                ));

        short activeCards = (short) user.getPaymentCards().stream()
                .filter(PaymentCard::isActive)
                .count();

        if (activeCards >= MAXIMUM_NUMBER_OF_CARDS) {
            throw new CardLimitExceedException("User already have maximum number of cards");
        }

        PaymentCard card = cardMapper.toEntity(dto);
        card.setUser(user);
        card.setActive(true);
        user.getPaymentCards().add(card);

        PaymentCard saved = cardRepository.save(card);

        return cardMapper.toDto(saved);
    }

    @Override
    public List<PaymentCardResponseDto> findUserCards(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("User with id: %d not found", userId));
        }

        return cardRepository.findAllByUserIdAndActiveTrue(userId).stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Override
    public Page<PaymentCardResponseDto> findActiveCards(Pageable pageable) {
        Specification<PaymentCard> activeSpecification =
                ((root, query, criteriaBuilder) ->
                        criteriaBuilder.isTrue(root.get("active")));

        return cardRepository.findAll(activeSpecification, pageable).map(cardMapper::toDto);
    }

    @Override
    @Cacheable(
            value = "userCards",
            key = "#cardId",
            unless = "#result == null"
    )
    public PaymentCardResponseDto findCard(Long cardId) {
        PaymentCard card = cardRepository.findByIdAndActiveTrue(cardId)
                .orElseThrow(() -> new CardNotFoundException(String.format("Card with id: %d Not Found", cardId)));

        return cardMapper.toDto(card);
    }

    @Override
    @Transactional
    @CachePut(value = "userCards", key = "#cardId")
    public PaymentCardResponseDto updateCard(Long id, PaymentCardCreateDto dto) {
        PaymentCard card = cardRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new CardNotFoundException(
                        String.format("Payment card with id: %d not found", id)
                ));

        card.setNumber(dto.getNumber());
        card.setExpirationDate(dto.getExpirationDate());
        return cardMapper.toDto(card);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userCards", allEntries = true)
    public void updateCardStatus(Long cardId, boolean active) {
        int updated = cardRepository.updateActiveStatus(cardId, active);

        if (updated == 0) {
            throw new CardNotFoundException(String.format("Card with id: %d not found", cardId));
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "userCards", allEntries = true)
    public void deleteCard(Long cardId) {
        PaymentCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(String.format("Card with id: %d not found", cardId)));

        card.setActive(false);
    }
}
