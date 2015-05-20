package dw.cadmdm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dw.metadata.*;

public class Controler {
	private Graph graph;
	private Classifier classifier;
	
	public Controler(){
		graph = initGraph(new ArrayList<Table>());
		classifier = new Classifier();
	}
	
	public void classify(){
		System.out.println("s");
		classifier.classify(graph);
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
	
	
	
	
	
	
	
	
	
	
	
}
