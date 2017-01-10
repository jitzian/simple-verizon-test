package mac.training.android.com.org.materialdesignbasic.singleton;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by raian on 1/9/17.
 *
 * This Singleton Class saves the oAuth Firebase in order to be used among the app
 */

public class FirebaseOAuth {
    private static final String TAG = FirebaseOAuth.class.getSimpleName();
    private static FirebaseOAuth instance;
    private static Context context;

    //Firebase Vars
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseOAuth(Context context){
        this.context = context;
    }

    public static synchronized FirebaseOAuth getInstance(Context context){
        if(instance == null){
            synchronized (TAG){
                if(instance == null){
                    instance = new FirebaseOAuth(context);
                }
            }
        }
        return instance;

    }

    //Provides UserId
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
