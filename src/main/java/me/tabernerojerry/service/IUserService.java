package me.tabernerojerry.service;

import me.tabernerojerry.domain.User;

public interface IUserService {

    // TODO: user UserDTO
    User saveUser(User user);

    Boolean verifyToken(String token);
}
