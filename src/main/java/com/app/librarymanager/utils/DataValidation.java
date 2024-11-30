package com.app.librarymanager.utils;

//import org.apache.commons.validator.routines.*;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.TreeSet;

public class DataValidation {

  private static final String SEP = "(?:\\-|\\s)";
  private static final String GROUP = "(\\d{1,5})";
  private static final String PUBLISHER = "(\\d{1,7})";
  private static final String TITLE = "(\\d{1,6})";

  /**
   * ISBN-10 consists of 4 groups of numbers separated by either dashes (-) or spaces.  The first
   * group is 1-5 characters, second 1-7, third 1-6, and fourth is 1 digit or an X.
   */
  private static final String ISBN10_REGEX =
      "^(?:(\\d{9}[0-9X])|(?:" + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9X])))$";

  /**
   * ISBN-13 consists of 5 groups of numbers separated by either dashes (-) or spaces.  The first
   * group is 978 or 979, the second group is 1-5 characters, third 1-7, fourth 1-6, and fifth is 1
   * digit.
   */
  private static final String ISBN13_REGEX =
      "^(978|979)(?:(\\d{10})|(?:" + SEP + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP
          + "([0-9])))$";

  public static boolean validISBN(String iSBN) {
    if (!iSBN.matches(ISBN10_REGEX) && !iSBN.matches(ISBN13_REGEX)) {
      return false;
    }
    /*
      Remove all hyphen and space ([- ]);
      OR (|) `ISBN` in the first position of the string (^ISBN),
      following with -10 or -13 (optional but matched at most one: ?:-1[0, 3],
      The group (-10, -13) is optional, so we need ? in the end of group (?:-1[03])?;
      ending with deleting :, which is also optional and matched at most one (:?).
     */
    String trimmedISBN = iSBN.replaceAll("[- ]|^ISBN(?:-1[03])?:?", "");

    char expectedLastDigit = getExpectedLastDigit(trimmedISBN);

    return expectedLastDigit == trimmedISBN.charAt(trimmedISBN.length() - 1);
  }

  private static char getExpectedLastDigit(String trimmedISBN) {
    char expectedLastDigit;
    if (trimmedISBN.length() == 13) {
      int valueOfLastDigit = 0;
      for (int i = 0; i + 1 < trimmedISBN.length(); i++) {
        valueOfLastDigit += (i % 2 * 2 + 1) * (trimmedISBN.charAt(i) - '0');
      }
      valueOfLastDigit = (10 - valueOfLastDigit % 10) % 10;
      expectedLastDigit = (char) (valueOfLastDigit + '0');
    } else { // trimmedISBN.length() == 10
      int valueOfLastDigit = 0;
      for (int i = 0; i + 1 < trimmedISBN.length(); i++) {
        valueOfLastDigit += (trimmedISBN.length() - i) * (trimmedISBN.charAt(i) - '0');
      }
      valueOfLastDigit = (11 - valueOfLastDigit % 11) % 11;
      if (valueOfLastDigit == 10) {
        expectedLastDigit = 'X';
      } else {
        expectedLastDigit = (char) (valueOfLastDigit + '0');
      }
    }
    return expectedLastDigit;
  }

  public static String checkInt(String name, String s) {
    try {
      int x = Integer.parseInt(s);
      if (x < 0) {
        throw new Exception("is a negative number");
      }
      return "";
    } catch (Exception e) {
      return "Fail when trying to read " + name + ": " + e.getMessage() + ".";
    }
  }

  public static String checkDouble(String name, String s) {
    try {
      double x = Double.parseDouble(s);
      if (x < 0) {
        throw new Exception("is a negative number");
      }
      return "";
    } catch (Exception e) {
      return "Fail when trying to read " + name + ": " + e.getMessage() + ".";
    }
  }

  public static boolean validEmail(String email) {
    JSONObject js = Fetcher.get("https://verifyemail.vercel.app/api/" + email);
    assert js != null;
    return !js.optString("message", "").equals("not valid email id.");
  }

  public static void main(String[] args) {

  }
}