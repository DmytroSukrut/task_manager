package org.example.to_do.SpecialClasses;

//DTO - Data Transfer Object

public class UserDTO {

    private Long UserID;
    private String UserName;
    private String UserEmail;

    public UserDTO(Long UserID, String UserName, String UserEmail) {
        this.UserID = UserID;
        this.UserName = UserName;
        this.UserEmail = UserEmail;
    }

    public Long getUserID() {
        return UserID;
    }
    public void setUserID(Long UserID) {
        this.UserID = UserID;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserEmail() {
        return UserEmail;
    }
    public void setUserEmail(String UserEmail) {
        this.UserEmail = UserEmail;
    }

}
