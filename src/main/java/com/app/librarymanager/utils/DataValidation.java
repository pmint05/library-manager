package com.app.librarymanager.utils;

//import org.apache.commons.validator.routines.*;

public class DataValidation {

  public static boolean validISBN(String iSBN) {
    return true;
  }

  public static boolean validEmail(String email) {
    return true;
  }
}

//  public static void validEmail(String email) {
////    Pattern pattern = Pattern.compile("foo");
////    Matcher matcher = pattern.matcher("foofoo");
//
//    System.out.println(email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9.-]+(.[a-zA-Z]{2,})+$"));
//
//  }
//
//  public static boolean validISBN(String iSBN) {
////    iSBN = iSBN.trim(); // should be handled by input controller
//
//    if (iSBN.matches("N/A")) {
//      return true;
//    }
//
//    /*
//      Explain
//     */
//    if (!iSBN.matches(
//        "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$")) {
//      return false;
//    }
//
//    /*
//      Remove all hyphen and space ([- ]);
//      OR (|) `ISBN` in the first position of the string (^ISBN),
//      following with -10 or -13 (optional but matched at most one: ?:-1[0, 3],
//      The group (-10, -13) is optional, so we need ? in the end of group (?:-1[03])?;
//      ending with deleting :, which is also optional and matched at most one (:?).
//     */
//    String trimmedISBN = iSBN.replaceAll("[- ]|^ISBN(?:-1[03])?:?", "");
//
////    System.out.println(trimmedISBN);
//
//    char expectedLastDigit;
//    if (trimmedISBN.length() == 13) {
//      int valueOfLastDigit = 0;
//      for (int i = 0; i + 1 < trimmedISBN.length(); i++) {
//        valueOfLastDigit += (i % 2 * 2 + 1) * (trimmedISBN.charAt(i) - '0');
//      }
//      valueOfLastDigit = (10 - valueOfLastDigit % 10) % 10;
//      expectedLastDigit = (char) (valueOfLastDigit + '0');
//    } else { // trimmedISBN.length() == 10
//      int valueOfLastDigit = 0;
//      for (int i = 0; i + 1 < trimmedISBN.length(); i++) {
////        System.out.printf("%d ", (trimmedISBN.length() - i));
//        valueOfLastDigit += (trimmedISBN.length() - i) * (trimmedISBN.charAt(i) - '0');
//      }
////      System.out.println();
//      valueOfLastDigit = (11 - valueOfLastDigit % 11) % 11;
////      System.out.println(valueOfLastDigit);
//      if (valueOfLastDigit == 10) {
//        expectedLastDigit = 'X';
//      } else {
//        expectedLastDigit = (char) (valueOfLastDigit + '0');
//      }
//    }
//
//    return expectedLastDigit == trimmedISBN.charAt(trimmedISBN.length() - 1);
//  }
//

