package edu.washington.singhm5.saveoffline;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * Created by Nabil on 12/6/2015.
 */
public class addUrlActivityTest extends ActivityInstrumentationTestCase2<addUrlActivity> {
    private Solo solo;

    public addUrlActivityTest() {
        super(addUrlActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testAddUrl() {
        solo.enterText(R.id.title_input, "Google home page");
        solo.enterText(R.id.url_input, "http://www.google.com");
        solo.clickOnButton(R.id.add_button);
        boolean textFound = solo.searchText("storage");
        assertTrue("URL add success", textFound);
    }

    public void testFailAddUrl() {
        solo.enterText(R.id.title_input, "Google Home page");
        solo.enterText(R.id.url_input, "");
        solo.clickOnButton(R.id.add_button);
        boolean textFound = solo.searchText("failed");
        assertTrue("URL cant be blank", textFound);
    }
}
