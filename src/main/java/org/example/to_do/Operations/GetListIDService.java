package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.springframework.stereotype.Service;

@Service
public class GetListIDService {

    private final UsersOperator usersOperator;

    public GetListIDService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public int getListID(int userID, String list_name) {
        return usersOperator.GetListID(userID, list_name);
    }
}
