package edu.washington.singhm5.saveoffline;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MenuItem signInOutMI;

    private List<Url.UrlInfo> mList;
    private static final String url = "http://cssgate.insttech.washington.edu/~singhm5/saveoffline/getUrls.php";
    private ListView mListView;
    private ArrayAdapter<Url.UrlInfo> mAdapter;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        /*swipeLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUrlList();
                swipeLayout.setRefreshing(false);
            }
        });
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
        updateUrlList();
    }

    private void updateUrlList() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new UserWebTask().execute(url);
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }

        mListView = (ListView) findViewById(R.id.url_list);
        mList = Url.ITEMS;
        mAdapter = new ArrayAdapter<Url.UrlInfo>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mList);
    }

    private void updateSignInOut() {
        if(SaveSharedPreference.getUserName(this).equals("")) {
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
            if(SaveSharedPreference.getUserName(this).equals("")) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                item.setTitle(getString(R.string.action_sign_in_short));
                Toast.makeText(this, SaveSharedPreference.getUserName(this) + " signed out",
                               Toast.LENGTH_SHORT).show();
                SaveSharedPreference.clearUser(this);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void addUrlActivity(View view) {
        startActivity(new Intent(MainActivity.this, AddUrlActivity.class));
    }

    private class UserWebTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "UserWebTask";

        @Override
        protected String doInBackground(String...urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl + "?id=" + SaveSharedPreference.getUserId(MainActivity.this));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                Log.d(TAG, "The string is: " + contentAsString);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch(Exception e ) {
                Log.d(TAG, "Something happened" + e.getMessage());
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
            return null;
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                mList.clear();
                Url.ITEMS.clear();

                JSONArray jsonarray = new JSONArray(s);
                for (int i=0; i<jsonarray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonarray.get(i);
                    String title = (String) jsonObject.get("title");
                    String url = (String) jsonObject.get("url");
                    Url.ITEMS.add(new Url.UrlInfo(title, url));

                }
                mList = Url.ITEMS;
                mListView.setAdapter(mAdapter);
            }
            catch(Exception e) {
                Log.d(TAG, "Parsing JSON Exception " + e.getMessage());
            }
        }
    }
}
