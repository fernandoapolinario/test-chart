package es.efor.plandifor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.derby.tools.sysinfo;

import junit.framework.TestCase;

public class TestMysqlEmb extends TestCase {

	public void testConn() {

		try {
			
			DriverManager
					.registerDriver(new com.mysql.embedded.jdbc.MySqlEmbeddedDriver());
			String url = "jdbc:mysql-embedded/plandifor_2";

			Properties props = new Properties();

			props.put("library.path",
					"C:\\Codigo\\java\\libs\\mysql-je-1.30\\lib");

//			props.put("--datadir", "data");
//			props.put("--basedir",
			//		"C:\\Archivos de programa\\MySQL\\MySQL Server 5.1");
			props.put("--default-character-set", "utf8");
			props.put("--default-collation", "utf8_general_ci");
			
			System.out.println("ssss");
			
			Connection connection = DriverManager.getConnection(url, props);		
			
			ResultSet rs = connection.createStatement().executeQuery(
					"select objet from Employee");
			
			assertNotNull(rs);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
