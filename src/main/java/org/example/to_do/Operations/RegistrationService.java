package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UsersOperator usersOperator;

    public RegistrationService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public void RegUser(String username, String email, String password) {
        usersOperator.InsertUser(username, email, password);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
    }
}
