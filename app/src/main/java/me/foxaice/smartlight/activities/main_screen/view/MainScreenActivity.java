package me.foxaice.smartlight.activities.main_screen.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import me.foxaice.smartlight.fragments.modes.bulb_mode.view.BulbModeFragment;
import me.foxaice.smartlight.fragments.modes.disco_mode.view.DiscoModeFragment;
import me.foxaice.smartlight.fragments.modes.music_mode.view.MusicModeFragment;
import me.foxaice.smartlight.fragments.settings.view.SettingsFragment;

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

        initViews();
        initToolbarAndDrawer();
        setClickAndTouchListeners();
        setDefaultFragment();
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
            beginSettingsFragmentTransaction()
                    .add(mSettingsContent.getId(), mSettingsFragment)
                    .commit();
        } else {
            beginSettingsFragmentTransaction()
                    .attach(mSettingsFragment)
                    .commit();
        }
        mSettingsImage.startAnimation(mSettingsImageAnimation.animIn);
    }

    @Override
    public void hideSettingsFragment() {
        mIsSettingsOpen = false;
        if (mSettingsFragment != null) {
            beginSettingsFragmentTransaction()
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

    void setModeFragmentByTag(String tag) {
        detachPreviousModeFragment();
        setNewModeFragmentByTag(tag);
    }

    private void initViews() {
        mContentView = findViewById(R.id.activity_main_screen_constraint_layout_content);
        mWaveBackground = findViewById(R.id.activity_main_screen_relative_layout_wave_background);
        mSettingsImage = (ImageView) findViewById(R.id.partial_toolbar_image_settings);
        mBulbModeButton = findViewById(R.id.activity_main_screen_image_bulb_mode);
        mMusicModeButton = findViewById(R.id.activity_main_screen_image_music_mode);
        mDiscoModeButton = findViewById(R.id.activity_main_screen_image_disco_mode);
        mModeContent = findViewById(R.id.activity_main_screen_frame_layout_mode_content);
        mSettingsContent = findViewById(R.id.activity_main_screen_fragment_settings);
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

    private void setClickAndTouchListeners() {
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
    }

    private void setDefaultFragment() {
        String currentFragmentTag = mPresenter.getFragmentTagFromSettings();
        if (currentFragmentTag == null) {
            currentFragmentTag = BulbModeFragment.TAG;
        }
        switch (currentFragmentTag) {
            case MusicModeFragment.TAG: {
                setDrawable(mMusicModeButton, R.drawable.button_mode_music_disabled);
                mMusicModeButton.setEnabled(false);
                setModeFragmentByTag(MusicModeFragment.TAG);
                break;
            }
            case DiscoModeFragment.TAG: {
                setDrawable(mDiscoModeButton, R.drawable.button_mode_disco_disabled);
                mDiscoModeButton.setEnabled(false);
                setModeFragmentByTag(DiscoModeFragment.TAG);
                break;
            }
            default: {
                setDrawable(mBulbModeButton, R.drawable.button_mode_bulb_disabled);
                mBulbModeButton.setEnabled(false);
                setModeFragmentByTag(BulbModeFragment.TAG);
            }
        }
    }

    private void setDrawable(View view, @DrawableRes int id) {
        ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(view.getContext(), id));
    }

    @SuppressLint("CommitTransaction")
    private FragmentTransaction beginSettingsFragmentTransaction() {
        return getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
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
        String preFragmentTag = getSupportFragmentManager().findFragmentById(mModeContent.getId()).getTag();
        View preView = getViewButtonByFragmentTag(preFragmentTag);
        View postView = getViewButtonByFragmentTag(tag);
        View otherView = (mBulbModeButton != preView && mBulbModeButton != postView)
                ? mBulbModeButton
                : (mMusicModeButton != preView && mMusicModeButton != postView)
                ? mMusicModeButton
                : mDiscoModeButton;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            new ButtonsAnimation.PostLollipop(this, mWaveBackground, mContentView, mModeContent, mSettingsContent, postView, preView, otherView).startAnimation();
        } else {
            new ButtonsAnimation.PreLollipop(this, mModeContent, mSettingsContent, postView, preView, otherView).startAnimation();
        }
    }

    private View getViewButtonByFragmentTag(String tag) {
        return tag.equals(BulbModeFragment.TAG) ?
                mBulbModeButton : tag.equals(MusicModeFragment.TAG) ?
                mMusicModeButton : mDiscoModeButton;
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
                    new BulbModeFragment() : tag.equals(MusicModeFragment.TAG) ?
                    new MusicModeFragment() : tag.equals(DiscoModeFragment.TAG) ?
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
