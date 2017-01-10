package mac.training.android.com.org.materialdesignbasic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import butterknife.BindView;
import butterknife.ButterKnife;
import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.adapter.GridViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridPhotoFragment extends Fragment {
    private static final String TAG = GridPhotoFragment.class.getSimpleName();
    private View rootView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.mGridView)
    GridView mGridView;

    @BindView(R.id.mDrawer)
    DrawerLayout mDrawer;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GridPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridPhotoFragment newInstance(String param1, String param2) {
        GridPhotoFragment fragment = new GridPhotoFragment();
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

        rootView = inflater.inflate(R.layout.fragment_grid_photo, container, false);
        ButterKnife.bind(this, rootView);

        mGridView.setAdapter(new GridViewAdapter(getContext()));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String imageURL = (String) view.getTag();
                Log.d(TAG, "*****Click to Launch Detail::" + imageURL);
//                DetailActivity.launch(HomeActivity.this, view.findViewById(R.id.image), url);
                Fragment fragment = new DetailGridPhotoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("imageURL", imageURL);
                fragment.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(TAG)
                        .replace(R.id.container_body, fragment, "DetailGridPhotoFragment")
                        .commit();

            }
        });

        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

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
