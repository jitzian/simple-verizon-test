package mac.training.android.com.org.materialdesignbasic.RetrofitRest;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by raian on 1/9/17.
 */

public class RetrofitService {

    public static Retrofit getInstace(String baseURL){
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseURL)
                .build();
    }

}
