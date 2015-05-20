package dw.cadmdm;

import java.util.List;

public class Table {
	
	//mysql
	String [] numericType = {
		"INTEGER", "INT", 
		 "SMALLINT", "TINYINT", 
		 "MEDIUMINT", "BIGINT", 
		 "DECIMAL", "NUMERIC",
		 "FLOAT", "DOUBLE"
	};
	
	//mysql
	String [] dateType = {
    	 "DATE","TIME", "DATETIME", "TIMESTAMP", "YEAR" 
	};
	
	private final String NONE = "none";
	private final String TRANS = "transactional";
	private final String COMP = "component";
	private final String CLASS = "classifier";
	
	private List<Attribute> primaryKeys;
	private List<Attribute> attributes; //all atributes including primary keys
	private List<ForeignKey> foreignKeys;
	private String name;
	private String classification;
	private int id;
	
	//from DB
	public Table(List<Attribute> pks, List<Attribute> attrs, List<ForeignKey> fks, String name,int id){
		this.primaryKeys = pks;
		this.attributes = attrs;
		this.foreignKeys = fks;
		this.id = id;
		this.name = name;
		this.classification = NONE;
	}
	
	//From XML
	public Table(List<Attribute> pks, List<Attribute> attrs, List<ForeignKey> fks, String name, String classf ,int id){
		this.primaryKeys = pks;
		this.attributes = attrs;
		this.foreignKeys = fks;
		this.id = id;
		this.name = name;
		this.classification = classf;
	}

	public List<Attribute> getPrimaryKeys() {
		return primaryKeys;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public List<ForeignKey> getAllforeignKeys() {
		return foreignKeys;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setToTransaction(){
		this.classification = TRANS;
	}
	
	public void setToComponent(){
		this.classification = COMP;
	}
	
	public void setToClassifier(){
		this.classification = CLASS;
	}
	
	public boolean isTransaction(){
		return this.classification == TRANS;
	}
	
	public boolean isComponent(){
		return this.classification == COMP;
	}
	
	public boolean isClassifier(){
		return this.classification == CLASS;
	}

	public boolean hasRelationshipWith(String tableName) {
		for(ForeignKey fk: foreignKeys ){
			if(fk.getRefTable().equals(tableName))
				return true;
		}
		
		return false;
	}
	
	/**
	 * % Of 1:N relationships
	 */
	public double FKPerc(){
		int FKSize = this.foreignKeys.size();
		int PKSize = this.primaryKeys.size();
		int ATTRSize = this.attributes.size();
		
		return (FKSize)/(FKSize + PKSize + ATTRSize);
	}
	
	public int numericDataTypeNr(){
		int count = 0;
		
		for(Attribute attr : attributes){
			for(int i = 0; i<numericType.length; i++){
				String[] a = attr.getType().split(" ");
				if( a[0].equalsIgnoreCase(numericType[i]) )
					count++;
			}
		}
		
		return count;
	}
	
	public boolean hasNumeric(){
		return numericDataTypeNr() > 0;
	}
	
	public int dateNr(){
		int count = 0;
		
		for(Attribute attr : attributes){
			for(int i = 0; i<dateType.length; i++){
				String[] a = attr.getType().split(" ");
				
				if( a[0].equalsIgnoreCase(dateType[i]) )
					count++;
			}
		}
		
		return count;
	}
	
	public boolean hasDateTypes(){
		return dateNr() > 0;
	}

	public void printFK(){
		System.out.println("FK'S for table "+ this.getName());
		for(ForeignKey fk: foreignKeys ){
			System.out.println(fk.getRefTable());
		}
	}
	
}
