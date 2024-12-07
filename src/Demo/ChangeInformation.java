package Demo;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ChangeInformation {
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;

    public ChangeInformation(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
    }

    public void showChangePasswordScreen(Stage stage, Scene previousScene) {
        VBox changePasswordBox = new VBox(10.0);  // 设置垂直布局和间距
        changePasswordBox.setPadding(new Insets(150.0));  // 设置内边距
        changePasswordBox.setAlignment(Pos.CENTER);  // 居中对齐

        PasswordField oldPasswordField = new PasswordField();  // 旧密码输入框
        oldPasswordField.setPromptText("输入旧密码");  // 提示文字

        // 新密码输入框
        PasswordField newPasswordField1 = new PasswordField();
        newPasswordField1.setPromptText("输入新密码, 密码要求：8-16个字符，包含字母、数字及符号.!#-*/+");

        // 限制新密码输入长度为8-16个字符，并且只能输入特定字符
        newPasswordField1.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 16) {
                return null;  // 超过16个字符，不允许修改
            }
            if (newText.matches("[a-zA-Z0-9.!#,+\\-*/]*") || newText.isEmpty()) {
                return change;  // 输入符合要求，允许修改
            }
            return null;  // 输入不符合要求，不允许修改
        }));

        // 再次输入新密码输入框
        PasswordField newPasswordField2 = new PasswordField();
        newPasswordField2.setPromptText("再次输入新密码");

        // 限制再次输入密码的长度和字符
        newPasswordField2.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 16) {
                return null;  // 超过16个字符，不允许修改
            }
            if (newText.matches("[a-zA-Z0-9.!#,+\\-*/]*") || newText.isEmpty()) {
                return change;  // 输入符合要求，允许修改
            }
            return null;  // 输入不符合要求，不允许修改
        }));

        Button changePasswordButton = new Button("确认修改");  // 确认修改按钮
        Label changeMessage = new Label();  // 修改信息提示标签

        // 设置确认修改按钮大小
        changePasswordButton.setMinWidth(100);
        changePasswordButton.setMinHeight(35);

        // 修改密码按钮点击事件
        changePasswordButton.setOnAction(e -> {
            String oldPassword = oldPasswordField.getText();  // 获取旧密码
            String newPassword1 = newPasswordField1.getText();  // 获取新密码
            String newPassword2 = newPasswordField2.getText();  // 获取再次输入的新密码

            if (!dbManager.validateUser(username, oldPassword)) {
                changeMessage.setText("旧密码不正确！");
                System.out.println("旧密码验证失败");
            } else if (!newPassword1.equals(newPassword2)) {
                changeMessage.setText("两次输入的新密码不一致！");
                System.out.println("新密码输入不一致");
            } else {
                dbManager.updatePassword(username, newPassword1);
                changeMessage.setText("密码修改成功！");
                PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
                pause.setOnFinished(event -> main.showMainScreen(stage, username, dbManager, TasksManager));
                pause.play();
            }
        });

        Button backButton = new Button("返回");  // 返回按钮
        backButton.setMinWidth(100);
        backButton.setMinHeight(35);
        backButton.setOnAction(e -> stage.setScene(previousScene));  // 点击返回上一个场景

        changePasswordBox.getChildren().addAll(oldPasswordField, newPasswordField1, newPasswordField2, changePasswordButton, changeMessage, backButton);
        Scene changePasswordScene = new Scene(changePasswordBox, 660.0, 440.0);
        stage.setScene(changePasswordScene);

        // 设置初始焦点
        PauseTransition focusPause = new PauseTransition(Duration.millis(10));
        focusPause.setOnFinished(event -> changePasswordButton.requestFocus());
        focusPause.play();
    }

    public void showChangeNicknameScreen(Stage stage, Scene previousScene) {
        VBox changeNicknameBox = new VBox(10.0);  // 设置垂直布局和间距
        changeNicknameBox.setPadding(new Insets(150.0));  // 设置内边距
        changeNicknameBox.setAlignment(Pos.CENTER);  // 居中对齐

        TextField newNicknameField = new TextField();  // 新昵称输入框
        newNicknameField.setPromptText("输入新昵称");  // 提示文字
        Button changeNicknameButton = new Button("确认修改");  // 确认修改按钮
        Label changeMessage = new Label();  // 修改信息提示标签

        // 设置确认修改按钮大小
        changeNicknameButton.setMinWidth(100);  // 设置最小宽度
        changeNicknameButton.setMinHeight(35);  // 设置最小高度
        // 修改昵称按钮点击事件
        changeNicknameButton.setOnAction(e -> {
            String newNickname = newNicknameField.getText();  // 获取新昵称
            dbManager.updateNickname(username, newNickname);  // 更新数据库中的昵称
            main.setNickname(newNickname);  // 更新主类中的昵称变量
            changeMessage.setText("昵称修改成功！");  // 修改成功提示
            PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
            pause.setOnFinished(event -> main.showMainScreen(stage, username, dbManager, TasksManager));  // 切换回主界面
            pause.play();
        });

        Button backButton = new Button("返回");  // 返回按钮
        backButton.setMinWidth(100);  // 设置最小宽度
        backButton.setMinHeight(35);  // 设置最小高度
        backButton.setOnAction(e -> stage.setScene(previousScene));  // 点击返回上一个场景

        // 将控件添加到VBox布局中
        changeNicknameBox.getChildren().addAll(newNicknameField, changeNicknameButton, changeMessage, backButton);
        Scene changeNicknameScene = new Scene(changeNicknameBox, 660.0, 440.0);  // 创建修改昵称场景
        stage.setScene(changeNicknameScene);  // 设置修改昵称场景

        // 设置初始焦点
        PauseTransition focusPause = new PauseTransition(Duration.millis(10));
        focusPause.setOnFinished(event -> changeNicknameButton.requestFocus());
        focusPause.play();
    }

    public void deleteUser(Stage stage) {
        VBox deleteBox = new VBox(20.0);  // 设置垂直布局和间距
        deleteBox.setPadding(new Insets(50.0));  // 设置内边距
        deleteBox.setAlignment(Pos.CENTER);  // 居中对齐

        // 从数据库获取用户信息
        ResultSet userInfo = dbManager.getUserInfo(username);

        // 创建用于显示用户信息的 VBox
        VBox userInfoBox = new VBox(10);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);  // 左对齐

        try {
            if (userInfo.next()) {
                userInfoBox.getChildren().addAll(
                        createAlignedRow("用户名:", userInfo.getString("username")),
                        createAlignedRow("昵称:", userInfo.getString("nickname")),
                        createAlignedRow("管理员权限:", userInfo.getBoolean("isAdmin") ? "是" : "否"),
                        createAlignedRow("注册来源:", userInfo.getString("source"))
                );

                // 计算注册时长
                LocalDateTime registerTime = userInfo.getTimestamp("register_time").toLocalDateTime();
                LocalDateTime currentTime = LocalDateTime.now();
                java.time.Duration duration = java.time.Duration.between(registerTime, currentTime);

                long days = duration.toDays();
                long hours = duration.minusDays(days).toHours();
                long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

                String durationText = days + "天 " + hours + "小时 " + minutes + "分钟";
                userInfoBox.getChildren().add(createAlignedRow("注册时长:", durationText));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userInfoBox.getChildren().add(new Label("无法获取用户信息"));
        }

        // 按钮
        Button deleteButton = new Button("确认注销");
        deleteButton.setMinWidth(100);
        deleteButton.setMinHeight(35);

        Button backButton = new Button("返回");
        backButton.setMinWidth(100);
        backButton.setMinHeight(35);

        // 按钮点击事件 - 确认注销
        deleteButton.setOnAction(e -> {
            PasswordConfirmation passwordConfirmation = new PasswordConfirmation(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager);
            passwordConfirmation.showPasswordConfirmationDialog(
                    stage,
                    null,
                    null,
                    null,
                    null,  // 不需要这些参数，因为是删除操作
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    null,
                    PasswordConfirmation.PasswordConfirmationAction.DELETE_USER
            ); // 调用密码确认
        });

        // 返回按钮点击事件
        Settings ST = new Settings(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager);
        backButton.setOnAction(e -> ST.showSettingsScreen(stage));

        // 底部按钮布局
        HBox buttonBox = new HBox(20, backButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        // 将用户信息和按钮添加到VBox布局中
        deleteBox.getChildren().addAll(userInfoBox, buttonBox);

        Scene deleteScene = new Scene(deleteBox, 660.0, 440.0);
        stage.setScene(deleteScene);
    }

    // 辅助方法，用于创建对齐的字段名和值行
    private HBox createAlignedRow(String label, String value) {
        Label fieldLabel = new Label(label);
        fieldLabel.setMinWidth(100);  // 设置固定宽度确保对齐
        fieldLabel.setStyle("-fx-font-weight: bold;");

        Label fieldValue = new Label(value);
        fieldValue.setStyle("-fx-font-size: 14px;");

        HBox row = new HBox(10, fieldLabel, fieldValue);  // 设置字段名和值之间的间距
        row.setAlignment(Pos.CENTER_LEFT);  // 左对齐
        return row;
    }

}
