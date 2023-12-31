package com.example.demo.security;

import com.example.demo.infrastructure.CommonUtils;
import io.ebean.config.CurrentUserProvider;

public class UserProvider implements CurrentUserProvider {

    @Override
    public Object currentUser() {
        return CommonUtils.getUserId();
    }
}
