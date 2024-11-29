package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EmployeeManager.db";
    private static final int DATABASE_VERSION = 2;

    // Bảng phòng ban
    private static final String TABLE_DEPARTMENT = "department";
    private static final String COLUMN_DEPT_ID = "id";
    private static final String COLUMN_DEPT_NAME = "name";

    // Bảng nhân viên
    private static final String TABLE_EMPLOYEE = "employee";
    private static final String COLUMN_EMP_ID = "id";
    private static final String COLUMN_EMP_NAME = "name";
    private static final String COLUMN_EMP_PHONE = "phone";
    private static final String COLUMN_EMP_EMAIL = "email";
    private static final String COLUMN_EMP_DEPT_ID = "department_id";
    private static final String COLUMN_EMP_PHOTO = "photo";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static Bitmap getBitmapFromBytes(byte[] imageBytes) {
        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DEPARTMENT + " (" +
                COLUMN_DEPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DEPT_NAME + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_EMPLOYEE + " (" +
                COLUMN_EMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMP_NAME + " TEXT NOT NULL, " +
                COLUMN_EMP_PHONE + " TEXT, " +
                COLUMN_EMP_EMAIL + " TEXT, " +
                COLUMN_EMP_DEPT_ID + " INTEGER, " +
                COLUMN_EMP_PHOTO + " BLOB, " +
                "FOREIGN KEY(" + COLUMN_EMP_DEPT_ID + ") REFERENCES " + TABLE_DEPARTMENT + "(" + COLUMN_DEPT_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPARTMENT);
        onCreate(db);
    }

    // Thêm phòng ban
    public long addDepartment(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEPT_NAME, name);
        return db.insert(TABLE_DEPARTMENT, null, values);
    }

    // Lấy danh sách phòng ban
    public Cursor getDepartments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DEPARTMENT, null, null, null, null, null, null);
    }

    // Xóa phòng ban
    public boolean deleteDepartment(int id) {
        if (hasEmployeesInDepartment(id)) {
            return false; // Không thể xóa nếu có nhân viên trong phòng ban
        }
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_DEPARTMENT, COLUMN_DEPT_ID + "=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0; // Trả về true nếu xóa thành công
    }

    // Thêm nhân viên
    public long addEmployee(String name, String phone, String email, int deptId, byte[] photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMP_NAME, name);
        values.put(COLUMN_EMP_PHONE, phone);
        values.put(COLUMN_EMP_EMAIL, email);
        values.put(COLUMN_EMP_DEPT_ID, deptId);
        values.put(COLUMN_EMP_PHOTO, photo); // Thêm ảnh vào ContentValues
        return db.insert(TABLE_EMPLOYEE, null, values);
    }

    // Lấy danh sách nhân viên
    public Cursor getEmployees() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.id, e.name, e.phone, e.email, e.department_id, e.photo " +
                "FROM " + TABLE_EMPLOYEE + " e";
        return db.rawQuery(query, null);
    }

    // Lấy nhân viên theo ID
    public Cursor getEmployeeById(int employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EMPLOYEE + " WHERE " + COLUMN_EMP_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(employeeId)});
    }

    // Xóa nhân viên
    public boolean deleteEmployee(int employeeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_EMPLOYEE, COLUMN_EMP_ID + " = ?", new String[]{String.valueOf(employeeId)});
        return rowsAffected > 0; // Trả về true nếu xóa thành công
    }

    // Kiểm tra phòng ban có nhân viên
    public boolean hasEmployeesInDepartment(int departmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_EMPLOYEE + " WHERE " + COLUMN_EMP_DEPT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(departmentId)});

        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0; // Trả về true nếu có nhân viên
        }
        return false; // Trả về false nếu không có nhân viên
    }

    // Cập nhật phòng ban
    public boolean updateDepartment(int departmentId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEPT_NAME, newName);
        int rowsAffected = db.update(TABLE_DEPARTMENT, values, COLUMN_DEPT_ID + " = ?", new String[]{String.valueOf(departmentId)});
        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }

    // Cập nhật nhân viên
    public boolean updateEmployee(int employeeId, String name, String phone, String email, int deptId, byte[] photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMP_NAME, name);
        values.put(COLUMN_EMP_PHONE, phone);
        values.put(COLUMN_EMP_EMAIL, email);
        values.put(COLUMN_EMP_DEPT_ID, deptId);
        values.put(COLUMN_EMP_PHOTO, photo); // Cập nhật ảnh trong ContentValues

        int rowsAffected = db.update(TABLE_EMPLOYEE, values, COLUMN_EMP_ID + " = ?", new String[]{String.valueOf(employeeId)});
        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }
}
