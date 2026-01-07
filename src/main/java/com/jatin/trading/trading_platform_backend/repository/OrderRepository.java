package com.jatin.trading.trading_platform_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jatin.trading.trading_platform_backend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findAllByUserId(long userId);

  Order findByUserIdAndOrderId(long userId, long orderId);

}
