package Demo;

public class GetUsers {
    private String username;
    private String phonenumber;
    private String nickname;
    private String gender;
    private String password;
    private String registerTime;
    private boolean isAdmin;
    private String source;  // 新增字段：来源信息

    // 构造方法
    public GetUsers(String username, String phonenumber, String nickname, String gender,
                    String password, boolean isAdmin, String source, String registerTime) {
        this.username = username;
        this.gender = gender;
        this.phonenumber = phonenumber;
        this.password = password;
        this.nickname = nickname;
        this.registerTime = registerTime;
        this.isAdmin = isAdmin;
        this.source = source;  // 初始化来源
    }

    // Getter 和 Setter 方法
    public String getSource() {
        return source;
    }

    public String getUsername() {
        return username;
    }

    public String getGender() {
        return gender;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

}
