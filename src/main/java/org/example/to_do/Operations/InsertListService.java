package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.springframework.stereotype.Service;

@Service
public class InsertListService {

    private final UsersOperator usersOperator;

    public InsertListService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public void insertList(int userID, String listName) {
        usersOperator.InsertList(userID, listName);
    }

}
