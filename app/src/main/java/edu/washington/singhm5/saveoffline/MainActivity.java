package edu.washington.singhm5.saveoffline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent;
        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, UrlListActivity.class);
        }
        startActivity(intent);
    }
}
