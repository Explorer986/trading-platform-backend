package com.jatin.trading.trading_platform_backend.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jatin.trading.trading_platform_backend.DTOs.OrderSubmitDTO;
import com.jatin.trading.trading_platform_backend.entity.Order;
import com.jatin.trading.trading_platform_backend.entity.Portfolio;
import com.jatin.trading.trading_platform_backend.entity.User;
import com.jatin.trading.trading_platform_backend.exception.BadRequestException;
import com.jatin.trading.trading_platform_backend.exception.OrderNotFilledException;
import com.jatin.trading.trading_platform_backend.exception.ResourceNotFoundException;
import com.jatin.trading.trading_platform_backend.repository.OrderRepository;
import com.jatin.trading.trading_platform_backend.repository.PortfolioRepository;
import com.jatin.trading.trading_platform_backend.repository.UserRepository;
import com.jatin.trading.trading_platform_backend.stockmodel.StockData;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private PortfolioRepository portfolioRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StockService stockService;

  enum ORDER_TYPE {
    BUY, SELL
  }

  // ✅ DO NOT throw exception for empty list
  @Override
  public List<Order> getAllOrders(int userId) {
    return orderRepository.findAllByUserId(Long.valueOf(userId));
  }

  @Override
  public Order getOrder(int userId, int orderId) {
    Order order =
            orderRepository.findByUserIdAndOrderId(
                    Long.valueOf(userId), Long.valueOf(orderId));

    if (order == null) {
      throw new ResourceNotFoundException("Order not found");
    }
    return order;
  }

  @Override
  public Order submitOrder(int userId, OrderSubmitDTO dto) {

    // 🔹 Normalize input
    if (dto.getOrderType() == null || dto.getStockTicker() == null) {
      throw new BadRequestException("Invalid input");
    }

    dto.setOrderType(dto.getOrderType().trim().toUpperCase());
    dto.setStockTicker(dto.getStockTicker().trim().toUpperCase());

    if (dto.getStockTicker().isBlank() || dto.getOrderType().isBlank()) {
      throw new BadRequestException("Fill all fields");
    }

    ORDER_TYPE type;
    try {
      type = ORDER_TYPE.valueOf(dto.getOrderType());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Order type must be BUY or SELL");
    }

    if (dto.getNoOfShares() <= 0) {
      throw new BadRequestException("Shares must be positive");
    }

    // 🔹 Market price from Finnhub
    StockData stockData = stockService.findStock(dto.getStockTicker());
    double marketPrice = stockData.getStockPrice().doubleValue();
    double totalCost = marketPrice * dto.getNoOfShares();

    // 🔹 Fetch user
    User user = userRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    double balance = user.getBalance();

    // 🔹 Fetch portfolio if exists
    Portfolio portfolio =
            portfolioRepository.findByUserIdAndStockTicker(
                    Long.valueOf(userId), dto.getStockTicker());

    // 🔴 SELL validations
    if (type == ORDER_TYPE.SELL) {
      if (portfolio == null) {
        throw new OrderNotFilledException("Stock not present in portfolio");
      }
      if (dto.getNoOfShares() > portfolio.getNoOfShares()) {
        throw new OrderNotFilledException("Insufficient shares to sell");
      }
    }

    // 🔴 BUY balance check
    if (type == ORDER_TYPE.BUY && balance < totalCost) {
      throw new OrderNotFilledException("Insufficient balance");
    }

    // 💰 Update balance
    if (type == ORDER_TYPE.BUY) {
      balance -= totalCost;
    } else {
      balance += totalCost;
    }

    // 💾 Save order
    Order order = new Order(
            userId,
            dto.getStockTicker(),
            dto.getOrderType(),
            dto.getNoOfShares(),
            marketPrice
    );
    orderRepository.save(order);

    // 💾 Update user balance
    user.setBalance(balance);
    userRepository.save(user);

    // 📦 Portfolio handling
    if (type == ORDER_TYPE.BUY) {

      if (portfolio == null) {
        String companyName = stockService.getCompanyName(dto.getStockTicker());

        portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setStockTicker(dto.getStockTicker());
        portfolio.setStockName(companyName);
        portfolio.setNoOfShares(dto.getNoOfShares());
        portfolio.setPrice(marketPrice);
        portfolio.setCost(marketPrice);
      } else {
        int totalShares =
                portfolio.getNoOfShares() + dto.getNoOfShares();

        double avgCost =
                ((portfolio.getCost() * portfolio.getNoOfShares()) + totalCost)
                        / totalShares;

        portfolio.setNoOfShares(totalShares);
        portfolio.setCost(avgCost);
        portfolio.setPrice(marketPrice);
      }

      updatePNL(portfolio);
      portfolioRepository.save(portfolio);
    }

    if (type == ORDER_TYPE.SELL) {
      int remainingShares =
              portfolio.getNoOfShares() - dto.getNoOfShares();

      if (remainingShares == 0) {
        portfolioRepository.delete(portfolio);
      } else {
        portfolio.setNoOfShares(remainingShares);
        portfolio.setPrice(marketPrice);
        updatePNL(portfolio);
        portfolioRepository.save(portfolio);
      }
    }

    return order;
  }

  private void updatePNL(Portfolio p) {
    double pnlPercent =
            ((p.getPrice() - p.getCost()) / p.getCost()) * 100;
    double pnlAmount =
            (p.getPrice() - p.getCost()) * p.getNoOfShares();

    p.setPNLInPercentage(pnlPercent);
    p.setPNLInDollars(pnlAmount);
  }

  @Override
  public void deleteOrder(Integer userId, Integer orderId) {
    Order order =
            orderRepository.findByUserIdAndOrderId(
                    Long.valueOf(userId), Long.valueOf(orderId));

    if (order == null) {
      throw new ResourceNotFoundException("Order not found");
    }
    orderRepository.delete(order);
  }
}
