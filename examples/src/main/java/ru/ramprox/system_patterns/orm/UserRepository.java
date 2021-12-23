package ru.ramprox.system_patterns.orm;

import java.sql.Connection;
import java.util.Optional;

public class UserRepository {

    private UnitOfWork unitOfWork;

    private UserMapper mapper;

    private Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
        mapper = new UserMapper(conn);
        unitOfWork = new UnitOfWork(conn, mapper);
    }

    public Optional<User> findById(long id) {
        Optional<User> user = Optional.ofNullable(unitOfWork.getUser(id));
        if(user.isPresent()) {
            return user;
        }
        user = mapper.findById(id);
        user.ifPresent(user1 -> unitOfWork.registerClean(user1));
        return user;
    }

    public void beginTransaction() {
        this.unitOfWork = new UnitOfWork(conn, mapper);
    }

    public void insert(User user) {
        unitOfWork.registerNew(user);
    }

    public void update(User user) {
        unitOfWork.registerUpdate(user);
    }

    public void delete(User user) {
        unitOfWork.registerDelete(user);
    }

    public void commitTransaction() {
        unitOfWork.commit();
        unitOfWork = null;
    }
}
