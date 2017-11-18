package me.foxaice.smartlight.activities.controllers_screen.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.controllers_screen.presenter.ControllerScreenPresenter;
import me.foxaice.smartlight.activities.controllers_screen.presenter.IControllerScreenPresenter;
import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view.ControllerListFragment;


public class ControllersScreenActivity extends AppCompatActivity implements IControllerScreenView {
    private ImageView mBackArrowImage;
    private IControllerScreenPresenter mPresenter = new ControllerScreenPresenter();
    private ViewHandler mViewHandler = new ViewHandler(this);

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
        mViewHandler.sendEmptyMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewHandler.removeAllCallbacksAndMessages();
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
        Snackbar.make(mBackArrowImage,
                String.format(Locale.ENGLISH, "Network is %sconnected", isEnabled ? "" : "dis"),
                Snackbar.LENGTH_SHORT
        ).show();
        ControllerListFragment.attachFragment(this,
                R.id.activity_controllers_screen_frame_layout,
                isEnabled, isConnected
        );
    }

    IControllerScreenPresenter getPresenter() {
        return mPresenter;
    }
}
