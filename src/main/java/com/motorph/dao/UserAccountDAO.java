package com.motorph.dao;

import com.motorph.model.UserAccount;
import com.motorph.model.Role;
import java.util.List;

public interface UserAccountDAO extends BaseDAO {

    UserAccount findById(String userId);

    UserAccount findByEmployeeId(String employeeId);

    UserAccount findByUsername(String username);

    List<UserAccount> findAll();

    void save(UserAccount user);

    void update(UserAccount user);

    void delete(UserAccount user);

    void changeRole(int userId, Role newRole);
}