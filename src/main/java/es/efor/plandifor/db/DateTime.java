/*
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the "License").  You can find a copy
 * of the License in the file CDDL.LICENSE at the root of the source archive
 * containing this file or at http://www.sun.com/cddl/cddl.html.
 * When distributing Covered Code, include this CDDL header in each file and
 * include the License file at the root of your source archive.
 * CDDL HEADER END
 */

/*
 * Copyright 2009 Sun Microsystems, Inc.  All rights reserved.
 */

package es.efor.plandifor.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * SQL date time utilities that use a specified timezone for all operations,
 * which is by default UTC.
 */
public class DateTime {

    /**
     * Set a date column in a prepared statement, using the standard timezone.
     * @param statement the prepared statement.
     * @param index the column index.
     * @param date the date to set the column to.
     * @throws SQLException if there is an error.
     */
    public static void setDate(PreparedStatement statement, int index,
      Date date)
      throws SQLException {
        if (date == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, new java.sql.Date(date.getTime()),
              timezone);
        }
    }

    /**
     * Set a date column in a result set, using the standard timezone.
     * @param result the result set.
     * @param index the column index.
     * @param date the date to set the column to.
     * @throws SQLException if there is an error.
     */
    public static void updateDate(ResultSet result, int index, Date date)
      throws SQLException {
        if (date == null) {
            result.updateNull(index);
        } else {
            // There is no updateDate() method that takes a calendar.
            Calendar cal = (Calendar) timezone.clone();
            cal.setTimeInMillis(date.getTime());
            result.updateDate(index,
              new java.sql.Date(cal.getTime().getTime()));
        }
    }

    /**
     * Get a date column from a result set, using the standard timezone.
     * @param result the result set.
     * @param index the column index.
     * @return the date.
     * @throws SQLException if there is an error.
     */
    public static Date getDate(ResultSet result, int index)
      throws SQLException {
        return result.getDate(index, timezone);
    }

    /**
     * Set a timestamp column in a prepared statement, using the standard
     * timezone.
     * @param statement the prepared statement.
     * @param index the column index.
     * @param date the date to set the column to.
     * @throws SQLException if there is an error.
     */
    public static void setTimestamp(PreparedStatement statement, int index,
      Date date)
      throws SQLException {
        if (date == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index,
              new java.sql.Timestamp(date.getTime()), timezone);
        }
    }

    /**
     * Set a date timestamp in a result set, using the standard timezone.
     * @param result the result set.
     * @param index the column index.
     * @param date the date to set the column to.
     * @throws SQLException if there is an error.
     */
    public static void updateTimestamp(ResultSet result, int index, Date date)
      throws SQLException {
        if (date == null) {
            result.updateNull(index);
        } else {
            // There is no updateDate() method that takes a calendar.
            Calendar cal = (Calendar) timezone.clone();
            cal.setTimeInMillis(date.getTime());
            result.updateTimestamp(index,
              new java.sql.Timestamp(cal.getTime().getTime()));
        }
    }

    /**
     * Get a timestamp column from a result set, using the standard timezone.
     * @param result the result set.
     * @param index the column index.
     * @return the date.
     * @throws SQLException if there is an error.
     */
    public static Date getTimestamp(ResultSet result, int index)
      throws SQLException {
        Timestamp ts = result.getTimestamp(index, timezone);
        return ts == null ? null : new Date(ts.getTime());
    }


    /**
     * Format a date into a string.
     * @param date the date to formatted.
     * @return the date string.
     */
    public static String dateStr(Date date) {
        Calendar cal = (Calendar) timezone.clone();
        cal.setTimeInMillis(date.getTime());
        return String.format("%1$td/%1$tm/%1$tY", cal);
    }


    /**
     * Format a date/time into a string.
     * @param date the date/time to formatted.
     * @return the date/time string.
     */
    public static String dateTimeStr(Date date) {
        Calendar cal = (Calendar) timezone.clone();
        cal.setTimeInMillis(date.getTime());
        return String.format("%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS %1%tZ", cal);
    }

    /**
     * Set the standard timezone to use.  The default is UTC.
     * @param timezone the timezone to use.
     */
    public static void setTimeZone(TimeZone timezone) {
        DateTime.timezone =  Calendar.getInstance(timezone, Locale.ROOT);
    }

    /**
     * Get the calendar containing the database timezone.
     * @return the calendar.
     */
    public static Calendar getCalendar() {
        return (Calendar) timezone.clone();
    }

    /**
     * Prevent instantiation.
     */
    private DateTime() {
    }

    /** Standard timezone. */
    private static Calendar timezone = Calendar.getInstance(
      TimeZone.getTimeZone("UTC"), Locale.ROOT);
}
