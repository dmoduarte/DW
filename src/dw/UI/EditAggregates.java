package dw.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JScrollPane;
import javax.swing.JTree;

public class EditAggregates extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EditAggregates dialog = new EditAggregates();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EditAggregates() {
		setBounds(100, 100, 362, 388);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		JTree tree = new JTree();
		DefaultMutableTreeNode t =new DefaultMutableTreeNode("Schemas");
		//for each fact table:
		for(int i = 0 ; i<3; i++){
			DefaultMutableTreeNode ti = new DefaultMutableTreeNode("Schema_"+i);
			t.add(ti);
			createNodes(ti);//add
			tree = new JTree(t);
			tree.setBounds(0, 0, 269-35, 269-28);
			scrollPane.add(tree);
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					JButton okButton = new JButton("OK");
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton cancelButton = new JButton("Cancel");
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
				}
			}
			scrollPane.add(tree);
			scrollPane.setViewportView(tree);
		}
		scrollPane.setBounds(35, 28, 269, 267);
		contentPanel.add(scrollPane);
		
		
	}
	
	
	private void createNodes(DefaultMutableTreeNode top) {
		//for each dimension related with fact table
	    DefaultMutableTreeNode factTable = null;
	    DefaultMutableTreeNode attribute = null;
	    
	    factTable = new DefaultMutableTreeNode("FactTable");//add factTable
	    top.add(factTable);
	    
	    //add all factTable columns
	    //for each factTable columns
	    for(int i = 0; i<3;i++){
	    	attribute = new DefaultMutableTreeNode("Attribute_"+i);
	    	factTable.add(attribute);
	    } 
	    
	    //add all dimensions related to fact table
	    //for each dimension
	    DefaultMutableTreeNode dimension = null;
	    for(int i = 0;i<3;i++){
	    	dimension = new DefaultMutableTreeNode("Dimension_"+i);
	    	top.add(dimension);
		    //add all Dimension columns
		    //for each Dimension columns
	    }
	    
	    
	    
	}
}
