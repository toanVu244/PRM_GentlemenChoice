package com.example.GentlemenChoice.api.Customer;

import com.example.GentlemenChoice.api.APIClient;
import com.example.GentlemenChoice.api.ApiResponse;
import com.example.GentlemenChoice.model.Customer;

import retrofit2.Call;


public class CustomerRepository {
    public static CustomerService getCustomerService() {
        return APIClient.getClient().create(CustomerService.class);
    }

    public static Call<ApiResponse> updateCustomerInfo(int customerId, Customer customer) {
        return getCustomerService().updateCustomerInfo(customerId, customer);
    }
}
