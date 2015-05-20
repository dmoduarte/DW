package dw.cadmdm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

	private Map<Integer,Table> dictionary;
	private int[][]adj;
	
	public Graph(List<Table> nodes){
		this.dictionary = createDictionary(nodes);
		this.adj = createAdjacencyMatrix(dictionary, nodes);
	}
	
	public Map<Integer,Table> getIdDictionary(){return dictionary;}
	
	public int[][] getAdjMatrix(){return adj;}
	
	private int[][] createAdjacencyMatrix(Map<Integer, Table> dictionary, List<Table> nodes) {
		this.adj = new int[dictionary.size()][dictionary.size()];
		
		
		for(int i = 0; i < dictionary.size(); i++)
			for(int j = 0; j < dictionary.size(); j++){
				
				Table t = dictionary.get(i);
				Table t2 = dictionary.get(j);
				
				if(t != null && t2 != null)
					this.adj[i][j] += (t.hasRelationshipWith(t2.getName())) ? 1 : 0;
				else
					System.out.println("huehue");
				
				//System.out.println("table "+t.getName() +"table "+t2.getName()+" "+adj[i][j]+" "+t.hasRelationshipWith(t2.getName()));
			}
		
		return adj;
	}
	
	private Map<Integer, Table> createDictionary(List<Table> nodes) {
		
		this.dictionary = new HashMap<Integer,Table>();
		for(Table t : nodes){				
			dictionary.put(t.getId(),t);			
		}
		
		return dictionary;
	}
	
	
	
}
