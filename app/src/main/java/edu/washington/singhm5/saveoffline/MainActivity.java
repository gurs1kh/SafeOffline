package edu.washington.singhm5.saveoffline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.washington.singhm5.saveoffline.Model.Url;

/*
* resource: https://github.com/jonndavis1993/Android-Tutorials/blob/master/app/src/main/java/com/simpleware/jonathan/listviewexample/MainActivity.java
 */
public class MainActivity extends AppCompatActivity {

    MenuItem signInOutMI;

    private List<Url.UrlInfo> mList;
    private static final String getUrl = "http://cssgate.insttech.washington.edu/~singhm5/saveoffline/getUrls.php";
    private static final String delUrl = "http://cssgate.insttech.washington.edu/~singhm5/saveoffline/delUrls.php";
    private ListView mListView;
    private Url mUrl;
    private SwipeRefreshLayout swipeLayout;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
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
            new UserWebTask().execute(getUrl);
        } else {
            mUrl = new Url(this);

            List<Url.UrlInfo> localList;
            localList = mUrl.getAllUrl();


            mListView = (ListView) findViewById(R.id.url_list);
            //mList will represent the final list, after new database is added or deleted
            mListView.setAdapter(new mAdapter(this, R.layout.list_item, localList));
            mListView.setClickable(true);

            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
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
        startActivity(new Intent(MainActivity.this, addUrlActivity.class));
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

        private void deleteUrl(String myurl, String deleteUrl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl + "?id=" + SaveSharedPreference.getUserId(MainActivity.this)
                                + "&url=" + deleteUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                Log.d(TAG, "The string is: " + contentAsString);

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
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            final Activity activity = MainActivity.this;
            mUrl = new Url(activity);
            mList = new ArrayList<>();

            // Parse JSON
            try {
                List<Url.UrlInfo> remoteList = new ArrayList<>();
                List<Url.UrlInfo> localList;

                mList.clear();
                localList = mUrl.getAllUrl();

                JSONArray jsonarray = new JSONArray(s);

                for (int i=0; i<jsonarray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonarray.get(i);
                    String title = (String) jsonObject.get("title");
                    String url = (String) jsonObject.get("url");
                    String reg_date = (String) jsonObject.get("reg_date");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    Date convertedDate = new Date();
                    try {
                        convertedDate = dateFormat.parse(reg_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int mod_date = (int) convertedDate.getTime();
                    remoteList.add(new Url.UrlInfo(title, url, mod_date, 0));
                }

                mListView = (ListView) findViewById(R.id.url_list);

                for (Url.UrlInfo url: localList) {
                    //Check if boolean is true
                    if(url.getDeleteStatus() == 1) {
                        deleteUrl(delUrl, url.getUrl()); //hard delete from server
                        mUrl.deleteUrl(url.getUrl()); //delete from local
                    }
                    Log.d(TAG, "Url Saved:" + url.toString());
                }

                int timelimit = localList.get(localList.size() - 1 ).getModDate();

                for(Url.UrlInfo url: remoteList) {
                    if(url.getModDate() > timelimit) {
                        mUrl.insertUrl(url.getTitle(), url.getUrl());
                    }
                }

                for(Url.UrlInfo url: localList) {
                    if(url.getDeleteStatus() == 0) {
                        mList.add(url);
                    }
                }
                //mList will represent the final list, after new database is added or deleted
                mListView.setAdapter(new mAdapter(activity, R.layout.list_item, mList));
                mListView.setClickable(true);
            }
            catch(Exception e) {
                Log.d(TAG, "Parsing JSON Exception " + e.getMessage());
            }
        }
    }

    private class mAdapter extends ArrayAdapter<Url.UrlInfo> {
        private int layout;
        private List<Url.UrlInfo> mObj;

        private mAdapter(Context context, int resource, List<Url.UrlInfo> obj) {
            super(context, resource, obj);
            layout = resource;
            mObj = obj;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            ViewHolder mViewholder;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sUrl = getItem(pos).getUrl();
                        mUrl.softDelete(sUrl);
                        updateUrlList();
                        Toast.makeText(getContext(), "URL will be deleted on refresh" , Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "List Item" + pos, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(),WebViewerActivity.class);

                        String UrlTitle = getItem(pos).getTitle();
                        String UrlLink = getItem(pos).getUrl();

                        Bundle bundle = new Bundle();
                        bundle.putString("title", UrlTitle);
                        bundle.putString("url", UrlLink);

                        i.putExtras(bundle);

                        startActivity(i);
                    }
                });

                viewHolder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "List Item" + pos, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), WebViewerActivity.class);
                        startActivity(i);
                    }
                });

                convertView.setTag(viewHolder);
            }
            mViewholder = (ViewHolder) convertView.getTag();
            mViewholder.title.setText(getItem(pos).getTitle());
            return convertView;
        }
    }

    public class ViewHolder {

        ImageView thumbnail;
        TextView title;
        Button button;
    }
}
