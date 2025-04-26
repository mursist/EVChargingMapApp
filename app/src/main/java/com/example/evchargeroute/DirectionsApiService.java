
// java/com/example/evchargeroute/DirectionsApiService.java
package com.example.evchargeroute;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class DirectionsApiService {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static DirectionsApi directionsApi;

    public static DirectionsApi getInstance() {
        if (directionsApi == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            directionsApi = retrofit.create(DirectionsApi.class);
        }
        return directionsApi;
    }

    public interface DirectionsApi {
        @GET("directions/json")
        Call<DirectionsResponse> getDirections(
                @Query("origin") String origin,
                @Query("destination") String destination,
                @Query("key") String apiKey
        );
    }
}
