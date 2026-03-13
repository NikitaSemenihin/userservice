package com.innowise.userservice.service;

import com.innowise.userservice.config.AuthContextInterceptor;
import com.innowise.userservice.config.RequestAuthContext;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.ForbiddenException;
import com.innowise.userservice.exception.UnauthorizedException;
import com.innowise.userservice.repository.PaymentCardRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AccessPolicyService {

    private final PaymentCardRepository paymentCardRepository;

    public AccessPolicyService(PaymentCardRepository paymentCardRepository) {
        this.paymentCardRepository = paymentCardRepository;
    }

    public RequestAuthContext requireContext(HttpServletRequest request) {
        Object attribute = request.getAttribute(AuthContextInterceptor.AUTH_CONTEXT_ATTR);
        if (!(attribute instanceof RequestAuthContext context)) {
            throw new UnauthorizedException("Missing authentication context");
        }
        return context;
    }

    public void requireAdmin(HttpServletRequest request) {
        RequestAuthContext context = requireContext(request);
        if (!context.isAdmin()) {
            throw new ForbiddenException("Admin role required");
        }
    }

    public void requireService(HttpServletRequest request, String expectedService) {
        RequestAuthContext context = requireContext(request);
        if (!expectedService.equalsIgnoreCase(context.serviceName())) {
            throw new ForbiddenException("Access allowed only for " + expectedService);
        }
    }

    public void requireSelfOrAdmin(HttpServletRequest request, Long targetUserId) {
        RequestAuthContext context = requireContext(request);
        if (context.isAdmin()) {
            return;
        }
        if (!context.isEndUser() || !targetUserId.equals(context.userId())) {
            throw new ForbiddenException("Access allowed only for owner or admin");
        }
    }

    public void requireUserOrAdmin(HttpServletRequest request) {
        RequestAuthContext context = requireContext(request);
        if (!context.isEndUser()) {
            throw new ForbiddenException("Only USER or ADMIN can access this endpoint");
        }
    }

    public void requireCardOwnerOrAdmin(HttpServletRequest request, Long cardId) {
        RequestAuthContext context = requireContext(request);
        if (context.isAdmin()) {
            return;
        }
        if (!context.isEndUser()) {
            throw new ForbiddenException("Access allowed only for card owner or admin");
        }

        Long ownerId = paymentCardRepository.findById(cardId)
                .map(card -> card.getUser().getId())
                .orElseThrow(() -> new CardNotFoundException("Card with id: " + cardId + " not found"));

        if (!ownerId.equals(context.userId())) {
            throw new ForbiddenException("Access allowed only for card owner or admin");
        }
    }
}
