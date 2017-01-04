package dw.UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import dw.UI.Visualizer.UITree;
import dw.cadmdm.Graph;
import dw.cadmdm.Table;
import dw.cadmdm.Attribute;

public class SchemaTree extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3001176188153639186L;
	private DefaultMutableTreeNode t;
	private JTree tree;
	private UITree uiTree;
	private Map<String,Attribute> references;
	private editAttribute curr;
	public SchemaTree(UITree tree){
		this.uiTree = tree;
		references = new HashMap<String,Attribute>();
		setLayout(new BorderLayout());
		curr = null;
	}
	
	
	public void loadTree(Graph model) {
		
		t =new DefaultMutableTreeNode(model.getName());
		
		Iterator<Table> it = model.getIdDictionary().values().iterator();
		while(it.hasNext()){//for each table
			Table table = it.next();
			DefaultMutableTreeNode ti = new DefaultMutableTreeNode(table.getName());
			t.add(ti);
			createNodes(table,ti);
		}
		tree = new JTree(t);
		tree.setBounds(getBounds());
		JScrollPane scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(getBounds());
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(tree);
		
		add(scrollPane);
		
		
		tree.addMouseListener(new MouseAdapter() {
		      public void mouseClicked(MouseEvent me) {
		        doMouseClicked(me);
		      }
		    });

	}
	
	  void doMouseClicked(MouseEvent me) {
		   
		    TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
		    if (tp != null && uiTree.app.state == uiTree.app.COLAPSING){
		      String name = tp.getLastPathComponent().toString().split(" Type:")[0];
		      Attribute a = references.get(name);
		      if(a != null){
		    	  
		    	  if(curr != null)
		    		  curr.dispose();
		    	  
		    	 curr = new editAttribute(a,tp);
		      }
		    }
		    
		  }
		
	  private DefaultMutableTreeNode find(final DefaultMutableTreeNode root, final String s) {
		    @SuppressWarnings("unchecked")
		    Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
		    while (e.hasMoreElements()) {
		        DefaultMutableTreeNode node = e.nextElement();
		        if (node.toString().equalsIgnoreCase(s)) {
		        	System.out.println("s: "+node);
		            return node;
		        }
		    }
		    return null;
		}
	
	private void createNodes(Table table,DefaultMutableTreeNode top) {
	    DefaultMutableTreeNode attribute = null;
	    for(dw.cadmdm.Attribute at:table.getAttributes()){
	    	attribute = new DefaultMutableTreeNode(at.getColumnName()+" Type:("+at.getType()+")");
	    	top.add(attribute);
	    	references.put(at.getColumnName(), at);
	    }	
	    for(dw.cadmdm.Attribute at:table.getPrimaryKeys()){
	    	attribute = new DefaultMutableTreeNode("(PK) "+at.getColumnName()+" Type:("+at.getType()+")");
	    	top.add(attribute);
	    	references.put(at.getColumnName(), at);
	    }
	    for(dw.cadmdm.ForeignKey fk:table.getAllforeignKeys()){
	    	attribute = new DefaultMutableTreeNode("(FK Ref "+ fk.getColapsedRefTable() +")"+fk.getColumnName()+" Type:("+fk.getType()+")");
	    	top.add(attribute);
	    	references.put(fk.getColumnName(), fk);
	    }
	}
	
	public DefaultMutableTreeNode getRoot(){
		return t;
	}
	
	public void reload(Graph model){
		this.removeAll();
		this.loadTree(model);
	}
	
	class editAttribute extends JFrame{
		private static final long serialVersionUID = 6357262073418738690L;
		JTextField attrname;
		public editAttribute(final Attribute a, final TreePath tp){
			super("Edit Column");
			setLocationRelativeTo(uiTree.frame);
		    setLayout(new GridLayout(3,1));
		    
		    attrname = new JTextField();
		    JLabel sourceLabel =  new JLabel("Source:");
		    JLabel sourceLabel2 = new JLabel(a.fromTable());
		    JLabel nameLabel = new JLabel("Name:");
		    attrname.setText(a.getColumnName());
		    
		    JPanel buttonPanel = new JPanel(new BorderLayout());
		    JButton ok = new JButton("OK");
		   
		    ok.addActionListener(
				    new ActionListener(){
						public void actionPerformed(ActionEvent m) {
							
							String attrTxt = attrname.getText();
							String old = a.getColumnName();
							
							if(attrTxt != null && !attrTxt.equals("") && !attrTxt.equalsIgnoreCase(old)){
								
								DefaultMutableTreeNode n = find(find(t,tp.getPathComponent(1).toString()), a.getColumnName()+" Type:("+a.getType()+")");
								Table t = uiTree.app.graph.getNode(tp.getPathComponent(1).toString());
								
								if(t.containsAttribute(attrTxt)){
									uiTree.setErrorDialog("Already exists column name: "+attrTxt);
								}else{
									if(n!=null){
										references.remove(old);
							    		n.setUserObject(attrname.getText()+" Type:("+a.getType()+")");
							    	 	a.setAlias(attrname.getText());
							    	 	references.put(a.getColumnName(), a);
									}
								}
							}
							
					    	 dispose();
						}
				    });
		    
		    buttonPanel.add(ok,BorderLayout.CENTER);

		    add(sourceLabel);
		    add(sourceLabel2);
		    add(nameLabel);
		    add(attrname);
		    add(buttonPanel);
		    
		    pack();
		    setVisible(true);
		}
	}
}
