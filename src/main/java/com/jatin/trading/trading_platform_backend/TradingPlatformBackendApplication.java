package com.jatin.trading.trading_platform_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import yahoofinance.YahooFinance;

import java.io.IOException;

@SpringBootApplication
public class TradingPlatformBackendApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(TradingPlatformBackendApplication.class, args);
		System.out.println("Server is listening on port 8080!");

	}
}
