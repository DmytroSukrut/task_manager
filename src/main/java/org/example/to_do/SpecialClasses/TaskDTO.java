package org.example.to_do.SpecialClasses;

//DTO - Data Transfer Object

public class TaskDTO {
    private int taskID;
    private String taskName;
    private String taskExplanation;
    private String taskStatus;

    public TaskDTO(int taskID, String taskName, String taskExplanation, String taskStatus) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.taskExplanation = taskExplanation;
        this.taskStatus = taskStatus;
    }

    public int getTaskID() {
        return taskID;
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
    public void setTaskID(int taskID) {
        this.taskID = taskID;
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
