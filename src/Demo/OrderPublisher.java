package Demo;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Timestamp;

public class OrderPublisher {

    private DatabaseTask tasksManager; // 数据库管理器
    private String publisherUsername; // 发布者用户名
    private DatabaseUser UserManager; // 用户数据库管理器
    private ScreenOfTask screenOfTask; // 引用 ManageTask 实例
    private Stage mainStage; // 主窗口的 Stage 引用
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;

    public OrderPublisher(DatabaseTask tasksManager, String publisherUsername, ScreenOfTask screenOfTask, Stage mainStage,
                          String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, DatabaseUser dbManager){
        this.tasksManager = tasksManager;
        this.publisherUsername = publisherUsername;
        this.UserManager = dbManager;
        this.screenOfTask = screenOfTask; // 初始化 ManageTask 实例
        this.mainStage = mainStage; // 初始化主窗口 Stage
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
    }
    // 性别枚举类型
    public enum Gender {
        MALE("男"),
        FEMALE("女"),
        NONE("无要求");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // 显示发布订单界面
    public void showPublishOrderScreen(String username, DatabaseUser dbManager) {
        Stage publishStage = new Stage();
        publishStage.setTitle("发布新订单");
        File iconFile = new File("+.png");
        if (iconFile.exists()) {
            Image icon = new Image(iconFile.toURI().toString());
            publishStage.getIcons().add(icon);
        } else {
            System.err.println("Icon file not found: " + iconFile.getAbsolutePath());
        }
        GridPane layout = new GridPane();
        layout.setPadding(new Insets(20));
        layout.setHgap(10);
        layout.setVgap(10);

        // 各种输入字段
        TextField buildingField = new TextField();
        TextField roomField = new TextField();
        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("默认注册手机号");
        TextField amountField = new TextField();

        // 性别选择框
        ComboBox<Gender> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll(Gender.values());
        genderComboBox.setValue(Gender.NONE); // 设置默认值

        layout.add(new Label("性别要求:"), 0, 0);
        layout.add(genderComboBox, 1, 0);
        layout.add(new Label("建筑:"), 0, 1);
        layout.add(buildingField, 1, 1);
        layout.add(new Label("房间:"), 0, 2);
        layout.add(roomField, 1, 2);
        layout.add(new Label("联系电话:"), 0, 3);
        layout.add(phoneNumberField, 1, 3);
        layout.add(new Label("报酬（元）:"), 0, 4);
        layout.add(amountField, 1, 4);

        Button publishButton = new Button("发布订单");


        publishButton.setOnAction(e -> {
            if (phoneNumberField.getText() == "") { // 检查文本是否为空或空白
                publishOrder(genderComboBox.getValue(), buildingField.getText(), roomField.getText(), dbManager.getPhonenumber(username), amountField.getText(), dbManager);
            } else {
                publishOrder(genderComboBox.getValue(), buildingField.getText(), roomField.getText(), phoneNumberField.getText(), amountField.getText(), dbManager);
            }
            publishStage.close(); // 订单发布成功后关闭发布窗口
        });
        layout.add(publishButton, 1, 5);

        Scene scene = new Scene(layout, 300, 300);
        publishStage.setScene(scene);
        publishStage.show();
    }

    // 发布订单逻辑
    private void publishOrder(Gender gender, String building, String room, String phoneNumber, String amountStr, DatabaseUser dbManager) {
        if (building.isEmpty() || room.isEmpty() || phoneNumber.isEmpty() || amountStr.isEmpty()) {
            showAlert("所有字段都是必填项！");
            return;
        }

        Float amount;
        try {
            amount = Float.parseFloat(amountStr);
        } catch (NumberFormatException e) {
            showAlert("请输入有效的报酬数额！");
            return;
        }

        Timestamp releaseDate = new Timestamp(System.currentTimeMillis());

        // 通过用户名获取发布者昵称
        String publicName = UserManager.getNickname(publisherUsername);
        tasksManager.insertNewTask(gender.toString(), building, room, phoneNumber, amount, releaseDate, publisherUsername, publicName);

        showAlert("订单发布成功！");

        // 刷新订单信息界面
        screenOfTask.showAllTasksScreen(mainStage, dbManager, tasksManager, publisherUsername,"my_p");
    }

    // 弹出警告信息
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
