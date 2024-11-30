package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EmployeeDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etPhone, etEmail;
    private Spinner spnDepartments;
    private Button btnSave, btnChooseImage;
    private ImageView imgEmployee;
    private DatabaseHelper databaseHelper;
    private int employeeId = -1;
    private ArrayList<Integer> departmentIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        // Ánh xạ các view
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        spnDepartments = findViewById(R.id.spnDepartments);
        btnSave = findViewById(R.id.btnSave);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        imgEmployee = findViewById(R.id.imgEmployee);
        databaseHelper = new DatabaseHelper(this);

        departmentIds = new ArrayList<>();
        loadDepartments();

        // Kiểm tra nếu có nhận ID nhân viên từ Intent để biết là đang sửa hay thêm mới
        Intent intent = getIntent();
        if (intent.hasExtra("employeeId")) {
            employeeId = intent.getIntExtra("employeeId", -1);
            loadEmployeeDetails(employeeId);
        }

        btnSave.setOnClickListener(v -> saveEmployee());

        btnChooseImage.setOnClickListener(v -> {
            Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentImage, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imgEmployee.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_employee_list) {
            Intent intentList = new Intent(this, EmployeeListActivity.class);
            startActivity(intentList);
            return true;

        }  else if (itemId == R.id.menu_about) {
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

    private void loadDepartments() {
        Cursor cursor = databaseHelper.getDepartments();
        ArrayList<String> departmentNames = new ArrayList<>();
        departmentIds.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                departmentNames.add(name);
                departmentIds.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDepartments.setAdapter(adapter);

        if (departmentNames.isEmpty()) {
            Toast.makeText(this, "Không có phòng ban nào. Vui lòng thêm phòng ban trước!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadEmployeeDetails(int employeeId) {
        Cursor cursor = databaseHelper.getEmployeeById(employeeId);
        if (cursor != null && cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndex("name")));
            etPhone.setText(cursor.getString(cursor.getColumnIndex("phone")));
            etEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
            int departmentId = cursor.getInt(cursor.getColumnIndex("department_id"));

            byte[] photo = cursor.getBlob(cursor.getColumnIndex("photo"));
            if (photo != null) {
                imgEmployee.setImageBitmap(DatabaseHelper.getBitmapFromBytes(photo));
            }

            cursor.close();

            int position = departmentIds.indexOf(departmentId);
            if (position >= 0) {
                spnDepartments.setSelection(position);
            }
        }
    }

    private void saveEmployee() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        int selectedDepartmentIndex = spnDepartments.getSelectedItemPosition();

        if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && selectedDepartmentIndex != -1) {
            int departmentId = departmentIds.get(selectedDepartmentIndex);

            byte[] photo = null;
            if (imgEmployee.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) imgEmployee.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                photo = stream.toByteArray();
            }

            if (employeeId == -1) {
                // Thêm mới nhân viên
                databaseHelper.addEmployee(name, phone, email, departmentId, photo);
                Toast.makeText(this, "Nhân viên đã được thêm", Toast.LENGTH_SHORT).show();
            } else {
                // Cập nhật nhân viên
                databaseHelper.updateEmployee(employeeId, name, phone, email, departmentId, photo);
                Toast.makeText(this, "Nhân viên đã được cập nhật", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        }
    }
}