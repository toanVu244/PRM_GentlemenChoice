package com.example.GentlemenChoice.api.CartItem;

import com.example.GentlemenChoice.api.APIClient;

public class CartItemRepository {
    public static CartItemService getCartItemService(){
        return APIClient.getClient().create(CartItemService.class);
    }
}
