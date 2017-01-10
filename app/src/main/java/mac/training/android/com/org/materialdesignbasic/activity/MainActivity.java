package mac.training.android.com.org.materialdesignbasic.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import mac.training.android.com.org.materialdesignbasic.R;
import mac.training.android.com.org.materialdesignbasic.activity.login.EmailPasswordActivity;
import mac.training.android.com.org.materialdesignbasic.fragments.GridPhotoFragment;
import mac.training.android.com.org.materialdesignbasic.fragments.PhotoFragment;
import mac.training.android.com.org.materialdesignbasic.fragments.HomeFragment;
import mac.training.android.com.org.materialdesignbasic.fragments.UploadImageFragment;
import mac.training.android.com.org.materialdesignbasic.singleton.FirebaseOAuth;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{
    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.mToolbar)
    Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item.getItemId() == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Log.d(TAG, "displayView");
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new PhotoFragment();
                title = getString(R.string.title_photo);
                break;
            case 2:
                fragment = new UploadImageFragment();
                title = getString(R.string.title_upload_Image);
                break;
            case 3:
                fragment = new GridPhotoFragment();
                title = getString(R.string.title_grid_Image);
                break;
            case 4://Logoff
                logOut();
                break;
            default:
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_body, fragment).commit();
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    public void logOut(){
        FirebaseOAuth.mAuth.signOut();
        startActivity(new Intent(this, EmailPasswordActivity.class));

    }
}
