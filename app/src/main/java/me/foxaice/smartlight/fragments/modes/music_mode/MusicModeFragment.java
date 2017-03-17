package me.foxaice.smartlight.fragments.modes.music_mode;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.music_mode_settings.view.MusicModeSettingsScreenActivity;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_music_mode, container, false);
        mMusicModePresenter.attachView(this);
        mHandler = new MusicModeHandler(this);

        mIsLandscapeOrientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mRootLayout = (ConstraintLayout) view;
        mWaveFormView = (WaveFormView) view.findViewById(R.id.fragment_music_mode_waveform);
        mPlayStopButtonImage = (ImageView) view.findViewById(R.id.fragment_music_mode_image_play_stop_frequency);
        mMaxVolumeText = (TextView) view.findViewById(R.id.fragment_music_mode_text_max_volume);
        mMinVolumeText = (TextView) view.findViewById(R.id.fragment_music_mode_text_min_volume);
        mColorModeText = (TextView) view.findViewById(R.id.fragment_music_mode_text_color_mode);
        mCurrentVolumeText = (TextView) view.findViewById(R.id.fragment_music_mode_text_volume);
        mCurrentFrequencyText = (TextView) view.findViewById(R.id.fragment_music_mode_text_frequency);
        ImageView settingsButtonImage = (ImageView) view.findViewById(R.id.fragment_music_mode_image_settings_frequency);

        settingsButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlaying) {
                    mPlayStopButtonImage.performClick();
                }
                startActivityForResult(new Intent(getContext(), MusicModeSettingsScreenActivity.class), REQUEST_CODE);
            }
        });

        mPlayStopButtonImage.setOnClickListener(new View.OnClickListener() {
            private Drawable mPlayDrawable = ContextCompat.getDrawable(getContext(), R.drawable.animated_button_play);
            private Drawable mPauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.animated_button_pause);

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.request_permission_dialog_title)
                                    .setMessage(R.string.request_permission_dialog_message)
                                    .setPositiveButton(R.string.request_permission_dialog_positive_button, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
                                        }
                                    })
                                    .setNegativeButton(R.string.request_permission_dialog_negative_button, null).show();
                        } else {
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
                        }
                    } else {
                        clickPlayPauseRecord();
                    }
                } else {
                    clickPlayPauseRecord();
                }
            }

            private void clickPlayPauseRecord() {
                if (mIsPlaying = !mIsPlaying) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        mPlayStopButtonImage.setImageDrawable(mPlayDrawable);
                        if (mPlayDrawable instanceof Animatable) {
                            ((Animatable) mPlayDrawable).start();
                        }
                    } else {
                        mPlayStopButtonImage.setImageDrawable(mPauseDrawable);
                    }
                    mMusicModePresenter.onTouchPlayButton();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        mPlayStopButtonImage.setImageDrawable(mPauseDrawable);
                        if (mPauseDrawable instanceof Animatable) {
                            ((Animatable) mPauseDrawable).start();
                        }
                    } else {
                        mPlayStopButtonImage.setImageDrawable(mPlayDrawable);
                    }
                    mMusicModePresenter.onTouchStopButton();
                }
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_IS_PLAYING)) {
                mPlayStopButtonImage.post(new Runnable() {
                    @Override
                    public void run() {
                        mPlayStopButtonImage.performClick();
                    }
                });
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_PLAYING, mIsPlaying);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MusicModeSettingsScreenActivity.RESULT_KEY_MUSIC_INFO_IS_CHANGED) {
            mMusicModePresenter.loadMusicInfoFromPreferences();
        }
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
    public void onDetach() {
        super.onDetach();
        mMusicModePresenter.detachView();
    }

    @Override
    public void drawWaveFormView(double[] data, String color, double max, @IMusicInfo.ViewType int viewType) {
        mWaveFormView.setAudioBufferColor(color);
        mWaveFormView.setViewType(viewType);
        if (viewType == IMusicInfo.ViewType.FREQUENCIES) {
            max = Math.pow(10, max / 10 > 7 ? 7 : max / 10);
            mWaveFormView.setMax(max);
            int shift = 2;
            int newSize = (int) (data.length * .86);
            double[] temp = new double[newSize - 2 * shift];
            for (int i = 0; i < newSize - 2 * shift; i++) {
                if (i >= temp.length / 2 - 1 - shift) {
                    temp[i] = temp[temp.length - 1 - i];
                } else {
                    temp[i] = data[i + shift];
                }
            }
            mWaveFormView.setAudioBuffer(temp);
        } else if (viewType == IMusicInfo.ViewType.WAVEFORM) {
            mWaveFormView.setMax(max * 8);
            mWaveFormView.setAudioBuffer(data);
        }
    }

    @Override
    public void setWaveFormVisible(boolean visible) {
        if (mRootLayout != null) {
            float density = getResources().getDisplayMetrics().density;
            if (mCacheWaveformVisibleConstraintSet == null) {
                mCacheWaveformVisibleConstraintSet = new ConstraintSet();
                mCacheWaveformVisibleConstraintSet.clone(mRootLayout);
            }
            if (mIsLandscapeOrientation) {
                if (visible) {
                    mCacheWaveformVisibleConstraintSet.applyTo(mRootLayout);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone((ConstraintLayout) mPlayStopButtonImage.getParent());
                    constraintSet.clear(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.BOTTOM);
                    constraintSet.clear(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.RIGHT);
                    constraintSet.connect(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, (int) (8 * density));
                    constraintSet.connect(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.BOTTOM, R.id.guidelineHorizontal, ConstraintSet.TOP, (int) (8 * density));
                    constraintSet.clear(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.TOP);
                    constraintSet.clear(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.LEFT);
                    constraintSet.connect(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) (8 * density));
                    constraintSet.connect(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.TOP, R.id.guidelineHorizontal, ConstraintSet.BOTTOM, (int) (8 * density));
                    constraintSet.applyTo((ConstraintLayout) mPlayStopButtonImage.getParent());
                } else {
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mCacheWaveformVisibleConstraintSet);
                    constraintSet.clear(R.id.fragment_music_mode_constraint_music_info, ConstraintSet.RIGHT);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_music_info, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_music_info, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                    constraintSet.setVerticalBias(R.id.fragment_music_mode_constraint_music_info, 0.25f);
                    constraintSet.constrainMaxWidth(R.id.fragment_music_mode_constraint_music_info, getResources().getDimensionPixelSize(R.dimen.music_info_max_width));
                    constraintSet.clear(R.id.fragment_music_mode_constraint_buttons);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.TOP, R.id.fragment_music_mode_constraint_music_info, ConstraintSet.BOTTOM);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.LEFT, R.id.fragment_music_mode_constraint_music_info, ConstraintSet.LEFT);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.RIGHT, R.id.fragment_music_mode_constraint_music_info, ConstraintSet.RIGHT);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                    constraintSet.setVerticalBias(R.id.fragment_music_mode_constraint_buttons, 0.1f);
                    constraintSet.constrainWidth(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.MATCH_CONSTRAINT);
                    constraintSet.constrainHeight(R.id.fragment_music_mode_constraint_buttons, getResources().getDimensionPixelSize(R.dimen.music_mode_buttons_size));
                    constraintSet.setVisibility(R.id.fragment_music_mode_waveform, ConstraintSet.GONE);
                    constraintSet.applyTo(mRootLayout);

                    constraintSet.clone((ConstraintLayout) mPlayStopButtonImage.getParent());
                    constraintSet.clear(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.BOTTOM);
                    constraintSet.clear(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.RIGHT);
                    constraintSet.connect(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) (8 * density));
                    constraintSet.connect(R.id.fragment_music_mode_image_play_stop_frequency, ConstraintSet.RIGHT, R.id.guidelineVertical, ConstraintSet.LEFT);
                    constraintSet.clear(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.TOP);
                    constraintSet.clear(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.LEFT);
                    constraintSet.connect(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) (8 * density));
                    constraintSet.connect(R.id.fragment_music_mode_image_settings_frequency, ConstraintSet.LEFT, R.id.guidelineVertical, ConstraintSet.RIGHT);
                    constraintSet.applyTo((ConstraintLayout) mPlayStopButtonImage.getParent());
                }
            } else {
                if (visible) {
                    mCacheWaveformVisibleConstraintSet.applyTo(mRootLayout);
                } else {
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mRootLayout);
                    constraintSet.clear(R.id.fragment_music_mode_constraint_music_info, ConstraintSet.BOTTOM);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_music_info, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_music_info, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                    constraintSet.setVerticalBias(R.id.fragment_music_mode_constraint_music_info, 0.25f);
                    constraintSet.constrainMaxWidth(R.id.fragment_music_mode_constraint_music_info, getResources().getDimensionPixelSize(R.dimen.music_info_max_width));
                    constraintSet.clear(R.id.fragment_music_mode_constraint_buttons);
                    constraintSet.constrainWidth(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.MATCH_CONSTRAINT);
                    constraintSet.constrainHeight(R.id.fragment_music_mode_constraint_buttons, getResources().getDimensionPixelSize(R.dimen.music_mode_buttons_size));
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.TOP, R.id.fragment_music_mode_constraint_music_info, ConstraintSet.BOTTOM);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
                    constraintSet.connect(R.id.fragment_music_mode_constraint_buttons, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                    constraintSet.setVerticalBias(R.id.fragment_music_mode_constraint_buttons, 0.1f);
                    constraintSet.setVisibility(R.id.fragment_music_mode_waveform, ConstraintSet.GONE);
                    constraintSet.applyTo(mRootLayout);
                }
            }
        }
    }

    @Override
    public void setCurrentVolumeText(double value) {
        mHandler.sendMessage(Message.obtain(mHandler, MusicModeHandler.SET_VOLUME, value));
    }

    @Override
    public void setFrequencyText(int value) {
        mHandler.sendMessage(Message.obtain(mHandler, MusicModeHandler.SET_FREQUENCY, value));
    }

    @Override
    public void setMaxVolumeText(int value) {
        mMaxVolumeText.setText(getString(R.string.max_volume_threshold, value));
    }

    @Override
    public void setMinVolumeText(int value) {
        mMinVolumeText.setText(getString(R.string.min_volume_threshold, value));
    }

    @Override
    public void setColorModeText(@IMusicInfo.ColorMode int colorMode) {
        Spanned spans = getColorModeSpannedFromResources(this.getContext(), colorMode);
        mColorModeText.setText(TextUtils.concat(getString(R.string.color_mode), spans));
    }

    @Override
    public String[] getBytesColorsFromResources() {
        return getResources().getStringArray(R.array.bytes_colors);
    }

    @Override
    public void onChangedControllerSettings() {
        mMusicModePresenter.updateControllerSettings();
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
