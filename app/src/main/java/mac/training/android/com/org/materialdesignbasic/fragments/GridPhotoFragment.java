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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.RetrofitRest.RestService;
import mac.training.android.com.org.materialdesignbasic.adapter.GridViewAdapter;
import mac.training.android.com.org.materialdesignbasic.component.DaggerNetComponent;
import mac.training.android.com.org.materialdesignbasic.constans.AppConstants;
import mac.training.android.com.org.materialdesignbasic.model.Result;
import mac.training.android.com.org.materialdesignbasic.module.NetModule;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 *
 *
 * This fragment displays images into a GridView
 * Once an specific image is selected, it'll be displayed into the DetailGridPhotoFrfagment
 *
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

    //Use of dagger injection to make use of retrofit
    @Inject
    Retrofit retrofit;

    private OnFragmentInteractionListener mListener;

    public GridPhotoFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_grid_photo, container, false);
        ButterKnife.bind(this, rootView);

        //Injection of Retrofit
        DaggerNetComponent.builder()
                .netModule(new NetModule(AppConstants.baseURL))
                .build()
                .inject(this);

        RestService restService = retrofit.create(RestService.class);

        //Use of RXJava to consume Rest Service
        Observable<Result> resultObservable = restService.getPhotos(AppConstants.method,
                AppConstants.API_KEY,
                "10",
                AppConstants.format,
                AppConstants.callBack);

        resultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError::" + e.getMessage());
                    }

                    @Override
                    public void onNext(Result result) {
                        //If OK so get the info and set the adapter with the retrieved data
                        Log.d(TAG, "onNext::" + result.getPhotos().getPhoto().size());
                        mGridView.setAdapter(new GridViewAdapter(getContext(), result.getPhotos().getPhoto()));
                        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String imageURL = (String) view.getTag();
                                Log.d(TAG, "*****Click to Launch Detail::" + imageURL);
                                Fragment fragment = new DetailGridPhotoFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("imageURL", imageURL);
                                fragment.setArguments(bundle);

                                //Once the user clicks over one image we go to another fragment and display detail
                                //It is necessary to add it to the basckstack so the user can come back on backpressed
                                getFragmentManager()
                                        .beginTransaction()
                                        .addToBackStack(TAG)
                                        .replace(R.id.container_body, fragment, "DetailGridPhotoFragment")
                                        .commit();
                            }
                        });
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
