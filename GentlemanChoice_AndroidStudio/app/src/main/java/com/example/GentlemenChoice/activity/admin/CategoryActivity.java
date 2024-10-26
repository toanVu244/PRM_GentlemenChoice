package com.example.GentlemenChoice.activity.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.GentlemenChoice.R;
import com.example.GentlemenChoice.adapters.CategoryAdapter;
import com.example.GentlemenChoice.api.Category.CategoryRepository;
import com.example.GentlemenChoice.api.Category.CategoryService;
import com.example.GentlemenChoice.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private ListView listViewCategory;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbarCategoryAdminHome);
        toolbar.setTitle("Các danh mục");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            CategoryActivity.this.finish();
        });

        listViewCategory = findViewById(R.id.listViewCategory);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        CategoryService cateService = CategoryRepository.getCategoryService();
        Call<List<Category>> call = cateService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (!response.isSuccessful()) {
                    Log.e("CategoryActivity", "Lỗi: " + response.code());
                    Toast.makeText(CategoryActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Category> categories = response.body();
                if (categories == null || categories.isEmpty()) {
                    Log.e("CategoryActivity", "Không nhận được dữ liệu");
                    Toast.makeText(CategoryActivity.this, "Không có dữ liệu", Toast.LENGTH_LONG).show();
                    return;
                }

                // Set the adapter for the ListView
                listViewCategory.setAdapter(new CategoryAdapter(categories, getApplicationContext()));

                listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Category category = (Category) parent.getItemAtPosition(position);
                        Intent intent = new Intent(CategoryActivity.this, CategoryDetailActivity.class);
                        intent.putExtra("category", category);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("CategoryActivity", "Failed to load data: " + t.getMessage(), t);
                Toast.makeText(CategoryActivity.this, "Tải dữ liệu thất bại: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}