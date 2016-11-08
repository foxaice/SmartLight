package me.foxaice.smartlight.activities.controllers_screen;

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
import me.foxaice.smartlight.activities.controllers_screen.view.IControllerScreenView;


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

    }

    IControllerScreenPresenter getPresenter() {
        return mPresenter;
    }

}
