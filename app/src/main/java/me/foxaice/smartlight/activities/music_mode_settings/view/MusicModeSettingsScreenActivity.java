package me.foxaice.smartlight.activities.music_mode_settings.view;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;


public class MusicModeSettingsScreenActivity extends AppCompatActivity implements IMusicModeSettingsView {
    public static final int RESULT_KEY_MUSIC_INFO_IS_NOT_CHANGED = 300;
    public static final int RESULT_KEY_MUSIC_INFO_IS_CHANGED = 301;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private RecyclerView mColorModeRecycler;
    private TextView mColorModeText;
    private TextView mMaxFrequencyText;
    private TextView mMaxVolumeText;
    private TextView mMinVolumeText;
    private ViewGroup mMaxFrequencyRootView;
    private SeekBar mMaxFrequencySeekBar;
    private SeekBar mMaxVolumeSeekBar;
    private SeekBar mMinVolumeSeekBar;
    private RadioButton mMaxFrequencyStaticTypeRadioButton;
    private RadioGroup mMaxFrequencyTypeRadioGroup;
    private RadioGroup mSoundViewTypeRadioGroup;
    private Animation mMaxFrequencySeekBarHideAnimation;
    private Animation mMaxFrequencySeekBarShowAnimation;

    @Override
    public void setColorModeByColorMode(@IMusicInfo.ColorMode int colorMode) {

    }

    @Override
    public void setSoundViewType(@IMusicInfo.ViewType int viewType) {

    }

    @Override
    public void setMaxFrequencyType(@IMusicInfo.MaxFrequencyType int maxFrequencyType) {

    }

    @Override
    public void setMaxFrequency(int maxFrequency) {

    }

    @Override
    public void setMaxVolume(int dBSPL) {

    }

    @Override
    public void setMinVolume(int dBSPL) {

    }

    @Override
    public Context getContext() {
        return null;
    }
}
