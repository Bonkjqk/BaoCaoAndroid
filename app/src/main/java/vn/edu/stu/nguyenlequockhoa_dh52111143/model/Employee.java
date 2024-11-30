package vn.edu.stu.nguyenlequockhoa_dh52111143.model;

public class Employee {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int departmentId;
    private byte[] photo; // Thêm trường để lưu trữ ảnh

    public Employee(int id, String name, String phone, String email, int departmentId, byte[] photo) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.departmentId = departmentId;
        this.photo = photo;
    }

    // Getter và Setter cho các trường
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    // Getter và Setter cho trường ảnh
    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
