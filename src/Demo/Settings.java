package Demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Settings {

    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;

    public Settings(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
    }

    public void showSettingsScreen(Stage stage) {
        VBox settingsBox = new VBox(10.0);  // 设置垂直布局和间距
        settingsBox.setPadding(new Insets(20.0));  // 设置内边距
        settingsBox.setAlignment(Pos.CENTER);  // 居中对齐

        Button logoutButton = new Button("退出登录");  // 退出登录按钮
        Button exitProgramButton = new Button("退出程序");  // 退出程序按钮
        Button backButton = new Button("返回");  // 返回按钮

        // 设置按钮大小
        logoutButton.setMinWidth(100);  // 设置最小宽度为
        logoutButton.setMinHeight(35);  // 设置最小高度为60
        exitProgramButton.setMinWidth(100);  // 设置最小宽度为
        exitProgramButton.setMinHeight(35);  // 设置最小高度为60
        backButton.setMinWidth(100);  // 设置最小宽度为
        backButton.setMinHeight(35);  // 设置最小高度为60

        // 设置退出登录和退出程序的动作
        logoutButton.setOnAction(e -> main.logout(stage));  // 点击退出登录按钮退出登录
        exitProgramButton.setOnAction(e -> stage.close());  // 点击退出程序按钮关闭程序
        backButton.setOnAction(e -> main.showMainScreen(stage, username, dbManager, TasksManager));  // 点击返回主界面


        // 如果不是超级用户，显示“修改密码”、“修改昵称”和“注销用户”按钮
        if (!username.equals(SUPER_USER)) {
            Button changePasswordButton = new Button("修改密码");  // 修改密码按钮
            Button changeNicknameButton = new Button("修改昵称");  // 修改昵称按钮
            Button deleteUserButton = new Button("注销用户");  // 注销用户按钮

            // 设置按钮的大小
            changePasswordButton.setMinWidth(100);  // 设置最小宽度
            changePasswordButton.setMinHeight(35);  // 设置最小高度为60
            changeNicknameButton.setMinWidth(100);  // 设置最小宽度为
            changeNicknameButton.setMinHeight(35);  // 设置最小高度为60
            deleteUserButton.setMinWidth(100);  // 设置最小宽度为
            deleteUserButton.setMinHeight(35);  // 设置最小高度为60

            // 设置按钮的动作
            ChangeInformation CI = new ChangeInformation(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager);  // 点击修改密码按钮显示修改密码界面
            changeNicknameButton.setOnAction(e -> CI.showChangeNicknameScreen(stage, stage.getScene()));  // 点击修改昵称按钮显示修改昵称界面
            deleteUserButton.setOnAction(e -> CI.deleteUser(stage));  // 点击注销用户按钮注销用户
            changePasswordButton.setOnAction(e -> CI.showChangePasswordScreen(stage, stage.getScene()));

            // 将这些控件添加到VBox布局中
            settingsBox.getChildren().addAll(changePasswordButton, changeNicknameButton, deleteUserButton);
        }

        // 超级用户仅显示“退出登录”、“退出程序”和“返回”按钮
        settingsBox.getChildren().addAll(logoutButton, exitProgramButton, backButton);

        // 创建设置场景并显示
        Scene settingsScene = new Scene(settingsBox, 660.0, 440.0);
        stage.setScene(settingsScene);  // 设置设置场景
    }
}
