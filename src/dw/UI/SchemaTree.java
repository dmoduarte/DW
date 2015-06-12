package dw.UI;

import java.awt.BorderLayout;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import dw.cadmdm.Graph;
import dw.cadmdm.Table;

public class SchemaTree{
	private DefaultMutableTreeNode t;
	
	public SchemaTree(Graph model,JPanel contentPanel) {
		
		
		t =new DefaultMutableTreeNode("DB_Name");
		
		Iterator<Table> it = model.getIdDictionary().values().iterator();
		while(it.hasNext()){//for each table
			Table table = it.next();
			DefaultMutableTreeNode ti = new DefaultMutableTreeNode(table.getName());
			t.add(ti);
			createNodes(table,ti);
		}
		JTree tree = new JTree(t);
		tree.setBounds(contentPanel.getBounds());
		JScrollPane scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(contentPanel.getBounds());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.add(tree);
		scrollPane.setViewportView(tree);
		
		contentPanel.add(scrollPane);
	}
	
	
	private void createNodes(Table table,DefaultMutableTreeNode top) {
	    DefaultMutableTreeNode attribute = null;
	    for(dw.cadmdm.Attribute at:table.getAttributes()){
	    	attribute = new DefaultMutableTreeNode(at.getColumnName()+" ("+at.getType()+")");
	    	top.add(attribute);
	    }	
	    for(dw.cadmdm.Attribute at:table.getPrimaryKeys()){
	    	attribute = new DefaultMutableTreeNode("(PK) "+at.getColumnName()+" ("+at.getType()+")");
	    	top.add(attribute);
	    }
	    for(dw.cadmdm.ForeignKey fk:table.getAllforeignKeys()){
	    	attribute = new DefaultMutableTreeNode("(FK Ref "+ fk.getRefTable() +")"+fk.getColumnName()+" ("+fk.getType()+")");
	    	top.add(attribute);
	    }
	}
	
	public DefaultMutableTreeNode getRoot(){
		return t;
	}
	
}
