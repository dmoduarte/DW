package dw.cadmdm;

import java.util.LinkedList;
import java.util.Queue;

public class MaxHierarchy {

	
	//private Map<String,Integer> colapsed;
	private Queue<Table> chain;
	private Table colapseResult;
	public MaxHierarchy(){
		this.chain = new LinkedList<Table>();
		//colapsed = new HashMap<String,Integer>();
	}
	
	public void addNode(Table table){
		chain.add(table);
	}
	public Table maximalEntity(){
		return this.chain.peek();
	}
	
	public Table collapse(){
		Table current = chain.remove();//maximal
		while(!chain.isEmpty() && !current.isTransaction()){
			current = current.collapse(chain.remove());
		}
		colapseResult = current;
		return colapseResult;
	}

	public void print() {
		String level = "";
		for(Table t : chain){
			level +=">"+t.getName();
		}
		System.out.println(level);
	}
	
}
