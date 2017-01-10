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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.constans.AppConstants;
import mac.training.android.com.org.materialdesignbasic.image.picker.ImagePicker;
import mac.training.android.com.org.materialdesignbasic.model.User;
import mac.training.android.com.org.materialdesignbasic.services.UploadService;
import mac.training.android.com.org.materialdesignbasic.singleton.FirebaseDataBase;
import mac.training.android.com.org.materialdesignbasic.singleton.FirebaseOAuth;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadImageFragment#newInstance} factory method to
 * create an instance of this fragment.
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
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;


    public UploadImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadImageFragment newInstance(String param1, String param2) {
        UploadImageFragment fragment = new UploadImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
                uploadImage();
            }
        });

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(AppConstants.KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(AppConstants.KEY_DOWNLOAD_URL);
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
                        Uri downloadUrl = (Uri) bundle.get("extra_download_url");
                        if (downloadUrl != null) {
                            Log.d(TAG, "::downloadUrl::" + downloadUrl.getPath());
                        }

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
        mDownloadUrl = intent.getParcelableExtra(AppConstants.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(AppConstants.EXTRA_FILE_URI);

        Log.d(TAG, "onUploadResultIntent::mDownloadUrl" + mDownloadUrl.toString());

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

    private static boolean isImageSelected;
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
    final String userId = FirebaseOAuth.getInstance(getContext()).getUid();
    public void submitPost(){
        Log.d(TAG, "saveIntoDataBase");
        FirebaseDataBase.getmDatabaseReference().child("post").child(FirebaseOAuth.getInstance(getContext()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange");
                User user = dataSnapshot.getValue(User.class);

                if(user == null){
                    Log.e(TAG, "User " + userId + "null");
                    Toast.makeText(getContext(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_LONG).show();
                }else{
                    writeNewPost(userId,
                            user.username,
                            mEditTextReference.getText().toString(),
                            mEditTextObservation.getText().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
                //do something with the inputs
            }
        });
    }

    public void writeNewPost(String userId, String username, String title, String body){
        Log.d(TAG, "newPost");

        String key = FirebaseDataBase.getmDatabaseReference().child("posts").push().getKey();


    }



}
