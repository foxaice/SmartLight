package me.foxaice.smartlight.activities.redefinition_zones_screen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.redefinition_zones_screen.presenter.IRedefinitionZonesScreenPresenter;
import me.foxaice.smartlight.activities.redefinition_zones_screen.presenter.RedefinitionZonesScreenPresenter;
import me.foxaice.smartlight.activities.redefinition_zones_screen.view.IRedefinitionZonesScreenView;
import me.foxaice.smartlight.activities.redefinition_zones_screen.view.RedefinitionZonesListAdapter;

public class RedefinitionZonesScreenActivity extends AppCompatActivity implements IRedefinitionZonesScreenView {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private IRedefinitionZonesScreenPresenter mPresenter = new RedefinitionZonesScreenPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redefinition_zones_screen);
        mPresenter.attach(this);
        TextView header = (TextView) findViewById(R.id.toolbar_settings_text_header_settings);
        ImageView backArrow = (ImageView) findViewById(R.id.toolbar_settings_image_back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        header.setText(R.string.redefinition_zones);
        final ImageView imageInfo = (ImageView) findViewById(R.id.activity_redefinition_zones_screen_image_info);
        imageInfo.setOnClickListener(new View.OnClickListener() {
            AlertDialog mDialog;

            @Override
            public void onClick(View v) {
                if (mDialog == null) {
                    ImageSpan spanBulbConnect = new ImageSpan(getContext(), R.drawable.bulb_connect);
                    ImageSpan spanBulbDisconnect = new ImageSpan(getContext(), R.drawable.bulb_disconnect);
                    SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.information_text));
                    builder.setSpan(spanBulbConnect, builder.length() - 1, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.append(getString(R.string.or)).setSpan(spanBulbDisconnect, builder.length() - 1, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    mDialog = new AlertDialog.Builder(getContext())
                            .setMessage(builder)
                            .setTitle(R.string.information)
                            .setIcon(R.drawable.settings_image_info_dark)
                            .setPositiveButton(R.string.dialog_positive_button_text, null)
                            .create();
                }
                mDialog.show();
            }
        });
        ListView listView = (ListView) findViewById(R.id.activity_redefinition_zones_screen_list_of_zone_connection);
        listView.setLongClickable(true);
        listView.setAdapter(new RedefinitionZonesListAdapter(this, getRedefinitionZoneNames(mPresenter.getZonesNames()), getRedefinitionZoneDrawablesID()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onListViewItemClick(position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onListViewItemClick(position);
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stopExecutorService();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_trasition, R.anim.slide_out_left);
    }

    @Override
    public Context getContext() {
        return this;
    }

    private int[] getRedefinitionZoneDrawablesID() {
        return new int[]{
                R.drawable.bulb_disconnect,
                R.drawable.bulb_group_1,
                R.drawable.bulb_group_2,
                R.drawable.bulb_group_3,
                R.drawable.bulb_group_4,
        };
    }

    private String[] getRedefinitionZoneNames(String[] zoneNames) {
        if (zoneNames.length < 4) {
            throw new IllegalArgumentException("zone names length must be 4 at least!");
        }
        return new String[]{
                getString(R.string.disconnect_from_zone),
                getString(R.string.connect_to_zone, zoneNames[0]),
                getString(R.string.connect_to_zone, zoneNames[1]),
                getString(R.string.connect_to_zone, zoneNames[2]),
                getString(R.string.connect_to_zone, zoneNames[3])
        };
    }
}
