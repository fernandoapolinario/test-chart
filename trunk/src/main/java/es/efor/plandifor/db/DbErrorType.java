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
 * Broad classification of SQL exception types.
 */
public enum DbErrorType {
    /** Record not found in database. */
    RECORD_NOT_FOUND,
    /** Attempt to insert a duplicate row. */
    DUPLICATE_RECORD,
    /** Cardinality error. */
    CARDINALITY_ERROR,
    /** Database contraint error. */
    CONSTRAINT_ERROR,
    /** Syntax error in the SQL query. */
    SYNTAX_ERROR,
    /** Other database error. */
    DATABASE_ERROR,
    /** Unknown error. */
    UNKNOWN_ERROR;

    /**
     * Classify a {@link Exception} into a broad error category.
     * @param e the exception to classify.
     * @return the appropriate classification.
     */
    public static DbErrorType classifyError(Exception e) {
        if (! (e instanceof SQLException)) {
            return DbErrorType.UNKNOWN_ERROR;
        }
        String type = ((SQLException) e).getSQLState();
        if (type.equals("23503")) {
            return DbErrorType.CONSTRAINT_ERROR;
        } else if (type.equals("23505")) {
            return DbErrorType.DUPLICATE_RECORD;
        } else if (type.equals("42")) {
            return DbErrorType.SYNTAX_ERROR;
        } else {
            return DbErrorType.DATABASE_ERROR;
        }
    }

    /**
     * Prevent instantiation.
     */
    private DbErrorType() {
    }
}
