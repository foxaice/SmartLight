package me.foxaice.smartlight.activities.controllers_screen.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.controllers_screen.presenter.ControllerScreenPresenter;
import me.foxaice.smartlight.activities.controllers_screen.presenter.IControllerScreenPresenter;
import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view.ControllerListFragment;


public class ControllersScreenActivity extends AppCompatActivity implements IControllerScreenView {
    public static final String NOTIFY_NETWORK_CHANGE = "NOTIFY_NETWORK_CHANGE";
    public static final String EXTRA_IS_CONNECTED = "EXTRA_IS_CONNECTED";
    public static final String EXTRA_IS_WIFI_ENABLED = "EXTRA_IS_WIFI_ENABLED";
    private ImageView mBackArrowImage;
    private IControllerScreenPresenter mPresenter = new ControllerScreenPresenter();
    private ViewHandler mHandler = new ViewHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controllers_screen);
        mBackArrowImage = (ImageView) findViewById(R.id.toolbar_settings_image_back_arrow);
        mBackArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_trasition, R.anim.slide_out_left);
    }

    @Override
    public void showControllersListFragment(boolean isEnabled, boolean isConnected) {
        Fragment controllerListFragment = getSupportFragmentManager().findFragmentByTag(ControllerListFragment.TAG);
        if (controllerListFragment == null) {
            controllerListFragment = new ControllerListFragment();
            Bundle args = new Bundle();
            args.putBoolean(EXTRA_IS_WIFI_ENABLED, isEnabled);
            args.putBoolean(EXTRA_IS_CONNECTED, isConnected);
            controllerListFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_controllers_screen_frame_layout, controllerListFragment, ControllerListFragment.TAG)
                    .commit();
        } else if (!isConnected
                && !(getSupportFragmentManager().findFragmentById(R.id.activity_controllers_screen_frame_layout) instanceof ControllerListFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_controllers_screen_frame_layout, controllerListFragment)
                    .commit();
        }
        if (isEnabled) {
            Snackbar.make(mBackArrowImage, "Network is connected.", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mBackArrowImage, "Network is disconnected.", Snackbar.LENGTH_SHORT).show();
        }
    }

    IControllerScreenPresenter getPresenter() {
        return mPresenter;
    }

}
