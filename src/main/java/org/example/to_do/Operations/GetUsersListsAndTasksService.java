package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.example.to_do.SpecialClasses.ListDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUsersListsAndTasksService {

    private final UsersOperator usersOperator;

    public GetUsersListsAndTasksService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public List<ListDTO> getUsersListsAndTasks(int userID) {
        return usersOperator.GetUsersListsAndTasks(userID);
    }
}
