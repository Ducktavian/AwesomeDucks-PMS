package com.motorph.dao;

import com.motorph.model.UserAccount;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface UserAccountDAO extends BaseDAO {
    UserAccount findById(String userId);
    UserAccount findByEmployeeId(String employeeId);
    List<UserAccount> findAll();
    void save(UserAccount user);
    void update(UserAccount user);
    void delete(UserAccount user);
}
