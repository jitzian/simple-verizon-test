package mac.training.android.com.org.materialdesignbasic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.model.Photo;
import mac.training.android.com.org.materialdesignbasic.model.Result;

/**
 * Created by raian on 1/9/17.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder>{
    private static final String TAG = RVAdapter.class.getSimpleName();
    Context context;
    List<Photo>lstRes;
    private List<Photo> itemsPendingRemoval = new ArrayList<>();

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    int lastInsertedIndex; // so we can add some more items for testing purposes
    boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Photo, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    public RVAdapter(Context context, List<Photo>lstRes) {
        this.context = context;
        this.lstRes = lstRes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_row_swipe, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Photo item = lstRes.get(position);
        holder.mTextViewTitle.setText(String.valueOf(lstRes.get(position).getOwner()));

        Glide.with(context)
                .load("http://www.freemagebank.com/wp-content/uploads/edd/2015/10/GL0000415LR.jpg")
                .centerCrop()
                .fitCenter()
                .into(holder.mImageViewThumbnail);

        if(itemsPendingRemoval.contains(lstRes.get(position))){
            // we need to show the "undo" state of the row
            holder.itemView.setBackgroundColor(Color.RED);
            holder.mTextViewTitle.setVisibility(View.GONE);
            holder.undo_button.setVisibility(View.VISIBLE);
            holder.undo_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(lstRes.indexOf(item));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return lstRes.size() > 0 ? lstRes.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.mImageViewThumbnail)
        ImageView mImageViewThumbnail;

        @BindView(R.id.mTextViewTitle)
        TextView mTextViewTitle;

        @BindView(R.id.undo_button)
        Button undo_button;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /***
     * This methods are for implement swiping funcionality in the Recyclerview
     *
     */
    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        Log.d(TAG, "pendingRemoval");
        final Photo item = lstRes.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(lstRes.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        Log.d(TAG, "remove");
        Photo item = lstRes.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (lstRes.contains(item)) {
            lstRes.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        Log.d(TAG, "isPendingRemoval");
        Photo item = lstRes.get(position);
        return itemsPendingRemoval.contains(item);
    }


}
