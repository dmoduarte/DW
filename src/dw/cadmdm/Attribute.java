package dw.cadmdm;

public class Attribute {

	private String columnName;
	private String alias;
	private String type;
	private boolean pk;
	
	public Attribute(String columnName, String type, boolean pk){
		this.columnName = columnName;
		this.type = type;
		this.pk = pk;
		this.alias = "";
	}
	
	public void setAlias(String name){
		this.alias = name;
	}
	
	public String getAlias(){
		return this.alias;
	}
	
	public String getColumnName(){
		return columnName;
	}
	
	public String getType(){
		return type;
	}
	
	public boolean primaryKey(){
		return pk;
	}
	
}
