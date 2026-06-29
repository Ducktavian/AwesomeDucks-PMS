
package com.motorph.service;

import com.motorph.dao.UserAccountDAO;
import com.motorph.model.UserAccount;
import com.motorph.util.PasswordUtil;
import com.motorph.util.Session;


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
        
        // Checks password
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new Exception("Invalid password.");
        }
        
        // Check if active
        if (!user.isActive()) {
            throw new Exception("User account is deactivated.");
        }
        
        return user;
    }

    /**
     * Changes the logged-in user's own password.
     * Verifies the current password, persists the new hash to DB,
     * and syncs the in-memory Session user so same-session re-changes work.
     */
    public void changePassword(String currentPassword, String newPassword) throws Exception {
        UserAccount user = Session.getCurrentUser();
        if (user == null) throw new IllegalStateException("No active session.");

        if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        user.setPasswordHash(newHash);  // keep Session in sync for same-session use
        userAccountDAO.update(user);    // persist to DB
    }

}
