package me.foxaice.smartlight.activities.renaming_zones_screen.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.activities.renaming_zones_screen.presenter.IRenamingZonesScreenPresenter;
import me.foxaice.smartlight.activities.renaming_zones_screen.presenter.RenamingZonesScreenPresenter;

public class RenamingZonesScreenActivity extends AppCompatActivity implements IRenamingZonesScreenView {
    private IRenamingZonesScreenPresenter mPresenter = new RenamingZonesScreenPresenter();
    private String[] mNamesOfZones;
    private RenamingZonesListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renaming_zones_screen);
        mPresenter.attach(this);
        initHeader();
        initBackArrow();
        initListView();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.toolbar_settings_text_header_settings)).setText(R.string.redefinition_zones);
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

    private void initListView() {
        mNamesOfZones = mPresenter.getZonesNames();
        mAdapter = new RenamingZonesListAdapter(this, mNamesOfZones);
        ListView listView = (ListView) findViewById(R.id.activity_redefinition_zones_screen_list_of_zone_connection);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new ItemClickListener());
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

    private void saveZoneName(int position, String nameOfZone) {
        if (nameOfZone == null || nameOfZone.length() == 0) {
            nameOfZone = mAdapter.getDefaultNames()[position];
        }
        mPresenter.saveZoneName(position, nameOfZone);
        updateZoneName(position);
    }

    private void updateZoneName(int position) {
        mNamesOfZones[position] = mPresenter.getZoneName(position);
        mAdapter.notifyDataSetChanged();
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        private EditText mEditText;
        private TextView mTextView;
        private AlertDialog mDialog;
        private int mPosition;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mDialog == null) {
                createDialog();
            }
            RenamingZonesListAdapter.ViewHolder holder = (RenamingZonesListAdapter.ViewHolder) view.getTag();
            mPosition = position;

            mTextView.setText(getString(R.string.name_of_zone, holder.header.getText()));
            mEditText.setText(holder.subHeader.getText());
            mEditText.setSelection(mEditText.length());
            showKeyboard();
            mDialog.show();
        }

        private void createDialog() {
            LinearLayout root = new LinearLayout(getContext());
            root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            root.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParams.bottomMargin = 5;
            textViewParams.topMargin = 5;
            mTextView = new TextView(getContext());
            mTextView.setLayoutParams(textViewParams);
            mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.toolbar));
            mTextView.setTextSize(21);
            mTextView.setGravity(Gravity.CENTER);

            ImageView separator = new ImageView(getContext());
            separator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
            separator.setBackgroundColor(Color.parseColor("#007e94"));

            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            editTextParams.setMargins(4, 16, 4, 4);
            mEditText = new EditText(getContext());
            mEditText.setLayoutParams(editTextParams);
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            mEditText.setFocusable(true);
            mEditText.setHint(R.string.hint_edit_name);

            root.addView(mTextView);
            root.addView(separator);
            root.addView(mEditText);

            mDialog = new AlertDialog.Builder(getContext())
                    .setIcon(R.drawable.settings_image_pencil2)
                    .setView(root)
                    .setPositiveButton(R.string.dialog_positive_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveZoneName(mPosition, mEditText.getText().toString());
                        }
                    })
                    .setNeutralButton(R.string.dialog_neutral_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveZoneName(mPosition, null);
                        }
                    })
                    .setNegativeButton(R.string.dialog_negative_button_text, null)
                    .create();
        }

        private void showKeyboard() {
            if (mDialog != null && mDialog.getWindow() != null) {
                mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        }
    }
}
