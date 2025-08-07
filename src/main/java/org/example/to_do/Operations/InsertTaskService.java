package org.example.to_do.Operations;

import org.example.to_do.DAO.UsersOperator;
import org.springframework.stereotype.Service;

@Service
public class InsertTaskService {

    private final UsersOperator usersOperator;

    public InsertTaskService(UsersOperator usersOperator) {
        this.usersOperator = usersOperator;
    }

    public void insertTask(int list_id, String TaskName, String TaskExplanation, String TaskStatus) {
        usersOperator.InsertTask(list_id, TaskName, TaskExplanation, TaskStatus);
    }
}
