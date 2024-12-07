package Demo;

import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.File;
import java.sql.Timestamp;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement; // 确保导入 PreparedStatement

import javafx.scene.layout.BorderPane;

public class ScreenOfTask {
    private Scene previousScene;

    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask taskManager;
    private DatabaseChat ChatManager;
    private Connection connection;

    public ScreenOfTask(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask taskManager, DatabaseChat ChatManager, Connection connection){
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME =SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.taskManager = taskManager;
        this.ChatManager = ChatManager;
        this.connection = connection;
    }

    public void loadTaskData(ObservableList<GetTasks> tasksData, String username, DatabaseUser dbManager, DatabaseTask tasksManager, String S) {
        tasksData.clear();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/main", "", "")) {
            // 构建SQL查询语句
            String sql;
            if (dbManager.isAdmin(username) || username.equals(SUPER_USER)) {
                // 如果是管理员或超级用户，则显示所有订单，并根据状态筛选
                if (S != null && !S.isEmpty()) {
                    sql = "SELECT * FROM Tasks WHERE state = ?";
                } else {
                    sql = "SELECT * FROM Tasks";
                }
            } else {
                // 非管理员用户只显示 Receiver 为 null 的订单，并根据状态筛选
                sql = "SELECT * FROM Tasks WHERE Receiver IS NULL";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (S != null && !S.isEmpty() && (dbManager.isAdmin(username) || username.equals(SUPER_USER))) {
                    pstmt.setString(1, S); // 设置状态参数
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 在这里处理异常，比如显示错误信息给用户
            System.err.println("数据库查询失败: " + e.getMessage());
        }
    }

    public void showOrderDetails(GetTasks getTasks, DatabaseUser dbManager, DatabaseTask TasksManager, String username, Stage S) {
        Stage detailsStage = new Stage();
        MyTasks MT = new MyTasks(SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, main, username, dbManager, TasksManager, ChatManager, connection);

        // 设置窗口图标
        File iconFile = new File("订单详情.png");
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
        column2.setPercentWidth(60);
        detailsLayout.getColumnConstraints().addAll(column1, column2);

        detailsLayout.add(new Label("订单号:"), 0, 0);
        detailsLayout.add(new Label(getTasks.getOrderNumber()), 1, 0);
        detailsLayout.add(new Label("性别要求:"), 0, 1);
        detailsLayout.add(new Label(getTasks.getGender()), 1, 1);
        detailsLayout.add(new Label("楼栋:"), 0, 2);
        detailsLayout.add(new Label(getTasks.getBuilding()), 1, 2);

        CancelTasks CT = new CancelTasks(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager,connection);
        if (getTasks.getState().equals("待接收") &&
                !(username.equals(TasksManager.getPublisher(getTasks.getOrderNumber())) || (dbManager.isAdmin(username) || username.equals(SUPER_USER)))){
            detailsLayout.add(new Label("房间:"), 0, 3);
            detailsLayout.add(new Label("******"), 1, 3);
            detailsLayout.add(new Label("报酬:"), 0, 4);
            detailsLayout.add(new Label(getTasks.getAmount() + "元"), 1, 4);
            detailsLayout.add(new Label("发布时间:"), 0, 5);
            detailsLayout.add(new Label(getTasks.getReleaseDate().toString()), 1, 5);
            detailsLayout.add(new Label("订单状态:"), 0, 6);
            detailsLayout.add(new Label(getTasks.getState()), 1, 6);

            Button closeButton = new Button("关闭");
            closeButton.setOnAction(e -> detailsStage.close());
            detailsLayout.add(closeButton, 0, 14);

            Button ReButton = new Button("接单");
            ReButton.setOnAction(e -> {
                    MT.AcceptTask(getTasks, username, getTasks.getOrderNumber(), TasksManager, dbManager, S) ;
                    detailsStage.close();
            });
            detailsLayout.add(ReButton, 1, 14);

        } else if ((dbManager.isAdmin(username) || username.equals(SUPER_USER) || username.equals(TasksManager.getReceiver(getTasks.getOrderNumber()))
                || username.equals(TasksManager.getPublisher(getTasks.getOrderNumber())))) {//进行中且查看用户为管理员与发布者、接收者

            detailsLayout.add(new Label("房间:"), 0, 3);
            detailsLayout.add(new Label(getTasks.getRoom()), 1, 3);
            detailsLayout.add(new Label("报酬:"), 0, 4);
            detailsLayout.add(new Label(getTasks.getAmount() + "元"), 1, 4);
            detailsLayout.add(new Label("发布时间:"), 0, 5);
            detailsLayout.add(new Label(getTasks.getReleaseDate().toString()), 1, 5);
            detailsLayout.add(new Label("状态:"), 0, 6);
            detailsLayout.add(new Label(getTasks.getState()), 1, 6);
            if (username.equals(getTasks.getPublisher())){                                                               //发布者查看
                if ((getTasks.getState().equals("待接收") || getTasks.getState().equals("进行中"))) {
                    Button CTButton = new Button("取消订单");
                    CTButton.setOnAction(e -> {
                        CT.CancelOfPublisher(getTasks,S,getTasks.getOrderNumber());
                        detailsStage.close();
                    });
                    detailsLayout.add(CTButton, 1, 14);
                }
                if (getTasks.getState().equals("退单中")){
                    Button ReButton = new Button("退单详情");
                    detailsLayout.add(ReButton, 0, 14);
                    ReButton.setOnAction(e -> {
                        CT.Chargebacks(getTasks,S,getTasks.getOrderNumber());
                        detailsStage.close();
                    });
                }
                if (!getTasks.getState().equals("待接收") && !(getTasks.getReceiver() == null || getTasks.getReceiver().isEmpty())){
                    detailsLayout.add(new Label("接单人联系电话:"), 0, 7);
                    detailsLayout.add(new Label(dbManager.getPhonenumber(getTasks.getReceiver())), 1, 7);
                    detailsLayout.add(new Label("接单人:"), 0, 8);
                    detailsLayout.add(new Label(getTasks.getReceiverName()), 1, 8);
                    detailsLayout.add(new Label("接单时间:"), 0, 9);
                    detailsLayout.add(new Label(getTasks.getReceive_Date().toString()), 1, 9);
                }
                if (getTasks.getState().equals("已取消")){
                    detailsLayout.add(new Label("取消时间:"), 0, 10);
                    detailsLayout.add(new Label(getTasks.getCompletion_Date().toString()), 1, 10);
                }
                if (getTasks.getState().equals("退单中")){
                    detailsLayout.add(new Label("退单申请时间:"), 0, 10);
                    detailsLayout.add(new Label(getTasks.getCompletion_Date().toString()), 1, 10);
                }
            }
            if (username.equals(getTasks.getReceiver())){                                                           //接收人查看
                detailsLayout.add(new Label("发布人联系电话:"), 0, 7);
                detailsLayout.add(new Label(dbManager.getPhonenumber(getTasks.getPublisher())), 1, 7);
                detailsLayout.add(new Label("发布人"), 0, 8);
                detailsLayout.add(new Label(getTasks.getPublisherName()), 1, 8);
                detailsLayout.add(new Label("接单时间:"), 0, 9);
                detailsLayout.add(new Label(getTasks.getReceive_Date().toString()), 1, 9);
                if (getTasks.getState().equals("已取消")){
                    detailsLayout.add(new Label("取消时间:"), 0, 10);
                    detailsLayout.add(new Label(getTasks.getCompletion_Date().toString()), 1, 10);
                }
                if (getTasks.getState().equals("进行中")){
                    Button CTButton = new Button("退单");
                    CTButton.setOnAction(e -> {
                        CT.CancelOfPublisher(getTasks,S,getTasks.getOrderNumber());
                        detailsStage.close();
                        showAllTasksScreen(S, dbManager, TasksManager, username,"my_c");
                    });
                    detailsLayout.add(CTButton, 0, 14);
                    Button ComButton = new Button("完成订单");
                    ComButton.setOnAction(e -> {
                        MT.CompletedtTask(getTasks,username,getTasks.getOrderNumber(),TasksManager,dbManager,new Stage());
                        detailsStage.close();
                        showAllTasksScreen(S, dbManager, TasksManager, username,"my_d");
                    });
                    detailsLayout.add(ComButton, 1, 14);
                }
                if (getTasks.getState().equals("退单中")){
                    detailsLayout.add(new Label("退单申请时间:"), 0, 10);
                    detailsLayout.add(new Label(getTasks.getCompletion_Date().toString()), 1, 10);
                }
            }
            if (username.equals(SUPER_USER) || dbManager.isAdmin(username)){                                        //管理用户查看
                detailsLayout.add(new Label("发布人 - 用户名:"), 0, 7);
                detailsLayout.add(new Label(getTasks.getPublisherName() + "-" + getTasks.getPublisher()), 1, 7);
                detailsLayout.add(new Label("发布人联系电话:"), 0, 8);
                detailsLayout.add(new Label(dbManager.getPhonenumber(getTasks.getPublisher())), 1, 8);
                if (!getTasks.getState().equals("待接收") && !(getTasks.getReceiver() == null || getTasks.getReceiver().isEmpty())){
                    detailsLayout.add(new Label("接单人 - 用户名:"), 0, 9);
                    detailsLayout.add(new Label(getTasks.getReceiverName() + "-" + getTasks.getReceiver()), 1, 9);
                    detailsLayout.add(new Label("接单人联系电话:"), 0, 10);
                    detailsLayout.add(new Label(dbManager.getPhonenumber(getTasks.getReceiver())), 1, 10);
                    detailsLayout.add(new Label("接单时间:"), 0, 11);
                    detailsLayout.add(new Label(getTasks.getReceive_Date().toString()), 1, 11);
                }
                if (getTasks.getState().equals("已取消")){
                    detailsLayout.add(new Label("取消时间:"), 0, 12);
                    detailsLayout.add(new Label(getTasks.getCompletion_Date().toString()), 1, 12);
                }
                if (getTasks.getState().equals("退单中")){
                    detailsLayout.add(new Label("退单申请时间:"), 0, 12);
                    detailsLayout.add(new Label(getTasks.getCompletion_Date().toString()), 1, 12);
                }
                if (getTasks.getState().equals("待接收") || getTasks.getState().equals("进行中")){
                    Button ReButton = new Button("取消订单");
                    ReButton.setOnAction(e -> {
                        CT.CancelOfPublisher(getTasks,S,getTasks.getOrderNumber());
                        detailsStage.close();
                        detailsLayout.add(ReButton, 0, 14);
                    });
                }
            }
        }
        SendMassege CM = new SendMassege(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager,ChatManager,connection);
        if (!getTasks.getState().equals("待接收") && !username.equals(SUPER_USER) && !dbManager.isAdmin(username)){
            Button ChatButton = new Button("发送信息");
            detailsLayout.add(ChatButton, 1, 15);
            ChatButton.setOnAction(e -> {
                CM.Chatbox(username,getTasks.getOrderNumber(),getTasks);
                detailsStage.close();
            });
        }

        Scene detailsScene = new Scene(detailsLayout, 340, 420);
        detailsStage.setScene(detailsScene);
        detailsStage.setTitle("订单详情");
        detailsStage.show();
    }

    public void showAllTasksScreen(Stage stage,DatabaseUser dbManager,DatabaseTask TasksManager, String username, String st) {
        VBox taskListBox = new VBox(10.0);
        taskListBox.setPadding(new Insets(20.0));
        taskListBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("订单信息");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-font-weight: bold;");
        taskListBox.getChildren().add(titleLabel);


        TableColumn<GetTasks, String> orderNumberCol = new TableColumn<>("订单号");
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        TableColumn<GetTasks, String> genderCol = new TableColumn<>("性别要求");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        TableColumn<GetTasks, Float> amountCol = new TableColumn<>("报酬（元）");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<GetTasks, Timestamp> releaseDateCol = new TableColumn<>("发布时间");
        releaseDateCol.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        TableColumn<GetTasks, String> buildingCol = new TableColumn<>("目标楼栋");
        buildingCol.setCellValueFactory(new PropertyValueFactory<>("building"));
        TableColumn<GetTasks, String> stateCol = new TableColumn<>("订单状态");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));

        TableView<GetTasks> taskTable = new TableView<>();
        ObservableList<GetTasks> tasksData = FXCollections.observableArrayList();
        taskTable.getColumns().addAll(orderNumberCol, genderCol, amountCol, releaseDateCol, buildingCol, stateCol);
        MyTasks MT = new MyTasks(SUPER_USER,SUPER_PASSWORD,SUPER_NICKNAME,main,username,dbManager,TasksManager, ChatManager, connection);
        if (dbManager.isAdmin(username) || username.equals(SUPER_USER)){
            // 创建一个HBox来存放状态按钮
            loadTaskData(tasksData, username, dbManager, TasksManager,"待接收");
            HBox statusButtonsBox = new HBox(0);
            statusButtonsBox.setAlignment(Pos.CENTER);
            taskListBox.getChildren().add(statusButtonsBox);

            // 定义状态按钮
            Button awaitingButton = new Button("待接收");
            Button ongoingButton = new Button("进行中");
            Button completedButton = new Button("已完成");
            Button cancelledButton = new Button("已取消");
            Button returnedButton = new Button("退单中");
            Button refundedButton = new Button("已退单");

            // 将按钮添加到HBox中
            statusButtonsBox.getChildren().addAll(awaitingButton, ongoingButton, completedButton, cancelledButton, returnedButton,refundedButton);
            awaitingButton.setOnAction(e -> loadTaskData(tasksData, username, dbManager, TasksManager,"待接收"));
            ongoingButton.setOnAction(e -> loadTaskData(tasksData, username, dbManager, TasksManager,"进行中"));
            completedButton.setOnAction(e -> loadTaskData(tasksData, username, dbManager, TasksManager,"已完成"));
            cancelledButton.setOnAction(e -> loadTaskData(tasksData, username, dbManager, TasksManager,"已取消"));
            returnedButton.setOnAction(e -> loadTaskData(tasksData, username, dbManager, TasksManager,"退单中"));
            refundedButton.setOnAction(e -> loadTaskData(tasksData, username, dbManager, TasksManager,"已退单"));
        }else {
            if (st==null){
                MT.MyTasksData(tasksData, username, dbManager, TasksManager,"all");
                taskTable.setItems(tasksData);
            }else{
                MT.MyTasksData(tasksData, username, dbManager, TasksManager,st);
                taskTable.setItems(tasksData);
            }
            HBox statusButtonsBox = new HBox(0);
            statusButtonsBox.setAlignment(Pos.CENTER);
            taskListBox.getChildren().add(statusButtonsBox);
            MT.MyTasksData(tasksData, username, dbManager, TasksManager,"all");
            // 定义状态按钮
            Button AllTasksButton = new Button("待接收订单");
            Button MyTasks_PButton = new Button("我发布的单");
            Button MyTasks_RButton = new Button("我接收的单");
            Button DisputeButton = new Button("争议的订单");
            Button ComButton = new Button("已完结订单");
            statusButtonsBox.getChildren().addAll(AllTasksButton, MyTasks_PButton, MyTasks_RButton, DisputeButton, ComButton);

            if (st.equals("all")) {
                AllTasksButton.requestFocus();
            } else if (st.equals("my_p")) {
                MyTasks_PButton.requestFocus();
            } else if (st.equals("my_r")) {
                MyTasks_RButton.requestFocus();
            }else if (st.equals("my_d")) {
                DisputeButton.requestFocus();
            }else {
                ComButton.requestFocus();
            }
            AllTasksButton.setOnAction(e -> {
                MT.MyTasksData(tasksData, username, dbManager, TasksManager, "all");
                AllTasksButton.requestFocus();
            });
            MyTasks_PButton.setOnAction(e -> {
                MT.MyTasksData(tasksData, username, dbManager, TasksManager, "my_p");
                MyTasks_PButton.requestFocus();
            });
            MyTasks_RButton.setOnAction(e -> {
                MT.MyTasksData(tasksData, username, dbManager, TasksManager, "my_r");
                MyTasks_RButton.requestFocus();
            });
            DisputeButton.setOnAction(e -> {
                MT.MyTasksData(tasksData, username, dbManager, TasksManager, "my_d");
                DisputeButton.requestFocus();
            });
            ComButton.setOnAction(e -> {
                MT.MyTasksData(tasksData, username, dbManager, TasksManager, "my_c");
                ComButton.requestFocus();
            });
        }
        //填充表
        taskTable.setItems(tasksData);
        //双击看详情
        taskTable.setRowFactory(tv -> {
            TableRow<GetTasks> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    GetTasks selectedTask = row.getItem();
                    showOrderDetails(selectedTask ,dbManager, TasksManager, username, stage);
                }
            });
            return row;
        });

        VBox centerButtonsBox = new VBox(10, taskTable);
        centerButtonsBox.setAlignment(Pos.CENTER);
        taskListBox.getChildren().add(centerButtonsBox);
        if (dbManager.isAdmin(username) || username.equals(SUPER_USER)){
        }else{
            Button publishButton = new Button("发布订单");
            publishButton.setMinWidth(100);
            publishButton.setMinHeight(35);
            publishButton.setOnAction(e -> {
                OrderPublisher orderPublisher = new OrderPublisher(TasksManager, username, this, stage, SUPER_USER, SUPER_PASSWORD, SUPER_NICKNAME, null, dbManager);
                orderPublisher.showPublishOrderScreen(username,dbManager);
            });
            taskListBox.getChildren().add(publishButton);
        }


        // 将订单按钮放在界面中央
        Button backButton = new Button("返回");
        backButton.setMinWidth(50);
        backButton.setMinHeight(18);
        backButton.setOnAction(e -> main.showMainScreen(stage, username, dbManager, TasksManager));

        // 使用 BorderPane 设置布局
        BorderPane rootPane = new BorderPane();
        rootPane.setCenter(taskListBox);
        // 将返回按钮放置在右下角
        BorderPane.setAlignment(backButton, Pos.BOTTOM_RIGHT);
        rootPane.setBottom(backButton);
        BorderPane.setMargin(backButton, new Insets(10, 10, 10, 10)); // 设置边距

        Scene taskScene = new Scene(rootPane, 660, 440);
        stage.setScene(taskScene);
    }

}
