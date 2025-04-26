
// java/com/example/evchargeroute/OpenChargeMapService.java
package com.example.evchargeroute;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class OpenChargeMapService {
    private static final String BASE_URL = "https://api.openchargemap.io/v3/";
    private static OpenChargeMapApi openChargeMapApi;

    public static OpenChargeMapApi getInstance() {
        if (openChargeMapApi == null) {
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

            openChargeMapApi = retrofit.create(OpenChargeMapApi.class);
        }
        return openChargeMapApi;
    }

    public interface OpenChargeMapApi {
        @GET("poi")
        Call<List<ChargingStation>> getChargingStations(
                @Query("latitude") double latitude,
                @Query("longitude") double longitude,
                @Query("distance") int distance,
                @Query("key") String apiKey
        );
        
        @GET("poi")
        Call<List<ChargingStation>> getChargingStations(
                @Query("boundingbox") String boundingBox,
                @Query("key") String apiKey
        );
        
        // İki nokta arasındaki şarj istasyonlarını getir (bounding box ile)
        default Call<List<ChargingStation>> getChargingStations(
                double minLat, double minLng, double maxLat, double maxLng, String apiKey) {
            String boundingBox = minLat + "," + minLng + "," + maxLat + "," + maxLng;
            return getChargingStations(boundingBox, apiKey);
        }
    }
}
