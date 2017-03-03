package me.foxaice.smartlight.fragments.modes.music_mode;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.ModeBaseView;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.fragments.modes.music_mode.presenter.IMusicModePresenter;
import me.foxaice.smartlight.fragments.modes.music_mode.presenter.MusicModePresenter;
import me.foxaice.smartlight.fragments.modes.music_mode.view.IMusicModeView;
import me.foxaice.smartlight.fragments.modes.music_mode.waveformview.WaveFormView;

public class MusicModeFragment extends ModeBaseView implements IMusicModeView {
    public static final String TAG = "MUSIC_MODE_FRAGMENT";
    private static final String KEY_IS_PLAYING = "KEY_IS_PLAYING";
    private static final int REQUEST_CODE = 300;
    private final IMusicModePresenter mMusicModePresenter = new MusicModePresenter();
    private Handler mHandler;
    private ImageView mPlayStopButtonImage;
    private TextView mMaxVolumeText;
    private TextView mMinVolumeText;
    private TextView mColorModeText;
    private TextView mCurrentVolumeText;
    private TextView mCurrentFrequencyText;
    private WaveFormView mWaveFormView;
    private ConstraintSet mCacheWaveformVisibleConstraintSet;
    private ConstraintLayout mRootLayout;
    private boolean mIsPlaying;
    private boolean mIsLandscapeOrientation;

    @SuppressWarnings("deprecation")
    public static Spanned getColorModeSpannedFromResources(Context context, @IMusicInfo.ColorMode int name) {
        int stringId;
        switch (name) {
            case IMusicInfo.ColorMode.RGBM:
                stringId = R.string.RGBM;
                break;
            case IMusicInfo.ColorMode.GBRY:
                stringId = R.string.GBRY;
                break;
            case IMusicInfo.ColorMode.BRGC:
                stringId = R.string.BRGC;
                break;
            case IMusicInfo.ColorMode.RBGY:
                stringId = R.string.RBGY;
                break;
            case IMusicInfo.ColorMode.GRBC:
                stringId = R.string.GRBC;
                break;
            case IMusicInfo.ColorMode.BGRM:
                stringId = R.string.BGRM;
                break;
            default:
                throw new IllegalArgumentException("Wrong Color Mode ModeName!");
        }
        String html = context.getString(stringId);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_PLAYING, mIsPlaying);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPlayStopButtonImage.performClick();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMusicModePresenter.loadMusicInfoFromPreferences();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsPlaying = false;
        mMusicModePresenter.onTouchStopButton();
        mMusicModePresenter.stopExecutorService();
    }



    @Override
    public void onChangedControllerSettings() {

    }

    @Override
    public void drawWaveFormView(double[] data, String color, double max, @IMusicInfo.ViewType int viewType) {

    }

    @Override
    public void setCurrentVolumeText(double value) {

    }

    @Override
    public void setFrequencyText(int value) {

    }

    @Override
    public void setMaxVolumeText(int value) {

    }

    @Override
    public void setMinVolumeText(int value) {

    }

    @Override
    public void setColorModeText(@IMusicInfo.ColorMode int colorMode) {

    }

    @Override
    public void setWaveFormVisible(boolean visible) {

    }

    @Override
    public String[] getBytesColorsFromResources() {
        return new String[0];
    }

    private static class MusicModeHandler extends Handler {
        private static final int SET_FREQUENCY = 0x0001;
        private static final int SET_VOLUME = 0x0002;
        private final WeakReference<MusicModeFragment> wrFragment;
        private final String mFrequencyText;
        private final String mVolumeText;

        MusicModeHandler(MusicModeFragment fragment) {
            this.wrFragment = new WeakReference<>(fragment);
            mFrequencyText = fragment.getString(R.string.frequency_with_param);
            mVolumeText = fragment.getString(R.string.volume_with_param);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicModeFragment fragment = wrFragment.get();
            if (fragment != null) {
                if (msg.what == SET_FREQUENCY) {
                    fragment.mCurrentFrequencyText.setText(
                            String.format(mFrequencyText, msg.obj)
                    );
                } else if (msg.what == SET_VOLUME) {
                    fragment.mCurrentVolumeText.setText(
                            String.format(mVolumeText, msg.obj)
                    );
                }
            }
        }
    }
}
