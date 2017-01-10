package mac.training.android.com.org.materialdesignbasic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.constans.AppConstants;
import mac.training.android.com.org.materialdesignbasic.model.Photo;

/**
 * Created by raian on 1/9/17.
 */

public class GridViewAdapter extends BaseAdapter{
    private static final String TAG = GridViewAdapter.class.getSimpleName();
    private Context context;
    private List<Photo> lstRes;

    public GridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return "Item " + String.valueOf(i + 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d(TAG, "getView");

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grid_item, viewGroup, false);
        }

        String imageUrl = AppConstants.imageUrl + String.valueOf(i + 1);
        view.setTag(imageUrl);

        ImageView image = (ImageView) view.findViewById(R.id.image);

        Glide.with(context)
                .load(imageUrl)
                .into(image);

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(getItem(i).toString());

        return view;
    }
}
