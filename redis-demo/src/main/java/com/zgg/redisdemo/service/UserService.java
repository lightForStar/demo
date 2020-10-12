package com.zgg.redisdemo.service;

import com.zgg.redisdemo.model.User;

public interface UserService {
    User getById(Integer id);

    User getByIdWithRedisTemplate(Integer id);

    void deleteById(Integer id);

    void add(User user);

    void update(User user);
}
