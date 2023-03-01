package com.gnani.firstSpringApi.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.gnani.firstSpringApi.domain.User;
import com.gnani.firstSpringApi.exceptions.EtAuthException;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String SQL_CREATE = "INSERT INTO ET_USERS(user_id,first_name,last_name,email,passowrd) VALUES(NEXTVAL('ET_USER_SEQ'),?,?,?,?);";
    private static final String SQL_COUNT_EMAIL = "SELECT COUNT(*) FROM ET_USERS WHERE EMAIL= ?;";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM ET_USERS WHERE USER_ID= ?;";
    private static final String SQL_FIND_BY_EMAIL = "SELECT * FROM ET_USERS WHERE EMAIL= ?;";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(String firstName, String lastName, String email, String password) throws EtAuthException {
        try {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

            KeyHolder kHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, hashedPassword);
                return ps;
            }, kHolder);
            return Optional.ofNullable(kHolder.getKeys())
                    .map(keys -> (Integer) keys.get("user_id"))
                    .orElse(null);

        } catch (Exception e) {
            throw new EtAuthException("Exception occurred while creating a user " + e);
        }

    }

    @Override
    public User findByEmail(String email, String password) throws EtAuthException {
        try {
            User user = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, new Object[] { email }, userRowMapper);
            if(user !=null){
                if (!BCrypt.checkpw(password, user.getPassword()))
                throw new EtAuthException("Invalid password for the login");
                return user;
            }
            throw new EtAuthException("No Such user found");
        } catch (EmptyResultDataAccessException e) {
            throw new EtAuthException("Invalid username or password");
        }
    }

    private RowMapper<User> userRowMapper = ((rs, rowNum) -> {
        return new User(rs.getInt("user_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("passowrd"));
    });

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.gnani.firstSpringApi.repositories.UserRepository#getCountByEmail(java.
     * lang.String)
     * This method gets the count of user by the email ID.
     */
    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_EMAIL, new Object[] { email }, Integer.class);
    }

    @Override
    public User findById(Integer userId) {

        return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[] { userId }, new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User(rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("passowrd"));
                return user;
            }
        });

    }

}