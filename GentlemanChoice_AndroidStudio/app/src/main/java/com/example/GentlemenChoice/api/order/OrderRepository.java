package com.example.GentlemenChoice.api.order;

import com.example.GentlemenChoice.api.APIClient;

public class OrderRepository {
    public static OrderService getOrderService(){
        return APIClient.getClient().create(OrderService.class);
    }


}
