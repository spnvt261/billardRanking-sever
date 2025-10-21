package com.billard.BillardRankings.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getCurrentDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.now().format(formatter);
    }

    public static void main(String[] args) {
        System.out.println(getCurrentDateString()); // Ví dụ: 21/10/2025
    }
}
