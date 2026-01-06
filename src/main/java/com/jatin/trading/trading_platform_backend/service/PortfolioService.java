package com.jatin.trading.trading_platform_backend.service;

import java.util.List;
import com.jatin.trading.trading_platform_backend.entity.Portfolio;

public interface PortfolioService {
  
  List<Portfolio> getPortfolio(int userId);
  Portfolio getPortfolioStock(int userId, int portfolioId);

}
