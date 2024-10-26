package com.example.GentlemenChoice.api.Category;

import com.example.GentlemenChoice.api.APIClient;

public class CategoryRepository {
    public static CategoryService getCategoryService(){
        return APIClient.getClient().create(CategoryService.class);
    }
}
