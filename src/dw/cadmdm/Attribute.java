package dw.cadmdm;

public class Attribute {

	private String columnName;
	private String type;
	private boolean pk;
	
	public Attribute(String columnName, String type, boolean pk){
		this.columnName = columnName;
		this.type = type;
		this.pk = pk;
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
