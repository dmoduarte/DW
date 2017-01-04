package dw.cadmdm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
	
	public void insertTable(Table t){
		graph.insertTable(t);
	}
	
	public void colapse(){
		this.state = COLAPSING;
		int counter = 0;
		
		Set<Table> MDM = new HashSet<Table>();//MultiDimensionalModel entities
		
		//star-schema..
		for(MaxHierarchy mh: maxHierarchies){
			mh.print();
			Table dim = mh.collapse();
			if(dim == null)continue;
			if(MDM.add(dim)){
				dim.setId(counter++);
			}
		}
		
		for(Table dim : MDM){
			dim.setAlias("DIM_"+dim.getOperationalName());
			dim.setToDimension();;
		}
		
		for(Table transac : graph.transactionalEntitites()){
			if(MDM.add(transac)){
				transac.setId(counter++);
				transac.setFactTable();
			}
		}
		
		String modelName = this.graph.getName(); 
		this.graph = new Graph(new ArrayList<Table>(MDM));
		this.graph.setModelName(modelName);
		
		for(Table transac : graph.factTables())
			updateForeignKeys(transac);

	}
	

	private void updateForeignKeys(Table transac) {
		for(ForeignKey fk : transac.getAllforeignKeys()){
			Table t = graph.getNode(fk.getRefTable());
			if(t!= null && t.isDimension())
				fk.setAliasRefTable("DIM_"+fk.getRefTable());
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
				child.removeExportedKey(parent.getOperationalName());
				if(parent.isTransaction()){
					child.unset(child.COMP);
					BFS(child,explored);
				}
			}
		}
		
		//remove all relationships from this table
		for(ExportedKey ek : parent.getExportedKeys()){
			Table child = graph.getNode(ek.getDestTable());
			if(child != null){
				child.removeRelationShip(parent.getOperationalName());
			}
		}

		graph.removeTable(parent);
		
		
	}
	
	private void BFS(Table root,boolean[]explored) {
		Queue<Table> queue = new LinkedList<Table>();
		for(ForeignKey outFK : root.getAllforeignKeys()){
			Table child = graph.getNode(outFK.getRefTable());
			if(!explored[child.getId()])
				queue.add(child);
		}
		
		while(!queue.isEmpty()){
			Table current = queue.remove();
			if(!explored[current.getId()]){ 
				current.unset(current.CLASS);
				explored[current.getId()] = true;
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
							String msg = "Transactional table "+current.getOperationalName()+" exports keys to non-transactional table "+ek.getDestTable()+" \n";
							warningMsgs.add(msg);
						}
					}
					
				}
			}
			if(!current.isClassified() && warningMsgs.size() < 10){
				String msg = "Table "+current.getOperationalName()+" could not be classified\n";
				warningMsgs.add(msg);
			}
			
			if(current.getAllforeignKeys().isEmpty() && current.getExportedKeys().isEmpty()){
				String msg = "Table "+current.getOperationalName()+" Has no Relations\n";
				warningMsgs.add(msg);
			}
			
		}
		
		return warningMsgs;
	}
	
	//TODO
	public List<Attribute> searchSubDimentions(){
		List<Attribute> attr = new ArrayList<Attribute>();
		for(Table t : graph.transactionalEntitites()){
			for(Attribute a : t.getAttributes()){
				if(!a.isNumeric())
					attr.add(a);
			}
		}
		return attr;
	}
	
	public boolean generateScript(Graph model, String path){
		ScriptGenerator sgen = new ScriptGenerator();
		try {
			sgen.generateScript(model, path);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int loadXML(String path) {
		try {
			List<Table> bucket = new ArrayList<Table>();
			XMLLoader xml = new XMLLoader();
			this.state = xml.LoadOLTPModel(path, bucket);
			
			if(state != this.CLASSIFICATION && state != this.PRESENTATION && state != this.COLAPSING)
				return -1;
			
			this.classifier = new Classifier(initGraph(bucket));
			graph.setModelName(xml.modelName);
			
			for(Table t : bucket)
				t.printTable();
			
			return state;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<Table> extractMetaData(String server,String port,String DB,String userName,String pw){
		MetadataExtractor mde;
		try {
			mde = new MetadataExtractor(server,port,DB,userName,pw);
			List<Table> result = mde.fetchMetaData();
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Graph initGraph(List<Table> tables){
		graph = new Graph(tables);
		return graph;
	}
	
	public boolean saveToXML(Graph model, String path){
		try {
			XMLCreator xml = new XMLCreator();
			
			if(state == PRESENTATION || state == CLASSIFICATION)
				xml.saveOLTPModel(model,state,path);
			else if(state == COLAPSING)
				xml.saveMDModel(model,state, path);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Graph getModel(){
		return this.graph;
	}

	public void computeHierarchies() {
		for(Table transac : graph.transactionalEntitites()){
			boolean[]explored = new boolean [graph.getIdDictionary().size()];
			List<MaxHierarchy> result = DFS(transac,explored);
			maxHierarchies.addAll(result);
		}
	}
	/**
	 * Depth First Search starting in a Transactional and ending in a Maximal Entity 
	 * Output : a set of Maximal Hierarchies for Table Transactional
	 */
	private List<MaxHierarchy> DFS(Table root,boolean[]explored) {
		List<MaxHierarchy> result = new ArrayList<MaxHierarchy>();
		List<ForeignKey> up = root.getAllforeignKeys();
		
		if(up.isEmpty() || explored[root.getId()]){// is maximal
			MaxHierarchy maxH = new MaxHierarchy();
			maxH.addNode(new HierarchyNode(root,null));
			result.add(maxH);
			explored[root.getId()] = false;
			return result;
		}
		
		explored[root.getId()] = true;
		
		for(ForeignKey fk : up){
			Table t = graph.getNode(fk.getRefTable());
			if(!explored[t.getId()]){
				List<MaxHierarchy> branchResult = DFS(t,explored);
				
				for(MaxHierarchy mh : branchResult){
					mh.addNode(new HierarchyNode(root,fk));
				}
				
				result.addAll(branchResult);
			}
		}
		
		explored[root.getId()] = false;
		return result;
		
	}
	
}
