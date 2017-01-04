package dw.cadmdm;

public class ForeignKey extends Attribute{

	private String refTable;
	private String refTableAttribute;
	private String aliasTable;
	
	public ForeignKey(String columnName, String type,String refTable, String refTableAttribute){
		super(columnName,type,refTable,false);
		this.refTable = refTable;
		this.refTableAttribute = refTableAttribute;
		this.aliasTable = "";
	}
	
	public String getRefTable(){
		return this.refTable;
	}
	
	public void setRefTable(String table){
		this.refTable = table;
	}
	
	public String getRefTableAttribute(){
		return this.refTableAttribute;
	}
	
	public void setRefTableAttribute(String attribute){
		this.refTableAttribute = attribute;
	}

	public boolean aliasRefTableIsSet(){
		return !aliasTable.equals("");
	}
	
	public void setAliasRefTable(String name) {
			aliasTable = name;
	}	
	
	public String getColapsedRefTable(){
		if(this.aliasRefTableIsSet())
			return this.aliasTable;
		else return getRefTable();
	}
	
}
