package me.foxaice.smartlight.activities.redefinition_zones_screen.view;

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
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.redefinition_zones_screen.presenter.IRedefinitionZonesScreenPresenter;
import me.foxaice.smartlight.activities.redefinition_zones_screen.presenter.RedefinitionZonesScreenPresenter;

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
        initHeader();
        initBackArrow();
        initImageInfo();
        initListView();
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

    private void initHeader() {
        ((TextView) findViewById(R.id.toolbar_settings_text_header_settings))
                .setText(R.string.redefinition_zones);
    }

    private void initBackArrow() {
        findViewById(R.id.toolbar_settings_image_back_arrow)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    private void initImageInfo() {
        findViewById(R.id.activity_redefinition_zones_screen_image_info)
                .setOnClickListener(new View.OnClickListener() {
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
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.activity_redefinition_zones_screen_list_of_zone_connection);
        listView.setLongClickable(true);
        listView.setAdapter(new RedefinitionZonesListAdapter(this,
                RedefinitionZonesScreenActivity.getRedefinitionZoneNames(this, mPresenter.getZonesNames()),
                RedefinitionZonesScreenActivity.getRedefinitionZoneDrawablesID()));
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

    private static String[] getRedefinitionZoneNames(Context context, String[] zoneNames) {
        if (zoneNames.length < ZONES_QUANTITY) {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "zone names must be %s at least!", ZONES_QUANTITY));
        }
        return new String[]{
                context.getString(R.string.disconnect_from_zone),
                context.getString(R.string.connect_to_zone, zoneNames[0]),
                context.getString(R.string.connect_to_zone, zoneNames[1]),
                context.getString(R.string.connect_to_zone, zoneNames[2]),
                context.getString(R.string.connect_to_zone, zoneNames[3])
        };
    }

    private static int[] getRedefinitionZoneDrawablesID() {
        return new int[]{
                R.drawable.bulb_disconnect,
                R.drawable.bulb_group_1,
                R.drawable.bulb_group_2,
                R.drawable.bulb_group_3,
                R.drawable.bulb_group_4,
        };
    }
}
