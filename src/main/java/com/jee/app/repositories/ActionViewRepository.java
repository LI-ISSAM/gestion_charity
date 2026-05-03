package com.jee.app.repositories;

import com.jee.app.model.ActionView;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionViewRepository
        extends JpaRepository<ActionView, Long> {

    boolean existsByActionAndUser(CharityAction action, Users user);
}