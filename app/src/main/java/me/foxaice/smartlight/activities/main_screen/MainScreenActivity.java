package me.foxaice.smartlight.activities.main_screen;

import android.os.Bundle;
import android.app.Activity;

import me.foxaice.smartlight.R;

public class MainScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
