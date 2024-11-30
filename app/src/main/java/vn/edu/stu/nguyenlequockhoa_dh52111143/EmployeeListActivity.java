package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.stu.nguyenlequockhoa_dh52111143.model.Employee;

public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddEmployee;
    private DatabaseHelper databaseHelper;
    private EmployeeAdapter adapter;
    private ArrayList<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        // Ánh xạ các view
        recyclerView = findViewById(R.id.recyclerView);
        btnAddEmployee = findViewById(R.id.btnAddEmployee);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);
        employeeList = new ArrayList<>();

        // Tải dữ liệu nhân viên từ cơ sở dữ liệu
        loadEmployees();

        // Xử lý sự kiện khi nhấn nút "Thêm Nhân Viên"
        btnAddEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); // 'main_menu' là tệp XML chứa định nghĩa menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_employee_list) {
            // Hiển thị danh sách nhân viên
            Intent intentList = new Intent(this, EmployeeListActivity.class);
            startActivity(intentList);
            return true;

        } else if (itemId == R.id.menu_about) {
            // Chuyển tới trang giới thiệu (About)
            Intent intentAbout = new Intent(this, AboutActivity.class);
            startActivity(intentAbout);
            return true;

        }else if (itemId == R.id.menu_main){
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void loadEmployees() {
        employeeList.clear();

        // Lấy dữ liệu nhân viên từ cơ sở dữ liệu
        Cursor cursor = databaseHelper.getEmployees();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                int departmentId = cursor.getInt(cursor.getColumnIndex("department_id"));
                byte[] photo = cursor.getBlob(cursor.getColumnIndex("photo"));

                Employee employee = new Employee(id, name, phone, email, departmentId,photo);
                employeeList.add(employee);

                Log.d("EmployeeList", "Employee: " + name + ", Department ID: " + departmentId);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "Không có nhân viên nào để hiển thị", Toast.LENGTH_SHORT).show();
            Log.e("EmployeeListActivity", "No employees found");
        }

        // Gắn adapter cho RecyclerView
        adapter = new EmployeeAdapter(employeeList, this, databaseHelper); // Truyền DatabaseHelper vào Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách khi quay lại màn hình
        loadEmployees();
    }
}