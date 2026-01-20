package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.CardLimitExceedException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PaymentCardRepository cardRepository;

    private final static int MAXIMUM_NUMBER_OF_CARDS = 5;

    public UserServiceImpl(UserRepository userRepository, PaymentCardRepository cardRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " Not Found")); //todo сделать {id}
    }

    @Override
    public Page<User> findUsers(Specification<User> specification, Pageable pageable) {
        return userRepository.findAll(specification, pageable);
    }

    @Override
    public User updateUser(Long id, User user) {
        User updatedUser = findUser(id);

        updatedUser.setName(user.getName());
        updatedUser.setSurname(user.getSurname());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setBirthDate(user.getBirthDate());
        updatedUser.setPaymentCards(user.getPaymentCards());
        return userRepository.save(updatedUser);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, boolean active) {
        userRepository.updateActiveStatus(id, active);
    }

    @Override
    @Transactional
    public PaymentCard addCard(Long userId, PaymentCard card) {
        User user = findUser(userId);

        if (user.getPaymentCards().size() >= MAXIMUM_NUMBER_OF_CARDS) {
            throw new CardLimitExceedException("User already have maximum number of cards");
        }
        card.setUser(user);
        user.getPaymentCards().add(card);

        return cardRepository.save(card);
    }

    @Override
    public List<PaymentCard> getUserCards(Long userId) {
        return cardRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public void updateCardStatus(Long cardId, boolean active) {
        cardRepository.updateActiveStatus(cardId, active);
    }
}
