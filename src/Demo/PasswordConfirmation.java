package Demo;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PasswordConfirmation {

    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;

    public PasswordConfirmation(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
    }
    public void showPasswordConfirmationDialog(Stage stage, GetUsers selectedGetUsers, ObservableList<GetUsers> usersData,
                                               TableView<GetUsers> userTable, String inputUsername, String inputPassword,
                                               String inputNickname, boolean isAdmin, String source, String gender, Label addUserMessage,
                                               String phonenumber, PasswordConfirmationAction action) {
        // 创建对话框
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("确认密码");

        // 根据操作设置对话框头部文本
        switch (action) {
            case DELETE_USER:
                if (selectedGetUsers != null) {
                    dialog.setHeaderText("请输入您的密码以确认删除该用户：");
                } else {
                    dialog.setHeaderText("请输入您的密码以确认注销此账号：");
                }
                break;
            case ADD_USER:
                dialog.setHeaderText("请输入您的密码以确认添加用户：");
                break;
        }

        // 创建密码输入字段
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("输入您的密码");

        // 将控件放入对话框中
        VBox dialogVBox = new VBox(10);
        dialogVBox.getChildren().addAll(new Label("密码:"), passwordField);
        dialog.getDialogPane().setContent(dialogVBox);

        // 添加按钮（确认和取消）
        ButtonType confirmButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // 点击确认按钮的处理
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        // 显示对话框并等待用户输入
        ScreenOfUser SA = new ScreenOfUser(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager);
        dialog.showAndWait().ifPresent(password -> {
            if (username.equals(SUPER_USER)) {
                if (SUPER_PASSWORD.equals(password)) {
                    switch (action) {
                        case DELETE_USER:
                            if (selectedGetUsers != null) {
                                dbManager.deleteUser(selectedGetUsers.getUsername());
                                usersData.remove(selectedGetUsers);
                                userTable.setItems(usersData);
                            } else {
                                dbManager.deleteUser(username);
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "注销成功！");
                                successAlert.showAndWait();
                                main.start(stage);
                            }
                            break;
                        case ADD_USER:
                            dbManager.addUser(inputUsername, inputPassword, inputNickname, isAdmin, source, gender, phonenumber);
                            addUserMessage.setText("用户添加成功！");

                            PauseTransition pause = new PauseTransition(Duration.seconds(1));
                            pause.setOnFinished(event -> SA.showAllUsersScreen(stage));
                            pause.play();
                            break;
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "密码错误，无法" + (action == PasswordConfirmationAction.ADD_USER ? "添加" : "删除") + "用户！");
                    alert.showAndWait();
                }
            } else {
                if (dbManager.validateUser(username, password)) {
                    switch (action) {
                        case DELETE_USER:
                            if (selectedGetUsers != null) {
                                dbManager.deleteUser(selectedGetUsers.getUsername());
                                usersData.remove(selectedGetUsers);
                                userTable.setItems(usersData);
                            } else {
                                dbManager.deleteUser(username);
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "注销成功！");
                                successAlert.showAndWait();
                                main.start(stage);
                            }
                            break;
                        case ADD_USER:
                            dbManager.addUser(inputUsername, inputPassword, inputNickname, isAdmin, source, gender, phonenumber);
                            addUserMessage.setText("用户添加成功！");

                            PauseTransition pause = new PauseTransition(Duration.seconds(1));
                            pause.setOnFinished(event -> SA.showAllUsersScreen(stage));
                            pause.play();
                            break;
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "密码错误，无法" + (action == PasswordConfirmationAction.ADD_USER ? "添加" : "删除") + "用户！");
                    alert.showAndWait();
                }
            }
        });
    }

    // 定义枚举来表示不同的动作
    enum PasswordConfirmationAction {
        DELETE_USER,
        ADD_USER
    }
}
