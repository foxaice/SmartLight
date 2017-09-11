package me.foxaice.smartlight.activities.main_screen;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.main_screen.presenter.IMainScreenPresenter;
import me.foxaice.smartlight.activities.main_screen.presenter.MainScreenPresenter;
import me.foxaice.smartlight.activities.main_screen.view.IMainScreenView;
import me.foxaice.smartlight.fragments.modes.bulb_mode.BulbModeFragment;
import me.foxaice.smartlight.fragments.modes.disco_mode.DiscoModeFragment;
import me.foxaice.smartlight.fragments.modes.music_mode.MusicModeFragment;
import me.foxaice.smartlight.fragments.settings.SettingsFragment;

public class MainScreenActivity extends AppCompatActivity implements IMainScreenView {

    private static final String KEY_BULB_GROUP = "KEY_BULB_GROUP";
    private static final String KEY_IS_SETTINGS_OPEN = "KEY_IS_SETTINGS_OPEN";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private IMainScreenPresenter mPresenter = new MainScreenPresenter();
    private View mContentView;
    private View mWaveBackground;
    private View mBulbModeButton;
    private View mMusicModeButton;
    private View mDiscoModeButton;
    private View mModeContent;
    private View mSettingsContent;
    private boolean mIsSettingsOpen = false;
    private SettingsFragment mSettingsFragment;
    private SettingsImageAnimation mSettingsImageAnimation;
    private ImageView mSettingsImage;
    private ListView mDrawerListView;
    private List<String> mZoneNamesList = new ArrayList<>();
    private ArrayAdapter<String> mDrawerListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mPresenter.attach(this);

        if (savedInstanceState != null) {
            mPresenter.setCurrentBulbGroup(savedInstanceState.getInt(KEY_BULB_GROUP));
            mIsSettingsOpen = savedInstanceState.getBoolean(KEY_IS_SETTINGS_OPEN);

            mSettingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_screen_fragment_settings);
            if (mSettingsFragment != null) {
                mSettingsImageAnimation = new SettingsImageAnimation(
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_rotate_in),
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_rotate_out)
                );
            }
        }

        initToolbarAndDrawer();

        mContentView = findViewById(R.id.activity_main_screen_constraint_layout_content);
        mWaveBackground = findViewById(R.id.activity_main_screen_relative_layout_wave_background);
        mSettingsImage = (ImageView) findViewById(R.id.partial_toolbar_image_settings);
        mBulbModeButton = findViewById(R.id.activity_main_screen_image_bulb_mode);
        mMusicModeButton = findViewById(R.id.activity_main_screen_image_music_mode);
        mDiscoModeButton = findViewById(R.id.activity_main_screen_image_disco_mode);
        mModeContent = findViewById(R.id.activity_main_screen_frame_layout_mode_content);
        mSettingsContent = findViewById(R.id.activity_main_screen_fragment_settings);

        mSettingsImage.setOnTouchListener(new View.OnTouchListener() {
            private ImageView backgroundImage = (ImageView) findViewById(R.id.partial_toolbar_image_settings_background);

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    backgroundImage.setPressed(false);
                    mPresenter.onTouchUpSettingsButton();
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    backgroundImage.setPressed(true);
                }
                return true;
            }
        });

        mBulbModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBulbModeFragment();
            }
        });
        mMusicModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMusicModeFragment();
            }
        });
        mDiscoModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiscoModeFragment();
            }
        });

        String currentFragmentTag = mPresenter.getFragmentTagFromSettings();
        if (currentFragmentTag == null) {
            currentFragmentTag = BulbModeFragment.TAG;
        }
        switch (currentFragmentTag) {
            case MusicModeFragment.TAG: {
                ((ImageView) mMusicModeButton).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.button_mode_music_disabled));
                mMusicModeButton.setEnabled(false);
                setModeFragmentByTag(MusicModeFragment.TAG);
                break;
            }
            case DiscoModeFragment.TAG: {
                ((ImageView) mDiscoModeButton).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.button_mode_disco_disabled));
                mDiscoModeButton.setEnabled(false);
                setModeFragmentByTag(DiscoModeFragment.TAG);
                break;
            }
            default: {
                ((ImageView) mBulbModeButton).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.button_mode_bulb_disabled));
                mBulbModeButton.setEnabled(false);
                setModeFragmentByTag(BulbModeFragment.TAG);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_BULB_GROUP, mPresenter.getCurrentBulbGroup());
        outState.putBoolean(KEY_IS_SETTINGS_OPEN, mIsSettingsOpen);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mZoneNamesList.clear();
        mZoneNamesList.addAll(Arrays.asList(getGroupNamesFromSettings()));
        mDrawerListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.saveCurrentModeTagToSettings();
    }

    @Override
    public void onBackPressed() {
        if (!isSettingsOpen() && !isNavigationDrawerOpen()) {
            super.onBackPressed();
        } else {
            closeNavigationDrawer();
            if (isSettingsOpen()) {
                hideSettingsFragment();
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String getCurrentFragmentTag() {
        return getSupportFragmentManager().findFragmentById(mModeContent.getId()).getTag();
    }

    @Override
    public void showMessageOfSelectedGroup(String selectedGroup) {
        String message;
        if (selectedGroup == null) {
            message = getString(R.string.select_all_groups);
        } else {
            message = getString(R.string.select_a_group, selectedGroup);
        }
        showSnackbarMessage(message);
    }

    @Override
    public void closeNavigationDrawer() {
        if (mDrawer != null) mDrawer.closeDrawers();
    }

    @Override
    public boolean isNavigationDrawerOpen() {
        return mDrawer.isDrawerOpen(mDrawerListView);
    }

    @Override
    public boolean isSettingsOpen() {
        return mIsSettingsOpen;
    }

    @Override
    public void showBulbModeFragment() {
        showFragmentByTag(BulbModeFragment.TAG);
    }

    @Override
    public void showMusicModeFragment() {
        showFragmentByTag(MusicModeFragment.TAG);
    }

    @Override
    public void showDiscoModeFragment() {
        showFragmentByTag(DiscoModeFragment.TAG);
    }

    @Override
    public void showSettingsFragment() {
        mIsSettingsOpen = true;
        if (mSettingsFragment == null) {
            mSettingsFragment = new SettingsFragment();
            mSettingsImageAnimation = new SettingsImageAnimation(
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_rotate_in),
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_rotate_out)
            );
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .add(mSettingsContent.getId(), mSettingsFragment)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .attach(mSettingsFragment)
                    .commit();
        }
        mSettingsImage.startAnimation(mSettingsImageAnimation.animIn);
    }

    @Override
    public void hideSettingsFragment() {
        mIsSettingsOpen = false;
        if (mSettingsFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .detach(mSettingsFragment)
                    .commit();
            mSettingsImage.startAnimation(mSettingsImageAnimation.animOut);
        }
    }

    @Override
    public void updateNavigationDrawerData(String[] data) {
        mDrawerListAdapter.clear();
        for (String item : data) {
            mDrawerListAdapter.add(item);
        }
        mDrawerListAdapter.notifyDataSetChanged();
    }

    private void initToolbarAndDrawer() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.partial_toolbar_toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.activity_main_screen_drawer_layout);
        mDrawerListView = (ListView) ((ViewStub) findViewById(R.id.activity_main_screen_stub_sidebar)).inflate();
        if (mDrawerListView != null) {
            mDrawerListAdapter = new ArrayAdapter<>(this, R.layout.item_sidebar_list_group, mZoneNamesList);
            mDrawerListView.setAdapter(mDrawerListAdapter);
            mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
        }
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mPresenter.onDrawerSlide();
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
    }

    private Bundle formArgumentBundleWithBulbInfo() {
        String key = mPresenter.getBulbInfoKey();
        Object data = mPresenter.getBulbInfoObject();
        Bundle bundle = new Bundle();
        if (data instanceof Parcelable) {
            bundle.putParcelable(key, (Parcelable) data);
        } else {
            throw new IllegalArgumentException("data IS NOT instanceof Parcelable");
        }
        return bundle;
    }

    private void showFragmentByTag(String tag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            presentFragmentByTag(tag);
        } else {
            presentFragmentByTagPreApi21(tag);
        }
    }

    private void presentFragmentByTagPreApi21(final String tag) {
        Fragment preFragment = getSupportFragmentManager().findFragmentById(mModeContent.getId());
        String preFragmentTag = preFragment.getTag();
        final View preView = preFragmentTag.equals(BulbModeFragment.TAG)
                ? mBulbModeButton
                : preFragmentTag.equals(MusicModeFragment.TAG)
                ? mMusicModeButton
                : mDiscoModeButton;
        final Drawable preViewDrawable = ContextCompat.getDrawable(this,
                preView == mBulbModeButton
                        ? R.drawable.button_mode_bulb_enabled
                        : preView == mMusicModeButton
                        ? R.drawable.button_mode_music_enabled
                        : R.drawable.button_mode_disco_enabled
        );

        final View curView = tag.equals(BulbModeFragment.TAG)
                ? mBulbModeButton
                : tag.equals(MusicModeFragment.TAG)
                ? mMusicModeButton
                : mDiscoModeButton;
        final Drawable curViewDrawable = ContextCompat.getDrawable(this,
                curView == mBulbModeButton
                        ? R.drawable.button_mode_bulb_disabled
                        : curView == mMusicModeButton
                        ? R.drawable.button_mode_music_disabled
                        : R.drawable.button_mode_disco_disabled
        );
        final Interpolator interpolator = new OvershootInterpolator();
        final Animation downScalePreView = new ScaleAnimation(1, 0, 1, 0, preView.getWidth() / 2, preView.getHeight() / 2);
        final Animation upScalePreView = new ScaleAnimation(0, 1, 0, 1, preView.getWidth() / 2, preView.getHeight() / 2);
        final Animation firstUpScaleCurView = new ScaleAnimation(1, 2, 1, 2, curView.getWidth() / 2, curView.getHeight() / 2);
        final Animation downScaleCurView = new ScaleAnimation(2, 0, 2, 0, curView.getWidth() / 2, curView.getHeight() / 2);
        final Animation secondUpScaleCurView = new ScaleAnimation(0, 1, 0, 1, curView.getWidth() / 2, curView.getHeight() / 2);
        final Animation downScaleModeContent = new ScaleAnimation(1, 0, 1, 0, mModeContent.getWidth() / 2, mModeContent.getHeight() / 2);
        final Animation translateModeContent = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        downScalePreView.setInterpolator(interpolator);
        upScalePreView.setInterpolator(interpolator);
        firstUpScaleCurView.setInterpolator(interpolator);
        downScaleCurView.setInterpolator(interpolator);
        secondUpScaleCurView.setInterpolator(interpolator);
        downScaleModeContent.setInterpolator(new AnticipateInterpolator());
        translateModeContent.setInterpolator(interpolator);
        downScalePreView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                preView.startAnimation(upScalePreView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        upScalePreView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ((ImageView) preView).setImageDrawable(preViewDrawable);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        firstUpScaleCurView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mBulbModeButton.setEnabled(false);
                mMusicModeButton.setEnabled(false);
                mDiscoModeButton.setEnabled(false);
                mBulbModeButton.bringToFront();
                mMusicModeButton.bringToFront();
                mDiscoModeButton.bringToFront();
                mSettingsContent.bringToFront();
                preView.startAnimation(downScalePreView);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                curView.startAnimation(downScaleCurView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        downScaleCurView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mModeContent.startAnimation(downScaleModeContent);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                curView.startAnimation(secondUpScaleCurView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        secondUpScaleCurView.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setModeFragmentByTag(tag);
                ((ImageView) curView).setImageDrawable(curViewDrawable);
                mModeContent.startAnimation(translateModeContent);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBulbModeButton.setEnabled(curView != mBulbModeButton);
                mDiscoModeButton.setEnabled(curView != mDiscoModeButton);
                mMusicModeButton.setEnabled(curView != mMusicModeButton);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        downScalePreView.setDuration(300);
        upScalePreView.setDuration(300);
        firstUpScaleCurView.setDuration(300);
        downScaleCurView.setDuration(300);
        secondUpScaleCurView.setDuration(1000);
        downScaleModeContent.setDuration(300);
        translateModeContent.setDuration(1000);
        curView.startAnimation(firstUpScaleCurView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void presentFragmentByTag(final String tag) {
        Fragment preFragment = getSupportFragmentManager().findFragmentById(mModeContent.getId());
        String preFragmentTag = preFragment.getTag();
        final View preView = preFragmentTag.equals(BulbModeFragment.TAG)
                ? mBulbModeButton
                : preFragmentTag.equals(MusicModeFragment.TAG)
                ? mMusicModeButton
                : mDiscoModeButton;
        final Drawable preViewDrawable = ContextCompat.getDrawable(this,
                preView == mBulbModeButton
                        ? R.drawable.button_mode_bulb_enabled
                        : preView == mMusicModeButton
                        ? R.drawable.button_mode_music_enabled
                        : R.drawable.button_mode_disco_enabled
        );

        final View curView = tag.equals(BulbModeFragment.TAG)
                ? mBulbModeButton
                : tag.equals(MusicModeFragment.TAG)
                ? mMusicModeButton
                : mDiscoModeButton;
        final Drawable curViewDrawable = ContextCompat.getDrawable(this,
                curView == mBulbModeButton
                        ? R.drawable.button_mode_bulb_disabled
                        : curView == mMusicModeButton
                        ? R.drawable.button_mode_music_disabled
                        : R.drawable.button_mode_disco_disabled
        );

        int[] coords = new int[]{0, 0};
        curView.getLocationInWindow(coords);
        int revealX = coords[0] + curView.getWidth() / 2;
        int revealY = (int) (coords[1] - mSettingsImage.getHeight() * 1.05f);
        int radius = (int) (Math.max(mWaveBackground.getHeight(), mWaveBackground.getWidth()) * 1.1);
        final int waveColor = ContextCompat.getColor(this,
                tag.equals(BulbModeFragment.TAG)
                        ? R.color.backgroundBulbButton
                        : tag.equals(MusicModeFragment.TAG)
                        ? R.color.backgroundMusicButton
                        : R.color.backgroundDiscoButton
        );
        final int finalColor = ContextCompat.getColor(this, R.color.backgroundActivity);
        final Animator firstAnim = ViewAnimationUtils.createCircularReveal(mWaveBackground, revealX, revealY, curView.getWidth() / 2, radius);
        final Animator secondAnim = ViewAnimationUtils.createCircularReveal(mContentView, revealX, revealY, curView.getWidth() / 2, radius);
        Animation scaleUp = new ScaleAnimation(1, 2, 1, 2, curView.getPivotX(), curView.getPivotY());
        Animation scaleDown = new ScaleAnimation(1, 0.5f, 1, 0.5f, curView.getPivotX(), curView.getPivotY());
        AnimationSet animationSet = new AnimationSet(true);
        scaleDown.setStartOffset(100);
        scaleDown.setDuration(200);
        scaleUp.setDuration(100);
        firstAnim.setDuration(300);
        animationSet.addAnimation(scaleUp);
        animationSet.addAnimation(scaleDown);
        animationSet.setInterpolator(new OvershootInterpolator());
        firstAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBulbModeButton.setEnabled(false);
                mMusicModeButton.setEnabled(false);
                mDiscoModeButton.setEnabled(false);
                mWaveBackground.setBackgroundColor(waveColor);
                mWaveBackground.bringToFront();
                curView.bringToFront();
                mSettingsContent.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Animation scaleDown = new ScaleAnimation(1, 0.1f, 1, 0.1f, curView.getPivotX(), curView.getPivotY());
                final Animation scaleUp = new ScaleAnimation(0.1f, 1, 0.1f, 1, curView.getPivotX(), curView.getPivotY());
                scaleDown.setDuration(250);
                scaleUp.setDuration(150);
                scaleDown.setInterpolator(new OvershootInterpolator());
                scaleUp.setInterpolator(new OvershootInterpolator());

                scaleUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        ((ImageView) curView).setImageDrawable(curViewDrawable);
                        ((ImageView) preView).setImageDrawable(preViewDrawable);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        secondAnim.setDuration(400);
                        secondAnim.setInterpolator(new AccelerateInterpolator());
                        secondAnim.start();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                scaleDown.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        curView.setAnimation(scaleUp);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                curView.startAnimation(scaleDown);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        secondAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mDrawer.setBackgroundColor(waveColor);
                mWaveBackground.setBackgroundColor(finalColor);
                mBulbModeButton.bringToFront();
                mMusicModeButton.bringToFront();
                mDiscoModeButton.bringToFront();
                mModeContent.bringToFront();
                mSettingsContent.bringToFront();
                setModeFragmentByTag(tag);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBulbModeButton.setEnabled(curView != mBulbModeButton);
                mMusicModeButton.setEnabled(curView != mMusicModeButton);
                mDiscoModeButton.setEnabled(curView != mDiscoModeButton);
                mDrawer.setBackgroundColor(finalColor);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                curView.bringToFront();
                mBulbModeButton.setEnabled(false);
                mMusicModeButton.setEnabled(false);
                mDiscoModeButton.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                firstAnim.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        curView.startAnimation(animationSet);
    }

    private void setModeFragmentByTag(String tag) {
        detachPreviousModeFragment();
        setNewModeFragmentByTag(tag);
    }

    private void detachPreviousModeFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(mModeContent.getId());
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(fragment)
                    .commit();
        }
    }

    private void setNewModeFragmentByTag(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = tag.equals(BulbModeFragment.TAG) ?
                    new BulbModeFragment() :
                    tag.equals(MusicModeFragment.TAG) ?
                            new MusicModeFragment() :
                            tag.equals(DiscoModeFragment.TAG) ?
                                    new DiscoModeFragment() : null;
            if (fragment == null) {
                throw new IllegalArgumentException("The TAG is NOT found!!!");
            }
            fragment.setArguments(formArgumentBundleWithBulbInfo());
            getSupportFragmentManager().beginTransaction().add(mModeContent.getId(), fragment, tag).commit();
        } else {
            fragment.setArguments(formArgumentBundleWithBulbInfo());
            getSupportFragmentManager().beginTransaction().attach(fragment).commit();
        }
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(mContentView, message, Snackbar.LENGTH_SHORT).show();
    }

    private String[] getGroupNamesFromSettings() {
        String[] groupNamesFromResources = getResources().getStringArray(R.array.zone_names);
        return mPresenter.getZonesNamesFromSettings(groupNamesFromResources);
    }

    private static class SettingsImageAnimation {
        private Animation animIn;
        private Animation animOut;

        private SettingsImageAnimation(Animation animIn, Animation animOut) {
            this.animIn = animIn;
            this.animOut = animOut;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPresenter.onDrawerItemClick(position, ((TextView) view).getText().toString());
        }
    }
}
