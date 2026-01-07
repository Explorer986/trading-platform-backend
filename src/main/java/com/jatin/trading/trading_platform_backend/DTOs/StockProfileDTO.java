package com.jatin.trading.trading_platform_backend.DTOs;

import lombok.Data;

@Data
public class StockProfileDTO {
    private String name;
    private String ticker;
    private String exchange;
    private String currency;
}
