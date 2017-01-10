package mac.training.android.com.org.materialdesignbasic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import mac.training.android.com.org.materialdesignbasic.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailGridPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailGridPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailGridPhotoFragment extends Fragment {
    private static final String TAG = DetailGridPhotoFragment.class.getSimpleName();
    private View rootView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_IMAGE_URL = "imageURL";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParamImgURL;

    @BindView(R.id.mImageViewDetailGridPhoto)
    ImageView mImageViewDetailGridPhoto;

    @BindView(R.id.mTextViewDetailGridPhoto)
    TextView mTextViewDetailGridPhoto;


    private OnFragmentInteractionListener mListener;

    public DetailGridPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailGridPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailGridPhotoFragment newInstance(String param1, String param2) {
        DetailGridPhotoFragment fragment = new DetailGridPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParamImgURL = getArguments().getString(ARG_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_detail_grid_photo, container, false);

        ButterKnife.bind(this, rootView);

        Glide.with(getContext())
                .load(mParamImgURL)
                .into(mImageViewDetailGridPhoto);

        return rootView;
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
}
