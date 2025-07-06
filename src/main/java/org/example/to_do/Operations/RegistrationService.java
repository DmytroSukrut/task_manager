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
        usersOperator.insertUser(username, email, password);
        System.out.println(username);
        System.out.println(email);
        System.out.println(password);
    }


}
