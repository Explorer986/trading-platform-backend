package com.jatin.trading.trading_platform_backend.service;

import com.jatin.trading.trading_platform_backend.DTOs.UserAmountDTO;
import com.jatin.trading.trading_platform_backend.DTOs.UserLoginDTO;
import com.jatin.trading.trading_platform_backend.DTOs.UserRegisterDTO;
import com.jatin.trading.trading_platform_backend.entity.User;

public interface UserService {

  User validateUser(UserLoginDTO userLoginDTO);

  UserRegisterDTO registerUser(UserRegisterDTO userRegisterDTO);

  UserAmountDTO updateUserBalance(int userId, UserAmountDTO userAmount);

}
