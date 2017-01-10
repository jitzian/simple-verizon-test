package mac.training.android.com.org.materialdesignbasic.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by raian on 1/9/17.
 */

@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String reference;
    public String observations;

    public Map<String, Boolean>stars = new HashMap<>();

    public Post(String uid, String author, String reference, String observations) {
        this.uid = uid;
        this.author = author;
        this.reference = reference;
        this.observations = observations;
    }

    @Exclude
    public Map<String, Object>toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("reference", reference);
        result.put("observations", observations);

        return result;
    }

}
