package Demo;

import java.sql.*;

// 数据库管理类，提供对数据库的增删改查操作
public class DatabaseChat {
    private Connection connection;  // 数据库连接对象

    // 构造函数，用于连接数据库
    public DatabaseChat(Connection connection) {
        this.connection = connection;
        // 调用方法，检查并创建users表（如果该表不存在）
        checkAndCreateChat();
    }

    // 方法：检查并创建users表（如果表不存在）
    private void checkAndCreateChat() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Chat (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " + // 添加自增主键
                "orderNumber VARCHAR(255), " + // 假设 Order_number 是唯一的
                "Sender VARCHAR(255) NOT NULL, " +
                "Receiver VARCHAR(255), " +
                "S_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "content VARCHAR(255) " +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 在 DatabaseTask 类中新增 insertNewTask 方法
    public void insertNewChat(String orderNumber, String Sender, String Receiver, String content) {
        String insertSQL = "INSERT INTO Chat (orderNumber, Sender, Receiver, S_Date, content) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {

            pstmt.setString(1, orderNumber);
            pstmt.setString(2, Sender);
            pstmt.setString(3, Receiver);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // 设置当前时间
            pstmt.setString(5, content);

            pstmt.executeUpdate(); // 执行插入操作
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}