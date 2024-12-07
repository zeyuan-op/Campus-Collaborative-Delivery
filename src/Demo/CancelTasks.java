package Demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class CancelTasks {
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;
    private DatabaseChat ChatManager;
    private Connection connection;

    public CancelTasks(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager, Connection connection){
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

    public void CancelOfPublisher(GetTasks getTasks, Stage stage, String OrderNumber) {
        ScreenOfTask SOT = new ScreenOfTask(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager, ChatManager,connection);
        // 创建自定义按钮
        if (username.equals(getTasks.getPublisher())&&getTasks.getState().equals("进行中")){
            Instant instant1 = getTasks.getReceive_Date().toInstant();
            Instant instant2 = Instant.now();
            Duration duration = Duration.between(instant1, instant2);
            boolean isGreaterThanHalfHour = duration.compareTo(Duration.ofMinutes(30)) > 0;
            if (!isGreaterThanHalfHour){
                showAlert("对不起！已被接收订单，半小时内不可取消!\n请电话联系接单者退单！\n接单人电话为："+dbManager.getPhonenumber(getTasks.getReceiver()));
                return;
            }
        }
        String S = "取消";
        if (getTasks.getState().equals("进行中")&&username.equals(getTasks.getReceiver())){
            Instant instant1 = getTasks.getReceive_Date().toInstant();
            Instant instant2 = Instant.now();
            Duration duration = Duration.between(instant1, instant2);
            boolean isGreaterThanHalfHour = duration.compareTo(Duration.ofMinutes(5)) > 0;
            if (isGreaterThanHalfHour){
                showAlert("对已接收超过5分钟的订单执行退单操作将影响您的信誉！");
            }
            S = "退单";
        }
        ButtonType yesButtonType = new ButtonType("是");
        ButtonType noButtonType = new ButtonType("否");

        // 创建确认对话框并设置自定义按钮
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "您确定要"+S+"订单号为：" + getTasks.getOrderNumber() + " 的订单吗？",
                yesButtonType, noButtonType);

        // 设置标题和头部文字（可选）
        confirmAlert.setTitle("确认"+S);
        confirmAlert.setHeaderText("点击是"+S+",点击否放弃。");

        // 显示对话框并等待用户响应
        Optional<ButtonType> result = confirmAlert.showAndWait();

        ScreenOfTask MTS = new ScreenOfTask(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager, ChatManager, connection);
        if (result.isPresent() && result.get() == yesButtonType) {
            // 准备 SQL 语句，同时更新多个字段
            String updateSQL = "UPDATE Tasks SET state = ? , Completion_Date = NOW() WHERE order_number = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                // 设置参数
                if (S.equals("取消")){
                    pstmt.setString(1, "已取消");  // 状态
                }else{
                    pstmt.setString(1, "退单中");  // 状态
                }
                pstmt.setString(2, OrderNumber);  // 订单号

                // 执行更新
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // 显示取消成功弹窗
                    if (S.equals("取消")){
                        showAlert("取消订单成功！");
                        SOT.showAllTasksScreen(stage, dbManager, TasksManager, username,"my_c");
                    }else {
                        showAlert("等待发布者确认！\n后续我们会继续跟踪该订单！");
                        SOT.showAllTasksScreen(stage, dbManager, TasksManager, username,"my_d");
                    }
                } else {
                    System.out.println("没有记录被更新。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void Chargebacks(GetTasks getTasks, Stage stage, String OrderNumber) {
        ScreenOfTask SOT = new ScreenOfTask(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager, ChatManager,connection);
        ButtonType yesButtonType = new ButtonType("重新发送");
        ButtonType noButtonType = new ButtonType("取消订单");

        // 创建确认对话框并设置自定义按钮
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "对订单号为" + getTasks.getOrderNumber() + " 的订单\n您可以选择重新发送，这将会更新订单发布时间\n或者取消订单，这将会将订单在平台下架",
                yesButtonType, noButtonType);

        // 设置标题和头部文字（可选）
        confirmAlert.setTitle("退单确认");
        confirmAlert.setHeaderText("请选择！");

        // 显示对话框并等待用户响应
        Optional<ButtonType> result = confirmAlert.showAndWait();

        String S = null;
        if (result.isPresent()) { // 确保用户确实做了选择
            if (result.get() == yesButtonType) {
                S = "重新发送";
            } else if (result.get() == noButtonType) {
                S = "取消订单";
            }
        }

        if (result.isPresent()) {
            // 准备 SQL 语句，同时更新多个字段
            String updateSQL = null;
            if (S.equals("重新发送")){
                updateSQL = "UPDATE Tasks SET state = ? , Release_Date = NOW() WHERE order_number = ?";
            }else if (S.equals("取消订单")){
                updateSQL = "UPDATE Tasks SET state = ? , Completion_Date = NOW() WHERE order_number = ?";
            }

            try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
                // 设置参数
                if (S.equals("重新发送")){
                    pstmt.setString(1, "待接收");  // 状态
                }else if (S.equals("取消订单")){
                    pstmt.setString(1, "已退单");  // 状态
                }
                pstmt.setString(2, OrderNumber);  // 订单号

                // 执行更新
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // 显示取消成功弹窗
                    if (S.equals("重新发送")){
                        showAlert("重新发送订单成功！");
                        SOT.showAllTasksScreen(stage, dbManager, TasksManager, username,"my_p");
                    }else {
                        showAlert("取消订单成功！");
                        SOT.showAllTasksScreen(stage, dbManager, TasksManager, username,"my_c");
                    }
                    // 返回上一界面
                    ScreenOfTask MTS = new ScreenOfTask(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager, ChatManager, connection);
                } else {
                    System.out.println("没有记录被更新。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
