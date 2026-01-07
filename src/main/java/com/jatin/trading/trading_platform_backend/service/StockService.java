package com.jatin.trading.trading_platform_backend.service;

import com.jatin.trading.trading_platform_backend.DTOs.StockProfileDTO;
import com.jatin.trading.trading_platform_backend.exception.BadRequestException;
import com.jatin.trading.trading_platform_backend.stockmodel.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class StockService {

  @Value("${finnhub.api.key}")
  private String apiKey;

  private final RestTemplate restTemplate = new RestTemplate();

  public StockData findStock(String ticker) {

    if (ticker == null || ticker.isBlank()) {
      throw new BadRequestException("Stock ticker cannot be empty");
    }

    try {
      String url =
              "https://finnhub.io/api/v1/quote?symbol=" + ticker.toUpperCase()
                      + "&token=" + apiKey;

      Map<String, Object> res = restTemplate.getForObject(url, Map.class);

      if (res == null || res.get("c") == null) {
        throw new BadRequestException("Invalid stock ticker");
      }

      Double currentPrice = (Double) res.get("c");
      if (currentPrice == 0.0) {
        throw new BadRequestException("Invalid stock ticker");
      }

      StockData data = new StockData();
      data.setStockTicker(ticker.toUpperCase());
      data.setStockPrice(BigDecimal.valueOf(currentPrice));
      data.setDayHigh(BigDecimal.valueOf((Double) res.get("h")));
      data.setDayLow(BigDecimal.valueOf((Double) res.get("l")));
      data.setPreviousDayClose(BigDecimal.valueOf((Double) res.get("pc")));

      return data;

    } catch (BadRequestException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Stock service unavailable");
    }
  }

  public String getCompanyName(String ticker) {
    try {
      String profileUrl =
              "https://finnhub.io/api/v1/stock/profile2?symbol=" + ticker + "&token=" + apiKey;

      StockProfileDTO profile =
              restTemplate.getForObject(profileUrl, StockProfileDTO.class);

      if (profile == null || profile.getName() == null) {
        return ticker.toUpperCase(); // fallback
      }

      return profile.getName();

    } catch (Exception e) {
      return ticker.toUpperCase(); // fallback safety
    }
  }
}
