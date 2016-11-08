package me.foxaice.smartlight.fragments.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.controllers_screen.ControllersScreenActivity;
import me.foxaice.smartlight.activities.redefinition_zones_screen.RedefinitionZonesScreenActivity;
import me.foxaice.smartlight.activities.renaming_zones_screen.RenamingZonesScreenActivity;
import me.foxaice.smartlight.fragments.settings.dialogs.ConnectionSettingsDialog;
import me.foxaice.smartlight.fragments.settings.presenter.ISettingsPresenter;
import me.foxaice.smartlight.fragments.settings.presenter.SettingsPresenter;
import me.foxaice.smartlight.fragments.settings.view.ISettingsView;
import me.foxaice.smartlight.fragments.settings.view.SettingsListAdapter;

public class SettingsFragment extends Fragment implements ISettingsView {
    public static final String TAG = "SETTING_FRAGMENT";
    private ISettingsPresenter mSettingsPresenter = new SettingsPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mSettingsPresenter.attachView(this);
        final String[] itemNames = {
                getResources().getString(R.string.controllers_settings),
                getResources().getString(R.string.redefinition_zones),
                getResources().getString(R.string.renaming_zones),
                getResources().getString(R.string.setting_ip),
                getResources().getString(R.string.setting_port)
        };
        final int[] idIMG = {
                R.drawable.settings_image_router_wireless,
                R.drawable.settings_image_bulb,
                R.drawable.settings_image_pencil,
                R.drawable.settings_image_ip,
                R.drawable.settings_image_port
        };

        SettingsListAdapter adapter = new SettingsListAdapter(this.getContext(), itemNames, idIMG);
        ListView listView = (ListView) view.findViewById(R.id.fragment_settings_list_of_settings);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSettingsPresenter.onListViewItemClick(position);
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSettingsPresenter.detachView();
    }

    @Override
    public void startControllerSettingsScreenActivity() {
        Intent intent = new Intent(getContext(), ControllersScreenActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.no_trasition);
    }

    @Override
    public void startRedefinitionZonesScreenActivity() {
        Intent intent = new Intent(getContext(), RedefinitionZonesScreenActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.no_trasition);
    }

    @Override
    public void startRenamingZonesScreenActivity() {
        Intent intent = new Intent(getContext(), RenamingZonesScreenActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.no_trasition);
    }

    @Override
    public void showSettingIPDialog() {
        DialogFragment dialog = new ConnectionSettingsDialog();
        dialog.show(getFragmentManager(), ConnectionSettingsDialog.TAG_IP);
    }

    @Override
    public void showSettingPortDialog() {
        DialogFragment dialog = new ConnectionSettingsDialog();
        dialog.show(getFragmentManager(), ConnectionSettingsDialog.TAG_PORT);
    }
}
