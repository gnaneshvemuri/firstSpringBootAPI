package com.gnani.firstSpringApi.services;

import com.gnani.firstSpringApi.domain.User;
import com.gnani.firstSpringApi.exceptions.EtAuthException;

public interface UserService {

    // To validate user
    User validateUser(String email, String password) throws EtAuthException;

    // To register the user
    User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException;

}
