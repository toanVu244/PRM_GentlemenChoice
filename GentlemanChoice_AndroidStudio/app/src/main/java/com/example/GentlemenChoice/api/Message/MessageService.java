package com.example.GentlemenChoice.api.Message;

import com.example.GentlemenChoice.model.ChatHistoryResponse;
import com.example.GentlemenChoice.model.MessageDtoRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageService {
    @POST("Message")
    Call<Void> sendMessage(@Body MessageDtoRequest messageDtoRequest);

    @POST("Message/admin")
    Call<Void> sendMessageAdmin(@Body MessageDtoRequest messageDtoRequest);

    @GET("Message/history/{CustomerId}")
    Call<ChatHistoryResponse> getChatHistoryByCustomerId(@Path("CustomerId") int customerId);
}
