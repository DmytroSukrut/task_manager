package org.example.to_do.DAO;

import org.example.to_do.database.Database;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class UsersOperator {

    private final Database database;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsersOperator(Database database) {
        this.database = database;
    }

    public void insertUser(String UserName, String email, String password) {
        String SQLRequest = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)) {

            ps.setString(1, UserName);
            ps.setString(2, email);
            ps.setString(3, hash_pass(password));
            ps.executeUpdate();
            System.out.println("User: " + UserName + " inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropUsers(){
        String SQLRequest1 = "TRUNCATE TABLE users";
        String SQLRequest2 = "ALTER SEQUENCE users_id_seq RESTART WITH 1";

        try(Connection con = database.getConnection();
            PreparedStatement ps1 = con.prepareStatement(SQLRequest1);
            PreparedStatement ps2 = con.prepareStatement(SQLRequest2)){
            ps1.executeUpdate();
            ps2.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private String hash_pass(String password) {
        return encoder.encode(password);
    }

    public boolean checkUser(String pas_to_check, String email) {
        String SQLRequest = "SELECT password FROM users WHERE email = ?";

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){
            ps.setString(1, email);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    String password = rs.getString("password");
                    return encoder.matches(pas_to_check, password);
                } else {
                    System.out.println("User " + email + " not found");
                    return false;
                }
            }


        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean DoesUserExists(String email) {
        String SQLRequest = "SELECT email FROM users WHERE email = ?";

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){

            ps.setString(1, email);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }

}
