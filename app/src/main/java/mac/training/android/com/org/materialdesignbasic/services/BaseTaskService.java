package mac.training.android.com.org.materialdesignbasic.services;

import android.app.Service;
import android.util.Log;

/**
 * Created by raian on 1/9/17.

 * Base class for Services that keep track of the number of active jobs and self-stop when the
 * count is zero.
 */

public abstract class BaseTaskService extends Service {
    private static final String TAG = BaseTaskService.class.getSimpleName();
    private int mNumTasks = 0;

    public void taskStarted() {
        Log.d(TAG, "taskStarted");
        changeNumberOfTasks(1);
    }

    public void taskCompleted() {
        Log.d(TAG, "taskCompleted");
        changeNumberOfTasks(-1);
    }

    private synchronized void changeNumberOfTasks(int delta) {
        Log.d(TAG, "changeNumberOfTasks:" + mNumTasks + ":" + delta);
        mNumTasks += delta;

        // If there are no tasks left, stop the service
        if (mNumTasks <= 0) {
            Log.d(TAG, "stopping");
            stopSelf();
        }
    }

}
