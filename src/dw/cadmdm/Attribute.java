package dw.cadmdm;

public class Attribute {

	//mysql
		public static final String [] numericType = {
			"INTEGER", "INT", 
			 "SMALLINT", "TINYINT", 
			 "MEDIUMINT", "BIGINT", 
			 "DECIMAL", "NUMERIC",
			 "FLOAT", "DOUBLE"
		};
		
	//mysql
		public static final String [] dateType = {
	    	 "DATE","TIME", "DATETIME", "TIMESTAMP", "YEAR" 
		};
		
	//mysql
		public static final String [] variableType = {"VARCHAR","VARBINARY"};	
		
	private String columnName;
	private int size;
	private String alias;
	private String type;
	private String source;
	private String aggFun;
	private String expression;
	private String path;
	private boolean pk;
	
	public Attribute(String columnName, String type, String sourceTable,boolean pk){
		this.columnName = columnName;
		this.source = sourceTable;
		this.type = type;
		this.pk = pk;
		this.expression = columnName;
		this.aggFun = "SUM";
		this.alias = "";
	}
	
	public void setSource(String source){
		this.source = source;
	}
	
	public void setAlias(String name){
		this.alias = name;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public String getPath(){
		return this.path;
	}
	
	public void setCalculated(String expr){
		if(isNumeric())
			this.expression = expr;
	}
	
	public String getExpression(){
		return this.expression;
	}
	
	public String getAggregateFunction(){
		return this.aggFun;
	}
	
	public void setAggregateFunction(String fun){
		this.aggFun = fun;
	}
	
	public String fromTable(){
		return source;
	}
	
	public String getAlias(){
		return this.alias;
	}
	
	public boolean aliasIsSet(){
		return !getAlias().equals("");
	}
	
	public String getOperationalName(){
		return columnName;
	}
	
	public String getColumnName(){
		if(!aliasIsSet())
			return getOperationalName();
		else
			return getAlias();
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}

	public int getSize(){
		return this.size;
	}
	
	public String printSize(){
		
		if(this.type.split(" ")[0].toUpperCase().equals("REAL") ||
				this.type.split(" ")[0].toUpperCase().equals("FLOAT") ||
				this.type.split(" ")[0].toUpperCase().equals("DOUBLE"))
			return this.type+","+4;
		
		else return this.type;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public boolean primaryKey(){
		return pk;
	}
	
	public boolean isNumeric(){
		for(int i = 0; i<numericType.length; i++){
			String[] a = this.getType().split(" ");
			if( a[0].equalsIgnoreCase(numericType[i]) )
				return true;
		}
		return false;
	}
	
	public boolean isDate(){
		for(int i = 0; i<dateType.length; i++){
			String[] a = this.getType().split(" ");
			
			if( a[0].equalsIgnoreCase(dateType[i]) )
				return true;
		}
		return false;
	}
	
	public boolean isVariable(){
		for(int i = 0; i<variableType.length; i++){
			String[] a = this.getType().split(" ");
			
			if( a[0].equalsIgnoreCase(variableType[i]) )
				return true;
		}
		return false;
	}
	
}
