package com.jatin.trading.trading_platform_backend.service;

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
    try {
      String url =
              "https://finnhub.io/api/v1/quote?symbol=" + ticker.toUpperCase()
                      + "&token=" + apiKey;

      Map<String, Object> res =
              restTemplate.getForObject(url, Map.class);

      if (res == null || (Double) res.get("c") == 0.0) {
        throw new BadRequestException("Invalid stock ticker");
      }

      StockData data = new StockData();
      data.setStockTicker(ticker.toUpperCase());
      data.setStockPrice(BigDecimal.valueOf((Double) res.get("c")));
      data.setDayHigh(BigDecimal.valueOf((Double) res.get("h")));
      data.setDayLow(BigDecimal.valueOf((Double) res.get("l")));
      data.setPreviousDayClose(BigDecimal.valueOf((Double) res.get("pc")));

      return data;

    } catch (Exception e) {
      throw new RuntimeException("Stock service unavailable");
    }
  }
}
