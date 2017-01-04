package dw.cadmdm;

import java.util.LinkedList;
import java.util.Queue;

public class MaxHierarchy {

	
	private Queue<HierarchyNode> chain;
	private Table colapseResult;
	public MaxHierarchy(){
		this.chain = new LinkedList<HierarchyNode>();
	}
	
	public void addNode(HierarchyNode node){
		chain.add(node);
	}
	public Table maximalEntity(){
		return this.chain.peek().table;
	}
	
	/*
	 * Returns a collapsed table
	 */
	public Table collapse(){
		
		if(chain.size() == 1)
			return chain.remove().table;
		else if(chain.size() == 0)
			return null;
		
		
		HierarchyNode current = chain.poll();//maximal
		HierarchyNode lower = chain.poll();
		boolean done = false;
		
		if(lower != null & current !=null){
			while(!done){
				
				if(lower == null || lower.table.isTransaction())
					done = true;
				else{
					current = current.colapse(lower);
					lower = chain.poll();
				}
				
			}
			
			colapseResult = current.table;
			
			current = lower;
			lower = chain.poll();
			done = lower == null;
			while(!done){
				if(current.table.isTransaction() && lower.table.isTransaction())
					for(ForeignKey fk : current.table.getAllforeignKeys()){
						if(!lower.table.containsForeignKey(fk)){
							ForeignKey nfk = new ForeignKey(fk.getColumnName(),fk.getType(),fk.fromTable(),fk.getRefTableAttribute());
							lower.table.getAllforeignKeys().add(nfk);
						}
					}
				
				current = lower;
				lower = chain.poll();
				
				done = lower == null;
			}
			
		}
		
		return colapseResult;
	}
	
	public void print() {
		String level = "";
		for(HierarchyNode hn : chain){
			String fkName = hn.colapseKey != null? hn.colapseKey.getColumnName() : "none";
			level +="->("+hn.table.getOperationalName()+","+fkName+")";
		}
		System.out.println(level);
	}
	
}
