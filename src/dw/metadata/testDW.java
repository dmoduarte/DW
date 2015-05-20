package dw.metadata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;



public class testDW {


	public Connection getConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		//System.setProperty("java.net.preferIPv6Addresses", "true");
		Connection conn = null;
		//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		//Properties connectionProps = new Properties();
		//connectionProps.put("user", this.userName);
		//connectionProps.put("password", this.password);
		//String connectionUrl  = "jdbc:sqlserver://localhost;database=AdventureWorks;integratedSecurity=true;";
		//Connection con = DriverManager.getConnection(connectionUrl, new Properties());
		//System.out.println("Connected to database");	
		//SQLServerDataSource ds = new SQLServerDataSource();
		//ds.setIntegratedSecurity(true);
		//ds.setServerName("localhost");
		//ds.setPortNumber(1433); 
		//ds.setDatabaseName("AdventureWorks");
		//conn = ds.getConnection();
		
		
		//Class.forName("com.mysql.jdbc.Driver").newInstance();
		 /*conn =
			       DriverManager.getConnection("jdbc:mysql://localhost/sakila?" +
			                                   "user=root&password=casa");*/
		System.out.println("Connected");
		DatabaseMetaData metaData =  conn.getMetaData();
		ResultSet rs = metaData.getTables(null, null, "%", null);
		
	    StringBuilder builder = new StringBuilder();
	    
	   ResultSet result = metaData.getTables(null,null,null,new String[] {"TABLE"});
	    while(result.next())
	    {
	     String tableName = result.getString(3);
	     
	     builder.append(tableName + "( ");
	     ResultSet columns = metaData.getColumns(null,null,tableName,null);
	     ResultSet c = metaData.getExportedKeys(conn.getCatalog(), null, tableName);
	     System.out.println("exported keys");
	     while(c.next()){
	         String fkTableName = c.getString("FKTABLE_NAME");
	         String fkColumnName = c.getString("FKCOLUMN_NAME");
	         System.out.println(fkColumnName);
	         System.out.println(fkTableName);
	     }
	     ResultSet c2 = metaData.getImportedKeys(conn.getCatalog(), null, tableName);
	     System.out.println("imported keys");
	     while(c2.next()){
	    	 String fkTableName = c2.getString("FKTABLE_NAME");
	         String fkColumnName = c2.getString("FKCOLUMN_NAME");
	         String pkTableName = c2.getString("PKTABLE_NAME");
	         String pkColumnName = c2.getString("PKCOLUMN_NAME");
	         System.out.println(fkTableName + "." + fkColumnName + " -> " + pkTableName + "." + pkColumnName);

	     }
	     //System.out.println( result.getSQLXML(3).getString());
	     while(columns.next())
	     {
	      String columnName = columns.getString(4);
	      String columnType = columns.getString(6);
	      String columnKey = columns.getString(9);
	      if( columnKey != null ){
	    	  columnKey = "Pri";
	      }
	      builder.append(columnName);
	      builder.append(" (" + columnType + ")");
	      builder.append(" (" + columnKey + ")");
	      builder.append(",");
	     }
	     builder.deleteCharAt(builder.lastIndexOf(","));
	     builder.append(" )");
	     builder.append("\n");
	     builder.append("----------------");
	     builder.append("\n");
	     
	     
	    }
	    
	   System.out.println(builder.toString());
	   
		
		return conn;
	}
	
	
	

	


	public static void main(String[] args) {
		Connection con = null;

		try {
			testDW Test = new testDW();
			con = Test.getConnection();

		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
				
					e.printStackTrace();
				}
			}
		}

	}
	
	

}
