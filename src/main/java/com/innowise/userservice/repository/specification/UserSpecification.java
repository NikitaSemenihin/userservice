package com.innowise.userservice.repository.specification;

import com.innowise.userservice.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null :
                criteriaBuilder.equal(root.get("name"), name);
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, criteriaBuilder) ->
                surname == null ? null :
                        criteriaBuilder.equal(root.get("surname"), surname);
    }
}
