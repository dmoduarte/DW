package dw.UI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import dw.cadmdm.Controler;
import dw.cadmdm.Table;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class Visualizer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UITree tree;
	//private mxGraphComponent presentationModel;
	private MouseListener l;
	private Controler app;

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
		tree = new UITree(this);
		l = null;
		loadData();
		modelPresentation();
	
	}
	
	private void loadData(){
		try {
			List<Table> tables = app.extractMetaData();
			app.initGraph(tables);
			tree.modelPanel.startModel(app.graph);
			tree.loadSchemaTree();
			//loadGraph(tree.getModelPresentationPane());
			//loadSchemaTree(tree.getSchemaPanel());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}


	private void modelPresentation() {
		l = null;
	
		tree.nextButton.addMouseListener(l = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				tree.nextButton.removeMouseListener(l);
				tree.setLoadingSpin();
				int trFound = app.classify();
				if(trFound == 0){
					tree.setErrorDialog(UITree.NOTRANSACTIONFOUND);
					}
				tree.unsetLoadingSpin();
				classifPresentation(trFound);
			}
		});
		
		
	}
	
	private void classifPresentation(final int foundTransactions){
		tree.nextButton.removeMouseListener(l);
		tree.modelPanel.refreshModel();
		
		tree.nextButton.addMouseListener( l = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				tree.nextButton.removeMouseListener(l);
				if(foundTransactions == 0){
					tree.setErrorDialog(UITree.NOTRANSACTIONFOUND);
					classifPresentation(foundTransactions);
					}else{
						tree.setLoadingSpin();
						List<String> msgs = app.checkClassification();
						if(!msgs.isEmpty()){
							tree.unsetLoadingSpin();
							tree.showWarningMessages(msgs);
							classifPresentation(foundTransactions);
						}
						else{
							app.computeHierarchies();
							app.colapse();
							tree.unsetLoadingSpin();
							colapsePresentation();
						}
						
				}
			}

		});
		
		
	}
	
	
	private void colapsePresentation() {
		tree.nextButton.removeMouseListener(l);
		
		tree.modelPanel.refreshModel();
		
		
	}
	
	
	
	class UITree{
		
		public static final int FRAMEWIDTH = 1200;
		public static final int FRAMEHEIGHT = 700;
		public static final int TRANSACTIONALMENU = 0;
		public static final int NONTRANSACTIONALMENU = 1;
		public static final String NOTRANSACTIONFOUND = "0 Transactional tables were found. User input is required";
		
		waitDialog dial;
		warningDialog warDial;
		JFrame frame;
		modelPanel modelPanel;
		JPanel contentPane;
		JPanel schemaPanel;
		JPopupMenu pmenu;
		JMenuItem transactionalSet;
		JMenuItem transactionalRemove;
		JMenuItem transactionalUnset;
		JPanel nextpanel;
		JButton nextButton;
		
		//Init UI Components
		public UITree(JFrame frame){
			this.frame = frame;
			//Init JFrame
			frame.setResizable(false);
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
			this.contentPane.setLayout(null);	
			
			JToolBar toolBar = new JToolBar();
			toolBar.setBounds(0, 0, (int) Math.round(0.08*FRAMEWIDTH), (int) Math.round(0.04*FRAMEHEIGHT));
			this.contentPane.add(toolBar);
			
			JMenuBar menuBar = new JMenuBar();
			toolBar.add(menuBar);
			
			JMenu mnOptions = new JMenu("Options");
			menuBar.add(mnOptions);
			
			JMenuItem mntmImportMetadata = new JMenuItem("Import MetaData");
			mnOptions.add(mntmImportMetadata);
			
			initNextButtonPaneContainer();
		}
		
		public void initModelPanel() {
			this.modelPanel = new modelPanel(app,this);
			this.schemaPanel = new JPanel();
			
			this.modelPanel.setBounds(270,1,910, 600);
			this.schemaPanel.setBounds(0,30,290,570);
			this.contentPane.add(modelPanel);
			this.contentPane.add(schemaPanel);
			
		}
		
		public void loadSchemaTree() {
			new SchemaTree(app.graph,schemaPanel);
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
		}
		
		public void setErrorDialog(String msg){
			//new InfoDialog(msg);
			JOptionPane.showMessageDialog(frame,msg,"",JOptionPane.ERROR_MESSAGE);
		}
		

		public void initNextButtonPaneContainer() {
			nextpanel = new JPanel();
			nextpanel.setBounds(1000, 650, 153, 50);
			nextButton = new JButton("Next");
			nextpanel.add(nextButton);
			contentPane.add(nextpanel);
			//next.setBounds(770, 610, 111, 23);
		}
		
		public void showWarningMessages(List<String> msgs){
			//this.warDial = new warningDialog(msg);
			String res = "";
			for(String msg:msgs){
				res += msg; 
			}
			JOptionPane.showMessageDialog(frame,res,"",JOptionPane.ERROR_MESSAGE);
		}
		
		public void setLoadingSpin(){
			this.dial = new waitDialog();
		}
		
		public void unsetLoadingSpin(){
			this.dial.unset();
		}
		
		
		public JPanel getContentPane(){
			return this.contentPane;
		}
		
		public JButton getNextPanelButton(){
			return this.nextButton;
		}
		
		
		public JPanel getSchemaPanel(){
			return this.schemaPanel;
		}
		
		class waitDialog extends JDialog{
			/**
			 * 
			 */
			public static final long serialVersionUID = -6730979993111146504L;
			ImageIcon loading;
			JLabel lab;
			
			public waitDialog(){
				setUndecorated(true);
				int dialogHeight = (int) Math.round(0.3*FRAMEHEIGHT);
				int dialogWidth =  (int)Math.round(0.3*FRAMEWIDTH);
			    int x = (int) Math.round(FRAMEWIDTH/2) - dialogWidth/2;
				int y = (int) Math.round(FRAMEHEIGHT/2) - dialogHeight/2;
				setBounds(x,y,dialogWidth,dialogHeight);
				loading = new ImageIcon("icons/ajax-loader.gif");
				lab = new JLabel("Please Wait... ", loading, JLabel.CENTER);
				add(lab);
				setVisible(true);
			}
			
			public void unset(){
				removeAll();
				dispose();
			}
			
		}
		
		class warningDialog extends JDialog implements ActionListener {
			  /**
			 * 
			 */
			public static final long serialVersionUID = 1360651079735275408L;
			public warningDialog(final List<String> msgs) {
				  
				int dialogHeight = (int) Math.round(0.3*FRAMEHEIGHT);
				int dialogWidth =  (int)Math.round(0.3*FRAMEWIDTH);
			    int x = (int) Math.round(FRAMEWIDTH/2) - dialogWidth/2;
				int y = (int) Math.round(FRAMEHEIGHT/2) - dialogHeight/2;
				setBounds(x,y,dialogWidth,dialogHeight);
			    setLayout(new GridLayout(msgs.size(),0));
			  
				for(String msg:msgs){
					add(new JTextField(msg,JLabel.TRAILING));
				}
			    
			    JPanel buttonPane = new JPanel();
			    JButton button = new JButton("OK"); 
			    buttonPane.add(button); 
			    button.addActionListener(this);
			    getContentPane().add(buttonPane, BorderLayout.SOUTH);
			    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			    pack(); 
			    setVisible(true);
			  }
			  public void actionPerformed(ActionEvent e) {
			    setVisible(false); 
			    dispose(); 
			  }
			  
			}
		
		
	}
	
	
	
}
