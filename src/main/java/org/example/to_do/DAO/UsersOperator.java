package org.example.to_do.DAO;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.to_do.SpecialClasses.ListDTO;
import org.example.to_do.SpecialClasses.TaskDTO;
import org.example.to_do.SpecialClasses.UserDTO;
import org.example.to_do.database.Database;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UsersOperator {

    private final Database database;

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsersOperator(Database database) {
        this.database = database;
    }

    public void InsertUser(String UserName, String email, String password) {
        String SQLRequest = "INSERT INTO users (id, name, email, password) VALUES (?,?,?,?)";

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)) {

            ps.setInt(1, GetLowestUserID());
            ps.setString(2, UserName);
            ps.setString(3, email);
            ps.setString(4, hash_pass(password));
            ps.executeUpdate();
            System.out.println("User: " + UserName + " inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void InsertList(int user_id, String ListName) {
        String SQLRequest = "INSERT INTO lists (id, user_id, name) VALUES (?,?,?)";

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){

            ps.setInt(1, GetLowestListID());
            ps.setInt(2, user_id);
            ps.setString(3, ListName);
            ps.executeUpdate();
            System.out.println("List: " + ListName + " inserted");
        } catch (SQLException e){
            if ("23505".equals(e.getSQLState())) {
                System.out.println("List \"" + ListName + "\" already exists for this user.");
            } else {
                e.printStackTrace();
            }
        }
    }

//    public int GetUserID(String UserEmail) {
//        String SQLRequest = "SELECT id FROM users WHERE email = ?";
//
//        try(Connection con = database.getConnection();
//            PreparedStatement ps = con.prepareStatement(SQLRequest)){
//
//            ps.setString(1, UserEmail);
//
//            try(ResultSet rs = ps.executeQuery()){
//                if(rs.next()){
//                    return rs.getInt("id");
//                }
//            }
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
//        return -1;
//    }

    public int GetListID(int user_id, String List_name) {
        String SQLRequest = "SELECT id FROM lists WHERE user_id = ? AND name = ?";

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){

            ps.setInt(1, user_id);
            ps.setString(2, List_name);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public void InsertTask(int list_id, String TaskName, String TaskExplanation, String TaskStatus) {
        String SQLRequest = "INSERT INTO to_do_tasks (id, list_id, name, explanation, status) VALUES (?,?,?,?,?::task_status)";

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){

            ps.setInt(1, GetLowestTDTID());
            ps.setInt(2, list_id);
            ps.setString(3, TaskName);
            ps.setString(4, TaskExplanation);
            ps.setString(5, TaskStatus);
            ps.executeUpdate();
            System.out.println("Task: " + TaskName + " inserted");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<ListDTO> GetUsersListsAndTasks(int user_id) {
        Map<String, ListDTO> lists_and_tasks = new HashMap<>();

        String SQLRequest = """
                SELECT
                    l.name AS list_name,
                    tdt.id AS task_id,
                    tdt.name AS task_name,
                    tdt.explanation AS task_explanation,
                    tdt.status AS task_status
                FROM lists l
                JOIN users u on l.user_id = u.id
                LEFT JOIN to_do_tasks tdt on l.id = tdt.list_id
                WHERE u.id = (?)
                ORDER BY
                    l.name,
                    tdt.id,
                    CASE tdt.status
                        WHEN 'started' THEN 1
                        WHEN 'not_started' THEN 2
                        WHEN 'finished' THEN 3
                        ELSE 4
                    END;
                """;

        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){

            ps.setInt(1, user_id);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    String listName = rs.getString("list_name");

                    ListDTO listDTO = lists_and_tasks.get(listName);
                    if(listDTO == null){
                        listDTO = new ListDTO(listName);
                        lists_and_tasks.put(listName, listDTO);
                    }

                    String taskName = rs.getString("task_name");
                    if(taskName != null){
                        int taskId = rs.getInt("task_id");
                        String taskExplanation = rs.getString("task_explanation");
                        String taskStatus = rs.getString("task_status");

                        TaskDTO taskDTO = new TaskDTO(taskId, taskName, taskExplanation, taskStatus);
                        listDTO.getTasks().add(taskDTO);
                    }

                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return new ArrayList<>(lists_and_tasks.values());
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

    public UserDTO GetUsersInfo(String email) {
        String SQLRequest = "SELECT id, name FROM users WHERE email = ?";
        try(Connection con = database.getConnection();
            PreparedStatement ps = con.prepareStatement(SQLRequest)){
            ps.setString(1, email);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Long id = rs.getLong("id");
                    String name = rs.getString("name");
                    return new UserDTO(id, name, email);
                } else {
                    System.out.println("User " + email + " not found");
                    return null;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public int GetLowestUserID() {
        String SQLRequest = """
                SELECT COALESCE(
                      (SELECT MIN(t.id + 1)
                       FROM users t
                       LEFT JOIN users t2 ON t.id + 1 = t2.id
                       WHERE t2.id IS NULL),
                      1
                    ) AS free_id
                """;
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(SQLRequest)) {
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("free_id");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 1;
    }

    public int GetLowestListID() {
        String SQLRequest = """
                SELECT COALESCE(
                      (SELECT MIN(t.id + 1)
                       FROM lists t
                       LEFT JOIN lists t2 ON t.id + 1 = t2.id
                       WHERE t2.id IS NULL),
                      1
                    ) AS free_id
                """;
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(SQLRequest)) {
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("free_id");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 1;
    }

    public int GetLowestTDTID() {
        String SQLRequest = """
                SELECT COALESCE(
                      (SELECT MIN(t.id + 1)
                       FROM to_do_tasks t
                       LEFT JOIN to_do_tasks t2 ON t.id + 1 = t2.id
                       WHERE t2.id IS NULL),
                      1
                    ) AS free_id
                """;
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(SQLRequest)) {
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt("free_id");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 1;
    }

    public boolean CheckUsersPassword(String pas_to_check, String email) {
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
