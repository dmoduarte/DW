package dw.metadata;

import java.util.List;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dw.cadmdm.*;

public class MetadataExtractor {
	
	
	public static void main(String[] args) {


	    try {
		    Connection conn = getMySqlConnection();
		    System.out.println("Got Connection.");
			fetchMetaData(conn);
			conn.close();/* if( columnKey != null ){
	    	  columnKey = "Pri";
	      }*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	   
	  }	
	
public static List<Table> fetchMetaData(Connection conn) throws SQLException{
	 	DatabaseMetaData metaData =  conn.getMetaData();
	 	StringBuilder builder = new StringBuilder();
	 	List<Table> tables = new ArrayList<Table>();
	 	ResultSet result = metaData.getTables(null,null,null,new String[] {"TABLE"});
	 	int counter;
	    for(counter = 0;result.next();counter++){
	    	
	    	 List <ForeignKey> fks = new ArrayList<ForeignKey>();
	    	 List <String> fksName = new ArrayList<String>();
	    	 List <Attribute> pks = new ArrayList<Attribute>();
	    	 List <String> pksName = new ArrayList<String>();
	    	 List<Attribute> attributes = new ArrayList<Attribute>();
	    	 List<ExportedKey> exportedKeys = new ArrayList<ExportedKey>();
	    	 
		     String tableName = result.getString(3);
		     
		     builder.append(tableName + "( ");
		     
		     ResultSet c = metaData.getExportedKeys(conn.getCatalog(), null, tableName);
		     System.out.println("exported keys: "+tableName);
		     while(c.next()){
		         String fkTableName = c.getString("FKTABLE_NAME");
		         String fkColumnName = c.getString("FKCOLUMN_NAME");
		         exportedKeys.add(new ExportedKey(fkTableName,fkColumnName));
		         System.out.println(fkColumnName);
		         System.out.println(fkTableName);
		     }
		     
		     ResultSet c2 = metaData.getImportedKeys(conn.getCatalog(), null, tableName);
		     System.out.println("imported keys: "+tableName);
		     while(c2.next()){
		    	 
		    	 String fkTableName = c2.getString("FKTABLE_NAME");
		         String fkColumnName = c2.getString("FKCOLUMN_NAME");
		         String pkTableName = c2.getString("PKTABLE_NAME");
		         String pkColumnName = c2.getString("PKCOLUMN_NAME");
		         fks.add(new ForeignKey(fkColumnName,"fk",pkTableName,pkColumnName));
		         fksName.add(fkColumnName);
		         System.out.println(fkTableName + "." + fkColumnName + " -> " + pkTableName + "." + pkColumnName);
	
		     }
		     ResultSet c3= metaData.getPrimaryKeys(null, null, tableName);

		     while (c3.next()) {
		       String columnName = c3.getString("COLUMN_NAME");
		       pks.add(new Attribute(columnName,"pk",true));
		       pksName.add(columnName);
		       System.out.println("getPrimaryKeys(): columnName=" + columnName);
		     }

		     //System.out.println( result.getSQLXML(3).getString());
		     ResultSet columns = metaData.getColumns(null,null,tableName,null);
		     while(columns.next()){
			      String columnName = columns.getString(4);
			      String columnType = columns.getString(6);
			      String columnKey = columns.getString(9);
			     /* if( columnKey != null ){
			    	  columnKey = "Pri";
			      }*/
			      if((!fksName.contains(columnName) && !pksName.contains(columnName))){
			    	  attributes.add(new Attribute(columnName,columnType,false));
				      builder.append(columnName);
				      builder.append(" (" + columnType + ")");
				      builder.append(" (" + columnKey + ")");
				      builder.append(",");
			      }
		     }
	     builder.deleteCharAt(builder.lastIndexOf(","));
	     builder.append(" )");
	     builder.append("\n");
	     builder.append("----------------");
	     builder.append("\n");
	     Table t;
	     tables.add((t = new Table(pks,attributes,fks,exportedKeys,tableName,counter)));
	     t.printFK();
	     System.out.println("has measures: "+t.numericDataTypeNr());
	    }
	    
	    System.out.println(builder.toString());
	    System.out.println("nr of tables: "+counter);
	    return tables;
}

public static Connection getMySqlConnection() throws Exception {
		
	    String driver = "org.gjt.mm.mysql.Driver";
	    String url = "jdbc:mysql://localhost/sakila?";
	    String username = "root";
	    String password = "root";

	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(url, username, password);
	    return conn;
	  }
	
	
}
