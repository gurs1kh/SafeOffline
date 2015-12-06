package edu.washington.singhm5.saveoffline;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.washington.singhm5.saveoffline.Model.Url;

public class addUrlActivity extends AppCompatActivity {

    private static final String TAG = "URL class";
    private String url = "http://cssgate.insttech.washington.edu/~singhm5/saveoffline/addUrl.php";
    private Url mUrlDB;

    private EditText mTitleInput;
    private EditText mUrlInput;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_url, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_url);

        mTitleInput = (EditText) findViewById(R.id.title_input);
        mUrlInput = (EditText) findViewById(R.id.url_input);
        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAndStore()) {
                    String title = mTitleInput.getText().toString();
                    String downloadUrl = mUrlInput.getText().toString();
                    url += "?id=" + SaveSharedPreference.getUserId(addUrlActivity.this)
                            + "&title=" + title
                            + "&url=" + downloadUrl;
                    new  AddUserWebTask().execute(url, downloadUrl, title);
                }
            }
        });
    }

    private boolean validateAndStore() {
        Activity activity = addUrlActivity.this;
        EditText title = (EditText) activity.findViewById(R.id.title_input);
        if(title.getText().length() == 0) {
            Toast.makeText(activity, "Please enter title", Toast.LENGTH_LONG).show();
            title.requestFocus();
            return false;
        }

        EditText url = (EditText) activity.findViewById(R.id.url_input);
        if(title.getText().length() == 0) {
            Toast.makeText(activity, "Please enter url", Toast.LENGTH_LONG).show();
            url.requestFocus();
            return false;
        }

        //store to file
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    activity.openFileOutput(getString(R.string.add_url), Context.MODE_PRIVATE));
            outputStreamWriter.write("title=" + title.getText().toString() + ";");
            outputStreamWriter.write("url=" + url.getText().toString() + ";");
            outputStreamWriter.close();
            Toast.makeText(activity, "Stored in File successfully", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        //Store Locally
        mUrlDB = new Url(activity);
        if(mUrlDB.insertUrl(title.getText().toString(), url.getText().toString())) {
            Log.d(TAG, "Url created" + mUrlDB.toString());
            Toast.makeText(activity, "Added URL to Local Database", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    private class AddUserWebTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "AddUserWebTask";

        @Override
        protected String doInBackground(String...args) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(args[0], args[1], args[3]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl, String downloadUrl, String title) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
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
            Log.i("FILEDIR",  getFilesDir().getAbsolutePath());
//            WebView webView = new WebView(addUrlActivity.this);
//            webView.loadUrl(downloadUrl);
//            webView.saveWebArchive(getFilesDir().getAbsolutePath()
//                    + File.separator + title + ".xml");
            return null;
        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Parse JSON
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("result");
                if (status.equalsIgnoreCase("success")) {
                    Toast.makeText(addUrlActivity.this, "URL successfully inserted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String reason = jsonObject.getString("error");
                    Toast.makeText(addUrlActivity.this, "Failed :" + reason, Toast.LENGTH_SHORT).show();
                }

                getFragmentManager().popBackStackImmediate();
            }
            catch(Exception e) {
                Log.d(TAG, "Parsing JSON Exception " + e.getMessage());
            }
        }
    }
}
