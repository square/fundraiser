package com.squareup.fundraiser.android;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Collects the following information:
 *
 * <ol>
 *  <li>Name</li>
 *  <li>Email</li>
 *  <li>Employer</li>
 *  <li>Occupation</li>
 *  <li>Address (street, city, state, zip)</li>
 *  <li>Eligibility (yes, no)</li>
 *  <li>Donation amount</li>
 *  <li>Outcome</li>
 * </ol>
 */
public class Main extends Activity {

  private TextView name;
  private TextView email;
  private TextView street;
  private TextView city;
  private TextView state;
  private TextView zip;
  private TextView employer;
  private TextView occupation;
  private TextView amount;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFormat(PixelFormat.RGBA_8888);

    setContentView(R.layout.main);

    name = findTextViewById(R.id.name);
    email = findTextViewById(R.id.email);
    street = findTextViewById(R.id.street);
    city = findTextViewById(R.id.city);
    state = findTextViewById(R.id.state);
    zip = findTextViewById(R.id.zip);
    employer = findTextViewById(R.id.employer);
    occupation = findTextViewById(R.id.occupation);
    amount = findTextViewById(R.id.amount);

    // Set up focus navigation around the zip field.
    state.setNextFocusDownId(R.id.zip);
    employer.setNextFocusUpId(R.id.zip);
    zip.setNextFocusUpId(R.id.state);
  }

  private TextView findTextViewById(int id) {
    return (TextView) findViewById(id);
  }

  
}
