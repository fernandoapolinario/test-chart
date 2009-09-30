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

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Enumeration;
import javax.sql.*;
import java.util.Properties;
import org.apache.derby.drda.*;
import org.apache.derby.jdbc.*;
import org.apache.derby.tools.*;

/**
 * This class wraps the <a href="http://db.apache.org/derby/">Derby</a> database
 * so that it is easy to start Derby and create a new database.  It can be used
 * to run Derby in one of three modes:
 * <ul>
 * <li>{@link DbMode#EMBEDDED}.  The database runs embedded within the invoking
 * JVM and is not externally accessible.</li>
 * <li>{@link DbMode#SERVER_EMBEDDED}.  The database runs within the invoking
 * JVM as a network server and is externally accessible while the host JVM is
 * running.</li>
 * <li>{@link DbMode#SERVER_DAEMON}.  The database runs in a separate JVM as a
 * network server and is externally accessible.  If the JVM that started the
 * server exits, the Derby server will continue to run unless it is explicitly
 * shut down.</li>
 * </ul>
 * In all the above cases, if the database does not exist when Derby is started,
 * it will be created.  During creation the following three SQL scripts will be
 * executed to create and populate the database, all held in the /sql directory
 * in the library JAR file:
 * <ul>
 * <li>{@link #CONFIGURE_DB_SQL}.  Configure the database, e.g. create users and
 * schemas, set permissions.</li>
 * <li>{@link #CREATE_DB_SQL}.  Create the database tables and indexes.</li>
 * <li>{@link #POPULATE_DB_SQL}.  Load any default values into the tables.</li>
 * </ul>
 */
public class DbManager {

    /**
     * Create a new DbManager instance.  If the mode is
     * {@link DbMode#SERVER_EMBEDDED} or {@link DbMode#SERVER_DAEMON}, the
     * server will listen only on localhost, and on the default port (1527).
     * @param dbRoot the Derby system directory.
     * @param dbMode the database's mode.
     * @param dbName the name of the database.
     * @param dbUser the database primary user.
     * @param dbPass the password of the database primary user.
     * @param configureDbSql SQL to configure the database.
     * @param createDbSql SQL to create the databaase.
     * @param populateDbSql SQL to populate the database.
     */
    public DbManager(String dbRoot, DbMode dbMode, String dbName, String dbUser,
      String dbPass, String configureDbSql, String createDbSql,
      String populateDbSql) {
        this.dbRoot = dbRoot;
        this.dbMode = dbMode;
        this.dbAddress = DEFAULT_ADDRESS;
        this.dbPort = DEFAULT_PORT;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.configureDbSql = configureDbSql;
        this.createDbSql = createDbSql;
        this.populateDbSql = populateDbSql;
        this.driver = null;
        this.daemon = null;
    }

    /**
     * Create a new DbManager instance, listening on the specified address and
     * port.  Note that only the modes {@link DbMode#SERVER_EMBEDDED} and
     * {@link DbMode#SERVER_DAEMON} are valid when specifying an address and
     * port, as in {@link DbMode#EMBEDDED} mode no networking is used. The
     * special address {@link #ALL_INTERFACES} (0.0.0.0) can be used to listen
     * on all available interfaces.
     * @param dbRoot the Derby system directory.
     * @param dbMode the database's mode.
     * @param dbAddress address the database server should listen on.
     * @param dbPort port the database server shouls listen on.
     * @param dbName the name of the database.
     * @param dbUser the database primary user.
     * @param dbPass the password of the database primary user.
     * @param configureDbSql SQL to configure the database.
     * @param createDbSql SQL to create the databaase.
     * @param populateDbSql SQL to populate the database.
     */
    public DbManager(String dbRoot, DbMode dbMode, String dbAddress, int dbPort,
      String dbName, String dbUser, String dbPass, String configureDbSql,
      String createDbSql, String populateDbSql) {
        if (dbMode == DbMode.EMBEDDED) {
            throw new IllegalArgumentException(
              "Cannot specify an address/port in embedded mode");
        }
        this.dbRoot = dbRoot;
        this.dbMode = dbMode;
        this.dbAddress = dbAddress;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.configureDbSql = configureDbSql;
        this.createDbSql = createDbSql;
        this.populateDbSql = populateDbSql;
        this.driver = null;
        this.daemon = null;
    }

    /**
     * Connect to the database.  If the database is not already running, it will
     * be started, and if the database does not exist it will be created.
     * @return a {@link Connection} to the database.
     * @throws DbException if the database can't be connected to.
     */
    public Connection connect()
      throws DbException {
        setupEnvironment();
        loadDriver();
        if (dbMode != DbMode.EMBEDDED) {
            startServerIfStopped();
        }
        Connection c = openDatabase();
        if (c == null) {
            c = createDatabase();
        }
        return c;
    }

    /**
     * Shut down the database, if running in server mode.
     * In {@link DbMode#EMBEDDED} mode this will have no effect.
     * @throws DbException if the database can't be shut down.
     */
    public void shutdown()
      throws DbException {
        if (driver != null) {
            closeDatabase();
        }
        stopServerIfStarted();
        if (driver != null) {
            unloadDriver();
        }
    }

    /**
     * Return the Derby system directory.
     * @return the Derby system directory.
     */
    public String getDbRoot() {
        return dbRoot;
    }

    /**
     * Return the database's mode.
     * @return the database's mode.
     */
    public DbMode getDbMode() {
        return dbMode;
    }

    /**
     * Return the address the database server is listening on.
     * @return the address the database server is listening on.
     */
    public String getDbAddress() {
        return dbAddress;
    }

    /**
     * Return the port the database server is listening on.
     * @return the port the database server is listening on.
     */
    public int getDbPort() {
        return dbPort;
    }

    /**
     * Return the database's name.
     * @return the database's name.
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Return the database primary user.
     * @return the database primary user.
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Return the password of database primary user.
     * @return the password of database primary user.
     */
    public String getDbPass() {
        return dbPass;
    }

    /**
     * Get a handle to the SQL used to create the database.
     * @return a handle to the SQL used to create the database.
     */
    public BufferedReader getDbCreateSql() {
        return new BufferedReader(new InputStreamReader(
          DbManager.class.getResourceAsStream(createDbSql)));
    }

    /**
     * Return a new {@link DataSource} that caches {@link Connection}s.
     * @param maxConn maximum number of {@link Connection}s allowed.
     * @param timeout the maximum time to wait for a free {@link Connection}.
     * @return the new {@link DataSource}.
     * @throws SQLException if the {@link DataSource} can't be created.
     */
    public DataSource getDataSource(int maxConn, int timeout)
      throws SQLException {
        ConnectionPoolDataSource source;
        if (dbMode == DbMode.SERVER_DAEMON) {
            ClientConnectionPoolDataSource s =
              new ClientConnectionPoolDataSource();
            s.setDatabaseName(dbName);
            s.setUser(dbUser);
            s.setPassword(dbPass);
            s.setConnectionAttributes("securityMechanism=8");
            source = s;
        } else {
            EmbeddedConnectionPoolDataSource s =
              new EmbeddedConnectionPoolDataSource();
            s.setDatabaseName(dbName);
            s.setUser(dbUser);
            s.setPassword(dbPass);
            source = s;
        }
        source.setLoginTimeout(timeout);
        return new DbDataSource(source, maxConn, timeout);
    }

    /**
     * Dispose of any unused connections in a data source obtained from
     * {@link #getDataSource(int, int)}.
     * @param dataSource the data source to dispose.
     * @throws SQLException if the data source was not obtained from
     * {@link #getDataSource(int, int)}, or if there is an error disposing of
     * it.
     */
    public void disposeDataSource(DataSource dataSource)
      throws SQLException {
        dataSource.unwrap(DbDataSource.class).dispose();
    }

    /**
     * Utility method - Quotes the string provided.
     * @param string the string to quote.
     * @return the quoted string.
     */
    public static String quote(String string) {
        return string.replaceAll(ESCAPE_REGEX, ESCAPE_REPLACEMENT);
    }

    /**
     * Utility method - perform a database commit, wrapping any thrown
     * exceptions in a {@link DbException}.
     * @param c The database connection.
     * @throws DbException if the commit fails.
     */
    public static void commit(Connection c)
      throws DbException {
        try {
            c.commit();
        } catch (SQLException e) {
            throw new DbException("Commit failed", e);
        }
    }

    /**
     * Utility method - perform a database commit, ignoring any exceptions
     * @param c The database connection.
     */
    public static void forceCommit(Connection c) {
        try {
            c.commit();
        } catch (SQLException e) {
            // Ignore
        }
    }

    /**
     * Utility method - perform a database rollback, but only if the passed
     * database is non-null.  Any exceptions are silently ignored.
     * @param c The database connection.
     */
    public static void rollback(Connection c) {
        if (c == null) {
            return;
        }
        try {
            c.rollback();
        } catch (SQLException e) {
            ;
        }
    }

    /**
     * Utility method - close a database connection, but only if it is non-null.
     * Any exceptions thrown are silently ignored.
     * @param c The database connection.
     */
    public static void close(Connection c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (SQLException e) {
            ;
        }
    }
    
    /**
     * Utility method - close a statement, but only if it is non-null.
     * Any exceptions thrown are silently ignored.
     * @param s statement to close.
     */
    public static void close(Statement s) {
        if (s == null) {
            return;
        }
        try {
            s.close();
        } catch (SQLException e) {
            ;
        }
    }

    /**
     * Utility method - close a result set, but only if it is non-null.
     * Any exceptions thrown are silently ignored.
     * @param rs the result set to close.
     */
    public static void close(ResultSet rs) {
        if (rs == null) {
            return;
        }
        try {
            rs.close();
        } catch (SQLException e) {
            ;
        }
    }
    /**
     * Although this method is public, it should not be called directly by the
     * application.  When the database is run in {@link DbMode#SERVER_DAEMON}
     * mode, this method is used to start up the daemon JVM.
     * @param arg Command-line arguments.
     */
    public static void main(final String[] arg) {
        if (arg.length != 3) {
            System.out.println("Invalid arguments");
            System.exit(2);
        }
        try {
            // Create a manager.
            final DbManager derby = new DbManager(
              arg[0], DbMode.SERVER_EMBEDDED, arg[1], Integer.parseInt(arg[2]),
              null, null, null, null, null, null);

            // Add a shutdownDbServer hook to stop the server.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        derby.shutdown();
                    } catch (DbException e) {
                        // Ignore.
                    }
                }
            });

            // Start up the server.
            derby.startServerIfStopped();

            // Close standard streams and wait.
            System.in.close();
            System.out.close();
            System.err.close();
            byte[] w = new byte[0];
            synchronized (w) {
                w.wait();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /**
     * Set up the JVM properties used by Derby.
     */
    protected void setupEnvironment() {
        // Set the Derby system directory & security mode.
        System.setProperty("derby.system.home", dbRoot);
        System.setProperty("derby.drda.securityMechanism",
          "STRONG_PASSWORD_SUBSTITUTE_SECURITY");
    }

    /**
     * Load the appropriate Derby JDBC driver - if the mode is
     * {@link DbMode#EMBEDDED}, load the embedded driver, otherwise load the
     * client driver.
     * @throws DbException if the driver can't be loaded.
     */
    protected void loadDriver()
      throws DbException {
        // Load the appropriate Derby driver.
        setupEnvironment();
        try {
            driver = (Driver) Class.forName(dbMode == DbMode.SERVER_DAEMON
              ? "org.apache.derby.jdbc.ClientDriver"
              : "org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        } catch (ClassNotFoundException e) {
            throw new DbException("Failed to load database driver", e);
        } catch (InstantiationException e) {
            throw new DbException("Failed to instantiate database driver", e);
        } catch (IllegalAccessException e) {
            throw new DbException("Failed to access database driver", e);
        }
    }

    /**
     * Unload the Derby JDBC driver.
     * @throws DbException if the driver can't be unloaded.
     */
    protected void unloadDriver()
      throws DbException {
        setupEnvironment();
        try {
            DriverManager.deregisterDriver(driver);

            // XXX Workaround for DERBY-2905.
            Enumeration<Driver> ds = DriverManager.getDrivers();
            while (ds.hasMoreElements()) {
                Driver d = ds.nextElement();
                if (d.getClass().getClassLoader() ==
                  getClass().getClassLoader()) {
                    DriverManager.deregisterDriver(d);
                }
            }
        } catch (SQLException e) {
            throw new DbException("Failed to unload database driver", e);
        } finally {
            driver = null;
        }
    }

    /**
     * Return true if the database is running.
     * In {@link DbMode#EMBEDDED} mode, always return true.
     * @return true if the database is running.
     * @throws DbException if the database status can't be determined.
     */
    protected boolean isRunning()
      throws DbException {
        // The database is always running when in embedded mode.
        if (dbMode == DbMode.EMBEDDED) {
            return true;
        }

        // Try pinging the server.
        setupEnvironment();
        try {
            NetworkServerControl derby = new NetworkServerControl(
              InetAddress.getByName(dbAddress), dbPort);
            try {
                derby.ping();
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            throw new DbException("Can't create database manager", e);
        }
    }

    /**
     * Start up the server in the required mode, if it isn't already running.
     * Only valid if the database mode is {@link DbMode#SERVER_EMBEDDED} or
     * DbMode#SERVER_DAEMON}.
     * @throws DbException if the mode is {@link DbMode#EMBEDDED} or if the
     * server can't be started.
     */
    protected void startServerIfStopped()
      throws DbException {
        // Check we are in a mode that requires a server.
        if (dbMode == DbMode.EMBEDDED) {
            throw new DbException("Illegal mode for database start (EMBEDDED)",
              DbErrorType.DATABASE_ERROR);
        }

        // If the server is already running, there is nothing to do.
        if (isRunning()) {
            return;
        }

        // Otherwise, start the server.
        boolean running = false;
        try {
            if (dbMode == DbMode.SERVER_DAEMON) {
                daemon = Runtime.getRuntime().exec(new String[] {
                    "java", "-classpath",
                    System.getProperty("java.class.path"),
                    DbManager.class.getName(),
                    dbRoot, dbAddress, Integer.toString(dbPort)
                });
            } else {
                NetworkServerControl derby = new NetworkServerControl(
                  InetAddress.getByName(dbAddress), dbPort);
                derby.start(null);
            }

            // Wait for the server to start (63.5 seconds maximum).
            int pause = 500;
            int loop = 7;
            do {
                Thread.sleep(pause);
                pause *= 2;
                loop--;
            } while (! (running = isRunning()) && loop > 0);
        } catch (Exception e) {
            throw new DbException("Failed to start database server", e);
        }
        if (! running) {
            throw new DbException("Failed to start database server",
              DbErrorType.DATABASE_ERROR);
        }
    }

    /**
     * Stop up the server if it is running.
     * @throws DbException if the server can't be stopped.
     */
    protected void stopServerIfStarted()
      throws DbException {

        // Stop the database.
        boolean running = true;
        try {
            Properties props = getDbProperties();
            props.put("shutdown", "true");
            Connection c = DriverManager.getConnection("jdbc:derby:", props);
            c.close();
        } catch (SQLException e) {
            String state = e.getSQLState();
            // Ignore "System shutdown" and "Successful shutdown" error.
            if (! state.equals("XJ015")) {
                throw new DbException("Failed to shutdown database", e);
            }
        }

        // If in embedded mode, of if the server is already stopped,
        // there is nothing more to do.
        if (dbMode == DbMode.EMBEDDED || ! isRunning()) {
            return;
        }

        // Stop the server.
        try {
            // If in DbMode.SERVER_DAEMON, terminate the daemon.
            if (dbMode == DbMode.SERVER_DAEMON && daemon != null) {
                daemon.destroy();
                daemon = null;
            } else {
                // Send a shutdown request.
                NetworkServerControl derby = new NetworkServerControl(
                  InetAddress.getByName(dbAddress), dbPort);
                derby.shutdown();
            }

            // Wait for the server to stop (63.5 seconds maximum).
            int pause = 500;
            int loop = 7;
            do {
                Thread.sleep(pause);
                pause *= 2;
                loop--;
            } while ((running = isRunning()) && loop > 0);
        } catch (Exception e) {
            throw new DbException("Failed to stop database server", e);
        }
        if (running) {
            throw new DbException("Failed to stop database server",
              DbErrorType.DATABASE_ERROR);
        }
    }

    /**
     * Open the database, return null if it doesn't exist.  If running in
     * {@link DbMode#SERVER_EMBEDDED} or {@link DbMode#SERVER_DAEMON} mode the
     * server must have been started before calling this method.
     * @return a {@link Connection} to the database.
     * @throws DbException if the database exists but couldn't be opened.
     */
    protected Connection openDatabase()
      throws DbException {

        // Try opening the database.
        Connection c;
        try {
            Properties props = getDbProperties();

            c = driver.connect(getDbURL(), props);
            c.setAutoCommit(false);
        } catch (SQLException e) {
            // "Database not found" error.
            String state = e.getSQLState();
            if (state.equals("XJ004") || state.equals("08004")) {
                c = null;
            } else {
                try {
                    closeDatabase();
                } catch (DbException ee) {
                    // Ignore.
                }
                throw new DbException("Failed to open database", e);
            }
        }
        return c;
    }

    /**
     * Create a new database.  This should only be called after checking the
     * database does not already exist, and if running in
     * {@link DbMode#SERVER_EMBEDDED} or {@link DbMode#SERVER_DAEMON} mode the
     * server must have been started before calling this method.
     * @return a {@link Connection} to the new database.
     * @throws DbException if the database creation fails.
     */
    protected Connection createDatabase()
      throws DbException {

        // Create the database.
        Connection c = null;
        Statement s = null;
        CallableStatement cs = null;
        try {
            // Create the new database.
            Properties props = getDbProperties();
            props.put("create", "true");
            c = driver.connect(getDbURL(), props);
            c.setAutoCommit(false);

            // Create the primary user's schema.
            s = c.createStatement();
            StringBuilder sb = new StringBuilder();
            sb.append("create schema ");
            sb.append(dbUser);
            s.executeUpdate(sb.toString());

            // Configure the database.
            String enc = System.getProperty("file.encoding");
            InputStream sql = new BufferedInputStream(
              DbManager.class.getResourceAsStream(configureDbSql));
            
            readFromInputStream(sql);            
            
            if (ij.runScript(c, sql, enc, System.out, enc) != 0) {
                throw new DbException("Database configuration script failed",
                  DbErrorType.SYNTAX_ERROR);
            }
            sql.close();

            // Add the user.
            sb.setLength(0);
            cs = c.prepareCall(
              "{call syscs_util.syscs_set_database_property(?, ?)}");
            sb.setLength(0);
            sb.append("derby.user.");
            sb.append(dbUser);
            cs.setString(1, sb.toString());
            cs.setString(2, dbPass);
            cs.execute();
            commit(c);

            // Restart to ensure properties are used.
            c.close();
            closeDatabase();
            c = openDatabase();

            // Run the table setup scripts.
            sql = new BufferedInputStream(
              DbManager.class.getResourceAsStream(createDbSql));
            if (ij.runScript(c, sql, enc, System.out, enc) != 0) {
                throw new DbException("Database creation script failed",
                  DbErrorType.SYNTAX_ERROR);
            }
            sql.close();
            
            FileOutputStream out = new FileOutputStream("log.txt");
            
            sql = new BufferedInputStream(
              DbManager.class.getResourceAsStream(populateDbSql));
            if (ij.runScript(c, sql, enc, out, enc) != 0) {
                throw new DbException("Database population script failed",
                  DbErrorType.SYNTAX_ERROR);
            }
            sql.close();
            commit(c);
        } catch (SQLException e) {
            try {
                if (c != null) {
                    DbManager.rollback(c);
                    c.close();
                }
                closeDatabase();
            } catch (SQLException ee) {
                // Ignore.
            } catch (DbException ee) {
                // Ignore.  }
            }
            throw new DbException("Failed to create database", e);
        } catch (IOException e) {
            try {
                if (c != null) {
                    DbManager.rollback(c);
                    c.close();
                }
                closeDatabase();
            } catch (SQLException ee) {
                // Ignore.
            } catch (DbException ee) {
                // Ignore.
            }
            throw new DbException("Failed to initialise database", e);
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                // Ignore.
            }
        }
        return c;
    }

    /**
     * Close the database.  Note that if the database is running in
     * {@link DbMode#SERVER_DAEMON} mode, this will close the database but leave
     * the server running.
     * @throws DbException if the database can't be closed.
     */
    protected void closeDatabase()
      throws DbException {
        try {
            Properties props = getDbProperties();
            props.put("shutdown", "true");
            Connection c = driver.connect(getDbURL(), props);
            c.close();
        } catch (SQLException e) {
            String state = e.getSQLState();
            // Ignore "Successful shutdown" error.
            if (! (state.equals("08006") )) {
                throw new DbException("Failed to close database", e);
            }
        }
    }

    /**
     * Get a {@link Properties} object suitable for connecting to the database.
     * @return a {@link Properties} object.
     */
    protected Properties getDbProperties() {
        Properties props = new Properties();
        props.put("securityMechanism", "8");
        props.put("user", dbUser);
        props.put("password", dbPass);
        return props;
    }

    /**
     * Get the URL for the database, the format of which depends on the mode.
     * @return the URL for the database.
     */
    protected String getDbURL() {
        StringBuilder sb = new StringBuilder("jdbc:derby:");
        if (dbMode == DbMode.SERVER_DAEMON) {
            sb.append("//");
            sb.append(ALL_INTERFACES.equals(dbAddress)
            ? DEFAULT_ADDRESS : dbAddress);
            if (dbPort != DEFAULT_PORT) {
                sb.append(":");
                sb.append(dbPort);
            }
            sb.append("/");
        }
        sb.append(dbName);
        return sb.toString();
    }

    /** Database execution mode. */
    public enum DbMode {
        /** Run as an embedded database. */
        EMBEDDED,
        /** Run as an embedded server. */
        SERVER_EMBEDDED,
        /** Run as a separate daemon. */
        SERVER_DAEMON
    };
    
    public void readFromInputStream(InputStream input) {
        
        BufferedInputStream bufferedInput = null;
        byte[] buffer = new byte[1024];
        
        try {
            
            int bytesRead = 0;
            
            //Keep reading from the file while there is any content
            //when the end of the stream has been reached, -1 is returned
            while ((bytesRead = input.read(buffer)) != -1) {
                
                //Process the chunk of bytes read
                //in this case we just construct a String and print it out
                String chunk = new String(buffer, 0, bytesRead);
                System.out.print(chunk);
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedInputStream
            try {
                if (bufferedInput != null)
                    bufferedInput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /** Default network address. */
    public static final String DEFAULT_ADDRESS = "localhost";
    /** Special address for listening on all available interfaces. */
    public static final String ALL_INTERFACES = "0.0.0.0";
    /** Default network port. */
    public static final int DEFAULT_PORT = 1527;
    /** SQL escape string */
    public static final String ESCAPE_STRING = "escape '\\'";

    /** SQL excape regex */
    private static final String ESCAPE_REGEX = "%|_";
    /** SQL excape regex */
    private static final String ESCAPE_REPLACEMENT = "\\\\$0";

    /** Database server's root directory. */
    private final String dbRoot;
    /** Database execution mode. */
    private final DbMode dbMode;
    /** Address(es) that the server listens on. */
    private final String dbAddress;
    /** Port that the server listens on. */
    private final int dbPort;
    /** Name of the database. */
    private final String dbName;
    /** Primary database user. */
    private final String dbUser;
    /** Primary database user's password. */
    private final String dbPass;
    /** SQL to configure the database. */
    private final String configureDbSql;
    /** SQL to create the database. */
    private final String createDbSql;
    /** SQL to populate the database. */
    private final String populateDbSql;
    /** Derby JDBC driver. */
    private Driver driver;
    /** Process object when running in {@link DbMode#SERVER_DAEMON} mode. */
    private Process daemon;
}
