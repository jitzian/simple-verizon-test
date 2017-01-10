package mac.training.android.com.org.materialdesignbasic.RetrofitRest;

import mac.training.android.com.org.materialdesignbasic.model.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by raian on 1/9/17.
 */

public interface RestService {
    //This is my BaseURL
    //https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=ca370d51a054836007519a00ff4ce59e&per_page=10&format=json&nojsoncallback=1

    //https://api.flickr.com/services/rest/?method=
    // flickr.photos.getRecent
    // &api_key=ca370d51a054836007519a00ff4ce59e
    // &per_page=10
    // &format=json
    // &nojsoncallback=1
    @GET("/services/rest/?method=flickr.photos.getRecent")
    Observable<Result>getPhotos(@Query("method") String method,
                                @Query("api_key") String apiKey,
                                @Query("per_page")String perPage,
                                @Query("format") String format,
                                @Query("nojsoncallback") String callback);

    @GET("/services/rest/?method=flickr.photos.getRecent")
    Observable<Result> getRecentPhotos(@Query("per_page") int perPage, @Query("page") int page);


}
