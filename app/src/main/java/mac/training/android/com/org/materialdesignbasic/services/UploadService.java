package mac.training.android.com.org.materialdesignbasic.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.activity.MainActivity;
import mac.training.android.com.org.materialdesignbasic.constans.AppConstants;

/**
 * Created by raian on 1/9/17.
 */

public class UploadService extends BaseTaskService{
    private static final String TAG = UploadService.class.getSimpleName();
    private static final int NOTIF_ID_DOWNLOAD = 0;

    private StorageReference mStorageRef;

    @Override
    public void onCreate() {
        super.onCreate();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);
        if (AppConstants.ACTION_UPLOAD.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(AppConstants.EXTRA_FILE_URI);
            uploadFromUri(fileUri);
        }
        return START_REDELIVER_INTENT;
    }

    // [START upload_from_uri]
    private void uploadFromUri(final Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // [START_EXCLUDE]
        taskStarted();
        showUploadProgressNotification();
        // [END_EXCLUDE]

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRef.child("photos")
                .child(fileUri.getLastPathSegment());
        // [END get_child_ref]

        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get the public download URL
                        Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

                        broadcastUploadFinished(downloadUri, fileUri);
                        showUploadFinishedNotification(downloadUri, fileUri);
                        taskCompleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        broadcastUploadFinished(null, fileUri);
                        showUploadFinishedNotification(null, fileUri);
                        taskCompleted();
                    }
                });
    }

    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;
        Log.d(TAG, "broadcastUploadFinished");

        String action = success ? AppConstants.UPLOAD_COMPLETED : AppConstants.UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(AppConstants.EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(AppConstants.EXTRA_FILE_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        Log.d(TAG, "showUploadFinishedNotification");
        // Make Intent to MainActivity
        Intent intent = new Intent(this, MainActivity.class)
                .putExtra(AppConstants.EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(AppConstants.EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Make PendingIntent for notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set message and icon based on success or failure
        boolean success = downloadUrl != null;
        String message = success ? "Upload finished" : "Upload failed";
        int icon = success ? R.drawable.ic_check_white_24 : R.drawable.ic_error_white_24dp;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIF_ID_DOWNLOAD, builder.build());

        Toast.makeText(this, "Upload Finished..", Toast.LENGTH_LONG).show();

    }

    /**
     * Show notification with an indeterminate upload progress bar.
     */
    private void showUploadProgressNotification() {
        Log.d(TAG, "showUploadProgressNotification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_file_upload_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Uploading...")
                .setProgress(0, 0, true)
                .setOngoing(true)
                .setAutoCancel(false);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIF_ID_DOWNLOAD, builder.build());
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.UPLOAD_COMPLETED);
        filter.addAction(AppConstants.UPLOAD_ERROR);

        return filter;
    }
}
