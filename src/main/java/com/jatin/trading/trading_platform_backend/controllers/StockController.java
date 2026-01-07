package com.jatin.trading.trading_platform_backend.controllers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jatin.trading.trading_platform_backend.service.StockService;
import com.jatin.trading.trading_platform_backend.stockmodel.StockData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(
        name = "Stock Controller",
        description = "Retrieves real-time stock price data using external provider"
)
@RestController
@RequestMapping("/stocks")
public class StockController {

  @Autowired
  StockService stockService;

  @Operation(
          summary = "Get stock data",
          description = "Retrieves current price and daily high/low for a stock"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Successfully retrieved stock details",
                  content = @Content(schema = @Schema(implementation = StockData.class))
          )
  })
  @GetMapping(value = "/{stockTicker}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StockData> getStock(
          @PathVariable("stockTicker") String stockTicker) {
    System.out.println("\n\n\n  stocks API hit \n\n\n");
    StockData stockData = stockService.findStock(stockTicker);
    return ResponseEntity.ok(stockData);
  }
}
