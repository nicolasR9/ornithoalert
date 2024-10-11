package com.nirocca.ornithoalert.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class BirdingtoursSpeciesListAnalyzer {
  private static final String FILE = "/Users/nicolas.rocca/tmp/HAV 02 2024 Artenliste (1).csv";

  public static void main(String[] args) throws IOException {
    //analyze();
    printSpeciesForDay(2);
  }

  private static void printSpeciesForDay(int day) throws IOException {
    List<String> lines = IOUtils.readLines(new BufferedInputStream(new FileInputStream(FILE)), Charset.defaultCharset());
    lines.remove(0); // headers
    for (String line : lines) {
      String[] parts = line.split(";");
      if (parts.length < 1) continue;
      String species = parts[0];
      List<Integer> days = getDays(parts);
      if (days.contains(day)) {
        System.out.println("\"" + species + "\",");
      }
    }
  }

  private static void analyze() throws IOException {
    List<String> lines = IOUtils.readLines(new BufferedInputStream(new FileInputStream(FILE)), Charset.defaultCharset());
    lines.remove(0); // headers
    int total = 0;
    for (String line : lines) {
      String[] parts = line.split(";");
      if (parts.length < 1) continue;
      String species = parts[0];
      List<Integer> days = getDays(parts);
      if (!days.isEmpty()) {
        ++total;
        System.out.print(species + " (");
        for (int i = 0; i < days.size(); i++) {
          System.out.print(days.get(i));
          if (i != days.size() - 1) {
            System.out.print(", ");
          }
        }
        System.out.println(")");
      }
    }
    System.out.println("Insgesamt: " + total);
  }

  private static List<Integer> getDays(String[] parts) {
    List<Integer> days = new ArrayList<>();
    for (int i = 1; i < parts.length; i++) {
      if ("x".equalsIgnoreCase(parts[i])) days.add(i);
    }
    return days;
  }
}
