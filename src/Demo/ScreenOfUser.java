package Demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

public class ScreenOfUser {
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;

    public ScreenOfUser(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
    }
    // 管理员查看所有用户信息界面
    public void showAllUsersScreen(Stage stage) {
        boolean isAdmin = dbManager.isAdmin(username);

        // 创建主布局 BorderPane
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20.0));

        // 创建并设置顶部标题
        Label titleLabel = new Label("所有用户信息");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-font-weight: bold;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER); // 设置HBox中的内容居中对齐

        root.setTop(titleBox); // 将HBox设置为BorderPane的顶部

        // 创建表格
        TableView<GetUsers> userTable = new TableView<>();
        ObservableList<GetUsers> usersData = FXCollections.observableArrayList();

        // 创建表格列
        TableColumn<GetUsers, String> usernameCol = new TableColumn<>("用户名");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<GetUsers, String> phonenumberCol = new TableColumn<>("电话号码");
        phonenumberCol.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));

        TableColumn<GetUsers, String> nicknameCol = new TableColumn<>("昵称");
        nicknameCol.setCellValueFactory(new PropertyValueFactory<>("nickname"));

        TableColumn<GetUsers, Boolean> isAdminCol = new TableColumn<>("是否为管理员");
        isAdminCol.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

        TableColumn<GetUsers, String> sourceCol = new TableColumn<>("来源");
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("source"));

        // 将列添加到表格中
        userTable.getColumns().addAll(usernameCol, phonenumberCol, nicknameCol, isAdminCol, sourceCol);

        // 获取用户数据并填充表格
        loadUserData(usersData);
        userTable.setItems(usersData);

        // 设置行工厂以处理双击事件
        userTable.setRowFactory(tv -> {
            TableRow<GetUsers> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    GetUsers selectedUser = row.getItem();
                    showUsersDetails(selectedUser, stage, usersData, userTable, username);
                }
            });
            return row;
        });

        // 添加用户按钮
        AddUser AU = new AddUser(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager);
        Button addUserButton = new Button("添加用户");
        addUserButton.setMinWidth(100);
        addUserButton.setMinHeight(35);
        addUserButton.setOnAction(e -> AU.showAddUserScreen(stage));

        // 返回按钮
        Button backButton = new Button("返回");
        backButton.setMinWidth(50);
        backButton.setMinHeight(18);
        backButton.setOnAction(e -> main.showMainScreen(stage, username, dbManager, TasksManager));

        // 使用HBox将按钮放在底部，并且返回按钮靠右
        HBox buttonBox = new HBox(10.0);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.getChildren().add(backButton);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.NEVER);
        spacer.setPrefHeight(20.0); // 设置间距高度为20像素

        // 将添加用户按钮和表格放入VBox中
        VBox centerBox = new VBox(10.0);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(spacer, userTable, addUserButton);

        // 设置BorderPane的中心和底部
        root.setCenter(centerBox);
        root.setBottom(buttonBox);

        // 创建场景并设置到舞台
        Scene userScene = new Scene(root, 660.0, 440.0);
        stage.setScene(userScene);
    }


    public void showUsersDetails(GetUsers getUsers, Stage stage, ObservableList<GetUsers> usersData, TableView<GetUsers> userTable, String username) {
        Stage detailsStage = new Stage();

        // 设置窗口图标
        File iconFile = new File("用户详情.png");
        if (iconFile.exists()) {
            Image icon = new Image(iconFile.toURI().toString());
            detailsStage.getIcons().add(icon);
        } else {
            System.err.println("Icon file not found: " + iconFile.getAbsolutePath());
        }

        GridPane detailsLayout = new GridPane();
        detailsLayout.setPadding(new Insets(50));
        detailsLayout.setHgap(10);
        detailsLayout.setVgap(10);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(40);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(60); // 注意这里应该是 60%，因为 40% + 60% = 100%
        detailsLayout.getColumnConstraints().addAll(column1, column2);

        // 添加标签和用户信息
        detailsLayout.add(new Label("用户名:"), 0, 0);
        detailsLayout.add(new Label(getUsers.getUsername()), 1, 0);
        detailsLayout.add(new Label("性别:"), 0, 1);
        detailsLayout.add(new Label(getUsers.getGender()), 1, 1);
        detailsLayout.add(new Label("电话号码:"), 0, 2);
        detailsLayout.add(new Label(getUsers.getPhonenumber()), 1, 2);
        detailsLayout.add(new Label("昵称:"), 0, 3);
        detailsLayout.add(new Label(getUsers.getNickname()), 1, 3);
        detailsLayout.add(new Label("密码:"), 0, 4);

        if (!username.equals(SUPER_USER)&&getUsers.getIsAdmin()) {
            // 管理员用户无法查看其他管理员的密码
            detailsLayout.add(new Label("********"), 1, 4);
        } else {
            // 超级用户可以查看所有用户的密码
            detailsLayout.add(new Label(getUsers.getPassword()), 1, 4);
        }

        detailsLayout.add(new Label("注册时间:"), 0, 5);
        detailsLayout.add(new Label(getUsers.getRegisterTime()), 1, 5);
        detailsLayout.add(new Label("管理员权限:"), 0, 6);
        String Ad;
        if (getUsers.getIsAdmin() == true) {
            Ad = "true";
        } else {
            Ad = "false"; // 这里应该是 "false" 而不是 "flase"
        }
        detailsLayout.add(new Label(Ad), 1, 6);
        detailsLayout.add(new Label("来源:"), 0, 7);
        detailsLayout.add(new Label(getUsers.getSource()), 1, 7);

        // 关闭按钮
        Button closeButton = new Button("关闭");
        closeButton.setOnAction(e -> detailsStage.close());
        detailsLayout.add(closeButton, 0, 10); // 更改行索引为 8

        // 删除用户按钮
        Button deleteButton = new Button("删除该用户");
        deleteButton.setOnAction(e -> {
            String username0 = getUsers.getUsername();  // 获取选中的用户
            if ((!username.equals(SUPER_USER) && !dbManager.isAdmin(username0)) || username.equals(SUPER_USER)) {
                // 显示密码确认对话框
                PasswordConfirmation PC = new PasswordConfirmation(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager);
                PC.showPasswordConfirmationDialog(
                        stage,
                        getUsers,
                        usersData,
                        userTable,
                        null,  // 不需要这些参数，因为是删除操作
                        null,
                        null,
                        false,
                        null,
                        null,
                        null,
                        null,
                        PasswordConfirmation.PasswordConfirmationAction.DELETE_USER
                );  // 添加删除时的密码确认功能
                detailsStage.close();
            } else {
                // 显示警告，提示无权限删除管理员用户
                Alert alert = new Alert(Alert.AlertType.WARNING, "你没有权限删除管理员用户！");
                alert.showAndWait();
            }
        });
        detailsLayout.add(deleteButton, 1, 10); // 更改行索引为 8

        Scene detailsScene = new Scene(detailsLayout, 340, 360);
        detailsStage.setScene(detailsScene);
        detailsStage.setTitle("用户详情");
        detailsStage.show();
    }

    // 加载用户数据的方法，从数据库获取用户信息
    public void loadUserData(ObservableList<GetUsers> usersData) {
        usersData.clear();  // 清空当前数据
        ResultSet rs = dbManager.getAllUsers();  // 从数据库获取所有用户信息
        try {
            while (rs != null && rs.next()) {
                String userUsername = rs.getString("username");
                String userphonenumber = rs.getString("phonenumber");
                String userNickname = rs.getString("nickname");
                String usergender = rs.getString("gender");
                String userPassword = rs.getString("password");
                String source = rs.getString("source");  // 获取 source 字段
                String register_time = rs.getString("register_time");
                boolean userIsAdmin = rs.getBoolean("isAdmin");

                // 将用户信息添加到数据列表中，包括 source 字段
                usersData.add(new GetUsers(userUsername, userphonenumber, userNickname, usergender, userPassword, userIsAdmin, source, register_time));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // 打印SQL异常
        }
    }

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
