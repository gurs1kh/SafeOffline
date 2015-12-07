package edu.washington.singhm5.saveoffline;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Nabil on 12/6/2015.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testNetworkConnection() {

    }
}
