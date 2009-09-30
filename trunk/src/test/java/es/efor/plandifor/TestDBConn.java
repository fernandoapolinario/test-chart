package es.efor.plandifor;

import es.efor.plandifor.db.DbException;
import es.efor.plandifor.db.DbManager;
import es.efor.plandifor.db.DbManager.DbMode;
import junit.framework.TestCase;

public class TestDBConn extends TestCase {

	public void testDb() {

		String dbRoot = "db";
		DbMode dbMode = DbMode.EMBEDDED;	
		String dbName = "plandifor_2";
		String dbUser = "plandifor_2";
		String dbPass = "plandifor_2";
		String configureDbSql = "Script_Config_PlanDIFOR_Derby.sql";
		String createDbSql = "PlanDIFOR_2_0_Derby_v19.sql";
		String populateDbSql = "Script_Carga_Datos_PlanDIFOR_Derby_v19.sql";

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
