package dw.cadmdm;

public class ExportedKey{

	private String destTable;
	private String destFk;
	
	
	public ExportedKey(String destTable, String destTableAttribute){
		this.destTable = destTable;
		this.destFk = destTableAttribute;
	}
	
	
	public String getDestTable(){
		return this.destTable;
	}
	
	public String getDestTableAttribute(){
		return this.destFk;
	}
	
	
}
