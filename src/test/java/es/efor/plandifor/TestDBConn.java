package es.efor.plandifor;

import es.efor.plandifor.db.DbException;
import es.efor.plandifor.db.DbManager;
import es.efor.plandifor.db.DbManager.DbMode;
import junit.framework.TestCase;

public class TestDBConn extends TestCase {

	public void testDb() {

		String dbRoot = "testdb";
		DbMode dbMode = DbMode.EMBEDDED;	
		String dbName = "test";
		String dbUser = "root";
		String dbPass = "root";
		String configureDbSql = "test.sql";
		String createDbSql = "createDbSql.sql";
		String populateDbSql = "populateDbSql.sql";

		DbManager db = new DbManager(dbRoot, dbMode, dbName, dbUser, dbPass,
				configureDbSql, createDbSql, populateDbSql);

		try {
			db.connect();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
