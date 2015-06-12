package dw.cadmdm;

import java.util.ArrayList;
import java.util.List;

public class Table{
	
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
	
	final String NONE = "none";
	final String TRANS = "transactional";
	final String COMP = "component";
	final String CLASS = "classifier";
	
	private List<Attribute> primaryKeys;
	private List<Attribute> attributes; //all atributes including primary keys
	private List<ForeignKey> foreignKeys;
	private List<ExportedKey> exportedKeys;
	private String name;
	private String classification;
	private int id;
	int transactional;
	int classifier;
	int component;
	
	
	//from DB
	public Table(List<Attribute> pks, List<Attribute> attrs, List<ForeignKey> fks, List<ExportedKey> exported, String name,int id){
		this.primaryKeys = pks;
		this.attributes = attrs;
		this.foreignKeys = fks;
		this.exportedKeys = exported;
		this.id = id;
		this.name = name;
		this.classification = NONE;
		transactional = 0;
		classifier = 0;
		component = 0;
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
	
	public List<ExportedKey> getExportedKeys(){
		return exportedKeys;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getClassification(){
		return this.classification;
	}
	
	
	public boolean isClassified(){
		return (!this.classification.equals(NONE));
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void unset(String classification){
		System.out.println("Unset: "+this.getName()+", current class: "+this.getClassification());
		System.out.println("transac :" + this.transactional);
		System.out.println("classf :" + this.classifier);
		System.out.println("comp :" + this.component);
		if(classification.equals(TRANS) && this.transactional > 0){
			this.transactional--;
			if(this.transactional == 0 && this.isTransaction()){
				setNext();
			}
		}else if(classification.equals(CLASS) && this.classifier > 0){
			this.classifier--;
			if(this.classifier == 0 && this.isClassifier())
				setNext();
		}else if(classification.equals(COMP) && this.component > 0){
			this.component--;
			if(this.component == 0 && this.isComponent())
				setNext();
		}
		
	}
	
	
	private void setNext() {
		if(this.transactional > 0)
			this.classification = TRANS;
		else if(this.classifier > 0)
			this.classification = CLASS;
		else if(this.component > 0)
			this.classification = COMP;
		else
			setToDefault();
	}
	
	public void setToDefault() {
		this.classification = NONE;
		this.classifier = 0;
		this.transactional = 0;
		this.component = 0;
		}
	

	/**
	 * Classifies this table as Transaction and returns a list of
	 * directly related ones
	 */
	public List<String> setToTransaction(){
		this.transactional++;
		this.classification = TRANS;
		
		List<String> component = new ArrayList<String>();
		for(ForeignKey fk : this.foreignKeys){
			component.add(fk.getRefTable());
		}
		return component;
	}
	
	
	/**
	 * Classify this table as component and returns a list of 
	 * directly related ones 
	 */
	public void setToComponent(){
		this.component++;
		
		if(this.classifier == 0 && this.transactional == 0)
			this.classification = COMP;
		
	}
	

	public void setToClassifier(){
		this.classifier++;
		
		if(this.transactional == 0)
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
	
	public int qntRelationship(String tableName) {
		int qnt = 0;
		for(ForeignKey fk: foreignKeys ){
			if(fk.getRefTable().equals(tableName))
				qnt++;
		}
		
		return qnt;
	}
	
	public int relationshipQt(){
		return this.foreignKeys.size() + this.exportedKeys.size();
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
	

	public void removeRelationShip(String fromTable) {
		
		List<ForeignKey> toRemove = new ArrayList<ForeignKey>();
		for(ForeignKey fk : foreignKeys){
			if(fk.getRefTable().equals(fromTable))
				toRemove.add(fk);
		}
		
		for(ForeignKey fk : toRemove){
			foreignKeys.remove(fk);
		}
		
	}

	public void setId(int id) {
		this.id = id;
	}

	public void removeExportedKey(String outTable) {
		List<ExportedKey> toRemove = new ArrayList<ExportedKey>();
		for(ExportedKey ek : exportedKeys){
			if(ek.getDestTable().equals(outTable))
				toRemove.add(ek);
		}
		
		for(ExportedKey ek : toRemove){
			exportedKeys.remove(ek);
		}
	}

	public Table collapse(Table lower) {
		
		System.out.println("Colapsing "+this.getName()+" into "+lower.getName());
		List<Attribute> newAttributes = new ArrayList<Attribute>();
		List<ForeignKey> newForeignKeys = lower.getAllforeignKeys();
		List<Attribute> newLvlID = new ArrayList<Attribute>();
		List<Attribute> newPrimaryKeys = lower.getPrimaryKeys();
		List<ExportedKey> newExportedKeys = new ArrayList<ExportedKey>();
		
		for(Attribute attr : this.getAttributes()){
			Attribute newAttr = new Attribute(attr.getColumnName(),attr.getType(),false);
			newAttr.setAlias(this.getName()+"."+attr.getColumnName());
			newAttributes.add(newAttr);
		}
		
		for(Attribute attr : lower.getAttributes()){
			Attribute newAttr = new Attribute(attr.getColumnName(),attr.getType(),false);
			newAttributes.add(newAttr);
		}
		
		for(Attribute pk: this.getPrimaryKeys()){
			Attribute newLvl = new Attribute(pk.getColumnName(),pk.getType(),false);
			newLvl.setAlias(this.getName().toUpperCase()+"ID_"+pk.getColumnName());
			newLvlID.add(newLvl);
		}
		
		return new Dimension(newPrimaryKeys,newAttributes,newForeignKeys,newExportedKeys,newLvlID,lower.getName(),lower.getId());
		
	}

}
