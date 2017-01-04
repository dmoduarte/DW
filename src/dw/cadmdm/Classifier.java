package dw.cadmdm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Classifier {
	
	private Graph currentModel;
	
	public Classifier(){
		this.currentModel = null;
	}
	
	public Classifier(Graph g){
		this.currentModel = g;
	}
	
	public void classify(Table t) {//Table t manually classified as transaction
		setTransaction(t);
	}
	
	public int classify(Graph model){
		int trFound = 0;
		currentModel = model;
		Iterator<Table> it = currentModel.getIdDictionary().values().iterator();
		while(it.hasNext()){
			Table t = it.next();
			if(t.isTransaction())
				continue;
			if(verifyTransaction(t)){
				setTransaction(t);
				trFound++;
			}
		}
		return trFound;
	}

	private void setTransaction(Table t) {
		
		List<String> components = t.setToTransaction();
		boolean[]explored = new boolean [currentModel.getIdDictionary().size()];
		
		for(String tname : components){//directly related tables are set to component
			Table component = currentModel.getNode(tname);
			setComponent(component);
			BFS(component,explored);
		}
	}

	private void setComponent(Table t) {
			t.setToComponent();
	}
	
	/**
	 * Breadth First Search Algorithm
	 * @param root
	 * @param explored
	 */
	private void BFS(Table root, boolean[]explored) {
		
		Queue<Table> queue = new LinkedList<Table>();
		for(ForeignKey outFK : root.getAllforeignKeys()){
			Table child = currentModel.getNode(outFK.getRefTable());
			if(!explored[child.getId()])
				queue.add(child);
		}
		
		while(!queue.isEmpty()){
			
			Table current = queue.remove();
			
			if(!explored[current.getId()]){ 
				current.setToClassifier();
				explored[current.getId()] = true;
				for(ForeignKey outFK : current.getAllforeignKeys()){
					Table adjTable = currentModel.getNode(outFK.getRefTable());
					if(!explored[adjTable.getId()])
						queue.add(adjTable);
				}
			}
		}
		
	}


	public boolean verifyTransaction(Table t) {
		if(t.getAllforeignKeys().size() >= 0){
			if(t.hasNumeric()){
				if(t.hasDateTypes() || isRelatedWithDate(t) ){
					return true;
				}
				return false;
			}
		
		}
		return false;
	}

	private boolean isRelatedWithDate(Table t) {
		for(ForeignKey fk : t.getAllforeignKeys()){
			Table outT = this.currentModel.getNode(fk.getRefTable());
			if(outT.hasDateTypes())
				return true;
		}
		return false;
	}

	
}
