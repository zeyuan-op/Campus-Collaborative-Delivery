package Demo;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AddUser {
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;

    public AddUser(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager,DatabaseTask TasksManager){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
    }
    // 显示添加用户界面的方法
    public void showAddUserScreen(Stage stage) {
        VBox addUserBox = new VBox(10.0);  // 设置垂直布局和间距
        addUserBox.setPadding(new Insets(150.0));  // 设置内边距
        addUserBox.setAlignment(Pos.CENTER);  // 居中对齐

        Label addUserLabel = new Label("添加用户");  // 标题

        TextField usernameField = new TextField();  // 用户名输入框
        usernameField.setPromptText("用户名");  // 提示文字

        TextField phonenumberField = new TextField();  // 用户名输入框
        phonenumberField.setPromptText("电话号码");  // 提示文字

        PasswordField passwordField1 = new PasswordField();  // 密码输入框
        passwordField1.setPromptText("输入密码");  // 提示文字

        PasswordField passwordField2 = new PasswordField();  // 再次输入密码输入框
        passwordField2.setPromptText("再次输入密码");  // 提示文字

        TextField nicknameField = new TextField();  // 昵称输入框
        nicknameField.setPromptText("昵称");  // 提示文字

        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("男", "女");  // 添加性别选项
        genderComboBox.setPromptText("请选择性别");  // 提示文字

        Button addUserButton = new Button("添加用户");  // 添加用户按钮
        addUserButton.setMinWidth(100);  // 设置最小宽度为
        addUserButton.setMinHeight(35);  // 设置最小高度为60
        Label addUserMessage = new Label();  // 提示信息标签

        // 超级用户可以选择是否设置新用户为管理员
        CheckBox isAdminCheckBox = new CheckBox("是否为管理员");  // 管理员选项框（仅超级用户可见）

        // 添加用户按钮点击事件
        addUserButton.setOnAction(e -> {
            String inputNickname = nicknameField.getText();
            String inputUsername = usernameField.getText();
            String inputPassword1 = passwordField1.getText();
            String inputPassword2 = passwordField2.getText();
            String selectedGender = genderComboBox.getValue();
            String phonenumber = phonenumberField.getText();

            // 定义 isAdmin 变量
            boolean isAdmin = false;  // 默认新用户为普通用户
            String source;  // 来源信息

            // 根据当前登录的用户设置来源
            source = username;  // 当前用户的用户名作为添加用户的来源

            // 如果是超级用户，可以根据是否勾选来决定是否设置为管理员
            if (username.equals(SUPER_USER)) {
                isAdmin = isAdminCheckBox.isSelected();  // 超级用户可以选择是否设置新用户为管理员
            } else {
                isAdmin = false;  // 普通用户只能是普通用户
            }

            PasswordConfirmation PC = new PasswordConfirmation(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager);
            // 判断两次输入的密码是否一致
            if (!inputPassword1.equals(inputPassword2)) {
                addUserMessage.setText("两次输入的密码不一致");
            } else if (!dbManager.userExists(inputUsername)) {
                // 验证当前用户密码
                PC.showPasswordConfirmationDialog(stage, null, null, null, inputUsername, inputPassword1, inputNickname,
                        isAdmin, source, selectedGender, addUserMessage, phonenumber, PasswordConfirmation.PasswordConfirmationAction.ADD_USER);
            } else {
                addUserMessage.setText("用户名已存在");  // 用户名已存在
            }
        });

        ScreenOfUser SA = new ScreenOfUser(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager);

        Button backButton = new Button("返回");  // 返回按钮
        backButton.setMinWidth(100);  // 设置最小宽度为
        backButton.setMinHeight(35);  // 设置最小高度为60
        backButton.setOnAction(e -> SA.showAllUsersScreen(stage));  // 返回用户列表界面

        // 将控件添加到VBox布局中
        addUserBox.getChildren().addAll(addUserLabel, nicknameField, usernameField, phonenumberField, passwordField1, passwordField2, genderComboBox);
        if (username.equals(SUPER_USER)) {
            addUserBox.getChildren().add(isAdminCheckBox);  // 仅超级用户界面添加这个选项框
        }
        addUserBox.getChildren().addAll(addUserButton, addUserMessage, backButton);
        Scene addUserScene = new Scene(addUserBox, 660.0, 440.0);  // 创建添加用户场景
        stage.setScene(addUserScene);  // 设置添加用户场景

        // 设置初始焦点
        PauseTransition focusPause = new PauseTransition(Duration.millis(10));
        focusPause.setOnFinished(event -> backButton.requestFocus());
        focusPause.play();
    }
}
