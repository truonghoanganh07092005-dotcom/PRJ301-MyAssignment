package model;

public class Department extends BaseModel {
    private String name;   // tên phòng ban
    private String code;   // nếu bạn có mã phòng ban (tuỳ)

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
