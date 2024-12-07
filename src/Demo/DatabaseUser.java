package Demo;

import java.sql.*;  // 引入Java的SQL包，用于进行数据库操作

// 数据库管理类，提供对数据库的增删改查操作
public class DatabaseUser {
    private Connection connection;  // 数据库连接对象

    // 构造函数，用于连接数据库
    public DatabaseUser(Connection connection) {
        this.connection = connection;
        checkAndCreateTable();
    }

    // 方法：检查并创建users表（如果表不存在）
    private void checkAndCreateTable() {
        // SQL语句，用于创建users表
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL, " +
                "gender VARCHAR(255) NOT NULL, " +
                "phonenumber VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "nickname VARCHAR(255), " +
                "isAdmin BOOLEAN DEFAULT FALSE, " +
                "register_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "source VARCHAR(255) " +  // 这里是来源字段
                ")";  // 注意这里不需要逗号

        try (Statement stmt = connection.createStatement()) {
            // 执行SQL语句，创建表
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
    }

    // 方法：添加用户，包含是否管理员的标志
    public void addUser(String username,String password, String nickname, boolean isAdmin, String source, String Gender, String phonenumber) {
        String sql = "INSERT INTO users (username, Gender, phonenumber, password, nickname, isAdmin, source) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, Gender);
            pstmt.setString(3, phonenumber);
            pstmt.setString(4, password);
            pstmt.setString(5, nickname);
            pstmt.setBoolean(6, isAdmin);
            pstmt.setString(7, source);  // 确保这里设置了 source 参数
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  // 打印SQL异常
        }
    }


    // 方法：检查用户是否存在
    public boolean userExists(String username) {
        // SQL语句，用于查询指定用户名的记录
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, username);
            // 执行查询操作并返回结果
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // 如果有结果，返回true（用户存在）
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
        return false;  // 用户不存在，返回false
    }

    // 方法：验证用户密码是否正确
    public boolean validateUser(String username, String password) {
        // SQL语句，用于查询指定用户名和密码的记录
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, username);
            stmt.setString(2, password);
            // 执行查询操作并返回结果
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // 如果有结果，说明密码正确，返回true
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
        return false;  // 密码不正确，返回false
    }

    // 方法：获取用户的昵称
    public String getNickname(String username) {
        // SQL语句，用于查询指定用户名的昵称
        String query = "SELECT nickname FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, username);
            // 执行查询操作并返回结果
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 如果有结果，返回昵称
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
        return null;  // 如果查询无结果，返回null
    }

    public String getPhonenumber(String phonenumber) {
        // SQL语句，用于查询指定用户名的昵称
        String query = "SELECT phonenumber FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, phonenumber);
            // 执行查询操作并返回结果
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 如果有结果，返回昵称
                return rs.getString("phonenumber");
            }
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
        return null;  // 如果查询无结果，返回null
    }

    public String getGender(String username) {
        // SQL语句，用于查询指定用户名的昵称
        String query = "SELECT gender FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, username);
            // 执行查询操作并返回结果
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 如果有结果，返回昵称
                return rs.getString("gender");
            }
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
        return null;  // 如果查询无结果，返回null
    }

    // 方法：检查用户是否为管理员
    public boolean isAdmin(String username) {
        // SQL语句，用于查询指定用户名是否为管理员
        String query = "SELECT isAdmin FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, username);
            // 执行查询操作并返回结果
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 如果有结果，返回isAdmin的布尔值
                return rs.getBoolean("isAdmin");
            }
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
        return false;  // 如果查询无结果或发生错误，返回false
    }

    // 方法：获取所有用户信息
    public ResultSet getAllUsers() {
        String query = "SELECT username, nickname, gender, phonenumber, password, register_time, isAdmin, source FROM users"; // 确保这里包含 source
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    // 方法：更新用户密码
    public void updatePassword(String username, String newPassword) {
        // SQL语句，用于更新指定用户名的密码
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, newPassword);  // 新密码
            stmt.setString(2, username);  // 用户名
            // 执行更新操作
            stmt.executeUpdate();
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
    }

    // 方法：更新用户昵称
    public void updateNickname(String username, String newNickname) {
        // SQL语句，用于更新指定用户名的昵称
        String query = "UPDATE users SET nickname = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, newNickname);  // 新昵称
            stmt.setString(2, username);  // 用户名
            // 执行更新操作
            stmt.executeUpdate();
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
    }

    // 方法：删除用户
    public void deleteUser(String username) {
        // SQL语句，用于删除指定用户名的记录
        String query = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // 设置SQL语句中的参数值
            stmt.setString(1, username);  // 用户名
            // 执行删除操作
            stmt.executeUpdate();
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
    }
    public ResultSet getUserInfo(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            return stmt.executeQuery();  // 返回包含用户信息的 ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
