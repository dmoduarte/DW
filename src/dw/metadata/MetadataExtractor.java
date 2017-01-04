package dw.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dw.cadmdm.*;

public class MetadataExtractor {
	
	private Connection conn;
	
	public MetadataExtractor(String server, String port ,String DB,String userName,String pw) throws ClassNotFoundException, SQLException{
		conn = this.getMySqlConnection(server,port,DB, userName, pw);
	}
	
	public List<Table> fetchMetaData() throws SQLException{
	 	DatabaseMetaData metaData =  conn.getMetaData();
	 	List<Table> tables = new ArrayList<Table>();
	 	ResultSet result = metaData.getTables(null,null,null,new String[] {"TABLE"});
	 	int counter;
	    for(counter = 0;result.next();counter++){
	    	
	    	 Map <String,ForeignKey> fks = new HashMap<String,ForeignKey>();
	    	 Map <String,Attribute> pks = new HashMap<String,Attribute>();
	    	 
	    	 List<Attribute> attributes = new ArrayList<Attribute>();
	    	 List<ExportedKey> exportedKeys = new ArrayList<ExportedKey>();
	    	 
		     String tableName = result.getString(3);
		     
		     ResultSet c = metaData.getExportedKeys(conn.getCatalog(), null, tableName);
		     while(c.next()){
		         String fkTableName = c.getString("FKTABLE_NAME");
		         String fkColumnName = c.getString("FKCOLUMN_NAME");
		         exportedKeys.add(new ExportedKey(fkTableName,fkColumnName));
		     }
		     
		     ResultSet c2 = metaData.getImportedKeys(conn.getCatalog(), null, tableName);
		     while(c2.next()){
		         String fkColumnName = c2.getString("FKCOLUMN_NAME");
		         String pkTableName = c2.getString("PKTABLE_NAME");
		         String pkColumnName = c2.getString("PKCOLUMN_NAME");
		         fks.put(fkColumnName,new ForeignKey(fkColumnName,"",pkTableName,pkColumnName));
		     }
		     ResultSet c3= metaData.getPrimaryKeys(null, null, tableName);
		     while (c3.next()) {
		       String columnName = c3.getString("COLUMN_NAME");
		       pks.put(columnName, new Attribute(columnName,"",tableName,true));
		     }

		     ResultSet columns = metaData.getColumns(null,null,tableName,null);
		     while(columns.next()){
			      String columnName = columns.getString(4);
			      String columnType = columns.getString(6);
			      int size = columns.getInt("COLUMN_SIZE");
			      
			      if(columnType.toUpperCase().equals("ENUM") || columnType.toUpperCase().equals("SET")){
			    	  columnType = "VARCHAR";
			    	  size = 250;
			      }
			      if(pks.containsKey(columnName)){
			    	  pks.get(columnName).setType(columnType);
			    	  pks.get(columnName).setSize(size);
			      }
			      if(fks.containsKey(columnName)){
			    	  fks.get(columnName).setType(columnType);
			    	  fks.get(columnName).setSize(size);
			      }
			      if(!pks.containsKey(columnName) && !fks.containsKey(columnName)){
			    	  
			    	  Attribute a = new Attribute(columnName,columnType,tableName,false);
			    	  a.setSize(size);
			    	  attributes.add(a);
			      }
		     }
	     List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>(fks.values());
	     List<Attribute> primaryKeys = new ArrayList<Attribute>(pks.values());
	     tables.add((new Table(primaryKeys,attributes,foreignKeys,exportedKeys,tableName,counter)));
	    }
	    
	    return tables;
	}

	public Connection getMySqlConnection(String server,String port,String DB, String userName, String pw) throws ClassNotFoundException, SQLException {
		
	    String driver = "org.gjt.mm.mysql.Driver";
	    String url = "";
	    if(server.equalsIgnoreCase("localhost"))
	    	url	= "jdbc:mysql://"+server+"/"+DB+"?";
	    else
	    	url = "jdbc:mysql://"+server+":"+port+"/"+DB+"?";
	    
	    //String url = "jdbc:mysql://localhost/moody?";
	    //String username = "root";
	    //String password = "root";
	    
	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(url, userName, pw);
	    return conn;
	  }
	
	
}
