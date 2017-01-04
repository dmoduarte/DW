package dw.UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dw.UI.Visualizer.UITree;
import dw.cadmdm.Attribute;
import dw.cadmdm.Table;

public class FunctionSelector extends JFrame {
	
	private String[] functions = { "SUM","AVG","COUNT","MAX","MIN"};
	
	private static final long serialVersionUID = 921279037800790717L;
	Map<String,JComboBox<String>> cboxes;
	
	public FunctionSelector(Table t,UITree tree){
		
		super("Select Aggregation Functions for: "+t.getName());
		setLocationRelativeTo(tree.frame);
		
		setLayout(new BorderLayout());
		cboxes = new HashMap<String,JComboBox<String>>();
		
		 final List<Attribute> numericalContainer = new ArrayList<Attribute>();
		    for(Attribute a : t.getAttributes()){
		    	if(a.isNumeric()){
		    		numericalContainer.add(a);
		    	}
		    }
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(numericalContainer.size(),1));
		
		for(Attribute a : numericalContainer){
			
			JComboBox<String> cbox = new JComboBox<String>();
			
			for (int i = 0; i < functions.length; i++)
		        cbox.addItem(functions[i]);
			
			cbox.setSelectedItem(a.getAggregateFunction());
			
			JLabel label = new JLabel(a.getOperationalName());
			cboxes.put(a.getOperationalName(), cbox);
			panel.add(label);
			panel.add(cbox);
		}
		
		JScrollPane sp= new JScrollPane(panel);
		getContentPane().add(sp,BorderLayout.CENTER);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	for(Attribute a : numericalContainer){
	        		String function = cboxes.get(a.getOperationalName()).getSelectedItem().toString();
	        		a.setAggregateFunction(function);
	        		dispose();
	        	}
	        }
	     });
		
		getContentPane().add(okButton,BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}
	
}
