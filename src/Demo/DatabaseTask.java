package Demo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.sql.*;  // 引入Java的SQL包，用于进行数据库操作

// 数据库管理类，提供对数据库的增删改查操作
public class DatabaseTask {
    private Connection connection;  // 数据库连接对象

    // 构造函数，用于连接数据库
    public DatabaseTask(Connection connection) {
        this.connection = connection;
        // 调用方法，检查并创建users表（如果该表不存在）
        checkAndCreateTask();
    }

    // 方法：检查并创建users表（如果表不存在）
    private void checkAndCreateTask() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Tasks (" +
                "Order_number VARCHAR(255) PRIMARY KEY, " +
                "Publisher_name VARCHAR(255) NOT NULL, " +
                "gender VARCHAR(255) NOT NULL, " +
                "building VARCHAR(255) NOT NULL, " +
                "room VARCHAR(255) NOT NULL, " +
                "phonenumber VARCHAR(255) NOT NULL, " +
                "amount FLOAT NOT NULL, " +
                "Release_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "Publisher VARCHAR(255) NOT NULL, " +
                "state VARCHAR(255), " +
                "Receive_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "Receiver VARCHAR(255), " +
                "Receiver_name VARCHAR(255), " +  // 去掉这里的逗号
                "Completion_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getAllTasks() {
        String query = "SELECT Order_number, Publisher_name, gender, building, room, phonenumber," +
                " amount, Release_Date, Publisher, state, Receiver, Receive_Date, Receiver_name, Completion_Date FROM Tasks"; // 确保这里包含 source
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 在 DatabaseTask 类中新增 insertNewTask 方法
    public void insertNewTask(String gender, String building, String room, String phoneNumber, Float amount, Timestamp releaseDate,
                              String publisher,String publisherName) {
        String insertSQL = "INSERT INTO Tasks (Order_number, Publisher_name, gender, building, room, phonenumber, " +
                "amount, Release_Date, Publisher, state, Receive_Date , Receiver, Receiver_name, Completion_Date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            String orderNumber = "ORD" + System.currentTimeMillis();

            pstmt.setString(1, orderNumber);
            pstmt.setString(2, publisherName);
            pstmt.setString(3, gender);
            pstmt.setString(4, building);
            pstmt.setString(5, room);
            pstmt.setString(6, phoneNumber);
            pstmt.setFloat(7, amount);
            pstmt.setTimestamp(8, releaseDate);
            pstmt.setString(9, publisher);
            pstmt.setString(10, "待接收");
            pstmt.setTimestamp(11, null);
            pstmt.setNull(12, java.sql.Types.VARCHAR);
            pstmt.setString(13, "");
            pstmt.setTimestamp(14, null);

            pstmt.executeUpdate(); // 执行插入操作
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getGender(String username) {
        String query = "SELECT gender FROM Tasks WHERE Order_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("gender");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPublisher(String Order_number) {
        String query = "SELECT Publisher FROM Tasks WHERE Order_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, Order_number);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Publisher");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getReceiver(String Order_number) {
        String query = "SELECT Receiver FROM Tasks WHERE Order_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, Order_number);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Receiver");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}