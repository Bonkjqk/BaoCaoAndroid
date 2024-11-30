package vn.edu.stu.nguyenlequockhoa_dh52111143;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Bắt sự kiện click để gọi điện thoại
        TextView tvPhone = findViewById(R.id.tvPhone);
        tvPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tvPhone.getText().toString().replace("Số điện thoại: ", "")));
            startActivity(intent);
        });

        // Khởi tạo bản đồ
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Thêm marker tại vị trí Trường Đại học Công nghệ Sài Gòn
        LatLng sguLocation = new LatLng(10.762622, 106.682492); // Toạ độ của ĐH Công nghệ Sài Gòn
        mMap.addMarker(new MarkerOptions().position(sguLocation).title("Trường ĐH Công nghệ Sài Gòn"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sguLocation, 15));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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

        }

        else if (itemId == R.id.menu_about) {
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
}