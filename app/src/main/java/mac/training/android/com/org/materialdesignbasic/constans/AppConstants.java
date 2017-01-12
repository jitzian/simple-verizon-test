package mac.training.android.com.org.materialdesignbasic.constans;

/**
 * Created by raian on 1/9/17.
 *
 * This class is only for constants
 *
 */

public class AppConstants {
    private static final int NOTIF_ID_DOWNLOAD = 0;

    //UPLOAD
    /** Intent Actions **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    /** Intent Extras **/
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    public static final String KEY_FILE_URI = "key_file_uri";
    public static final String KEY_DOWNLOAD_URL = "key_download_url";

    //DOWNLOADS
    /** Actions **/
    public static final String ACTION_DOWNLOAD = "action_download";
    public static final String DOWNLOAD_COMPLETED = "download_completed";
    public static final String DOWNLOAD_ERROR = "download_error";

    /** Extras **/
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";

    //Flicker
    public static String baseURL = "https://api.flickr.com/";
    public static final String API_KEY = "52b082213eb8821fb5a02307573b4c7d";
    public static final String method = "flickr.photos.getRecent";
    public static final String perPage = "100";
    public static final String format = "json";
    public static final String callBack = "nojsoncallback";

    //Sports
    public static String imageUrl = "http://lorempixel.com/800/600/sports/";


}
