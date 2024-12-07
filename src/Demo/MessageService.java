package Demo;
import java.io.*;
import java.sql.*;
import java.util.*;

public class MessageService {
    private Connection connection;

    public MessageService(Connection connection) {
        this.connection = connection;
    }

    // 发送消息
    public void sendMessage(Message message) {
        String query = "INSERT INTO messages (sender, receiver, message, timestamp) VALUES (?, ?, ?, ?)"; // SQL查询语句，用于将消息插入到数据库
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getReceiver());
            stmt.setString(3, message.getContent());
            stmt.setLong(4, message.getTimestamp());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 将消息保存到本地文件 "chat_history.txt"
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("chat_history.txt", true))) {
            writer.write(message.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 接收消息
    public void receiveMessage() {
        // 读取本地文件中的聊天记录并打印
        try (BufferedReader reader = new BufferedReader(new FileReader("chat_history.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 从数据库中删除已过期的消息（根据时间戳）
        String query = "DELETE FROM messages WHERE timestamp < ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            long currentTime = System.currentTimeMillis();
            stmt.setLong(1, currentTime);             // 设置删除条件,时间戳小于当前时间
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查看聊天记录
    public void viewChatHistory() {
        File file = new File("chat_history.txt");
        if (file.exists() && file.length() > 0) {                  // 检查本地文件是否存在且非空
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {                                                            // 如果文件为空，则从数据库中读取消息并打印
            String query = "SELECT * FROM messages ORDER BY timestamp";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String receiver = rs.getString("receiver");
                    String content = rs.getString("message");
                    long timestamp = rs.getLong("timestamp");
                    Message message = new Message(sender, receiver, content, timestamp);
                    System.out.println(message);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 根据用户名获取消息
    public List<Message> getMessagesForUser(String username) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE sender = ? OR receiver = ? ORDER BY timestamp";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String receiver = rs.getString("receiver");
                    String content = rs.getString("message");
                    long timestamp = rs.getLong("timestamp");


                    Message message = new Message(sender, receiver, content, timestamp);
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
