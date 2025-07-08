package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UsersOperator usersOperator;

    public LoginService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public boolean LoginUser(String email, String password) {
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        return usersOperator.CheckUsersPassword(password, email);
    }


}
