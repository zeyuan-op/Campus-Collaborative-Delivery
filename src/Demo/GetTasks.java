package Demo;

import java.sql.Timestamp;

public class GetTasks {
    private String orderNumber;       // 订单号
    private String publisherName;     // 发布者名称
    private String gender;
    private String building;
    private String room;
    private String phoneNumber;
    private Float amount;
    private Timestamp releaseDate;
    private String publisher;         // 发布者用户名
    private String state;
    private Timestamp Receive_Date;
    private String receiver;          // 接收者用户名
    private String receiverName;      // 接收者名称
    private Timestamp Completion_Date;

    // 构造方法
    public GetTasks(String orderNumber, String publisherName, String gender, String building, String room,
                    String phoneNumber, Float amount, Timestamp releaseDate, String publisher,String state,
                    Timestamp Receive_Date, String receiver, String receiverName, Timestamp Completion_Date) {
        this.orderNumber = orderNumber;
        this.publisherName = publisherName;
        this.gender = gender;
        this.building = building;
        this.room = room;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.releaseDate = releaseDate;
        this.publisher = publisher;
        this.state = state;
        this.Receive_Date = Receive_Date;
        this.receiver = receiver;
        this.receiverName = receiverName;
        this.Completion_Date = Completion_Date;
    }

    // Getter 方法
    public String getOrderNumber() {
        return orderNumber;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getGender() {
        return gender;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoom() {
        return room;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Float getAmount() {
        return amount;
    }

    public Timestamp getReceive_Date() {
        return Receive_Date;
    }

    public Timestamp getCompletion_Date() {
        return Completion_Date;
    }

    public String getPublisher() {
        return publisher;
    }
    public String getState() {
        return state;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getReceiverName() {
        return receiverName;
    }
}
