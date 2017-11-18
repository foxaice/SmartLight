package me.foxaice.smartlight.fragments.modes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import me.foxaice.smartlight.fragments.modes.bulb_mode.view.BulbModeFragment;
import me.foxaice.smartlight.fragments.modes.disco_mode.view.DiscoModeFragment;
import me.foxaice.smartlight.fragments.modes.music_mode.view.MusicModeFragment;

public final class ModeFragmentManager {
    private ModeFragmentManager() {}

    public static void detachFragmentById(AppCompatActivity activity, int containerId) {
        Fragment fragment = findFragmentById(activity, containerId);
        if (fragment != null) detachFragment(activity, fragment);
    }

    public static String getFragmentTag(AppCompatActivity activity, int containerId) {
        Fragment fragment = findFragmentById(activity, containerId);
        return fragment != null ? fragment.getTag() : null;
    }

    public static void attachFragment(AppCompatActivity activity, int containerId, String tag, @Nullable Bundle args) {
        Fragment fragment = findFragmentByTag(activity, tag);
        boolean fragmentIsNotCreated = fragment == null;
        if (fragmentIsNotCreated) {
            fragment = tag.equals(BulbModeFragment.TAG) ?
                    new BulbModeFragment() : tag.equals(MusicModeFragment.TAG) ?
                    new MusicModeFragment() : tag.equals(DiscoModeFragment.TAG) ?
                    new DiscoModeFragment() : null;
            if (fragment == null) {
                throw new IllegalArgumentException("The TAG is NOT found!!!");
            }
        }
        fragment.setArguments(args);

        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (fragmentIsNotCreated) {
            transaction.add(containerId, fragment, tag);
        } else {
            transaction.attach(fragment);
        }
        transaction.commit();
    }

    private static Fragment findFragmentById(AppCompatActivity activity, int id) {
        return activity.getSupportFragmentManager().findFragmentById(id);
    }

    private static Fragment findFragmentByTag(AppCompatActivity activity, String tag) {
        return activity.getSupportFragmentManager().findFragmentByTag(tag);
    }

    private static void detachFragment(AppCompatActivity activity, Fragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .commit();
    }
}
