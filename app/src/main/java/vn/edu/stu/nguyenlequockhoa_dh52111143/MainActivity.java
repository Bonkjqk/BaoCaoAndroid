package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.Intent;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.stu.nguyenlequockhoa_dh52111143.model.Department;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddDepartment, btnViewEmployees;
    private DatabaseHelper databaseHelper;
    private DepartmentAdapter adapter;
    private ArrayList<Department> departmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAddDepartment = findViewById(R.id.btnAddDepartment);
        btnViewEmployees = findViewById(R.id.btnViewEmployees);
        databaseHelper = new DatabaseHelper(this);

        departmentList = new ArrayList<>();
        loadDepartments();

        btnAddDepartment.setOnClickListener(v -> showAddDepartmentDialog());
        btnViewEmployees.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // Xử lý các sự kiện của menu khi được chọn
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

        } else if (itemId == R.id.menu_main){
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void loadDepartments() {
        departmentList.clear();
        Cursor cursor = databaseHelper.getDepartments();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                departmentList.add(new Department(id, name));
            } while (cursor.moveToNext());
        }
        adapter = new DepartmentAdapter(departmentList, this, databaseHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void showAddDepartmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Phòng Ban");

        final EditText input = new EditText(this);
        input.setHint("Tên phòng ban");
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                databaseHelper.addDepartment(name);
                loadDepartments();
                Toast.makeText(this, "Phòng ban đã được thêm", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tên phòng ban không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}