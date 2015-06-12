package dw.cadmdm;

import java.util.List;

public class Dimension extends Table{
	
	private List<Attribute> lvlID;
	public Dimension(List<Attribute> pks, List<Attribute> attrs, List<ForeignKey> fks, List<ExportedKey> exported,List<Attribute> lvlID,String name,int id){
		super(pks,attrs,fks,exported,name,id);
		this.lvlID = lvlID;
	}
	
}
