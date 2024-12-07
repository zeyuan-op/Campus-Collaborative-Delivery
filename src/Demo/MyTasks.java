package Demo;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyTasks {
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;
    private DatabaseChat ChatManager;
    private Connection connection;

    public MyTasks(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager, DatabaseChat ChatManager, Connection connection){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
        this.ChatManager = ChatManager;
        this.connection = connection;
    }

    public void CompletedtTask(GetTasks getTasks, String username, String OrderNumber, DatabaseTask TasksManager, DatabaseUser dbManager, Stage stage){
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("订单已完成确认");
        confirmAlert.setHeaderText("您确定订单号为：" + getTasks.getOrderNumber() + "的订单以及帮送完成吗？");
        confirmAlert.setContentText("点击确定完成，点击取消放弃。");
        // 显示对话框并等待用户响应
        ButtonType result = confirmAlert.showAndWait().orElse(ButtonType.CANCEL);

        // 如果用户点击了确定按钮
        if (result == ButtonType.OK) {
            String updateSQL = "UPDATE Tasks SET state = ?, Receiver = ?, Completion_Date = NOW() WHERE order_number = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                // 设置参数
                pstmt.setString(1, "已完成");  // 状态
                pstmt.setString(2, username);  // 接收者用户名
                pstmt.setString(3, OrderNumber);  // 订单号

                // 执行更新
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // 显示接单成功弹窗
                    showAlert("订单已完成！");
                    // 返回上一界面

                    ScreenOfTask MTS = new ScreenOfTask(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager, ChatManager,connection);
                } else {
                    System.out.println("没有记录被更新。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void AcceptTask(GetTasks getTasks, String username, String OrderNumber, DatabaseTask TasksManager, DatabaseUser dbManager, Stage stage){
        if (!TasksManager.getGender(OrderNumber).equals("无要求")){
            if (!dbManager.getGender(username).equals(TasksManager.getGender(OrderNumber))){
                showAlert("对不起！性别要求不符，请查看其他订单！");
                return;
            }
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认接单");
        confirmAlert.setHeaderText("您确定要接收订单号为：" + getTasks.getOrderNumber() + ",目的地为：" + getTasks.getBuilding() + "的订单吗？");
        confirmAlert.setContentText("点击确定接单，点击取消放弃。");
        // 显示对话框并等待用户响应
        ButtonType result = confirmAlert.showAndWait().orElse(ButtonType.CANCEL);

        // 如果用户点击了确定按钮
        if (result == ButtonType.OK) {
                // 准备 SQL 语句，同时更新多个字段
            String updateSQL = "UPDATE Tasks SET state = ?, Receiver = ?, Receiver_name = ?, Receive_Date = NOW() WHERE order_number = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                // 设置参数
                pstmt.setString(1, "进行中");  // 状态
                pstmt.setString(2, username);  // 接收者用户名
                pstmt.setString(3, dbManager.getNickname(username));  // 接收者昵称
                pstmt.setString(4, OrderNumber);  // 订单号

                // 执行更新
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // 显示接单成功弹窗
                    showAlert("接单成功！");
                    // 返回上一界面

                    ScreenOfTask MTS = new ScreenOfTask(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager, ChatManager,connection);
                } else {
                    System.out.println("没有记录被更新。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void MyTasksData(ObservableList<GetTasks> tasksData, String username, DatabaseUser dbManager, DatabaseTask tasksManager, String S) {
        tasksData.clear();
        // 构建SQL查询语句
        String sql;
        String S1 = null, S2 = null, S3 = null, S4 = null, S5 = null;
        if (S.equals("all")) {
            sql = "SELECT * FROM Tasks WHERE state = ?";
            S1 = "待接收"; S2 = null;
        } else if (S.equals("my_p")) {
            sql = "SELECT * FROM Tasks WHERE Publisher = ? AND (state = ? OR state = ?)";
            S1 = username; S2 = "待接收";S3 = "进行中";
        } else if (S.equals("my_r")) {
            sql = "SELECT * FROM Tasks WHERE Receiver = ? AND state = ?";
            S1 = username; S2 = "进行中";
        }else if (S.equals("my_d")) {
            sql = "SELECT * FROM Tasks WHERE state = ? AND (Publisher = ? OR Receiver = ?)";
            S1 = "退单中"; ;S2 = username;S3 = username;
        }else {
            sql = "SELECT * FROM Tasks WHERE (Receiver = ? OR Publisher = ?) AND (state = ? OR state = ? OR state = ?)";
            S1 = username; S2 = username;S3 = "已完成";S4 = "已取消";S5 = "已退单";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (S1 != null && !S1.isEmpty()) {
                pstmt.setString(1, S1); // 设置状态参数
            }
            if (S2 != null && !S2.isEmpty()) {
                pstmt.setString(2, S2); // 设置状态参数
            }
            if (S3 != null && !S3.isEmpty()) {
                pstmt.setString(3, S3); // 设置状态参数
            }
            if (S4 != null && !S4.isEmpty()) {
                pstmt.setString(4, S4); // 设置状态参数
            }
            if (S5 != null && !S5.isEmpty()) {
                pstmt.setString(5, S5); // 设置状态参数
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String orderNumber = rs.getString("Order_number");
                    String publisherName = rs.getString("Publisher_name");
                    String gender = rs.getString("gender");
                    String building = rs.getString("building");
                    String room = rs.getString("room");
                    String phoneNumber = rs.getString("phonenumber");
                    Float amount = rs.getFloat("amount");
                    Timestamp releaseDate = rs.getTimestamp("Release_Date");
                    String publisher = rs.getString("Publisher");
                    String state = rs.getString("state");
                    Timestamp receiveDate = rs.getTimestamp("Receive_Date");
                    String receiver = rs.getString("Receiver");
                    String receiverName = rs.getString("Receiver_name");
                    Timestamp Completion_Date = rs.getTimestamp("Completion_Date");

                    tasksData.add(new GetTasks(
                            orderNumber, publisherName, gender, building, room, phoneNumber, amount, releaseDate,
                            publisher, state, receiveDate, receiver, receiverName,Completion_Date
                    ));
                }
            }
        }catch (SQLException e) {
        e.printStackTrace();
    }
}
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
