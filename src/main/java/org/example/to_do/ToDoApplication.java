package org.example.to_do;

import org.example.to_do.DAO.UsersOperator;
import org.example.to_do.database.Database;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.sql.Connection;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ToDoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ToDoApplication.class, args);
//        Database db = context.getBean(Database.class);
//
//        try (Connection connection = db.getConnection()){
//            System.out.println("Database connection established");
//        } catch (Exception e)  {
//            e.printStackTrace();
//        }
//
        //UsersOperator UsOperator = context.getBean(UsersOperator.class);




        //boolean IsCorrect = UsOperator.checkUser("Ababagalamaga", "Ltrdolil@gmail.com");
        //System.out.println(IsCorrect);
        //UsOperator.InsertUser("Limoncik", "Ltril@gmail.com", "Ababagalamaga");
        //UsOperator.dropUsers();
    }
}
