package org.example.to_do.SpecialClasses;

//DTO - Data Transfer Object

import java.util.ArrayList;
import java.util.List;

public class ListDTO {
    private String listName;
    private List<TaskDTO> tasks = new ArrayList<TaskDTO>();

    public ListDTO(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }
    public List<TaskDTO> getTasks() {
        return tasks;
    }
    public void setListName(String listName) {
        this.listName = listName;
    }
    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }
}
