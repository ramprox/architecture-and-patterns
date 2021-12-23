package ru.ramprox.system_patterns.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitOfWork {

    private final List<User> newUsers = new ArrayList<>();
    private final List<User> updateUsers = new ArrayList<>();
    private final List<User> deleteUsers = new ArrayList<>();

    private final Map<Long, User> identityMap = new HashMap<>();

    private final Connection conn;
    private final UserMapper mapper;

    public UnitOfWork(Connection conn, UserMapper mapper) {
        this.conn = conn;
        this.mapper = mapper;
    }

    public User getUser(long id) {
        for(User user : deleteUsers) {
            if(user.getId() == id) {
                return null;
            }
        }
        return identityMap.get(id);
    }

    public void registerNew(User user) {
        this.newUsers.add(user);
        registerClean(user);
    }

    public void registerUpdate(User user) {
        if(!this.newUsers.contains(user) && !this.updateUsers.contains(user)) {
            this.updateUsers.add(user);
        }
    }

    public void registerDelete(User user) {
        identityMap.remove(user.getId());
        if(newUsers.remove(user)) {
            return;
        }
        this.updateUsers.remove(user);
        if(!deleteUsers.contains(user)) {
            this.deleteUsers.add(user);
        }
    }

    public void registerClean(User user) {
        identityMap.put(user.getId(), user);
    }

    public void commit() {
        try {
            conn.setAutoCommit(false);
            insert();
            update();
            delete();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void update() {
        updateUsers.forEach(mapper::update);
    }

    private void insert() {
        newUsers.forEach(mapper::insert);
    }

    private void delete() {
        deleteUsers.forEach(mapper::delete);
    }
}
