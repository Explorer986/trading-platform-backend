package com.jatin.trading.trading_platform_backend.service;

import java.util.List;
import com.jatin.trading.trading_platform_backend.DTOs.OrderSubmitDTO;
import com.jatin.trading.trading_platform_backend.entity.Order;

public interface OrderService {

  List<Order> getAllOrders(int userId);
  Order getOrder(int userId, int orderId);
  Order submitOrder(int userId, OrderSubmitDTO orderSubmitDTO);

  void deleteOrder(Integer userId, Integer orderId);
}
