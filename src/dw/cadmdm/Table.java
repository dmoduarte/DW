package dw.cadmdm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Table{
	
	final String NONE = "none";
	public final String TRANS = "transactional";
	public final String COMP = "component";
	public final String CLASS = "classifier";
	public final String DIM = "dimension";
	public final String FACTT = "facttable";
	
	private List<Attribute> primaryKeys;
	private List<Attribute> attributes; //all atributes including primary keys
	private List<ForeignKey> foreignKeys;
	private List<ExportedKey> exportedKeys;
	private String name;
	private String alias;
	private String classification;
	private int id;
	int transactional;
	int classifier;
	int component;
	boolean colapsed;
	
	
	public Table(List<Attribute> pks, List<Attribute> attrs, List<ForeignKey> fks, List<ExportedKey> exported, String name,int id){
		this.primaryKeys = pks;
		this.attributes = attrs;
		this.foreignKeys = fks;
		this.exportedKeys = exported;
		this.id = id;
		this.name = name;
		alias = "";
		this.classification = NONE;
		this.transactional = 0;
		this.classifier = 0;
		this.component = 0;
		this.colapsed = false;
	}
	
	public Table(Table t){
		this.attributes = new ArrayList<Attribute>();
		this.foreignKeys = new ArrayList<ForeignKey>();
		this.primaryKeys = new ArrayList<Attribute>();
		this.exportedKeys = new ArrayList<ExportedKey>();
		
		for(Attribute attr : t.getAttributes()){
			Attribute newAttr = new Attribute(attr.getColumnName(),attr.getType(),attr.fromTable(),false);
			newAttr.setSize(attr.getSize());
			this.attributes.add(newAttr);
		}
		
		for(Attribute attr: t.getPrimaryKeys()){
			Attribute newPK = new Attribute(attr.getColumnName(),attr.getType(),attr.fromTable(),true);
			newPK.setSize(attr.getSize());
			this.primaryKeys.add(newPK);
		}
		
		for(ForeignKey fk : t.getAllforeignKeys()){
			ForeignKey newFK = new ForeignKey(fk.getColumnName(),fk.getType(),fk.getRefTable(),fk.getRefTableAttribute());
			newFK.setSize(fk.getSize());
			this.foreignKeys.add(newFK);
		}
		for(ExportedKey ek : t.getExportedKeys()){
			ExportedKey newEK = new ExportedKey(ek.getDestTable(),ek.getDestTableAttribute());
			this.exportedKeys.add(newEK);
		}
	
		this.id = t.getId();
		this.name = t.getName();
		alias = "";
		this.transactional = t.transactional;
		this.classifier = t.classifier;
		this.component = t.component;
		this.colapsed = t.isColapsed();
		this.classify(t.getClassification());
	}
	
	public Table() {
	}

	public Set<Attribute> getAllColumns(){
		Set<Attribute> attrSet = new HashSet<Attribute>(this.attributes);
		attrSet.addAll(this.primaryKeys);
		attrSet.addAll(this.foreignKeys);
		return attrSet;
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
	
	private void eraseForeignKeys(){
		if(this.colapsed){
			this.foreignKeys = new ArrayList<ForeignKey>();
		}
	}
	
	public List<ExportedKey> getExportedKeys(){
		return exportedKeys;
	}
	
	public int getId(){
		return id;
	}
	
	public void setAlias(String name){
		this.alias = name;
	}
	
	public String getAlias(){
		return this.alias;
	}
	
	public boolean aliasIsSet(){
		return !getAlias().equals("");
	}
	
	public String getOperationalName(){
		return name;
	}
	
	public String getName(){
		if(!aliasIsSet())
			return getOperationalName();
		else
			return getAlias();
	}
	
	
	public String getClassification(){
		return this.classification;
	}
	
	
	public boolean isClassified(){
		return (!this.classification.equals(NONE));
	}
	
	public void setName(String name){
		this.name = name;
		
		Set<Attribute> attrs = this.getAllColumns();
		for(Attribute a : attrs)
			a.setSource(this.getName());
	}
	
	public void unset(String classification){
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
		
		List<String> component = new ArrayList<String>();
		
		if(!this.isTransaction()){
			this.transactional++;
			this.classification = TRANS;
			
			for(ForeignKey fk : this.foreignKeys){
				component.add(fk.getRefTable());
			}
		}
		return component;
		
	}
	
	
	/**
	 * Classify this table as component and returns a list of 
	 * directly related ones 
	 */
	public void setToComponent(){
		this.component++;
		
		if(!this.isClassified())
			this.classification = COMP;
	}
	

	public void setToClassifier(){
		this.classifier++;
		
		if(!this.isClassified())
			this.classification = CLASS;
	}
	
	public void classify(String c){
		this.classification = c;
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
	 * Number of N:1 relationships with tableName 
	 */
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
			if(attr.isNumeric())
				count++;
		}
		return count;
	}
	
	public boolean hasNumeric(){
		return numericDataTypeNr() > 0;
	}
	
	public int dateNr(){
		int count = 0;
		
		for(Attribute attr : this.getAllColumns()){
			if(attr.isDate())
				count++;
		}
		
		return count;
	}
	
	public boolean hasDateTypes(){
		return dateNr() > 0;
	}
	
	public void printTable(){
		
		for(Attribute attr : this.getAttributes()){
			System.out.println("->"+attr.getColumnName());
		}
		
		for(Attribute pk : this.getPrimaryKeys()){
			System.out.println("->"+pk.getColumnName());
		}
		
		for(ForeignKey fk : this.getAllforeignKeys()){
			System.out.println("FK->"+fk.getColumnName());
		}
		
		for(ExportedKey ek : this.getExportedKeys()){
			System.out.println("ExportedKey->"+ek.getDestTableAttribute());
		}
		
		System.out.println("Classification :"+this.classification);
		
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
	
	public void setFactTable(){
		this.classification = FACTT;
		this.primaryKeys = new ArrayList<Attribute>(this.foreignKeys);//composite
	}
	
	public boolean isFactTable(){
		return this.classification.equals(FACTT);
	}
	
	public boolean isDimension(){
		return this.classification.equals(DIM);
	}
	
	public void setColapsed(){
		colapsed = true;
		eraseForeignKeys();
	}
	
	public void setToDimension(){
		this.classification = DIM;
		setColapsed();
	}
	
	public boolean isColapsed(){
		return this.colapsed;
	}
	public boolean containsAttribute(String name){
		for(Attribute a : this.getAttributes()){
			if(a.getColumnName().equals(name))
				return true;
		}
		return false;
	}
	
	public boolean containsForeignKey(ForeignKey fk){
		for(ForeignKey a : this.getAllforeignKeys()){
			if(a.getColumnName().equals(fk.getColumnName()) && a.getRefTable().equals(fk.getRefTable()) && a.getRefTableAttribute().equals(fk.getRefTableAttribute()) )
				return true;
		}
		return false;
	}
	
	public int qntAttribute(String name){
		int c = 0;
		for(Attribute a : this.getAttributes()){
			if(a.getColumnName().equals(name))
				c++;
		}
		return c;
	}
	
	public Table collapse(Table lower,ForeignKey colapseKey) {
		
		System.out.println("Colapsing "+this.getName()+" into "+lower.getName());
	
		for(Attribute attr : this.getAttributes()){
			
			Attribute newAttr = new Attribute(attr.getOperationalName(),attr.getType(),attr.fromTable(),false);
			newAttr.setAlias(newAttr.fromTable()+"."+newAttr.getColumnName());
			newAttr.setSize(attr.getSize());
			
			if(!lower.containsAttribute(newAttr.getColumnName()))
				lower.getAttributes().add(newAttr);
			
		}
		
		Attribute newLvlID = new Attribute(colapseKey.getOperationalName(),colapseKey.getType(),colapseKey.fromTable(),false);
		newLvlID.setAlias(newLvlID.fromTable()+"."+newLvlID.getColumnName());
		newLvlID.setSize(colapseKey.getSize());
		
		if(!lower.containsAttribute(newLvlID.getColumnName()) && !lower.isTransaction())
			lower.getAttributes().add(newLvlID);
		
		return lower;
	}

	public void join(Table ti) {
		
		for(Attribute attr : ti.getAttributes())
			if(!this.containsAttribute(attr.getColumnName()))
				this.getAttributes().add(attr);
		
	}

}
