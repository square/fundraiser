package com.squareup.fundraiser.android;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.widget.TextView;

import java.io.*;

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

  private TextView[] textViews;

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

    // Note: The order matches that in openCsv().
    textViews = new TextView[] {
      name, email, street, city, state, zip, employer, occupation, amount
    };

    // Set up focus navigation around the zip field.
    state.setNextFocusDownId(R.id.zip);
    employer.setNextFocusUpId(R.id.zip);
    zip.setNextFocusUpId(R.id.state);
  }

  private TextView findTextViewById(int id) {
    return (TextView) findViewById(id);
  }

  /**
   * Validates input. Returns null if all input is valid. Otherwise, switches
   * focus to first invalid field and returns an error message.
   */
  private String validate() {
    for (TextView textView : textViews) {
      if (textView.getText().toString().trim().length() == 0) {
        textView.requestFocus();
        return "All fields are required.";
      }
    }

    if (!Email.isValidEmail(email.getText())) {
      email.requestFocus();
      return "Please enter a valid email address.";
    }

    return null;
  }

  /**
   * Opens the CSV file, creating it if necessary.
   */
  private Writer openCsv() throws IOException {
    File csv = new File("/sdcard/donations.csv");
    boolean exists = csv.exists();
    Writer out = new BufferedWriter(new FileWriter(csv), 4096);
    if (!exists)  {
      // Note: This order matches textViews.
      try {
        out.write("Name,Email,Street,City,State,Zip,Employer,Occupation" +
          ",Amount,Status");
      } catch (IOException e) {
        try {
          out.close();
        } catch (IOException e1) { /* ignore */ }
        csv.delete();
        throw e;
      }
    }
    return out;
  }

  /**
   * Appends the field values to the CSV file.
   */
  private void appendTextViews() throws IOException {
    Writer out = openCsv();
    try {
      out.write("\n");
      for (TextView textView : textViews) {
        String escaped = textView.getText().toString().replace("\"", "\"\"");
        out.write("\"" + escaped + "\",");
      }
    } finally {
      out.close();
    }
  }

  /**
   * Appends the result to the CSV file.
   */
  private void appendResult(int result) throws IOException {
    Writer out = openCsv();
    try {
      out.write(result == RESULT_OK ? "OK" : "CANCELED");
    } finally {
      out.close();
    }
  }
}
