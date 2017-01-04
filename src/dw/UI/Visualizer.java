package dw.UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import dw.cadmdm.Controler;
import dw.cadmdm.Table;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class Visualizer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UITree tree;
	private MouseListener l;
	

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
		l = null;
		tree = new UITree(this);
		loadData();
		modelPresentation();
	}
	
	private void loadData(){
		try {
			tree.app.initGraph(new ArrayList<Table>());
			tree.modelPanel.startModel(tree.app.graph);
			tree.schemaTree.loadTree(tree.app.graph);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}


	private void modelPresentation() {
		this.setTitle("CADMDM: 1/3 Presentation Phase");
		tree.nextButton.removeMouseListener(l);
		tree.initNextButtonPaneContainer();
		tree.nextButton.addMouseListener(l = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				tree.nextButton.removeMouseListener(l);
				
				if(tree.app.graph.getIdDictionary().isEmpty()){
					tree.setInfoDialog("No model found");
					modelPresentation();
				}
				
				else{
					int trFound = tree.app.classify();
					if(trFound == 0){
						tree.setErrorDialog(UITree.NOTRANSACTIONFOUND);
						}
					classifPresentation(trFound);
				}
				
			}
		});
		
	}
	
	private void classifPresentation(final int foundTransactions){
		this.setTitle("CADMDM: 2/3 Classification Phase");
		tree.nextButton.removeMouseListener(l);
		tree.initNextButtonPaneContainer();
		tree.modelPanel.refreshModel();
		
		tree.nextButton.addMouseListener( l = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				tree.nextButton.removeMouseListener(l);
				if(foundTransactions == 0){
					tree.setErrorDialog(UITree.NOTRANSACTIONFOUND);
					classifPresentation(foundTransactions);
					}else{
						List<String> msgs = tree.app.checkClassification();
						if(!msgs.isEmpty()){
							tree.showWarningMessages(msgs);
							classifPresentation(foundTransactions);
						}
						else{
							tree.app.computeHierarchies();
							tree.app.colapse();
							colapsePresentation();
						}
				}
			}

		});
	}
	
	
	private void colapsePresentation() {
		this.setTitle("CADMDM: 3/3 Derivation Phase");
		tree.nextButton.removeMouseListener(l);
		tree.initFinishButtonPaneContainer();
		tree.schemaTree.reload(tree.app.graph);
		tree.modelPanel.restartModel(tree.app.graph);
		
		
		tree.nextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				tree.initExitFrame();
			} 
		});
		
	}
	
	class UITree{
		
		public static final int FRAMEWIDTH = 900; //1220
		public static final int FRAMEHEIGHT = 600; //1200
		public static final int TRANSACTIONALMENU = 0;
		public static final int NONTRANSACTIONALMENU = 1;
		public static final int NONCLASSIFIEDMENU = 2;
		public static final int AGGMENU = 3;
		public static final String NOTRANSACTIONFOUND = "0 Transactional tables were found. User input is required";
		
		Controler app;
		JFrame frame;
		modelPanel modelPanel;
		SchemaTree schemaTree;
		JPanel contentPane;
		JInternalFrame mboxFrame;
		JPopupMenu pmenu;
		JMenuItem transactionalSet;
		JMenuItem transactionalRemove;
		JMenuItem transactionalUnset;
		JMenuItem nonclassifiedRemove;
		JMenuItem aggregationSet;
		JMenuItem functionsSet;
		JMenuItem nameSet;
		JFileChooser browser;
		JPanel nextpanel;
		JButton nextButton;
		
		
		//Init UI Components
		public UITree(JFrame frame){
			app = new Controler();
			this.frame = frame;
			
			//Init JFrame
			frame.setResizable(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setBounds(0, 0, FRAMEWIDTH, FRAMEHEIGHT);
			
			//Init Components
			initContent();
			initModelPanel();
		}
		
		public void initContent() {

			this.contentPane = new JPanel();
			this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			frame.setContentPane(contentPane);
			
			this.contentPane.setLayout(new BorderLayout());	
			
			JToolBar toolBar = new JToolBar();
			this.contentPane.add(toolBar,BorderLayout.NORTH);
			
			JMenuBar menuBar = new JMenuBar();
			toolBar.add(menuBar);
			
			JMenu mnOptions = new JMenu("Options");
			menuBar.add(mnOptions);
			
			JMenuItem mntmImportMetadata = new JMenuItem("Import from DB");
			mnOptions.add(mntmImportMetadata);
			
			JMenuItem mntmImportXML = new JMenuItem("Import from XML");
			mnOptions.add(mntmImportXML);
			
			JMenuItem mntmSaveXML = new JMenuItem("Save to XML");
			mnOptions.add(mntmSaveXML);
			
			mntmImportXML.addActionListener(new OpenXML());
			mntmSaveXML.addActionListener(new SaveXML());
			mntmImportMetadata.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					initLoadDBMenu();
				} 
			});
			
			nextpanel = new JPanel();
			JPanel container = new JPanel();
			container.setLayout(new BorderLayout());
			
			nextButton = new JButton("Next");
			nextpanel.add(nextButton);
			container.add(nextpanel,BorderLayout.SOUTH);
			contentPane.add(container, BorderLayout.EAST);
			
			
		}
		
		public void initModelPanel() {
			
			this.modelPanel = new modelPanel(this);
			this.schemaTree = new SchemaTree(this);
		
		    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
	                true, schemaTree, modelPanel);
		    
	        splitPane.setOneTouchExpandable(true);
	        this.contentPane.add(splitPane,BorderLayout.CENTER);
			
		}
		
		
		public void initPmenu(int menu) {
			
			pmenu = new JPopupMenu();
			if(menu == TRANSACTIONALMENU){
				transactionalRemove = new JMenuItem("Remove Transaction");
				transactionalUnset = new JMenuItem("Unset Transaction");
				
				pmenu.add(transactionalRemove);
				pmenu.add(transactionalUnset);
			}else if(menu == NONTRANSACTIONALMENU){
				transactionalSet = new JMenuItem("Set to Transaction");
				pmenu.add(transactionalSet);
			}
			else if(menu == NONCLASSIFIEDMENU){
				nonclassifiedRemove = new JMenuItem("Remove Entity");
				pmenu.add(nonclassifiedRemove);
				
				transactionalSet = new JMenuItem("Set to Transaction");
				pmenu.add(transactionalSet);
			}else if(menu == AGGMENU){
				aggregationSet = new JMenuItem("New Aggregate");
				pmenu.add(aggregationSet);
				
				functionsSet = new JMenuItem("Edit Functions");
				pmenu.add(functionsSet);
				
				nameSet = new JMenuItem("Change Name");
				pmenu.add(nameSet);
			}
			
		}
		
		public void setInfoDialog(String msg){
			JOptionPane.showMessageDialog(frame,msg);
		}
		
		public void setErrorDialog(String msg){
			JOptionPane.showMessageDialog(frame,msg,"",JOptionPane.ERROR_MESSAGE);
		}
		

		public void initNextButtonPaneContainer() {
			nextpanel.remove(nextButton);
			nextButton = new JButton("Next");
			nextpanel.add(nextButton);
			nextpanel.repaint();
			nextpanel.revalidate();
			repaint();
			revalidate();
		}
		
		public void initFinishButtonPaneContainer() {
			nextpanel.remove(nextButton);
			nextButton = new JButton("Finish");
			nextpanel.add(nextButton);
			nextpanel.repaint();
			nextpanel.revalidate();
			repaint();
			revalidate();
		}
		
		
		public void initExitFrame(){
			new exitFrame();
		}
		
		public void initLoadDBMenu(){
			new loadDBFrame();
		}
		
		public void showWarningMessages(List<String> msgs){
			String res = "";
			for(String msg:msgs){
				res += msg; 
			}
			JOptionPane.showMessageDialog(frame,res,"User input required",JOptionPane.ERROR_MESSAGE);
		}
		
		public JButton getNextPanelButton(){
			return this.nextButton;
		}
		
		class OpenXML implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		      JFileChooser c = new JFileChooser();
		      int rVal = c.showOpenDialog(frame);
		      if (rVal == JFileChooser.APPROVE_OPTION) {
		    	  app = new Controler();
		    	  int state = app.loadXML(c.getCurrentDirectory().toString()+"/"+c.getSelectedFile().getName());
		    	 if(state < 0 || state > app.CLASSIFICATION)
		    		 setErrorDialog("Failed Loading XML");
		    	 else{
		    		 
		    		 tree.schemaTree.reload(app.graph);
		    		 tree.modelPanel.restartModel(app.graph);
		    		 setInfoDialog("Model loaded!");
		    		 
		    		 if(app.state == app.PRESENTATION)
		    			 modelPresentation();
		    		 else if(app.state == app.CLASSIFICATION)
		    			 classifPresentation(app.graph.transactionalEntitites().size());
		    		 else if(app.state == app.COLAPSING)
		    			 colapsePresentation();
		    		 
		    	 }
		      }
		      if (rVal == JFileChooser.CANCEL_OPTION) {
		      }
		    }
		  }

		  class SaveXML implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		      JFileChooser c = new JFileChooser();
		      int rVal = c.showSaveDialog(frame);
		      if (rVal == JFileChooser.APPROVE_OPTION) {
		    	if(!app.saveToXML(app.graph,c.getCurrentDirectory().toString()+"/"+c.getSelectedFile().getName()))
		    		setErrorDialog("Failed saving Model to XML");
		    	else setInfoDialog("XML file saved!");
		      }
		      if (rVal == JFileChooser.CANCEL_OPTION) {
		      }
		    }
		  }
		  
		  
		  class GenScript implements ActionListener {
			    public void actionPerformed(ActionEvent e) {
			      JFileChooser c = new JFileChooser();
			      int rVal = c.showSaveDialog(frame);
			      if (rVal == JFileChooser.APPROVE_OPTION) {
			    	if(!app.generateScript(app.graph,c.getCurrentDirectory().toString()+"/"+c.getSelectedFile().getName()))
			    		setErrorDialog("Failed generating SQL script");
			    	
			    	else setInfoDialog("Script generated!");
			      }
			      if (rVal == JFileChooser.CANCEL_OPTION) {
			      }
			    }
			  }
		
		class exitFrame extends JFrame{
			
			public static final long serialVersionUID = 1360651079735275408L;
			public exitFrame() {
				
			    setLayout(new GridLayout(2,0));
			    setLocationRelativeTo(tree.frame);
			    JButton buttonScript = new JButton("Generate Script"); 
			    buttonScript.addActionListener(new GenScript());
			    
			    JButton buttonXML = new JButton("Save XML"); 
			    buttonXML.addActionListener(new SaveXML());
			    
			    add(buttonScript);
			    add(buttonXML);
			    pack();
			    setVisible(true);
			  }
			  
			}
		class loadDBFrame extends JFrame {
			JTextField login;
			JPasswordField pw;
			JTextField server,port,db;
			int response;
			
			public static final int CANCEL = -1;
			public static final int LOAD = 1;
			
			public static final long serialVersionUID = 1360651079735275408L;
			
			public loadDBFrame() {
				
				super("Load MetaData-MySQL");
				setLocationRelativeTo(tree.frame);
			    setLayout(new GridLayout(6,1));
				
			    JLabel loginLabel = new JLabel("Login: ");
			    JLabel pwLabel = new JLabel("Password: ");
			    JLabel servLabel = new JLabel("Server: ");
			    JLabel portLabel = new JLabel("Port: ");
			    JLabel dbLabel = new JLabel("DataBase: ");
			    
			    login = new JTextField();
			    pw = new JPasswordField(10);
			    server = new JTextField();
			    port = new JTextField();
			    db = new JTextField();
			    
			    //JPanel buttonPanel = new JPanel();
			    //buttonPanel.setLayout(new GridLayout(0,2));
			    JButton load = new JButton("Load");
			    
			    load.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						
						String logintxt = login.getText();
						String pwtxt = new String(pw.getPassword());
						String servertxt = server.getText();
						String porttxt = port.getText();
						String dbtxt = db.getText();
						
						if(logintxt != null && pwtxt != null && servertxt != null && porttxt != null && dbtxt != null)
							if(!logintxt.equals("") && !pwtxt.equals("") && !servertxt.equals("") && !porttxt.equals("") && !dbtxt.equals("")){
								
								List<Table> extraction = app.extractMetaData(servertxt,porttxt,dbtxt,logintxt,pwtxt);
								if( extraction == null )
									tree.setErrorDialog("Failed Metadata Exctraction");
								else{
									app = new Controler();
									app.initGraph(extraction);
									app.graph.setModelName(dbtxt);
									modelPanel.restartModel(app.graph);
									schemaTree.reload(app.graph);
									setInfoDialog("Metadata Loaded!");
									modelPresentation();
								}
								
							}else setInfoDialog("You did not fill all fields");
						else setInfoDialog("You did not fill all fields");
						
						dispose();
					}
				});
			    
			    JButton cancel = new JButton("Cancel"); 
			    cancel.addActionListener(
			    new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("Cancel");
						dispose();
					}
			    });
			    
			    add(loginLabel);
			    add(login);
			    add(pwLabel);
			    add(pw);
			    add(servLabel);
			    add(server);
			    add(portLabel);
			    add(port);
			    add(dbLabel);
			    add(db);
			    add(cancel);
			    add(load);
			    pack();
			    setVisible(true);
			}
		}
		
	}
	
	
	
}
