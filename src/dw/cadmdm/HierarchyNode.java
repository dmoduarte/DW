package dw.cadmdm;


/**
 * This node represents a node in a maximal hierarchy
 *
 */
public class HierarchyNode {

	Table table;
	ForeignKey colapseKey;
	
	public HierarchyNode(Table refTable, ForeignKey colapseKey){
		this.table = refTable;
		this.colapseKey = colapseKey;
	}
	
	public HierarchyNode colapse(HierarchyNode lower){
		table.collapse(lower.table,lower.colapseKey);
		return lower;
	}
	
}
