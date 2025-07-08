package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.springframework.stereotype.Service;

@Service
public class GetUserInfoService {

    private final UsersOperator usersOperator;

    public GetUserInfoService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public String GetUsersInfo(String email) {
        return usersOperator.GetUsersInfo(email);
    }


}