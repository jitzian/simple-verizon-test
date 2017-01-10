package mac.training.android.com.org.materialdesignbasic.singleton;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by raian on 1/9/17.
 */

public class FirebaseDataBase {
    private static final String TAG = FirebaseDataBase.class.getSimpleName();
    private static FirebaseDataBase instance = null;
    private static Context context;
    public static DatabaseReference mDatabaseReference;

    public FirebaseDataBase(Context context){
        this.context = context;
    }

    public static synchronized FirebaseDataBase getInstance(Context context){
        if(instance == null){
            synchronized (TAG){
                if(instance == null){
                    instance = new FirebaseDataBase(context);
                }
            }
        }
        return instance;
    }

    public static DatabaseReference getmDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
