package com.squareup.fundraiser.android;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;

/**
 * Collects the following information:
 *
 * <ol>
 *   <li>Name</li>
 *   <li>Email</li>
 *   <li>Employer</li>
 *   <li>Occupation</li>
 *   <li>Address (street, city, state, zip)</li>
 *   <li>Eligibility (yes, no)</li>
 *   <li>Donation amount</li>
 *   <li>Outcome</li>
 * </ol>
 */
public class Main extends Activity {


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getWindow().setFormat(PixelFormat.RGBA_8888);
    }
}
