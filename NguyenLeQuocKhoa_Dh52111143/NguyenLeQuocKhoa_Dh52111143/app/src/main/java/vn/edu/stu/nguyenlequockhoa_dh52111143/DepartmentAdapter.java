package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.stu.nguyenlequockhoa_dh52111143.model.Department;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {

    private ArrayList<Department> departmentList;
    private Context context;
    private DatabaseHelper databaseHelper;

    public DepartmentAdapter(ArrayList<Department> departmentList, Context context, DatabaseHelper databaseHelper) {
        this.departmentList = departmentList;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_department, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Department department = departmentList.get(position);
        holder.tvName.setText(department.getName());

        // Sửa phòng ban
        holder.btnEdit.setOnClickListener(v -> showEditDialog(department, position));

        // Xóa phòng ban
        holder.btnDelete.setOnClickListener(v -> {
            // Kiểm tra nếu phòng ban có nhân viên
            if (databaseHelper.hasEmployeesInDepartment(department.getId())) {
                Toast.makeText(context, "Không thể xóa phòng ban vì có nhân viên liên kết!", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xác nhận");
            builder.setMessage("Bạn có chắc chắn muốn xóa phòng ban này?");
            builder.setPositiveButton("Xóa", (dialog, which) -> {
                databaseHelper.deleteDepartment(department.getId());
                departmentList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Phòng ban đã được xóa", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    private void showEditDialog(Department department, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa Phòng Ban");

        final EditText input = new EditText(context);
        input.setText(department.getName());
        builder.setView(input);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                boolean isUpdated = databaseHelper.updateDepartment(department.getId(), newName);
                if (isUpdated) {
                    department.setName(newName); // Cập nhật trong danh sách
                    notifyItemChanged(position);
                    Toast.makeText(context, "Phòng ban đã được cập nhật", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }


}