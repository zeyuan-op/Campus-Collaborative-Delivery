package Demo;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.application.Application;  // 引入JavaFX的应用程序包
import javafx.geometry.Pos;  // 设置元素的对齐方式
import javafx.scene.control.*;  // 引入JavaFX控件库
import javafx.scene.layout.BorderPane;  // 引入边界布局
import javafx.scene.layout.Region;
import javafx.scene.image.Image;  // 引入 Image 类
import javafx.scene.layout.HBox;  // 导入 HBox 类
import javafx.scene.layout.VBox;  // 导入 VBox 类
import java.sql.*;
import javafx.scene.layout.Priority;
import javafx.util.Duration;


// 主类继承Application，JavaFX应用程序入口
public class Main extends Application {
    private DatabaseUser dbManager;  // 数据库管理器
    private DatabaseTask TasksManager;  // 数据库管理器
    private DatabaseChat ChatManager;  // 数据库管理器
    private String username = "";  // 当前用户的用户名
    private String nickname = "";  // 当前用户的昵称
    private boolean isAdmin = false;  // 是否为管理员
    private Scene previousScene;
    private Connection connection;
    private String url = "jdbc:mysql://localhost:3306/main";  // 使用的数据库url
    // 超级用户信息，用于特殊权限登录
    private static final String SUPER_USER = "";     // 超级用户名
    private static final String SUPER_PASSWORD = "";// 一定要与数据库密码一致
    private static final String SUPER_NICKNAME = "";
    // 应用程序启动方法
    public void start(Stage stage) {
        try {
            // 使用传入的URL、用户名和密码连接数据库
            connection = DriverManager.getConnection(url, SUPER_USER, SUPER_PASSWORD);
        } catch (SQLException e) {
            // 捕获并打印SQL异常
            e.printStackTrace();
        }
        dbManager = new DatabaseUser(connection);
        TasksManager = new DatabaseTask(connection);
        ChatManager = new DatabaseChat(connection);
        LoginAndRegister loginAndRegister = new LoginAndRegister(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, this, username, dbManager,TasksManager);
        // 创建根布局VBox
        VBox root = new VBox();
        root.setPadding(new Insets(20.0));
        root.setAlignment(Pos.CENTER);  // 设置元素居中对齐

        ScreenOfTask screenOfTask = new ScreenOfTask(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, this, username, dbManager, TasksManager, ChatManager, connection);

        // 欢迎标题
        Label titleLabel = new Label("欢迎！请选择操作");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // 设置标题字体大小和加粗

        // 创建一个独立的VBox用于按钮
        VBox centerBox = new VBox(10); // 设置按钮之间的间距
        centerBox.setAlignment(Pos.CENTER); // 设置对齐方式为居中

        Button loginButton = new Button("登录");
        Button registerButton = new Button("注册");

        // 设置按钮大小
        loginButton.setMinWidth(100);
        loginButton.setMinHeight(35);  // 设置最小高度为60
        registerButton.setMinWidth(100);
        registerButton.setMinHeight(35);  // 设置最小高度为60

        LoginAndRegister LAR = new LoginAndRegister(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, this, username, dbManager, TasksManager);
        loginButton.setOnAction(e -> LAR.showLoginScreen(stage));  // 点击登录按钮显示登录界面
        registerButton.setOnAction(e -> loginAndRegister.showRegisterScreen(stage));  // 点击注册按钮显示注册界面

        // 将登录和注册按钮添加到中心布局
        centerBox.getChildren().addAll(loginButton, registerButton);

        // 创建两个不同的Spacer以填充可用空间
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS); // 使其在垂直方向上生长
        VBox.setVgrow(spacer2, Priority.ALWAYS); // 使其在垂直方向上生长

        // 将欢迎标题添加到根布局
        root.getChildren().add(titleLabel);
        // 将两个占位符和中心布局（按钮）添加到根布局
        root.getChildren().addAll(spacer1, centerBox, spacer2);

        // 创建底部布局
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER_RIGHT); // 设置对齐方式为右对齐
        Button exitButton = new Button("退出程序");

        // 设置退出按钮大小
        exitButton.setMinWidth(50);  // 设置最小宽度为
        exitButton.setMinHeight(18);  // 设置最小高度为60

        exitButton.setOnAction(e -> stage.close());  // 点击退出按钮关闭程序
        bottomBox.getChildren().add(exitButton); // 添加退出按钮

        // 将底部布局添加到根布局
        root.getChildren().add(bottomBox);

        // 设置场景和窗口属性
        Scene scene = new Scene(root, 660.0, 440.0);
        stage.setScene(scene);
        stage.setTitle("校园协送");

        // 设置窗口左上角的图标
        stage.getIcons().add(new Image("1.png"));

        stage.show();  // 显示窗口
    }

    // 显示主界面的方法
    public void showMainScreen(Stage stage, String username, DatabaseUser dbManager, DatabaseTask TasksManager) {
        BorderPane mainPane = new BorderPane();  // 使用边界布局
        Label welcomeLabel;
        if (username.equals(SUPER_USER)){
            welcomeLabel = new Label("欢迎, " + SUPER_NICKNAME);  // 显示当前用户的昵称
        }else{
            welcomeLabel = new Label("欢迎, " + dbManager.getNickname(username));  // 显示当前用户的昵称
        }
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;"); // 设置标题字体大小和加粗
        Button settingsButton = new Button("设置");  // 设置按钮
        Button taskButton = new Button("查看订单");  // 订单按钮
        Button TaskManageButton = new Button("订单管理");  // 订单按钮

        // 设置按钮大小
        settingsButton.setMinWidth(100);  // 设置最小宽度为
        settingsButton.setMinHeight(35);  // 设置最小高度为60
        TaskManageButton.setMinWidth(100);
        TaskManageButton.setMinHeight(35);
        taskButton.setMinWidth(100);  // 设置最小宽度为
        taskButton.setMinHeight(35);  // 设置最小高度为60

        ScreenOfTask screenOfTask = new ScreenOfTask(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,this,username,dbManager, TasksManager, ChatManager,connection);
        Settings ST = new Settings(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,this,username,dbManager,TasksManager);

        // 设置按钮点击事件，显示设置界面
        settingsButton.setOnAction(e -> ST.showSettingsScreen(stage));
        taskButton.setOnAction(e -> screenOfTask.showAllTasksScreen(stage, dbManager, TasksManager,username, "null"));
        TaskManageButton.setOnAction(e -> screenOfTask.showAllTasksScreen(stage, dbManager, TasksManager, username,"null"));

        // 创建一个 VBox 用于放置按钮
        VBox buttonContainer = new VBox(10); // 设置按钮之间的间距为 10 像素
        buttonContainer.setAlignment(Pos.CENTER); // 设置对齐方式为中心

        BorderPane.setAlignment(welcomeLabel, Pos.TOP_CENTER);  // 设置欢迎标签的对齐方式
        mainPane.setTop(welcomeLabel);  // 将标签放在顶部
        mainPane.setCenter(buttonContainer);  // 将 VBox 放置在主界面中央

        // 如果当前用户是管理员，显示查看所有用户信息的按钮
        ScreenOfTask MT = new ScreenOfTask(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, this, username, dbManager, TasksManager, ChatManager, connection);
        ScreenOfUser SA = new ScreenOfUser(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, this, username, dbManager, TasksManager);
        if (dbManager.isAdmin(username) || username.equals(SUPER_USER)) {
            Button viewUsersButton = new Button("用户管理");  // 查看用户按钮
            viewUsersButton.setMinWidth(100);  // 设置最小宽度为
            viewUsersButton.setMinHeight(35);  // 设置最小高度为60
            viewUsersButton.setOnAction(e -> SA.showAllUsersScreen(stage));  // 点击显示所有用户信息界面
            buttonContainer.getChildren().add(viewUsersButton); // 将查看用户按钮添加到 VBox 中
            buttonContainer.getChildren().add(TaskManageButton);
        }else{
            buttonContainer.getChildren().add(taskButton); // 将查看订单按钮添加到 VBox 中
            Button publishButton = new Button("发布订单");
            publishButton.setMinWidth(100);
            publishButton.setMinHeight(35);
            publishButton.setOnAction(e -> {
                OrderPublisher orderPublisher = new OrderPublisher(TasksManager, username, MT, stage, SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, null, dbManager);
                orderPublisher.showPublishOrderScreen(username,dbManager);
            });
            buttonContainer.getChildren().add(publishButton);
        }

        // 设置按钮始终放在右下角
        BorderPane.setAlignment(settingsButton, Pos.BOTTOM_RIGHT);  // 设置设置按钮的对齐方式
        mainPane.setBottom(settingsButton);  // 将设置按钮放在底部

        previousScene = stage.getScene();  // 保存当前场景，供返回时使用

        Scene mainScene = new Scene(mainPane, 660.0, 440.0);  // 创建主界面场景
        stage.setScene(mainScene);  // 设置主界面场景
    }

    // 退出登录
    public void logout(Stage stage) {
        username = "";  // 清空当前用户名
        nickname = "";  // 清空当前昵称
        isAdmin = false;  // 重置管理员标志
        start(stage);  // 返回主界面
    }

    public void setNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 主函数，启动应用程序
    public static void main(String[] args) {
        launch(args);  // 启动JavaFX应用程序
    }
}
