package dw.cadmdm;

public class ForeignKey extends Attribute{

	private String refTable;
	private String refTableAttribute;
	
	public ForeignKey(String columnName, String type,String refTable, String refTableAttribute){
		super(columnName,type,false);
		this.refTable = refTable;
		this.refTableAttribute = refTableAttribute;
	}
	
	public String getRefTable(){
		return this.refTable;
	}
	
	public String getRefTableAttribute(){
		return this.refTableAttribute;
	}
	
	
}
