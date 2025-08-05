package org.example.to_do.Operations;


import org.example.to_do.DAO.TasksOperator;
import org.springframework.stereotype.Service;

@Service
public class GetTaskService {

    private final TasksOperator tasksOperator;

    public GetTaskService(TasksOperator tasksOperator) {
        this.tasksOperator = tasksOperator;
    }

    public String GetRandomTask(){
        return tasksOperator.GetRandomTask();
    }

}
