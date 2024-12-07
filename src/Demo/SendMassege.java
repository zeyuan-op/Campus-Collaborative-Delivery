package Demo;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class SendMassege extends Application {
    private String SUPER_USER;
    private String SUPER_PASSWORD;
    private String SUPER_NICKNAME;
    private Main main;
    private String username;
    private DatabaseUser dbManager;
    private DatabaseTask TasksManager;
    private DatabaseChat ChatManager;
    private Connection connection;

    public SendMassege(String SUPER_USER, String SUPER_PASSWORD, String SUPER_NICKNAME, Main main, String username, DatabaseUser dbManager, DatabaseTask TasksManager, DatabaseChat ChatManager, Connection connection) {
        this.SUPER_USER = SUPER_USER;
        this.SUPER_PASSWORD = SUPER_PASSWORD;
        this.SUPER_NICKNAME = SUPER_NICKNAME;
        this.main = main;
        this.username = username;
        this.dbManager = dbManager;
        this.TasksManager = TasksManager;
        this.ChatManager = ChatManager;
        this.connection = connection;
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    static class ChatRecord {
        private final String sender;
        private final Timestamp sDate;
        private final String content;

        public ChatRecord(String sender, Timestamp sDate, String content) {
            this.sender = sender;
            this.sDate = sDate;
            this.content = content;
        }

        public Timestamp getSDate() {
            return sDate;
        }

        @Override
        public String toString() {
            return "发送方: " + sender + " 时间: " + sDate + "\n" + content;
        }
    }

    public void Chatbox(String username, String orderNumber, GetTasks getTasks) {
        List<ChatRecord> chatRecords = new ArrayList<>();

        // 查询数据库中的聊天记录
        String selectSQL = "SELECT * FROM Chat WHERE Receiver = ? AND orderNumber = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, orderNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String Order_number = rs.getString("orderNumber");
                String sender = rs.getString("Sender");
                Timestamp sDate = rs.getTimestamp("S_Date");
                String content = rs.getString("content");
                chatRecords.add(new ChatRecord(sender, sDate, content));

                // 删除数据库中的记录
                String deleteSQL = "DELETE FROM Chat WHERE orderNumber = ? AND Sender = ? AND S_Date = ? AND content = ?";
                try (PreparedStatement deletePstmt = connection.prepareStatement(deleteSQL)) {
                    deletePstmt.setString(1, orderNumber);
                    deletePstmt.setString(2, sender);
                    deletePstmt.setTimestamp(3, sDate);
                    deletePstmt.setString(4, content);
                    deletePstmt.executeUpdate();
                }
            }

            // 按时间排序
            Collections.sort(chatRecords, Comparator.comparing(ChatRecord::getSDate));

            // 将聊天记录写入文件
            File dir = new File("聊天记录/" + username);
            if (!dir.exists()) {
                dir.mkdirs(); // 创建所有必要的父目录
            }

            // 创建文件
            File file = new File(dir, orderNumber + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (ChatRecord record : chatRecords) {
                    writer.write(record.toString());
                    writer.newLine();
                }
            }

            // 显示聊天记录
            Stage stage = new Stage();
            start(stage,orderNumber,getTasks);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String orderNumber, String Receiver, String content) {
        try {

            // 当前时间
            Timestamp sDate = new Timestamp(System.currentTimeMillis());

            // 保存到本地文件
            writeMessageToLocal(orderNumber, "我", sDate, content);

            // 插入到数据库
            ChatManager.insertNewChat(orderNumber, username, Receiver, content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMessageToLocal(String orderNumber, String sender, Timestamp sDate, String content) throws IOException {
        File file = new File("聊天记录/" + username + "/" + orderNumber + ".txt");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("发送方: " + sender + " 时间: " + sDate + "\n" + content);
            writer.newLine();
        }
    }

    public void start(Stage primaryStage, String orderNumber, GetTasks getTasks) {

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        // 创建一个列表视图用于显示聊天记录
        ListView<Label> chatArea = new ListView<>();
        chatArea.setPrefSize(380, 400); // 设置预设大小以适应界面

        // 创建一个文本区域用于输入消息
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("输入消息");
        messageArea.setPrefRowCount(4);

        String Receiver;
        if (username.equals(getTasks.getReceiver())) {
            Receiver = getTasks.getPublisher();
        } else {
            Receiver = getTasks.getReceiver();
        }

        // 创建发送按钮
        Button sendButton = new Button("发送");
        sendButton.setOnAction(e -> {
            String content = messageArea.getText();
            sendMessage(orderNumber, Receiver, content);

            // 清空输入框
            messageArea.clear();

            // 刷新聊天记录
            displayChatRecords(chatArea, orderNumber, Receiver);
        });

        // 创建一个按钮来刷新聊天记录
        Button refreshButton = new Button("刷新");
        refreshButton.setOnAction(e -> displayChatRecords(chatArea, orderNumber, Receiver));

        // 创建标题标签并设置为加粗
        Label titleLabel = new Label(orderNumber + " 订单的聊天记录");
        titleLabel.setStyle("-fx-font-weight: bold;");

        // 添加组件到根容器
        root.getChildren().addAll(
                titleLabel,
                chatArea,
                new Label("发送消息"),
                messageArea,
                sendButton,
                refreshButton
        );

        // 设置场景
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("消息发送与接收");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 初始化加载聊天记录
        displayChatRecords(chatArea, orderNumber, Receiver);
    }

    private void displayChatRecords(ListView<Label> chatArea, String orderNumber, String Receiver) {
        // 清除当前聊天记录
        chatArea.getItems().clear();

        // 从文件中读取聊天记录
        File file = new File("聊天记录/" + username + "/" + orderNumber + ".txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                String lastSender = "";  // 用于记录上一条消息的发送者

                while ((line = reader.readLine()) != null) {
                    Label label = new Label(line);

                    // 检查当前行是否包含发送方信息
                    if (line.startsWith("发送方: ")) {
                        // 提取发送方名称
                        int startIndex = line.indexOf(':') + 2;
                        int endIndex = line.indexOf(' ', startIndex);
                        lastSender = line.substring(startIndex, endIndex).trim();
                    }

                    // 根据上一条消息的发送者设置背景颜色
                    if (lastSender.equals("我")) {
                        label.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-border-radius: 10;");
                    } else if (lastSender.equals(Receiver)) {
                        label.setStyle("-fx-background-color: #ffffff; -fx-padding: 5; -fx-border-radius: 10;");
                    } else {
                        // 如果无法确定发送者，使用默认样式
                        label.setStyle("-fx-padding: 5; -fx-border-radius: 10;");
                    }

                    chatArea.getItems().add(label);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // 启动 JavaFX 应用程序
        launch(args);
    }
}