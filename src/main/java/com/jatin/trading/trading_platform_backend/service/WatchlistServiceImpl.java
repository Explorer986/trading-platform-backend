package com.jatin.trading.trading_platform_backend.service;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jatin.trading.trading_platform_backend.entity.Watchlist;
import com.jatin.trading.trading_platform_backend.exception.InternalServerErrorException;
import com.jatin.trading.trading_platform_backend.exception.ResourceAlreadyExistsException;
import com.jatin.trading.trading_platform_backend.exception.ResourceNotFoundException;
import com.jatin.trading.trading_platform_backend.repository.WatchlistRepository;
import com.jatin.trading.trading_platform_backend.stockmodel.StockData;

@Service
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

  @Autowired
  WatchlistRepository watchlistRepository;

  @Autowired
  StockService stockService;

  @Override
  public List<Watchlist> getWatchlist(int userId) {
    List<Watchlist> list = watchlistRepository.findAllByUserId(Integer.valueOf(userId));
    if (list == null || list.isEmpty()) {
      throw new ResourceNotFoundException("Watchlist is empty");
    }
    return list;
  }

  @Override
  public Watchlist addStockWatchlist(int userId, String stockTicker) {

    try {
      stockTicker = stockTicker.trim().toUpperCase();

      Watchlist existing =
              watchlistRepository.findByUserIdAndStockTicker(Long.valueOf(userId), stockTicker);

      if (existing != null) {
        throw new ResourceAlreadyExistsException("Stock already in watchlist");
      }

      // 🔥 Fetch stock price from Finnhub
      StockData stockData = stockService.findStock(stockTicker);

      Watchlist watchlist = new Watchlist();
      String companyName =
              stockService.getCompanyName(stockTicker);

      watchlist.setUserId(userId);
      watchlist.setStockTicker(stockTicker.toUpperCase());
      watchlist.setStockName(companyName);
      watchlist.setPrice(stockData.getStockPrice().doubleValue());
      watchlist.setPreviousDayClose(stockData.getPreviousDayClose().doubleValue());

      return watchlistRepository.save(watchlist);

    } catch (Exception e) {
      throw new InternalServerErrorException("Unable to add stock to watchlist");
    }
  }

  @Override
  public void removeStockWatchlist(int userId, String stockTicker) {
    watchlistRepository.removeByUserIdAndStockTicker(
            Long.valueOf(userId), stockTicker.toUpperCase());
  }
}
