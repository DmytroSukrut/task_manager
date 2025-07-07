package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.springframework.stereotype.Service;

@Service
public class UserExistsService {
    private final UsersOperator usersOperator;

    public UserExistsService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public boolean DoesUserExists(String email) {
        return usersOperator.DoesUserExists(email);
    }
}
