package mac.training.android.com.org.materialdesignbasic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.RetrofitRest.RestService;
import mac.training.android.com.org.materialdesignbasic.RetrofitRest.RetrofitService;
import mac.training.android.com.org.materialdesignbasic.adapter.RVAdapter;
import mac.training.android.com.org.materialdesignbasic.component.DaggerNetComponent;
import mac.training.android.com.org.materialdesignbasic.constans.AppConstants;
import mac.training.android.com.org.materialdesignbasic.listener.SwipeRVHelper;
import mac.training.android.com.org.materialdesignbasic.model.Photo;
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
 * {@link PhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFragment extends Fragment {
    private static final String TAG = PhotoFragment.class.getName();
    private View rootView;

    //RecyclerView Variables
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RVAdapter adapter;

    //Dagger
    @Inject
    Retrofit retrofit;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment();
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
        rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        return rootView;
    }

    public void initRecyclerView(){
        Log.d(TAG, "initRecyclerView");
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        SwipeRVHelper swipeRVHelper = new SwipeRVHelper(mRecyclerView, getContext());
        swipeRVHelper.setUpItemTouchHelper();
        swipeRVHelper.setUpAnimationDecoratorHelper();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mRecyclerView);

        initRecyclerView();

        Log.d(TAG, "onViewCreated");

        //Dagger Injection
        DaggerNetComponent.builder()
                .netModule(new NetModule(AppConstants.baseURL))
                .build()
                .inject(this);

        RestService restService = retrofit.create(RestService.class);

        Observable<Result> resultObservable = restService.getPhotos(AppConstants.method,
                AppConstants.API_KEY,
                AppConstants.perPage,
                AppConstants.format,
                AppConstants.callBack);

//        Observable<Result> resultObservable = restService.getRecentPhotos(10, 10);
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
                        Log.d(TAG, "onNext::" + result.getPhotos().getPhoto().size());
                        List<Photo>lstRes = result.getPhotos().getPhoto();
                        for(int i = 0; i < lstRes.size(); i ++){
                            Log.d(TAG, lstRes.get(i).getId());
                            Log.d(TAG, lstRes.get(i).getTitle());
                        }

                        adapter = new RVAdapter(getContext(), result.getPhotos().getPhoto());
                        mRecyclerView.setAdapter(adapter);
                    }
                });
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
