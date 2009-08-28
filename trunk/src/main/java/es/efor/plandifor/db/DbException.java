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
 * Copyright 2008 Sun Microsystems, Inc.  All rights reserved.
 */

package es.efor.plandifor.db;

import java.sql.SQLException;

/**
 * Class to wrap the various exceptions types that can be thrown when creating
 * and opening a Derby database.
 */
public class DbException extends Exception {

    /**
     * Create a new DbException, appending the messages of any nested
     * {@link SQLException}s onto the specified message.
     * @param message the error message.
     * @param e the nested exception.
     */
    public DbException(String message, Exception e) {
        super(formatException(message, e, asciiBreak), e);
        this.message = message;
        this.errorType = DbErrorType.classifyError(e);
    }

    /**
     * Create a new DbException.
     * @param message the error message.
     * @param errType the error type.
     */
    public DbException(String message, DbErrorType errType) {
        super(message);
        this.message = message;
        this.errorType = errType;
    }

    /**
     * Return a String describing the exception.
     * @return a String describing the exception.
     */
    @Override
    public String getMessage() {
        return formatException(message, getCause(), "; ");
    }
    
    /**
     * Return a HTML-formatted version of the exception message.
     * @return a HTML-formatted version of the exception message.
     */
    public String getHTMLMessage() {
        return formatException(message, getCause(), "<br>");
    }

    /**
     * Return the error type.
     * @return the error type.
     */
    public DbErrorType getErrorType() {
        return errorType;
    }

    /**
     * Format the messages of any nested {@link SQLException}s and append them
     * to the specified message.
     * @param message the error message.
     * @param t The nested {@link Throwable}.
     * @param separator String used to separate message segments.
     * @return A formatted string containing the message and formatted messages
     * of any nested exceptions.
     */
    private static String formatException(String message, Throwable t,
      String separator) {
        StringBuilder m = new StringBuilder(message);
        m.append(separator);
        if (t instanceof SQLException) {
            SQLException sqle = (SQLException) t;
            StringBuilder em = new StringBuilder();
            em.append(sqle.getLocalizedMessage().trim());
            em.append(" [");
            em.append(sqle.getSQLState().trim());
            em.append("]");
            m.append(em);
            while ((sqle = sqle.getNextException()) != null) {
                m.append(separator);
                em.setLength(0);
                em.append(sqle.getMessage().trim());
                em.append(" [");
                em.append(sqle.getSQLState().trim());
                em.append("]");
                m.append(em);
            }
        } else if (t != null) {
            m.append(t.getMessage().trim());
        }
        return m.toString();
    }

    /** ASCII line break. */
    private static final String asciiBreak =
      System.getProperty("line.separator");

    /** Original message string. */
    private final String message;
    /** Error type. */
    private final DbErrorType errorType;
    /** For serialization. */
    private static final long serialVersionUID = 1;
}
