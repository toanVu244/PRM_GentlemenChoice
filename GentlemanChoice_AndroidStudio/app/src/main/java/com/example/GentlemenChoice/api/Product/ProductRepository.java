package com.example.GentlemenChoice.api.Product;

import com.example.GentlemenChoice.api.APIClient;

public class ProductRepository {
    public static ProductService getProductService(){
        return APIClient.getClient().create(ProductService.class);
    }
}
