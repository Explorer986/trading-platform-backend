package com.jatin.trading.trading_platform_backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import com.stockmarket.stockmarketapi.filters.AuthFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;


@SpringBootApplication
@OpenAPIDefinition(
		servers = {@Server(url = "http://localhost:8080")}
)
public class TradingPlatformBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingPlatformBackendApplication.class, args);
		System.out.println("Server is listening on port 8080!");
	}
	@Bean
	public FilterRegistrationBean<AuthFilter> FilterResgistrationBean() {
		FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
		AuthFilter authFilter = new AuthFilter();
		registrationBean.setFilter(authFilter);
		registrationBean.addUrlPatterns("/api/*");
		return registrationBean;
	}
}
