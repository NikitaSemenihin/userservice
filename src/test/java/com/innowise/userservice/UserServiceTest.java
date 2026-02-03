package com.innowise.userservice;

import com.innowise.userservice.exception.CardLimitExceedException;
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
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardRepository cardRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PaymentCardMapper cardMapper;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createUser_success() {
        UserCreateDto dto = new UserCreateDto();

        dto.setName("Michael");
        dto.setSurname("Bay");
        dto.setEmail("Michael@michaelbay.com");
        dto.setBirthDate(LocalDate.of(1965, 2, 17));


        User mappedUser = new User();
        mappedUser.setId(1L);
        mappedUser.setActive(true);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setActive(true);
        responseDto.setId(1L);

        when(userMapper.toEntity(dto)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(mappedUser);
        when(userMapper.toDto(mappedUser)).thenReturn(responseDto);

        UserResponseDto result = service.createUser(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userMapper).toEntity(dto);
        verify(userRepository).save(mappedUser);
    }

    @Test
    void updateUser_success() {
        UserCreateDto dto = new UserCreateDto();

        dto.setName("Tom");
        dto.setSurname("Cruise");
        dto.setEmail("tom@andjerry.com");
        dto.setBirthDate(LocalDate.of(1962, 7, 3));

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setActive(true);

        when(userRepository.findByIdAndActiveTrue(existingUser.getId()))
                .thenReturn(Optional.of(existingUser));


        service.updateUser(1L, dto);

        assertEquals("Tom", existingUser.getName());
        assertEquals("Cruise", existingUser.getSurname());
        assertEquals("tom@andjerry.com", existingUser.getEmail());
    }


    @Test
    void addCard_success() {
        PaymentCardCreateDto dto = new PaymentCardCreateDto();

        dto.setNumber("4585234558485025");
        dto.setExpirationDate(LocalDate.of(9999, 12, 31));

        User user = new User();
        user.setId(1L);
        user.setPaymentCards(new ArrayList<>());

        PaymentCard card = new PaymentCard();
        card.setActive(true);

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto();

        when(userRepository.findByIdAndActiveTrue(user.getId()))
                .thenReturn(Optional.of(user));
        when(cardMapper.toEntity(dto)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(responseDto);

        PaymentCardResponseDto result = service.addCard(user.getId(), dto);

        assertNotNull(result);
        assertEquals(user, card.getUser());
        verify(cardRepository).save(card);
    }

    @Test
    void addCard_limitExceeded_throws() {
        User user = new User();
        user.setPaymentCards(
                IntStream.range(0, 5)
                        .mapToObj(i -> {
                            PaymentCard c = new PaymentCard();
                            c.setActive(true);
                            return c;
                        })
                        .toList()
        );

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        assertThrows(
                CardLimitExceedException.class,
                () -> service.addCard(1L, new PaymentCardCreateDto())
        );

        verify(cardRepository, never()).save(any());
    }

    @Test
    void findUserCards_success() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(cardRepository.findAllByUserIdAndActiveTrue(userId))
                .thenReturn(List.of(new PaymentCard(), new PaymentCard()));

        List<PaymentCardResponseDto> result = service.findUserCards(userId);

        assertEquals(2, result.size());
    }

    @Test
    void deleteUser_shouldDeactivateUserAndCards() {
        User user = new User();
        user.setActive(true);

        List<PaymentCard> cards = List.of(activeCard(), activeCard());
        user.setPaymentCards(cards);

        when(userRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(user));

        service.deleteUser(1L);

        assertFalse(user.isActive());
        user.getPaymentCards()
                .forEach(card -> assertFalse(card.isActive()));
    }

    private PaymentCard activeCard() {
        PaymentCard card = new PaymentCard();
        card.setActive(true);
        return card;
    }
}
