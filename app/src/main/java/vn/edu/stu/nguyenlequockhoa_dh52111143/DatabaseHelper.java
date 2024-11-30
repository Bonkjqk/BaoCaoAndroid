package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "EmployeeManager1.db";
    private static final int DATABASE_VERSION = 2;

    private static String DATABASE_PATH;
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        copyDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Copy file database từ thư mục assets sang thư mục databases của ứng dụng
     * nếu nó chưa tồn tại.
     */
    private void copyDatabase() {
        try {
            File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
            if (!dbFile.exists()) {
                InputStream input = context.getAssets().open(DATABASE_NAME);
                File folder = new File(DATABASE_PATH);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                OutputStream output = new FileOutputStream(dbFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                input.close();

                Log.d(TAG, "Database copied successfully.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error copying database: " + e.getMessage());
        }
    }

    /**
     * Mở database từ đường dẫn đã copy.
     */
    @Override
    public SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // Các phương thức xử lý truy vấn và cập nhật dữ liệu
    // Ví dụ: Thêm, lấy danh sách, cập nhật, và xóa phòng ban hoặc nhân viên
    public static Bitmap getBitmapFromBytes(byte[] imageBytes) {
        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return null;
    }
    // Thêm phòng ban
    public long addDepartment(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        return db.insert("department", null, values);
    }

    // Lấy danh sách phòng ban
    public Cursor getDepartments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("department", null, null, null, null, null, null);
    }

    // Xóa phòng ban
    public boolean deleteDepartment(int id) {
        if (hasEmployeesInDepartment(id)) {
            return false; // Không thể xóa nếu có nhân viên trong phòng ban
        }
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("department", "id=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0; // Trả về true nếu xóa thành công
    }

    // Thêm nhân viên
    public long addEmployee(String name, String phone, String email, int deptId, byte[] photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        values.put("email", email);
        values.put("department_id", deptId);
        values.put("photo", photo); // Thêm ảnh vào ContentValues
        return db.insert("employee", null, values);
    }

    // Lấy danh sách nhân viên
    public Cursor getEmployees() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.id, e.name, e.phone, e.email, e.department_id, e.photo " +
                "FROM employee e";
        return db.rawQuery(query, null);
    }

    // Lấy nhân viên theo ID
    public Cursor getEmployeeById(int employeeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM employee WHERE id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(employeeId)});
    }

    // Xóa nhân viên
    public boolean deleteEmployee(int employeeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("employee", "id = ?", new String[]{String.valueOf(employeeId)});
        return rowsAffected > 0; // Trả về true nếu xóa thành công
    }

    // Kiểm tra phòng ban có nhân viên
    public boolean hasEmployeesInDepartment(int departmentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM employee WHERE department_id = ?";
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
        values.put("name", newName);
        int rowsAffected = db.update("department", values, "id = ?", new String[]{String.valueOf(departmentId)});
        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }

    // Cập nhật nhân viên
    public boolean updateEmployee(int employeeId, String name, String phone, String email, int deptId, byte[] photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        values.put("email", email);
        values.put("department_id", deptId);
        values.put("photo", photo); // Cập nhật ảnh trong ContentValues

        int rowsAffected = db.update("employee", values, "id = ?", new String[]{String.valueOf(employeeId)});
        return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    }
}
