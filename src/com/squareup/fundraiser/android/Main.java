package com.squareup.fundraiser.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.squareup.android.*;

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

  private static final String TAG = "SquareFundraiser";

  private static final int VALIDATION_DIALOG = 0;
  private static final int IO_ERROR_DIALOG = 1;
  private static final int CLEAR_DIALOG = 2;

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

    findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        pay();
      }
    });

    findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(CLEAR_DIALOG);
      }
    });
  }

  /** Handles pay button. */
  private void pay() {
    if (validate() != null) {
      showDialog(VALIDATION_DIALOG);
      return;
    }

    try {
      appendTextViews();
    } catch (IOException e) {
      Log.e(TAG, "Error writing CSV.", e);
      showDialog(IO_ERROR_DIALOG);
      return;
    }

    Square square = new Square(this);
    LineItem donation = new LineItem.Builder()
      .description("Contribution from " + name.getText())
      .price(new Money(0, Currency.USD))
      .build();
    square.squareUp(Bill.containing(donation));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    try {
      appendResult(resultCode);
    } catch (IOException e) {
      Log.e(TAG, "Error writing CSV.", e);
      showDialog(IO_ERROR_DIALOG);
    }
  }

  @Override protected Dialog onCreateDialog(int id) {
    switch (id) {
      case VALIDATION_DIALOG:
        return new AlertDialog.Builder(this)
          .setCancelable(true)
          .setTitle(validate())
          .setMessage("Please try again.")
          .setNegativeButton("Dismiss", null)
          .create();
      case CLEAR_DIALOG:
        return new AlertDialog.Builder(this)
          .setCancelable(true)
          .setTitle("Are you sure?")
          .setPositiveButton("Dismiss", null)
          .setNegativeButton("Clear", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              Intent intent = new Intent(Main.this, Main.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
            }
          })
          .create();
      case IO_ERROR_DIALOG:
        return new AlertDialog.Builder(this)
          .setCancelable(true)
          .setTitle("I/O Error")
          .setMessage("Please check your SD card and try again.")
          .setNegativeButton("Dismiss", null)
          .create();
    }

    throw new AssertionError();
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
        return "Missing Values";
      }
    }

    if (!Email.isValidEmail(email.getText())) {
      email.requestFocus();
      return "Invalid Email Address";
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
