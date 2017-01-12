package mac.training.android.com.org.materialdesignbasic.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.constans.AppConstants;
import mac.training.android.com.org.materialdesignbasic.image.picker.ImagePicker;
import mac.training.android.com.org.materialdesignbasic.model.Post;
import mac.training.android.com.org.materialdesignbasic.model.User;
import mac.training.android.com.org.materialdesignbasic.services.UploadService;
import mac.training.android.com.org.materialdesignbasic.singleton.FirebaseOAuth;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This fragment is not finished yet. But the purpose of it is to upload imaged into FirebaseStorage
 * What is completed is the upload of the image into Firebase.
 *
 * The next step is to get the URL and display the image into the PhotoFragment
 *
 *
 */
public class UploadImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = UploadImageFragment.class.getSimpleName();
    private static final int GALLERY_INTENT = 2;
    private View rootView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //Start FloatingMenuVars
    @BindView(R.id.menu_red)
    FloatingActionMenu menuRed;

    @BindView(R.id.fabLoadImage)
    FloatingActionButton fabLoadImage;

    @BindView(R.id.fabUploadImage)
    FloatingActionButton fabUploadImage;

    @BindView(R.id.mImageViewLoad)
    ImageView mImageViewLoad;

    @BindView(R.id.mEditTextReference)
    EditText mEditTextReference;

    @BindView(R.id.mEditTextObservation)
    EditText mEditTextObservation;

    private BroadcastReceiver mBroadcastReceiver;

    //Uri's
    private Uri downloadUrl = null;
    private Uri mFileUri = null;

    private DatabaseReference mDatabase;



    public UploadImageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    public boolean validateInputs(){
        Log.d(TAG, "validateInputs");
        if((mEditTextReference.getText().toString().length() > 0)
                && (mEditTextObservation.getText().toString().length() > 0)
                && isImageSelected) {
            Log.d(TAG, "-" + mEditTextReference.getText());
            Log.d(TAG, "-" + mEditTextObservation.getText());
            Log.d(TAG, "-" + mImageViewLoad.getDrawable() + " - " + mImageViewLoad.getHeight() + " - " + mImageViewLoad.getWidth());
            Log.d(TAG, "-" + isImageSelected);
            Log.d(TAG, "COMPLETE");
            return true;
        }
        else{
            Log.d(TAG, "INCOMPLETE");
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_upload_image, container, false);

        //Bind controls from the view
        ButterKnife.bind(this, rootView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fabLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Select Image");
                loadImgFromPhoneGallery(view);
            }
        });

        fabUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "uploadImage");
                if(validateInputs()){
                    uploadImage();
//                    submitPost();
                }else{
                    Toast.makeText(getContext(), "Please provide the necessary inputs in order to save the record..", Toast.LENGTH_LONG).show();
                }

//                uploadImage();
            }
        });

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(AppConstants.KEY_FILE_URI);
            downloadUrl = savedInstanceState.getParcelable(AppConstants.KEY_DOWNLOAD_URL);
        }

        onNewIntent(getActivity().getIntent());

        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, " -- onReceive:" + intent.getAction());
                hideProgressDialog();

                switch (intent.getAction()) {
                    case AppConstants.DOWNLOAD_COMPLETED:
                        // Get number of bytes downloaded
                        long numBytes = intent.getLongExtra(AppConstants.EXTRA_BYTES_DOWNLOADED, 0);

                        // Alert success
                        showMessageDialog(getString(R.string.success), String.format(Locale.getDefault(),
                                "%d bytes downloaded from %s",
                                numBytes,
                                intent.getStringExtra(AppConstants.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case AppConstants.DOWNLOAD_ERROR:
                        // Alert failure
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                                "Failed to download from %s",
                                intent.getStringExtra(AppConstants.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case AppConstants.UPLOAD_COMPLETED:
                        Log.d(TAG, "Upload was OK:");

                        Bundle bundle = intent.getExtras();
                        downloadUrl = (Uri) bundle.get(AppConstants.EXTRA_DOWNLOAD_URL);
                        if (downloadUrl != null) {
                            Log.d(TAG, "::downloadUrl::" + downloadUrl);
                        }
                        submitPost();

                        break;
                    case AppConstants.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpRegisterReceivers();
    }

    public void setUpRegisterReceivers() {
        Log.d(TAG, "setUpRegisterReceivers");

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        manager.registerReceiver(mBroadcastReceiver, DownloadService.getIntentFilter());
        manager.registerReceiver(mBroadcastReceiver, UploadService.getIntentFilter());

    }

    public void onNewIntent(Intent intent) {
        // Check if this Activity was launched by clicking on an upload notification
        if (intent.hasExtra(AppConstants.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }

    }

    private void onUploadResultIntent(Intent intent) {
        Log.d(TAG, "onUploadResultIntent");
        // Got a new intent from MyUploadService with a success or failure
        downloadUrl = intent.getParcelableExtra(AppConstants.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(AppConstants.EXTRA_FILE_URI);

        Log.d(TAG, "onUploadResultIntent::mDownloadUrl" + downloadUrl.toString());

        updateUI(FirebaseOAuth.mAuth.getCurrentUser());
    }

    private void updateUI(FirebaseUser user) {
        // Signed in or Signed out
//        if (user != null) {
//            findViewById(R.id.layout_signin).setVisibility(View.GONE);
//            findViewById(R.id.layout_storage).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.layout_signin).setVisibility(View.VISIBLE);
//            findViewById(R.id.layout_storage).setVisibility(View.GONE);
//        }
//
//        // Download URL and Download button
//        if (mDownloadUrl != null) {
//            ((TextView) findViewById(R.id.picture_download_uri))
//                    .setText(mDownloadUrl.toString());
//            findViewById(R.id.layout_download).setVisibility(View.VISIBLE);
//        } else {
//            ((TextView) findViewById(R.id.picture_download_uri))
//                    .setText(null);
//            findViewById(R.id.layout_download).setVisibility(View.GONE);
//        }
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }


    private void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        //Unregister receiver
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static boolean isImageSelected = false;
    private static Uri uri;

    public void loadImgFromPhoneGallery(View view) {
        Log.d(TAG, "loadImgFromPhoneGalley");
        ImagePicker.pickImage(this, "Select your image:");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult::" + "requestCode::" + requestCode + " || " + "resultCode::" + resultCode + " || " + "data::" + data.getType());
        Bitmap bitmap;

        bitmap = ImagePicker.getImageFromResult(getContext(), requestCode, resultCode, data);
        uri = data.getData();
        Log.d(TAG, " - " + uri);
        mImageViewLoad.setImageBitmap(bitmap);
        isImageSelected = true;

    }

    //    private StorageReference mStorageReference;
    public void uploadImage() {
        Log.d(TAG, "uploadImage");
        // Toast message in case the user does not see the notificatio
        Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        getActivity()
                .startService(new Intent(getContext(), UploadService.class)
                        .putExtra(AppConstants.EXTRA_FILE_URI, uri)
                        .setAction(AppConstants.ACTION_UPLOAD));

    }

    //This method saves into de DB
    public void submitPost(){
        Log.d(TAG, "saveIntoDataBase");
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "saveIntoDataBase::userId::" + userId);

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange::dataSnapshot" + dataSnapshot);
//                User user = dataSnapshot.getValue(User.class);

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                User user = new User(firebaseUser.getUid(), firebaseUser.getEmail());
                Log.d(TAG, "::onDataChange::dataSnapshot::" + dataSnapshot + ":user:" + user);

                Log.d(TAG, "::firebaseUser::" + firebaseUser);
                if(firebaseUser == null){
                    Log.e(TAG, "User " + userId + " is null");
                    Toast.makeText(getContext(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_LONG).show();
                }else{
                    Log.d(TAG, "Lets go to save the post");
                    Log.d(TAG, "userId::" + userId);
                    Log.d(TAG, "user.username::" + user.username);
                    Log.d(TAG, "mEditTextReference.getText().toString()::" + mEditTextReference.getText().toString());
                    Log.d(TAG, "mEditTextObservation.getText().toString()::" + mEditTextObservation.getText().toString());
                    Log.d(TAG, "mDownloadUrl.toString()::" + downloadUrl);
                    writeNewPost(userId,
                            user.username,
                            mEditTextReference.getText().toString(),
                            mEditTextObservation.getText().toString(),
                            downloadUrl.toString()
                    );

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                //do something with the inputs
            }
        });
    }

    public void writeNewPost(String userId, String username, String reference, String observations, String imgURL){
        Log.d(TAG, "newPost");

        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, reference, observations, imgURL);
        Map<String, Object>postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);

    }



}
