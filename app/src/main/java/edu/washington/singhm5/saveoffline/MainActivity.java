package edu.washington.singhm5.saveoffline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MenuItem signInOutMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        signInOutMI = menu.findItem(R.id.action_sign_in_out);
        updateSignInOut();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (signInOutMI != null) {
            updateSignInOut();
        }
    }

    private void updateSignInOut() {
        if(SaveSharedPreference.getUserName(this).length() == 0) {
            signInOutMI.setTitle(getString(R.string.action_sign_in_short));
        } else {
            signInOutMI.setTitle(getString(R.string.action_sign_out));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_in_out) {
            if(SaveSharedPreference.getUserName(this).length() == 0) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                item.setTitle(getString(R.string.action_sign_in_short));
                Toast.makeText(this, SaveSharedPreference.getUserName(this) + " signed out",
                               Toast.LENGTH_SHORT).show();
                SaveSharedPreference.clearUserName(this);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
