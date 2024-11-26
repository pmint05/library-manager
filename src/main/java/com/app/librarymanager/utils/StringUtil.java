package com.app.librarymanager.utils;

import org.apache.commons.lang3.text.WordUtils;

public class StringUtil {

  public static String toCapitalize(String s) {
    return WordUtils.capitalizeFully(s.toLowerCase());
  }
}
