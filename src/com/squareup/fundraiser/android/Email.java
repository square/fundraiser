// Copyright 2010 Square, Inc.
package com.squareup.fundraiser.android;

import java.util.regex.Pattern;

/**
 * Validates Email addresses.
 *
 * @author Eric Burke (eric@squareup.com)
 */
public final class Email {

  /**
   * This pattern comes from http://www.regular-expressions.info/email.html.
   */
  private static Pattern EMAIL_PATTERN = Pattern.compile(
      "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
          + "@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
          + "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

  /**
   * Returns true if the given email address looks valid.
   */
  public static boolean isValidEmail(CharSequence email) {
    return EMAIL_PATTERN.matcher(email).matches();
  }

  private Email() {}
}
