package com.gnani.firstSpringApi.repositories;

import com.gnani.firstSpringApi.domain.User;
import com.gnani.firstSpringApi.exceptions.EtAuthException;

public interface UserRepository {

    Integer create(String firstName, String lastName, String email, String password) throws EtAuthException;

    User findByEmail(String email, String password) throws EtAuthException;

    Integer getCountByEmail(String email);

    User findById(Integer userId);
}
