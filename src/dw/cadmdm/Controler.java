package dw.cadmdm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import dw.metadata.*;

public class Controler {
	public final int PRESENTATION = 0;
	public final int CLASSIFICATION = 1;
	public final int COLAPSING = 2;
	
	public Graph graph;
	private Classifier classifier;
	public List<MaxHierarchy> maxHierarchies;
	public int state;
	
	public Controler(){
		graph = initGraph(new ArrayList<Table>());
		classifier = new Classifier();
		maxHierarchies = new ArrayList<MaxHierarchy>();
		state = PRESENTATION;
	}
	
	public void setState(int state){
		if(state == PRESENTATION || state == CLASSIFICATION || state == COLAPSING)
			this.state = state;
	}
	public int classify(){
		System.out.println("Classifying...");
		state = CLASSIFICATION;
		return classifier.classify(graph);
	}
	
	public void classify(Table table){
		classifier.classify(table);
		state = CLASSIFICATION;
	}
	
	public void colapse(){
		this.state = COLAPSING;
		//Star-schema...
		for(MaxHierarchy mh: maxHierarchies){
			mh.print();
			//mh.collapse();
		}
	}
	
	public void unsetTable(Table parent){
		
		boolean [] explored = new boolean [graph.getIdDictionary().size()];
				for(ForeignKey fk : parent.getAllforeignKeys()){
					Table child = graph.getNode(fk.getRefTable());
					if(child != null){
						if(parent.isTransaction()){
							child.unset(child.COMP);
							BFS(child,explored);
						}
					}
				}
		parent.unset(parent.TRANS);
	}
	
	public void removeTable(Table parent) {
		
		boolean [] explored = new boolean [graph.getIdDictionary().size()];
		
		//remove all relationships to this table
		for(ForeignKey fk : parent.getAllforeignKeys()){
			Table child = graph.getNode(fk.getRefTable());
			if(child != null){
				if(parent.isTransaction()){
					child.unset(child.COMP);
					child.removeExportedKey(parent.getName());
					BFS(child,explored);
				}
			}
		}
		
		//remove all relationships from this table
		for(ExportedKey ek : parent.getExportedKeys()){
			Table child = graph.getNode(ek.getDestTable());
			if(child != null){
				child.removeRelationShip(parent.getName());
			}
			
		}
		
		//set Tables without relationship to default
		Iterator<Table> it = graph.getIdDictionary().values().iterator();
		while(it.hasNext()){
			Table t = it.next();
			if(t.relationshipQt() == 0)
				t.setToDefault();
		}

		graph.removeTable(parent);
		
		
	}
	
	private void BFS(Table root,boolean[]explored) {
		System.out.println("----");
		Queue<Table> queue = new LinkedList<Table>();
		for(ForeignKey outFK : root.getAllforeignKeys()){
			Table child = graph.getNode(outFK.getRefTable());
			if(!explored[child.getId()])
				queue.add(child);
		}
		
		while(!queue.isEmpty()){
			Table current = queue.remove();
			if(!explored[current.getId()] && !classifier.verifyTransaction(current) && !current.isTransaction()){ 
				current.unset(current.CLASS);
				explored[current.getId()] = true;
				System.out.println("current class: "+current.getName()+", current count: "+current.classifier);
				for(ForeignKey outFK : current.getAllforeignKeys()){
					Table adjTable = graph.getNode(outFK.getRefTable());
					if(!explored[adjTable.getId()])
						queue.add(adjTable);
				}
			}
		}
		
	}

	//Returns list of warning messages....
	public List<String> checkClassification(){
		
		List<String> warningMsgs = new ArrayList<String>();
		Iterator<Table> it = graph.getIdDictionary().values().iterator();
	
		while(it.hasNext() && warningMsgs.size() < 10){
			Table current = it.next();
			if(current.isTransaction()){
				if(!current.getExportedKeys().isEmpty()){
					
					for(ExportedKey ek:current.getExportedKeys()){
						Table destT = graph.getNode(ek.getDestTable());
						if(!destT.isTransaction() && warningMsgs.size() < 10){
							String msg = "Transactional table "+current.getName()+" exports keys to non-transactional table "+ek.getDestTable()+" \n";
							warningMsgs.add(msg);
						}
					}
					
				}
			}
			if(!current.isClassified() && warningMsgs.size() < 10){
				String msg = "Table "+current.getName()+" could not be classified, will be automatically removed in next phase\n  -> Otherwise delete it or classify it manually\n";
				warningMsgs.add(msg);
			}
		}
		
		return warningMsgs;
	}
	
	
	public void refineModel(){
		//remove directly related components
		
	}
	
	
	
	public List<Table> loadXML() {
		return null;
	}

	public List<Table> extractMetaData() throws SQLException, Exception{
		return MetadataExtractor.fetchMetaData(MetadataExtractor.getMySqlConnection());
	}
	
	public Graph initGraph(List<Table> tables){
		graph = new Graph(tables);
		return graph;
	}
	
	public void saveToXML(Graph model){
		
	}
	
	public Graph getModel(){
		return this.graph;
	}

	public void computeHierarchies() {
		
		for(Table transac : graph.transactionalEntitites()){
			
			for(ForeignKey fk : transac.getAllforeignKeys()){
				boolean[]explored = new boolean [graph.getIdDictionary().size()];
				Table comp = graph.getNode(fk.getRefTable());
				System.out.println("Comp :"+comp.getName());
				explored[transac.getId()] = true;
				explored[comp.getId()] = true;
				List<MaxHierarchy> result = DFS(comp,explored);

				for(MaxHierarchy mh : result )
					mh.addNode(transac);
				
				maxHierarchies.addAll(result);
			}
			//System.out.println("Max entities found so far:");
			//for(MaxHierarchy mh : maxHierarchies){
				//System.out.println(">"+mh.maximalEntity().getName());
			//}
		}
		
	}
	/**
	 * Depth First Search starting in a Component and ending at a Maximal Entity 
	 * Output : a Maximal Hierarchy for Table Component
	 */
	private List<MaxHierarchy> DFS(Table root,boolean[]explored) {
		List<MaxHierarchy> result = new ArrayList<MaxHierarchy>();
		List<ForeignKey> up = root.getAllforeignKeys();
		explored[root.getId()] = true;
		
		if(up.isEmpty()){// is maximal
			MaxHierarchy maxH = new MaxHierarchy();
			maxH.addNode(root);
			result.add(maxH);
			return result;
		}
		
		for(ForeignKey fk : up){
			Table t = graph.getNode(fk.getRefTable());
			
			if(!explored[t.getId()]){
				List<MaxHierarchy> branchResult = DFS(t,explored);
				
				for(MaxHierarchy mh : branchResult)
					mh.addNode(root);
				
				result.addAll(branchResult);
			}
			
			
		}
		
		return result;
		
	}


	
	
	
	
	
	
	
	
	
	
	
}
