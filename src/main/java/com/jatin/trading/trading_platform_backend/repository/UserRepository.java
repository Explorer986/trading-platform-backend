package com.jatin.trading.trading_platform_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.jatin.trading.trading_platform_backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = "SELECT COUNT(*) FROM accounts a WHERE a.email = ?1", nativeQuery = true)
  int getCountByEmail(String email);

  User findByEmail(String email);

  @Query(value = "SELECT COUNT(*) FROM accounts a WHERE a.username = ?1", nativeQuery = true)
  int getCountByUsername(String username);

  User findByUsername(String username);
}
