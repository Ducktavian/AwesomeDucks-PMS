
package com.motorph.service;

import com.motorph.dao.UserAccountDAO;
import com.motorph.model.UserAccount;
import com.motorph.util.PasswordUtil;


public class AuthService {
    
    private UserAccountDAO userAccountDAO;
    
    public AuthService(UserAccountDAO userAccountDAO) {
        this.userAccountDAO = userAccountDAO;
    }
    
    public UserAccount login(String username, String password) throws Exception {
        
        UserAccount user = userAccountDAO.findByUsername(username);
        
        if (user == null) {
            throw new Exception("User not found.");
        }
        
        // Checks passowrd
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new Exception("Invalid password.");
        }
        
        // Check if active
        if (!user.isActive()) {
            throw new Exception("User account is deactivated.");
        }
        
        return user;
    }
        
    
 
}
