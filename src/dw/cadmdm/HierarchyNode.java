package dw.cadmdm;
/**
 * This node represents a node in a maximal hierarchy
 * @author david
 *
 */
public class HierarchyNode {

	HierarchyNode child;
	Table table;
	
	public HierarchyNode(Table refTable,HierarchyNode child){
		this.table = refTable;
		this.child = child;
	}
	
	public Table getTable(){
		return this.table;
	}
	
	public void setChild(HierarchyNode child){
		this.child = child;
	}

	public boolean collapse(HierarchyNode minimal){
		
		if(!child.getTable().getName().equals(minimal.getTable().getName())){
			//child.getTable().collapseTable(this.getTable());
			return child.collapse(minimal);
		}else{
			return true;
		}
		
	}
	
}
