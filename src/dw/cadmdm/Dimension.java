package dw.cadmdm;

import java.util.List;

public class Dimension extends Table {

	private List<Attribute> hierarchies;
	public Dimension(List<Attribute> hierarchies){
		super();
		this.hierarchies = hierarchies;
	}
	
	public List<Attribute> hierarchies(){
		return hierarchies;
	}
	
}
