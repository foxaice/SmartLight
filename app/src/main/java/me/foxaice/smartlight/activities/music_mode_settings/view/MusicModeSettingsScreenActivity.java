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

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.music_mode_settings.presenter.IMusicModeSettingsPresenter;
import me.foxaice.smartlight.activities.music_mode_settings.presenter.MusicModeSettingsPresenter;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.utils.AnimationUtils;

public class MusicModeSettingsScreenActivity extends AppCompatActivity implements IMusicModeSettingsView {
    public static final int RESULT_KEY_MUSIC_INFO_IS_NOT_CHANGED = 300;
    public static final int RESULT_KEY_MUSIC_INFO_IS_CHANGED = 301;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private IMusicModeSettingsPresenter mPresenter = new MusicModeSettingsPresenter();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_mode_settings);

        mPresenter.attach(this);
        mPresenter.loadMusicInfoFromPreferences();

        mColorModeText = (TextView) findViewById(R.id.activity_music_mode_settings_text_color_mode);
        mColorModeRecycler = (RecyclerView) findViewById(R.id.activity_music_mode_settings_recycler_color_mode);
        mMaxFrequencyText = (TextView) findViewById(R.id.activity_music_mode_settings_text_max_frequency);
        mMaxVolumeText = (TextView) findViewById(R.id.activity_music_mode_settings_text_max_volume);
        mMinVolumeText = (TextView) findViewById(R.id.activity_music_mode_settings_text_min_volume);
        mMaxFrequencySeekBar = (SeekBar) findViewById(R.id.activity_music_mode_settings_seekbar_max_frequency);
        mMaxVolumeSeekBar = (SeekBar) findViewById(R.id.activity_music_mode_settings_seekbar_max_volume);
        mMinVolumeSeekBar = (SeekBar) findViewById(R.id.activity_music_mode_settings_seekbar_min_volume);
        mMaxFrequencyRootView = (ViewGroup) findViewById(R.id.activity_music_mode_settings_rl_max_frequency_type);
        mMaxFrequencyStaticTypeRadioButton = (RadioButton) findViewById(R.id.activity_music_mode_settings_radio_button_max_frequency_type_static);
        mMaxFrequencyTypeRadioGroup = (RadioGroup) findViewById(R.id.activity_music_mode_settings_radio_group_max_frequency_type);
        mSoundViewTypeRadioGroup = (RadioGroup) findViewById(R.id.activity_music_mode_settings_radio_group_plot_type);
        ImageView closeButtonImage = (ImageView) findViewById(R.id.activity_music_mode_settings_image_close);
        ImageView resetButtonImage = (ImageView) findViewById(R.id.activity_music_mode_settings_image_reset);

        closeButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resetButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.resetMusicInfo();
            }
        });

        prepareColorModeView();
        prepareSoundViewTypeView();
        prepareMaxFrequencyTypeView();
        prepareMaxVolumeView();
        prepareMinVolumeView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detach();
    }

    @Override
    public void finish() {
        if (mPresenter.isMusicModeChanged()) {
            mPresenter.saveMusicInfoToPreferences();
            setResult(RESULT_KEY_MUSIC_INFO_IS_CHANGED);
        } else {
            setResult(RESULT_KEY_MUSIC_INFO_IS_NOT_CHANGED);
        }
        super.finish();
    }

    @Override
    public void setColorModeByColorMode(@IMusicInfo.ColorMode int colorMode) {
        if (mColorModeRecycler != null) {
            int position = ((ColorModesAdapter) mColorModeRecycler.getAdapter()).getPositionOfColorMode(colorMode);
            setColorModeByPosition(position);
        }
    }

    @Override
    public void setSoundViewType(@IMusicInfo.ViewType int viewType) {
        int id = viewType == IMusicInfo.ViewType.FREQUENCIES
                ? R.id.activity_music_mode_settings_radio_button_plot_type_frequencies
                : viewType == IMusicInfo.ViewType.WAVEFORM
                ? R.id.activity_music_mode_settings_radio_button_plot_type_waveform
                : R.id.activity_music_mode_settings_radio_button_plot_type_none;
        mSoundViewTypeRadioGroup.check(id);
    }

    @Override
    public void setMaxFrequencyType(@IMusicInfo.MaxFrequencyType int maxFrequencyType) {
        int id = maxFrequencyType == IMusicInfo.MaxFrequencyType.DYNAMIC
                ? R.id.activity_music_mode_settings_radio_button_max_frequency_type_dynamic
                : R.id.activity_music_mode_settings_radio_button_max_frequency_type_static;
        mMaxFrequencyTypeRadioGroup.check(id);
    }

    @Override
    public void setMaxFrequency(int maxFrequency) {
        mMaxFrequencySeekBar.setProgress(convertHzValueToProgress(maxFrequency));
    }

    @Override
    public void setMaxVolume(int dBSPL) {
        mMaxVolumeSeekBar.setProgress(dBSPL);
    }

    @Override
    public void setMinVolume(int dBSPL) {
        mMinVolumeSeekBar.setProgress(dBSPL);
    }

    @Override
    public Context getContext() {
        return this;
    }

    void onColorModeClicked(int position) {
        @IMusicInfo.ColorMode
        int colorMode = ((ColorModesAdapter) mColorModeRecycler.getAdapter()).getColorModeOfPosition(position);
        mPresenter.onChangeColorMode(colorMode);
    }

    private void setColorModeByPosition(int position) {
        if (mColorModeRecycler != null) {
            int size = mColorModeRecycler.getChildCount();
            if (position < size) {
                for (int i = 0; i < size; i++) {
                    ColorModeHolder holder = (ColorModeHolder) mColorModeRecycler.findViewHolderForAdapterPosition(i);
                    if (i == position) {
                        Spanned spans = holder.getColorModeSpannedText();
                        spans = (Spanned) TextUtils.concat(getString(R.string.color_mode), spans);
                        mColorModeText.setText(spans);

                        holder.itemView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.itemView.setBackground(ContextCompat.getDrawable(this, R.drawable.selector_button_background_without_frame_gray));
                        } else {
                            //noinspection deprecation
                            holder.itemView.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.selector_button_background_without_frame_gray));
                        }
                    }
                }
            }
        }
    }

    private void showMaxFrequencySeekBar() {
        if (mMaxFrequencySeekBar.getVisibility() == View.GONE) {
            mMaxFrequencyRootView.startAnimation(mMaxFrequencySeekBarShowAnimation);
        }
    }

    private void hideMaxFrequencySeekBar() {
        if (mMaxFrequencySeekBar.getVisibility() == View.VISIBLE) {
            mMaxFrequencyRootView.startAnimation(mMaxFrequencySeekBarHideAnimation);
        }
    }

    private int convertProgressToHzValue(int progress) {
        return progress * 100 + 1000;
    }

    private int convertHzValueToProgress(int hz) {
        return hz / 100 - 10;
    }

    private void prepareColorModeView() {
        mColorModeRecycler.setAdapter(new ColorModesAdapter(this));
        mColorModeRecycler.addItemDecoration(new ColorModesAdapter.SpacesItemDecoration(3));
        mColorModeRecycler.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mColorModeRecycler.post(new Runnable() {
            @Override
            public void run() {
                setColorModeByColorMode(mPresenter.getColorMode());
            }
        });
    }

    private void prepareSoundViewTypeView() {
        @IMusicInfo.ViewType int viewType = mPresenter.getSoundViewType();
        mSoundViewTypeRadioGroup.check(viewType == IMusicInfo.ViewType.FREQUENCIES
                ? R.id.activity_music_mode_settings_radio_button_plot_type_frequencies
                : viewType == IMusicInfo.ViewType.WAVEFORM
                ? R.id.activity_music_mode_settings_radio_button_plot_type_waveform
                : R.id.activity_music_mode_settings_radio_button_plot_type_none
        );
        mSoundViewTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.activity_music_mode_settings_radio_button_plot_type_frequencies) {
                    mPresenter.onChangeSoundViewType(IMusicInfo.ViewType.FREQUENCIES);
                } else if (checkedId == R.id.activity_music_mode_settings_radio_button_plot_type_waveform) {
                    mPresenter.onChangeSoundViewType(IMusicInfo.ViewType.WAVEFORM);
                } else if (checkedId == R.id.activity_music_mode_settings_radio_button_plot_type_none) {
                    mPresenter.onChangeSoundViewType(IMusicInfo.ViewType.NONE);
                }
            }
        });
    }

    private void prepareMaxFrequencyTypeView() {
        if (mPresenter.getMaxFrequencyType() == IMusicInfo.MaxFrequencyType.DYNAMIC) {
            mMaxFrequencyTypeRadioGroup.check(R.id.activity_music_mode_settings_radio_button_max_frequency_type_dynamic);
            mMaxFrequencyText.setText(getString(R.string.max_frequency));
            mMaxFrequencySeekBar.setVisibility(View.GONE);
        } else {
            mMaxFrequencyTypeRadioGroup.check(R.id.activity_music_mode_settings_radio_button_max_frequency_type_static);
            mMaxFrequencyText.setText(getString(R.string.max_frequency_with_param, mPresenter.getMaxFrequency()));
            mMaxFrequencySeekBar.setVisibility(View.VISIBLE);
        }
        mMaxFrequencyTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.activity_music_mode_settings_radio_button_max_frequency_type_dynamic) {
                    hideMaxFrequencySeekBar();
                    mMaxFrequencyText.setText(getString(R.string.max_frequency));
                    mPresenter.onChangeMaxFrequencyType(IMusicInfo.MaxFrequencyType.DYNAMIC);
                } else if (checkedId == R.id.activity_music_mode_settings_radio_button_max_frequency_type_static) {
                    showMaxFrequencySeekBar();
                    mMaxFrequencyText.setText(getString(R.string.max_frequency_with_param, convertProgressToHzValue(mMaxFrequencySeekBar.getProgress())));
                    mPresenter.onChangeMaxFrequencyType(IMusicInfo.MaxFrequencyType.STATIC);
                }
            }
        });

        mMaxFrequencySeekBar.setProgress(convertHzValueToProgress(mPresenter.getMaxFrequency()));
        mMaxFrequencySeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMaxFrequencyStaticTypeRadioButton.isChecked()) {
                    mMaxFrequencyText.setText(getString(R.string.max_frequency_with_param, convertProgressToHzValue(seekBar.getProgress())));
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPresenter.onChangeMaxFrequency(convertProgressToHzValue(seekBar.getProgress()));
            }
        });

        float addedHeight = getResources().getDimension(R.dimen.additional_height_for_seekbar);
        mMaxFrequencySeekBarShowAnimation = AnimationUtils.getChangeViewGroupHeightAnimation(mMaxFrequencyRootView, addedHeight);
        mMaxFrequencySeekBarHideAnimation = AnimationUtils.getChangeViewGroupHeightAnimation(mMaxFrequencyRootView, -addedHeight);
        mMaxFrequencySeekBarShowAnimation.setDuration(200);
        mMaxFrequencySeekBarHideAnimation.setDuration(200);
        mMaxFrequencySeekBarShowAnimation.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMaxFrequencySeekBar.setVisibility(View.VISIBLE);
            }
        });
        mMaxFrequencySeekBarHideAnimation.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMaxFrequencySeekBar.setVisibility(View.GONE);
            }
        });
    }

    private void prepareMaxVolumeView() {
        mMaxVolumeText.setText(getString(R.string.max_volume_threshold, mPresenter.getMaxVolume()));
        mMaxVolumeSeekBar.setProgress(mPresenter.getMaxVolume());
        mMaxVolumeSeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMaxVolumeText.setText(getString(R.string.max_volume_threshold, seekBar.getProgress()));
                if (progress <= mMinVolumeSeekBar.getProgress()) {
                    if (progress != 0) {
                        mMinVolumeSeekBar.setProgress(progress - 1);
                        mPresenter.onChangeMinVolume(progress - 1);
                    } else {
                        seekBar.setProgress(progress + 1);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPresenter.onChangeMaxVolume(seekBar.getProgress());
            }
        });
    }

    private void prepareMinVolumeView() {
        mMinVolumeText.setText(getString(R.string.min_volume_threshold, mPresenter.getMinVolume()));
        mMinVolumeSeekBar.setProgress(mPresenter.getMinVolume());
        mMinVolumeSeekBar.setOnSeekBarChangeListener(new SeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMinVolumeText.setText(getString(R.string.min_volume_threshold, seekBar.getProgress()));
                if (progress >= mMaxVolumeSeekBar.getProgress()) {
                    if (progress != mMaxVolumeSeekBar.getMax()) {
                        mMaxVolumeSeekBar.setProgress(progress + 1);
                        mPresenter.onChangeMaxVolume(progress + 1);
                    } else {
                        seekBar.setProgress(progress - 1);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPresenter.onChangeMinVolume(seekBar.getProgress());
            }
        });
    }

    private static class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
}
