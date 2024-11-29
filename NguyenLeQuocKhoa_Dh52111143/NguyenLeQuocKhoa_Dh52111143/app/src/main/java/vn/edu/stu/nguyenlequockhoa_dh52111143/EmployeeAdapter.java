package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import vn.edu.stu.nguyenlequockhoa_dh52111143.model.Employee;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder>{

    private ArrayList<Employee> employeeList;
    private Context context;
    private DatabaseHelper databaseHelper;

    public EmployeeAdapter(ArrayList<Employee> employeeList, Context context, DatabaseHelper databaseHelper) {
        this.employeeList = employeeList;
        this.context = context;
        this.databaseHelper = databaseHelper; // Gán giá trị đúng
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        // Đặt mã và tên nhân viên
        holder.tvId.setText("Mã: " + employee.getId());
        holder.tvName.setText("Tên: " + employee.getName());

        // Gán tên phòng ban
        int departmentId = employee.getDepartmentId();
        String departmentName = getDepartmentName(departmentId);

        if (departmentName != null) {
            holder.tvDepartment.setText("Phòng: " + departmentName);
        } else {
            holder.tvDepartment.setText("Không xác định");
        }

        // Thiết lập hình ảnh cho nhân viên (tạm thời sử dụng hình ảnh mặc định hoặc từ URI nếu có)
        byte[] photo = employee.getPhoto();
        if (photo != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            holder.imgEmployee.setImageBitmap(bitmap);
        } else {
            holder.imgEmployee.setImageResource(R.drawable.emoloyee_def); // Đặt ảnh mặc định
        }

        // Log kiểm tra
        Log.d("EmployeeAdapter", "Employee: " + employee.getName() + ", Department: " + departmentName);

        // Bắt sự kiện click để sửa thông tin nhân viên
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmployeeDetailActivity.class);
            intent.putExtra("employeeId", employee.getId()); // Truyền ID nhân viên để lấy thông tin chi tiết
            intent.putExtra("name", employee.getName());
            intent.putExtra("phone", employee.getPhone());
            intent.putExtra("email", employee.getEmail());
            intent.putExtra("departmentId", employee.getDepartmentId());
            intent.putExtra("photo", employee.getPhoto());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            boolean isDeleted = databaseHelper.deleteEmployee(employee.getId());
            if (isDeleted) {
                employeeList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Đã xóa: " + employee.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Lỗi: Không thể xóa nhân viên", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDepartmentName(int departmentId) {
        Cursor cursor = databaseHelper.getDepartments();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                if (id == departmentId) {
                    cursor.close();
                    return name;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return "Không xác định";
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvDepartment;
        Button btnDelete;
        ImageView imgEmployee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvName = itemView.findViewById(R.id.tvName);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgEmployee = itemView.findViewById(R.id.imgEmployee);
        }
    }
}