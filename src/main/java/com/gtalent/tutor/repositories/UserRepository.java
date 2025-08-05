package com.gtalent.tutor.repositories;

import com.gtalent.tutor.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
