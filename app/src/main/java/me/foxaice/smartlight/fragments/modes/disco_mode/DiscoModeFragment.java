package me.foxaice.smartlight.fragments.modes.disco_mode;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.ModeBaseView;
import me.foxaice.smartlight.fragments.modes.disco_mode.presenter.DiscoModePresenter;
import me.foxaice.smartlight.fragments.modes.disco_mode.presenter.IDiscoModePresenter;
import me.foxaice.smartlight.fragments.modes.disco_mode.view.IDiscoModeView;

public class DiscoModeFragment extends ModeBaseView implements IDiscoModeView {
    public static final String TAG = "DISCO_MODE_FRAGMENT";
    private IDiscoModePresenter mDiscoModePresenter = new DiscoModePresenter();
    private ImageView mSpeedUpTextImage;
    private ImageView mSpeedUpIconImage;
    private ImageView mSpeedDownTextImage;
    private ImageView mSpeedDownIconImage;
    private ImageView mNextModeTextImage;
    private ImageView mNextModeIconImage;
    private ImageView mVinylImage;
    private Handler mVinylDrawerHandler;

    private Animation[] mAnimationsOfVinylImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disco_mode, container, false);
        mAnimationsOfVinylImage = new Animation[]{
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_speed_up),
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_next_mode),
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_speed_down)
        };

        mSpeedUpTextImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_text_speed_up);
        mSpeedUpIconImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_icon_speed_up);
        mSpeedDownTextImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_text_speed_down);
        mSpeedDownIconImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_icon_speed_down);
        mNextModeTextImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_text_next_mode);
        mNextModeIconImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_icon_next_mode);
        mVinylImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_vinyl);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDiscoModePresenter.attachView(this);
    }


    @Override
    public void onChangedControllerSettings() {

    }

    @Override
    public void spinVinylImage(@Action int action) {

    }

    @Override
    public void setPressedSpeedUpButton(boolean isPressed) {

    }

    @Override
    public void setPressedSpeedDownButton(boolean isPressed) {

    }

    @Override
    public void setPressedNextModeButton(boolean isPressed) {

    }
}
