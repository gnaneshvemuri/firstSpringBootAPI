package com.gnani.firstSpringApi.services;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gnani.firstSpringApi.domain.User;
import com.gnani.firstSpringApi.exceptions.EtAuthException;
import com.gnani.firstSpringApi.repositories.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    // This is for accessing the DB data using interface so there is decoupling
    // between different tires of services like Business service, Data service,
    // Resources services.
    @Autowired
    UserRepository userRepository;

    @Override
    public User validateUser(String email, String password) throws EtAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if (email != null)
            email = email.toLowerCase();
        if (!pattern.matcher(email).matches())
            throw new EtAuthException("Invalid Email");
        Integer count = userRepository.getCountByEmail(email);
        if (count == 0)
            throw new EtAuthException("No such User exists");
        return userRepository.findByEmail(email, password);
    }

    @Override
    public User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException {

        Pattern pattern = Pattern.compile("^(.+)@(.+)$");

        if (email != null)
            email = email.toLowerCase();
        if (!pattern.matcher(email).matches())
            throw new EtAuthException("Invalid Email Format");
        Integer count = userRepository.getCountByEmail(email);

        if (count != 0)
            throw new EtAuthException("Email already exists");

        Integer userId = userRepository.create(firstName, lastName, email, password);

        return userRepository.findById(userId);
    }

}
