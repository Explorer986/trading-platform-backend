package com.jatin.trading.trading_platform_backend.exception;

public class OrderNotFilledException extends RuntimeException {

  public OrderNotFilledException(String message) {
    super(message);
  }
  
}
