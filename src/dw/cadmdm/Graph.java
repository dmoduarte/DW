package dw.cadmdm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Graph {

	private String name;
	private Map<Integer,Table> dictionary;
	private int[][]adj;
	
	public Graph(List<Table> nodes){
		this.dictionary = createDictionary(nodes);
		this.adj = createAdjacencyMatrix(dictionary);
	}
	
	public Map<Integer,Table> getIdDictionary(){return dictionary;}
	
	public int[][] getAdjMatrix(){return adj;}
	
	private int[][] createAdjacencyMatrix(Map<Integer, Table> dictionary) {
		this.adj = new int[dictionary.size()][dictionary.size()];
		
		
		for(int i = 0; i < dictionary.size(); i++)
			for(int j = 0; j < dictionary.size(); j++){
				
				Table t = dictionary.get(i);
				Table t2 = dictionary.get(j);
				
				if(t != null && t2 != null)
					this.adj[i][j] += (t.hasRelationshipWith(t2.getOperationalName())) ? t.qntRelationship(t2.getOperationalName()) : 0;
				
			}
		
		return adj;
	}
	
	public Table getNode(String name){
		Iterator<Table> it = dictionary.values().iterator();
		
		while(it.hasNext()){
			Table t = it.next();
			if(t.getOperationalName().equals(name) || t.getAlias().equals(name))
				return t;
		}
		return null;
		
	}
	
	private Map<Integer, Table> createDictionary(List<Table> nodes) {
		
		this.dictionary = new HashMap<Integer,Table>();
		for(Table t : nodes){				
			dictionary.put(t.getId(),t);			
		}
		
		return dictionary;
	}

	public void removeTable(Table table) {
		Table removal = dictionary.remove(table.getId());
		List<Table> update = new ArrayList<Table>();
		if(removal != null){
			Iterator<Table> it = dictionary.values().iterator();
			while(it.hasNext()){
				Table next = it.next();
				if(next.getId()>removal.getId())
					next.setId(next.getId()-1);
				
				update.add(next);
			}
			dictionary = this.createDictionary(update);
			this.createAdjacencyMatrix(dictionary);
		}
		
	}

	public List<Table> factTables(){
		List<Table> result = new ArrayList<Table>();
		Iterator<Table> it = this.getIdDictionary().values().iterator();
		while(it.hasNext()){
			Table t = it.next();
			if(t.isFactTable()){
				result.add(t);
			}
		}
		return result;
	}
	
	public List<Table> dimensionTables(){
		List<Table> result = new ArrayList<Table>();
		Iterator<Table> it = this.getIdDictionary().values().iterator();
		while(it.hasNext()){
			Table t = it.next();
			if(t.isDimension()){
				result.add(t);
			}
		}
		return result;
	}
	
	public List<Table> transactionalEntitites() {
		List<Table> result = new ArrayList<Table>();
		Iterator<Table> it = this.getIdDictionary().values().iterator();
		while(it.hasNext()){
			Table t = it.next();
			if(t.isTransaction()){
				result.add(t);
			}
		}
		return result;
	}

	public void insertTable(Table t) {
		this.getIdDictionary().put(t.getId(), t);
		this.createAdjacencyMatrix(this.getIdDictionary());
	}
	
	public int nextID(){
		return this.getIdDictionary().values().size();
	}

	public void setModelName(String DBName) {
		this.name = DBName;
	}
	
	public String getName(){
		return this.name;
	}
	
}
