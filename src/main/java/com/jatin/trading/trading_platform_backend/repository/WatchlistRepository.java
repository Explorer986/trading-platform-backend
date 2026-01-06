package com.jatin.trading.trading_platform_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jatin.trading.trading_platform_backend.entity.Watchlist;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findAllByUserId(long userId);

    Watchlist findByUserIdAndStockTicker(long userId, String stockTicker);

    void removeByUserIdAndStockTicker(long userId, String stockTicker);

}
