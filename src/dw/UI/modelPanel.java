package dw.UI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import dw.UI.Visualizer.UITree;
import dw.cadmdm.Controler;
import dw.cadmdm.Graph;
import dw.cadmdm.Table;

public class modelPanel extends JPanel {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4794363540800028266L;
	mxCell [] v;
	Controler app;
	UITree tree;
	public mxGraphComponent presentationModel;
	
	public modelPanel(Controler app, UITree tree){
		this.app = app;
		this.tree = tree;
		setLayout(new BorderLayout());
	}
	
	public void startModel(Graph g){
		presentationModel = loadModel(g);
		add(presentationModel,BorderLayout.CENTER);
	}
	
	public void restartModel(Graph g){
		presentationModel = loadModel(g);
		presentationModel = refreshModel();
		//add(presentationModel,BorderLayout.CENTER);
		 for(int i = 0 ; i < v.length; i++){
		    	System.out.println("---");
		    	System.out.println("-->"+v[i].getValue().toString());
		    }
	}

	public mxGraphComponent loadModel(Graph g) {
		mxGraph graph  = new mxGraph();
		
		if(g !=  null){
			
			Object parent = graph.getDefaultParent();
			v = new mxCell[g.getIdDictionary().size()];
			
			graph.getModel().beginUpdate();
			try
			{
				
				for(int i = 0; i < g.getIdDictionary().size(); i++){
					
					mxCell v1;
					if(v[i] == null){
					
					v1 = (mxCell) graph.insertVertex(parent, null, g.getIdDictionary().get(i).getName(), 20, 20, 80,
							30);
					v1.setId(String.valueOf(i));
					v[i] = v1;
					}else
						v1 = v[i];
					
					for(int j = 0; j < g.getIdDictionary().size(); j++){
						if(g.getAdjMatrix()[i][j]>0){
							
							int relNum = g.getAdjMatrix()[i][j];
							
							while(relNum > 0){
								mxCell v2;
								
								if(v[j]==null){
									 v2 = (mxCell) graph.insertVertex(parent, null, g.getIdDictionary().get(j).getName(), 240, 150,
											80, 30);
									 v2.setId(String.valueOf(j));
									 v[j] = v2;
								}else
									 v2 = v[j];
								
								graph.insertEdge(parent, null, "", v2, v1);
								relNum--;
							}
							//graph.insertEdge(parent, null, "", v1, v2);
						}
					}
				}
				//mxGraphLayout circleLayout = new mxCircleLayout(graph);
				//circleLayout.execute(parent);
				
				mxGraphLayout organicLayout = new mxFastOrganicLayout(graph);
				mxGraphLayout parallelEdgeLayout = new mxParallelEdgeLayout(graph);
				organicLayout.execute(parent);
				parallelEdgeLayout.execute(parent);
				
			    // Settings for edges
			    Map<String, Object> edge = new HashMap<String, Object>();
			   
			    //edge.put(mxConstants.STYLE_ROUNDED, true);
			    //edge.put(mxConstants.STYLE_ORTHOGONAL, true);
			    //edge.put(mxConstants.EDGESTYLE_ENTITY_RELATION, true);
			    edge.put(mxConstants.ARROW_CLASSIC, true);
			   // edge.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
			    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
			    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
			   
			   // edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
			   // edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
			    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // default is #6482B9
			    edge.put(mxConstants.STYLE_FONTCOLOR, "#446299");
			    
			    mxStylesheet edgeStyle = new mxStylesheet();
			    edgeStyle.setDefaultEdgeStyle(edge);
			    graph.setStylesheet(edgeStyle);
				
			}
			finally
			{
				graph.getModel().endUpdate();
			}
			
			graph.setCellsEditable(false);
		    graph.setAllowDanglingEdges(false);
		    graph.setAllowLoops(false);
		    graph.setCellsDeletable(true);
		    graph.setCellsCloneable(false);
		    graph.setCellsDisconnectable(false);
		    graph.setDropEnabled(false);
		    graph.setSplitEnabled(false);
		    graph.setCellsBendable(false);
		    graph.setLabelsClipped(false);
		    graph.setAutoSizeCells(false);
		    graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "snow", v);
	
			}
		
		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphComponent.getGraphControl().addMouseListener(
			new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(app.state == app.CLASSIFICATION){
				final Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				if(cell != null && cell instanceof mxCell)
					if(!(((mxCell)cell).getValue().toString()).equals("")){
						Table t = app.graph.getNode(((mxCell)cell).getValue().toString());
						
						if(t.isTransaction()){
							displayTransactionMenu(e,cell);
						}else{
							displayMenu(e,cell);
						}
						
						
					}
				}
				}
	
			private void displayMenu(MouseEvent e,final Object cell) {
				tree.initPmenu(UITree.NONTRANSACTIONALMENU);
				tree.pmenu.show(e.getComponent(), e.getX(), e.getY());
				tree.transactionalSet.addActionListener(new ActionListener(){
					  public void actionPerformed(ActionEvent e){
						  app.classify( app.graph.getNode(((mxCell)cell).getValue().toString()) );
						  refreshModel();
					  	}
					  });
			}
	
			private void displayTransactionMenu(MouseEvent e, final Object cell) {
				tree.initPmenu(UITree.TRANSACTIONALMENU);
				tree.pmenu.show(e.getComponent(), e.getX(), e.getY());
				
				tree.transactionalRemove.addActionListener(new ActionListener(){
					  public void actionPerformed(ActionEvent e){
						  tree.setLoadingSpin();
						  Table t = app.graph.getNode(((mxCell)cell).getValue().toString());
						  app.removeTable(t);
						  removeCell(v[t.getId()],t.getId());
						  refreshModel();
						  tree.unsetLoadingSpin();
					  	}
	
					 });
				tree.transactionalUnset.addActionListener(new ActionListener(){
					  public void actionPerformed(ActionEvent e){
						  tree.setLoadingSpin();
						  Table t = app.graph.getNode(((mxCell)cell).getValue().toString());
						  app.unsetTable(t);;
						  refreshModel();
						  tree.unsetLoadingSpin();
					 	}
				
					});
				}
			});
		
		//graphComponent.setSwimlaneSelectionEnabled(false);
		//graphComponent.setPageBreaksVisible(false);
		//graphComponent.setInvokesStopCellEditing(false);
		//graphComponent.setAntiAlias(false);
		//graphComponent.setAutoScroll(false);
		//graphComponent.setDragEnabled(false);
		//graphComponent.setExportEnabled(false);
		//graphComponent.setFoldingEnabled(false);
		//graphComponent.setCenterPage(false);
		//graphComponent.setAutoExtend(false);
		//graphComponent.setPanning(false);
		//graphComponent.setImportEnabled(false);
		//graphComponent.setCenterZoom(true);
		//graphComponent.setConnectable(false);
		//graphComponent.setEnabled(false);
		//graphComponent.setEscapeEnabled(false);
		//graphComponent.setEventsEnabled(false);
		graphComponent.setGridVisible(true);
		
		return graphComponent;
	}
	
	public void removeCell(mxCell cell,int id){
		mxGraph g = presentationModel.getGraph();
		mxCell [] toRemove = {cell};
		g.removeCells(toRemove);
		this.removeElement(id);
	}
	
	
	
	public mxGraphComponent refreshModel(){
		mxGraph g = presentationModel.getGraph();

		mxCell[]transac = new mxCell[v.length];
		mxCell[]comp = new mxCell[v.length];
		mxCell[]classif = new mxCell[v.length];
		mxCell[]def = new mxCell[v.length];
		
		int count = 0;
		for(int i = 0;i<app.getModel().getIdDictionary().size();i++){
			Table t = app.getModel().getIdDictionary().get(i);
			if(t.isTransaction()){
				transac[count] = v[i];
				count++;
			}
		}
		
		count = 0;
		for(int i = 0;i<app.getModel().getIdDictionary().size();i++){
			Table t = app.getModel().getIdDictionary().get(i);
			if(t.isComponent()){
				comp[count] = v[i];
				count++;
			}
		}
		
		count = 0;
		for(int i = 0;i<app.getModel().getIdDictionary().size();i++){
			Table t = app.getModel().getIdDictionary().get(i);
			if(t.isClassifier()){
				classif[count] = v[i];
				count++;
			}
		}
		
		count = 0;
		for(int i = 0;i<app.getModel().getIdDictionary().size();i++){
			Table t = app.getModel().getIdDictionary().get(i);
			if(!t.isClassified()){
				def[count] = v[i];
				count++;
			}
		}
		
		
		g.setCellStyles(mxConstants.STYLE_FILLCOLOR, "red", transac);
		g.setCellStyles(mxConstants.STYLE_FILLCOLOR, "green", comp);
		g.setCellStyles(mxConstants.STYLE_FILLCOLOR, "blue", classif);
		g.setCellStyles(mxConstants.STYLE_FILLCOLOR, "snow", def);
		//g.setCellStyles(mxConstants.STYLE_OPACITY, "20", classif);
		g.refresh();
		presentationModel.refresh();
		return presentationModel;
	}
	
	  public void removeElement(int del) {
			 mxCell[]newV = new mxCell[v.length-1];
			 	int j = 0;
				for(int i = 0;i<v.length;i++){
					
					if(i != del){
						newV[j]= v[i];
						j++;
					}
				}
			    v = newV;
			    for(int i = 0 ; i < v.length; i++){
			    	System.out.println("---");
			    	System.out.println("-->"+v[i].getValue().toString());
			    }
			}
	
}
