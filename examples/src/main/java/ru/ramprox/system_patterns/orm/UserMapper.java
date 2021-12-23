package ru.ramprox.system_patterns.orm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserMapper {

    private static String findByIdQuery = "select id, username, password from users where id = ?;";
    private static String insertQuery = "insert into users (id, username, password) value(?, ?, ?);";
    private static String updateQuery = "update users set id = ?, username = ?, password = ? where id = ?;";
    private static String deleteQuery = "delete from users where id = ?;";

    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    private Connection conn;

    public UserMapper(Connection conn) {
        this.conn = conn;
    }

    public Optional<User> findById(long id) {
        try (PreparedStatement selectUser = conn.prepareStatement(findByIdQuery)) {
            selectUser.setLong(1, id);
            ResultSet rs = selectUser.executeQuery();
            logger.info(selectUser.toString().substring(selectUser.getClass().getName().length() + 2));
            if (rs.next()) {
                User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return Optional.empty();
    }

    public void insert(User user) {
        try (PreparedStatement insertUser = conn.prepareStatement(insertQuery)) {
            insertUser.setLong(1, user.getId());
            insertUser.setString(2, user.getUsername());
            insertUser.setString(3, user.getPassword());
            insertUser.executeUpdate();
            logger.info(insertUser.toString().substring(insertUser.getClass().getName().length() + 2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        try (PreparedStatement updateUser = conn.prepareStatement(updateQuery)) {
            updateUser.setLong(1, user.getId());
            updateUser.setString(2, user.getUsername());
            updateUser.setString(3, user.getPassword());
            updateUser.setLong(4, user.getId());
            updateUser.executeUpdate();
            logger.info(updateUser.toString().substring(updateUser.getClass().getName().length() + 2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(User user) {
        try (PreparedStatement deleteUser = conn.prepareStatement(deleteQuery)) {
            deleteUser.setLong(1, user.getId());
            deleteUser.execute();
            logger.info(deleteUser.toString().substring(deleteUser.getClass().getName().length() + 2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
