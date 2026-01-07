package com.jatin.trading.trading_platform_backend.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jatin.trading.trading_platform_backend.Constants;
import com.jatin.trading.trading_platform_backend.DTOs.UserAccessTokenDTO;
import com.jatin.trading.trading_platform_backend.DTOs.UserAmountDTO;
import com.jatin.trading.trading_platform_backend.DTOs.UserLoginDTO;
import com.jatin.trading.trading_platform_backend.DTOs.UserRegisterDTO;
import com.jatin.trading.trading_platform_backend.entity.User;
import com.jatin.trading.trading_platform_backend.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import com.jatin.trading.trading_platform_backend.security.JwtUtil;
@Tag(name = "User Controller",
                description = "To sign up for an account and log in with the provided login credentials. Provides endpoint for withdrawing and depositing cash into the created account.")
@RestController
@RequestMapping("/auth")
public class UserController {

        @Autowired
        UserService userService;

        @Operation(summary = "Register user", description = "Adds the user to the database. Must have unique email and password.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Sign Up was successful!",
                                        content = @Content(schema = @Schema(
                                                        implementation = UserRegisterDTO.class))),
                        @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                                        content = @Content),
                        @ApiResponse(responseCode = "409", description = "CONFLICT",
                                        content = @Content)})
        @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<UserRegisterDTO> registerUser(
                        @RequestBody UserRegisterDTO userRegisterDTO) {
                System.out.println("\n\n\n Register user API hit \n\n\n\n");
                userService.registerUser(userRegisterDTO);
                return new ResponseEntity<>(userRegisterDTO, HttpStatus.CREATED);
        }

        @Operation(summary = "Login user", description = "Authenticates the user. Returns JWT Token for Bearer authentication.")
//        @ApiResponses(value = {
//                @ApiResponse(responseCode = "200", description = "Successful login!", content = @Content(schema = @Schema(implementation = UserAccessTokenDTO.class))),
//                @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content),
//                @ApiResponse(responseCode = "401", description = "UNAUTHORIZED", content = @Content)
//        })
        @PostMapping(value = "/login")
        public Map<String, String> login(@RequestBody UserLoginDTO userLoginDTO) {
                System.out.println("\n\n\n login api hit \n\n\n ");
                User user = userService.validateUser(userLoginDTO);
                String token;
                token = JwtUtil.generateToken(userLoginDTO.getUsername());
                System.out.println(token);
                return Map.of("token", token);
        }
//        public ResponseEntity<UserAccessTokenDTO> loginUser(
//                        @RequestBody UserLoginDTO userLoginDTO) {
//                User user = userService.validateUser(userLoginDTO);
//                Map<String, String> accessTokenMap = generateJWTToken(user);
//                UserAccessTokenDTO accessToken =
//                                new UserAccessTokenDTO(accessTokenMap.get("accessToken"));
//                return new ResponseEntity<>(accessToken, HttpStatus.OK);
//        }

        @Operation(summary = "Deposit to user account", description = "To load up funds to user's accounts to purchase stocks.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully deposited amount into user's account", content = @Content(schema = @Schema(implementation = UserAmountDTO.class))),
                @ApiResponse(responseCode = "404", description = "NOT FOUND (when user does not exist)", content = @Content)
        })
        @PutMapping(value = "/api/deposit", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<UserAmountDTO> depositUserBalance(HttpServletRequest request,
                        @RequestBody UserAmountDTO userAmountDTO) {
                Integer userId = (Integer) request.getAttribute("userId");
                userAmountDTO = userService.updateUserBalance(userId, userAmountDTO);
                return new ResponseEntity<>(userAmountDTO, HttpStatus.OK);
        }

        @Operation(summary = "Withdraw from user account", description = "To collect profits from user's accounts.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully withdrew amount from user's account", content = @Content(schema = @Schema(implementation = UserAmountDTO.class))),
                @ApiResponse(responseCode = "404", description = "NOT FOUND (when user does not exist)", content = @Content),
                @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content)
        })
        @PutMapping(value = "/api/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<UserAmountDTO> withdrawUserBalance(HttpServletRequest request,
                        @RequestBody UserAmountDTO userAmountDTO) {
                Integer userId = (Integer) request.getAttribute("userId");
                userAmountDTO.setAmount(-1.0 * userAmountDTO.getAmount());
                userAmountDTO = userService.updateUserBalance(userId, userAmountDTO);
                return new ResponseEntity<>(userAmountDTO, HttpStatus.OK);
        }

        public static Map<String, String> generateJWTToken(User user) {
                long timestamp = System.currentTimeMillis();
                String token = Jwts.builder()
                                .signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                                .setIssuedAt(new Date(timestamp))
                                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
                                .claim("userId", user.getUserId()).claim("email", user.getEmail())
                                .compact();
                Map<String, String> map = new HashMap<>();
                map.put("accessToken", token);
                return map;
        }

}
