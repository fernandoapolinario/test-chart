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
 * Copyright 2007 Sun Microsystems, Inc.  All rights reserved.
 */

package es.efor.plandifor.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

/**
 * Implement a DataSource that uses {@link MiniConnectionPoolManager} to
 * cache the database connections.
 */
class DbDataSource implements DataSource {

    /**
     * Create a new data source.
     * @param source the Derby data source to use for the pooled connections.
     * @param maxConn the maximum number of connections in the pool.
     * @param timeout the maximum time, in seconds, to wait for a connection to
     * become available.
     */
    DbDataSource(ConnectionPoolDataSource source, int maxConn, int timeout) {
        this.source = source;
        this.pool = new MiniConnectionPoolManager(source, maxConn, timeout);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    
    public Connection getConnection()
      throws SQLException {
        Connection c = pool.getConnection();
        c.setAutoCommit(false);
        return c;
    }

    /**
     * {@inheritDoc}
     * The underlying pool manager does not support this functionality, so if
     * this method is called a {@link SQLException} will be thrown.
     * @param username connection username.
     * @param password connection password.
     * @return never returns, always throws a {@link SQLException}.
     * @throws SQLException always.
     */
    
    public Connection getConnection(String username, String password)
      throws SQLException {
        throw new UnsupportedOperationException(
          "Cannot specify username/password for connections");
    }

    /**
     * {@inheritDoc}
     * @param out {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    
    public void setLogWriter(PrintWriter out)
      throws SQLException {
        source.setLogWriter(out);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    
    public PrintWriter getLogWriter()
      throws SQLException {
        return source.getLogWriter();
    }

    /**
     * {@inheritDoc}
     * @param seconds {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    
    public void setLoginTimeout(int seconds)
      throws SQLException {
        source.setLoginTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    
    public int getLoginTimeout()
      throws SQLException {
        return source.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     * @param iface {@inheritDoc}
     * @return {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    
    public <T> T unwrap(Class<T> iface)
      throws SQLException {
        try {
            return iface.cast(this);
        } catch (ClassCastException e) {
            throw new SQLException("Unable to unwrap " + this.toString() +
              " to " + iface.toString());
        }
    }

    /**
     * {@inheritDoc}
     * @param iface {@inheritDoc}
     * @return {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    
    public boolean isWrapperFor(Class<?> iface)
      throws SQLException {
        return iface.isInstance(this);
    }

    /**
     * Dispose of all the currently unused connections in the pool.
     * @throws SQLException if there is an error.
     */
    public void dispose()
      throws SQLException {
        pool.dispose();
    }

    /** The Derby connection pool data source. */
    private final ConnectionPoolDataSource source;
    /** The connection pool manager. */
    private final MiniConnectionPoolManager pool;
}
