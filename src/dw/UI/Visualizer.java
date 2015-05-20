package dw.UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.CardLayout;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import dw.cadmdm.Controler;
import dw.cadmdm.Graph;
import dw.cadmdm.Table;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Visualizer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UITree tree;
	private mxGraphComponent graph;
	private Controler app;
	mxCell [] v;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Visualizer frame = new Visualizer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Visualizer() {
		
		app = new Controler();
		tree = new UITree();
		modelPresentation();
		
		/*
		JPanel EditClassf = new JPanel();
		root.add(EditClassf, "name_2774569172174");
		EditClassf.setLayout(null);
		
		final JPanel editpanel = new JPanel();
		editpanel.setBounds(607, 607, 153, 37);
		contentPane.add(editpanel);
		
		JButton btnEdit = new JButton("Edit Classification");
		editpanel.add(btnEdit);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(0, 0, 401, 30);
		contentPane.add(toolBar);
		
		JMenuBar menuBar = new JMenuBar();
		toolBar.add(menuBar);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenuItem mntmImportMetadata = new JMenuItem("Import MetaData");
		mnOptions.add(mntmImportMetadata);
		
		
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});*/
		
	
	
	}
	
	private void modelPresentation() {
		insertGraph(tree.getModelPresentationPane());
		
		CardLayout card = (CardLayout) tree.getRootPane().getLayout();
		card.show(tree.getRootPane(), "ModelPanel");
	
		tree.getNextPanelButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//passar para proximo card
				classifPresentation();
			}
		});
		
		
	}
	
	private void classifPresentation(){
		app.classify();
		System.out.println("wut");
		tree.getClassifPresentationPane().add(this.refreshGraph(),"ClassifiedModel");
		
		CardLayout card = (CardLayout) tree.getRootPane().getLayout();
		card.show(tree.getRootPane(), "ClassificationPanel");
		
		
		tree.getNextPanelButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//passar para proximo card
				//classifPresentation();
			}
		});
		
	}
	
	private void insertGraph(JPanel panel){
		Graph g = null;
		try {
			List<Table> tables = app.extractMetaData();
			g = app.initGraph(tables);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		graph = graph(panel,g);
		panel.add(graph, "ModelGraph");
	}

	private mxGraphComponent graph(JPanel ModelPanel, Graph g){
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
					
					v1 = (mxCell) graph.insertVertex(parent, null, g.getIdDictionary().get(i).getName(), 20+i, 20+i, 80+i,
							30+i);
					v1.setId(String.valueOf(i));
					v[i] = v1;
					}else
						v1 = v[i];
					
					for(int j = 0; j < g.getIdDictionary().size(); j++){
						//System.out.println("Table : "+g.getIdDictionary().get(i).getName()+" has rel with: "+g.getIdDictionary().get(j).getName()+": "+ (g.getAdjMatrix()[i][j]>0) );
						if(g.getAdjMatrix()[i][j]>0){
							
							mxCell v2;
							
							if(v[j]==null){
								 v2 = (mxCell) graph.insertVertex(parent, null, g.getIdDictionary().get(j).getName(), 240+j, 150+j,
										80+j, 30+j);
								 v2.setId(String.valueOf(j));
								 v[j] = v2;
							}else
								 v2 = v[j];
							
							graph.insertEdge(parent, null, "Edge", v1, v2);
						}
					}
				}
				//mxGraphLayout circleLayout = new mxCircleLayout(graph);
				//circleLayout.execute(parent);
				
				mxGraphLayout organicLayout = new mxFastOrganicLayout(graph);
				organicLayout.execute(parent);
				
				System.out.println("visualizer nr of tables: "+g.getIdDictionary().size());
				
			    // Settings for edges
			    Map<String, Object> edge = new HashMap<String, Object>();
			    edge.put(mxConstants.STYLE_ROUNDED, true);
			    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
			    edge.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
			    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
			    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
			   
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
		    graph.setCellsDeletable(false);
		    graph.setCellsCloneable(false);
		    graph.setCellsDisconnectable(false);
		    graph.setDropEnabled(false);
		    graph.setSplitEnabled(false);
		    graph.setCellsBendable(false);
		    graph.setLabelsClipped(false);
	
			}
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
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
		//graphComponent.setCenterZoom(false);
		//graphComponent.setConnectable(false);
		//graphComponent.setEnabled(false);
		//graphComponent.setEscapeEnabled(false);
		//graphComponent.setEventsEnabled(false);
		graphComponent.setGridVisible(true);
		return graphComponent;
	}
	
	private mxGraphComponent refreshGraph(){
		mxGraph g = graph.getGraph();
		//Object[] cells = g.getChildCells(g.getDefaultParent());
		
		mxCell[]transac = new mxCell[v.length];
		mxCell[]comp = new mxCell[v.length];
		mxCell[]classif = new mxCell[v.length];;
		
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
		
		g.setCellStyles(mxConstants.STYLE_FILLCOLOR, "red", transac);
		g.setCellStyles(mxConstants.STYLE_FILLCOLOR, "green", comp);
		g.setCellStyles(mxConstants.STYLE_FILLCOLOR, "blue", classif);
		graph.refresh();
		return graph;
	}
	
	
	private class UITree{
		
		private JPanel modelPresentation;
		private JPanel classificationPresentation;
		private JPanel contentPane;
		private JButton nextButton;
		private JPanel root;
		private JPanel nextpanel;
		private JButton next;
		
		
		
		//Init UI Components
		public UITree(){
			
			//Init JFrame
			setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 946, 682);
			
			//Init Components
			initContentPane();
			initNextButtonPaneContainer();
			initRootPane();
			initModelPresentationPane();
			initClassifPresentationPane();
			
		}
		
		private void initContentPane() {
			this.contentPane = new JPanel();
			this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			this.contentPane.setLayout(null);	
			
			JToolBar toolBar = new JToolBar();
			toolBar.setBounds(0, 0, 401, 30);
			contentPane.add(toolBar);
			
			JMenuBar menuBar = new JMenuBar();
			toolBar.add(menuBar);
			
			JMenu mnOptions = new JMenu("Options");
			menuBar.add(mnOptions);
			
			JMenuItem mntmImportMetadata = new JMenuItem("Import MetaData");
			mnOptions.add(mntmImportMetadata);
			
		}

		private void initNextButtonPaneContainer() {
			nextpanel = new JPanel();
			nextpanel.setBounds(607, 607, 153, 37);
			next = new JButton("Next");
			nextpanel.add(next);
			contentPane.add(nextpanel);
			//next.setBounds(770, 610, 111, 23);
		}

		private void initRootPane() {
			this.root = new JPanel();
			this.root.setBounds(10, 41, 910, 555);
			this.contentPane.add(root);
			this.root.setLayout(new CardLayout(0, 0));
		}

		private void initClassifPresentationPane() {
			this.classificationPresentation = new JPanel();
			//this.classificationPresentation.setBackground(Color.RED);
			this.classificationPresentation.setLayout(new CardLayout(0, 0));
			root.add(this.classificationPresentation, "ClassificationPanel");
			//this.classificationPresentation.setVisible(false);
		}
		
		private void initModelPresentationPane(){
			this.modelPresentation = new JPanel();
			this.modelPresentation.setLayout(new CardLayout(0, 0));
			root.add(this.modelPresentation, "ModelPanel");
			//this.modelPresentation.setVisible(false);
		}
		
		private JPanel getContentPane(){
			return this.contentPane;
		}
		
		private JPanel getRootPane(){
			return this.root;
		}
		
		private JPanel getModelPresentationPane(){
			return this.modelPresentation;
		}
		
		private JPanel getClassifPresentationPane(){
			return this.classificationPresentation;
		}
		
		private JPanel getNextButtonContainer(){
			return this.nextpanel;
		}
		
		private JButton getNextPanelButton(){
			return this.next;
		}
		
	}
	
	
	
}
