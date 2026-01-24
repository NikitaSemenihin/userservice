package com.innowise.userservice;

import com.innowise.userservice.exception.CardLimitExceedException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardRepository cardRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findUser_notFound_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUser(1L));
    }

    @Test
    void addCard_limitExceeded_throws() {
        User user = new User();
        user.setPaymentCards(List.of(
                new PaymentCard(), new PaymentCard(), new PaymentCard(),
                new PaymentCard(), new PaymentCard()
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(CardLimitExceedException.class,
                () -> userService.addCard(1L, new PaymentCard()));

    }

    @Test
    void getUserCards_returnsList() {
        List<PaymentCard> cards = List.of(new PaymentCard());
        when(cardRepository.findAllByUserId(1L)).thenReturn(cards);

        List<PaymentCard> result = userService.getUserCards(1L);

        assertEquals(1, result.size());
    }

    @Test
    void updateUserStatus_eviction() {
        doNothing().when(userRepository).updateActiveStatus(1L, true);
        userService.updateUserStatus(1L, true);
        verify(userRepository).updateActiveStatus(1L, true);
    }
}
