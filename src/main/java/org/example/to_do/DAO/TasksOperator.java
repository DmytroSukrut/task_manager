package org.example.to_do.DAO;

import org.example.to_do.database.Database;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class TasksOperator {

    private final Database database;

    public TasksOperator(Database database) {
        this.database = database;
    }

    public String GetRandomTask() {
        String SQLRequest = "SELECT task FROM tasks WHERE id = ?";
        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){

            int rand = 1 + (int)(Math.random() * 1130);
            ps.setInt(1, rand);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("task");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
