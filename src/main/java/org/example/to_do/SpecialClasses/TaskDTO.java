package org.example.to_do.SpecialClasses;

//DTO - Data Transfer Object

public class TaskDTO {
    private String taskName;
    private String taskExplanation;
    private String taskStatus;

    public TaskDTO(String taskName, String taskExplanation, String taskStatus) {
        this.taskName = taskName;
        this.taskExplanation = taskExplanation;
        this.taskStatus = taskStatus;
    }

    public String getTaskName() {
        return taskName;
    }
    public String getTaskExplanation() {
        return taskExplanation;
    }
    public String getTaskStatus() {
        return taskStatus;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public void setTaskExplanation(String taskExplanation) {
        this.taskExplanation = taskExplanation;
    }
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
