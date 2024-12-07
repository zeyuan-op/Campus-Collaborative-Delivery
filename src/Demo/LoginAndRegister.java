package Demo;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;  // 导入 HBox 类

public class LoginAndRegister {
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;

    public LoginAndRegister(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
    }

    // 显示登录界面的方法
    public void showLoginScreen(Stage stage) {
        // 创建根布局VBox
        VBox root = new VBox(25);  // 20为元素之间的间距
        root.setPadding(new Insets(150));  // 设置内边距
        root.setAlignment(Pos.CENTER);  // 设置元素居中对齐

        // 登录标题
        Label loginLabel = new Label("登录");  // 登录标题
        loginLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // 设置标题字体大小和加粗

        // 用户名和密码输入框
        TextField usernameField = new TextField();  // 用户名输入框
        usernameField.setPromptText("用户名");  // 输入提示文字
        PasswordField passwordField = new PasswordField();  // 密码输入框
        passwordField.setPromptText("密码");  // 输入提示文字
        Label loginMessage = new Label();

        // 登录按钮
        Button loginButton = new Button("登录");  // 登录按钮
        loginButton.setMinWidth(100);  // 设置最小宽度
        loginButton.setMinHeight(35);  // 设置最小高度

        // 登录按钮的点击事件
        loginButton.setOnAction(e -> {
            String inputUsername = usernameField.getText();  // 获取输入的用户名
            String inputPassword = passwordField.getText();  // 获取输入的密码

            // 验证超级用户登录
            if (inputUsername.equals(SUPER_USER) && inputPassword.equals(SUPER_PASSWORD)) {
                username = SUPER_USER;
                String nickname = SUPER_NICKNAME;
                boolean isAdmin = true;
                loginMessage.setText("超级用户登录成功！");
                main.showMainScreen(stage, username, dbManager, TasksManager);  // 超级用户登录后直接进入设置界面
            } else if (dbManager.validateUser(inputUsername, inputPassword)) {
                username = inputUsername;
                String nickname = dbManager.getNickname(inputUsername);
                boolean isAdmin = dbManager.isAdmin(inputUsername);
                loginMessage.setText("登录成功！");
                main.showMainScreen(stage, username, dbManager, TasksManager);  // 普通用户成功登录后显示主界面
            } else if (!dbManager.userExists(inputUsername)) {
                loginMessage.setText("用户名不存在!");
            } else {
                loginMessage.setText("密码错误!");
            }
        });

        loginMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-font-weight: bold;");

        // 返回按钮
        Button backButton = new Button("返回");  // 返回按钮
        backButton.setMinWidth(50);  // 设置最小宽度
        backButton.setMinHeight(25);  // 设置最小高度
        backButton.setOnAction(e -> main.start(stage));  // 点击返回到主界面

        // 创建底部布局，用于放置返回按钮
        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);  // 将返回按钮对齐到右侧

        // 将所有控件添加到根布局
        root.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton, loginMessage, bottomBox);

        // 创建登录场景
        Scene loginScene = new Scene(root, 660.0, 440.0);
        stage.setScene(loginScene);  // 设置登录场景

        // 设置初始焦点
        PauseTransition focusPause = new PauseTransition(Duration.millis(10));
        focusPause.setOnFinished(event -> loginButton.requestFocus());
        focusPause.play();
    }

    public void showRegisterScreen(Stage stage) {
        // 创建根布局VBox
        VBox registerBox = new VBox(10.0);  // 设置垂直布局和间距
        registerBox.setPadding(new Insets(150.0));  // 设置内边距
        registerBox.setAlignment(Pos.CENTER);  // 居中对齐

        Label registerLabel = new Label("注册");  // 注册标题
        registerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // 设置标题字体大小和加粗

        // 昵称输入框
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("昵称");  // 输入提示文字

        // 用户名输入框
        TextField usernameField = new TextField();
        usernameField.setPromptText("用户名");  // 输入提示文字

        // 限制用户名输入长度为8-16个字符，且只能输入字母和数字

        usernameField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // 允许输入长度在8到16个字符之间，并且只允许字母和数字
            if (newText.length() > 16) {
                return null;  // 超过16个字符，不允许修改
            }
            if (newText.matches("[a-zA-Z0-9]*") || newText.isEmpty()) {
                return change;  // 输入符合要求，允许修改
            }
            return null;  // 输入不符合要求，不允许修改
        }));

        // 电话输入框
        TextField phonenumberField = new TextField();
        phonenumberField.setPromptText("输入电话号码");  // 输入提示文字

        // 限制电话输入长度为10个字符，且只能输入数字
        phonenumberField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // 允许输入长度在8到16个字符之间，并且只允许字母和数字
            if (newText.length() > 10) {
                return null;  // 超过16个字符，不允许修改
            }
            if (newText.matches("[0-9]*") || newText.isEmpty()) {
                return change;  // 输入符合要求，允许修改
            }
            return null;  // 输入不符合要求，不允许修改
        }));

        // 第一次密码输入框
        PasswordField passwordField1 = new PasswordField();
        passwordField1.setPromptText("输入密码,密码要求：8-16个字符，包含字母、数字及符号.!#-*/+");  // 输入提示文字

        // 限制密码输入长度为8-16个字符，并且只能输入特定字符
        passwordField1.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // 允许输入长度在8到16个字符之间
            if (newText.length() > 16) {
                return null;  // 超过16个字符，不允许修改
            }
            // 允许大小写字母、数字和指定符号
            if (newText.matches("[a-zA-Z0-9.!#,+\\-*/]*") || newText.isEmpty()) {
                return change;  // 输入符合要求，允许修改
            }
            return null;  // 输入不符合要求，不允许修改
        }));

        // 再次输入密码框
        PasswordField passwordField2 = new PasswordField();
        passwordField2.setPromptText("再次输入密码");  // 输入提示文字

        // 再次限制密码输入长度为8-16个字符，并且只能输入特定字符
        passwordField2.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            // 允许输入长度在8到16个字符之间
            if (newText.length() > 16) {
                return null;  // 超过16个字符，不允许修改
            }
            // 允许大小写字母、数字和指定符号
            if (newText.matches("[a-zA-Z0-9.!#,+\\-*/]*") || newText.isEmpty()) {
                return change;  // 输入符合要求，允许修改
            }
            return null;  // 输入不符合要求，不允许修改
        }));

        // 性别选择框
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("男", "女");  // 添加性别选项
        genderComboBox.setPromptText("请选择性别");  // 提示文字

        // 注册按钮
        Button registerButton = new Button("注册");
        Label registerMessage = new Label();  // 注册信息提示标签
        registerMessage.setStyle("-fx-text-fill: red;");  // 设置文本颜色为红色

        // 设置注册按钮大小
        registerButton.setMinWidth(100);
        registerButton.setMinHeight(35);

        // 注册按钮点击事件
        registerButton.setOnAction(e -> {
            String inputNickname = nicknameField.getText();
            String inputUsername = usernameField.getText();
            String phonenumber = phonenumberField.getText();
            String inputPassword1 = passwordField1.getText();
            String inputPassword2 = passwordField2.getText();
            String selectedGender = genderComboBox.getValue();

            // 检查是否选择了性别
            if (selectedGender == null) {
                registerMessage.setText("请选择性别");
            } else if (!inputPassword1.equals(inputPassword2)) {
                registerMessage.setText("两次输入的密码不一致");
            } else if (!dbManager.userExists(inputUsername) && !inputUsername.equals(SUPER_USER)) {
                dbManager.addUser(inputUsername, inputPassword1, inputNickname, false, "自主注册", selectedGender, phonenumber);  // 添加用户并传入性别
                registerMessage.setText("注册成功！");
                PauseTransition pause = new PauseTransition(Duration.seconds(1));  // 注册成功后2秒返回登录界面
                pause.setOnFinished(event -> showLoginScreen(stage));
                pause.play();
            } else {
                registerMessage.setText("用户名已存在");
            }
        });

        // 返回按钮
        Button backButton = new Button("返回");
        backButton.setMinWidth(50);
        backButton.setMinHeight(25);
        backButton.setOnAction(e -> main.start(stage));  // 点击返回主界面

        // 创建底部布局，用于放置返回按钮
        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);  // 将返回按钮对齐到右侧

        // 将控件添加到VBox布局中
        registerBox.getChildren().addAll(registerLabel, nicknameField, usernameField, phonenumberField, passwordField1, passwordField2, genderComboBox, registerButton, registerMessage, bottomBox);

        // 创建注册场景
        Scene registerScene = new Scene(registerBox, 660.0, 440.0);
        stage.setScene(registerScene);  // 设置注册场景

        // 设置用户名输入框为初始焦点
        PauseTransition focusPause = new PauseTransition(Duration.millis(10));
        focusPause.setOnFinished(event -> backButton.requestFocus());
        focusPause.play();
    }
}