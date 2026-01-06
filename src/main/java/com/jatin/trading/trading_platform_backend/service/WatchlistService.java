package com.jatin.trading.trading_platform_backend.service;

import java.util.List;
import com.jatin.trading.trading_platform_backend.entity.Watchlist;

public interface WatchlistService {

  List<Watchlist> getWatchlist(int userId);

  Watchlist addStockWatchlist(int userId, String stockTicker);

  void removeStockWatchlist(int userId, String stockTicker);

}
