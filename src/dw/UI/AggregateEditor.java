package dw.UI;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import dw.UI.Visualizer.UITree;
import dw.cadmdm.Attribute;
import dw.cadmdm.ExportedKey;
import dw.cadmdm.ForeignKey;
import dw.cadmdm.Table;


public class AggregateEditor extends JFrame {

  /**
	 * 
	 */
private static final long serialVersionUID = 1404142190895358007L;

@SuppressWarnings("rawtypes")
JList leftList,rightList;
JScrollPane spLeft, spRight;
JSplitPane splitPane;
JPanel panel;
CheckableItem[] numericalItems, groupByItems;

	
@SuppressWarnings({ "rawtypes", "unchecked" })
public AggregateEditor(Table t, final UITree tree) {
	super("New Aggregate from: "+t.getName());
	setLocationRelativeTo(tree.frame);
	
    final List<Attribute> leftAttrContainer = new ArrayList<Attribute>();
    for(Attribute a : t.getAttributes()){
    	if(a.isNumeric()){
    		leftAttrContainer.add(a);
    	}
    }
    
    List<Attribute> groupBy = new ArrayList<Attribute>();
    
    List<Attribute> rightAttrContainer = new ArrayList<Attribute>();
    for(ForeignKey fk : t.getAllforeignKeys()){
    		groupBy.add(fk);
    		rightAttrContainer.add(fk);
    }
    
    numericalItems = createData(leftAttrContainer);
    leftList = new JList(numericalItems);

    leftList.setCellRenderer(new CheckListRenderer());
    leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    leftList.setBorder(new EmptyBorder(0, 4, 0, 0));
    leftList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int index = leftList.locationToIndex(e.getPoint());
        CheckableItem item = (CheckableItem) leftList.getModel()
            .getElementAt(index);
        item.setSelected(!item.isSelected());
        Rectangle rect = leftList.getCellBounds(index, index);
        leftList.repaint(rect);
      }
    });
    
    groupByItems = createData(rightAttrContainer);
    rightList = new JList(groupByItems);

    rightList.setCellRenderer(new CheckListRenderer());
    rightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    rightList.setBorder(new EmptyBorder(0, 4, 0, 0));
    rightList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int index = rightList.locationToIndex(e.getPoint());
        CheckableItem item = (CheckableItem) rightList.getModel()
            .getElementAt(index);
        item.setSelected(!item.isSelected());
        Rectangle rect = rightList.getCellBounds(index, index);
        rightList.repaint(rect);
      }
    });
    
    JButton leftAddButton = new JButton("Add Calculated");
    leftAddButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	  
    	  final inputDialog id = new inputDialog(tree.frame);
    	  
    	  id.button.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	String expression = id.textArea.getText();
	        	String function = id.cbox.getSelectedItem().toString();
	        	String attrName = id.attrName.getText();
	        	String dataType = id.dataType.getSelectedItem().toString();
	        	
	        	if( (!expression.equals("") && expression !=null) && (!attrName.equals("") && attrName != null)  ){
		        	
	        		Attribute calculated = new Attribute(attrName,dataType,"New Table",false);
		        	calculated.setCalculated(expression);
		        	calculated.setAggregateFunction(function);
		        	calculated.setSize(20);
		        	
		        	leftAttrContainer.add(calculated);
		        	numericalItems = createData(leftAttrContainer);
		        	leftList.setListData(numericalItems);
		        	
	        	}
	        	
	        	spLeft.revalidate();
	        	spLeft.repaint();
	        	splitPane.revalidate();
	        	splitPane.repaint();
	        	panel.revalidate();
	        	panel.repaint();
	        	
	        }
	      });

      }
    });
    
    JButton finishButton = new JButton("Finish");
    finishButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
      	 
        	List<Attribute> attributes = new ArrayList<Attribute>();
        	List<Attribute> pkeys = new ArrayList<Attribute>();
        	List<ForeignKey> fkeys = new ArrayList<ForeignKey>();
        	
        	for(int i = 0; i < numericalItems.length ; i++){
        		if(numericalItems[i].isSelected()){
        			Attribute ref = numericalItems[i].getAttribute();
        			Attribute newAttr = new Attribute(ref.getColumnName(),ref.getType(),"New Table",ref.primaryKey());
        			attributes.add(newAttr);
        		}
        	}
        	
        	for(int i = 0; i < groupByItems.length ; i++){
        		if(groupByItems[i].isSelected()){
        			fkeys.add((ForeignKey) groupByItems[i].getAttribute());
        		}
        	}
        	
        	if(!attributes.isEmpty()){
        		String defName = tree.app.graph.getNode("New Table") != null ? "NewTable("+tree.app.graph.getIdDictionary().values().size()+")" : "New Table";
	        	Table aggregated = new Table(pkeys,attributes,fkeys,new ArrayList<ExportedKey>(),defName,tree.app.graph.nextID());
	        	aggregated.setFactTable();
	        	tree.app.insertTable(aggregated);
				tree.schemaTree.reload(tree.app.graph);
				tree.modelPanel.restartModel(tree.app.graph);
				tree.modelPanel.refreshModel();
        	}
        	dispose();
        }
      });
    
    spLeft= new JScrollPane(leftList);
    spRight = new JScrollPane(rightList);
    spLeft.setBorder(BorderFactory
            .createTitledBorder("Measures"));
    spRight.setBorder(BorderFactory
            .createTitledBorder("Group By"));
    panel = new JPanel(new GridLayout(2, 1));
    panel.add(leftAddButton);
    panel.add(finishButton);
    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            true, spLeft, spRight);
    splitPane.setOneTouchExpandable(true);
    
    getContentPane().add(splitPane, BorderLayout.CENTER);
    getContentPane().add(panel, BorderLayout.SOUTH);
    pack();
    setVisible(true);
  }

  private CheckableItem[] createData(List<Attribute>attrs) {
    CheckableItem[] items = new CheckableItem[attrs.size()];
    int i = 0;
    for (Attribute a : attrs) {
      items[i++] = new CheckableItem(a);
    }
    return items;
  }

  class CheckableItem {
    private Attribute attr;

    private boolean isSelected;

    public CheckableItem(Attribute attr) {
      this.attr = attr;
      isSelected = false;
    }

    public void setSelected(boolean b) {
      isSelected = b;
    }

    public boolean isSelected() {
      return isSelected;
    }
    public Attribute getAttribute(){
    	return attr;
    }
    public String toString() {
      return attr.getColumnName();
    }
    
  }

  
  
  @SuppressWarnings("rawtypes")
class CheckListRenderer extends JCheckBox implements ListCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5378608630392303417L;

	public CheckListRenderer() {
      setBackground(UIManager.getColor("List.textBackground"));
      setForeground(UIManager.getColor("List.textForeground"));
    }

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean hasFocus) {
      setEnabled(list.isEnabled());
      setSelected(((CheckableItem) value).isSelected());
      setFont(list.getFont());
      setText(value.toString());
      return this;
    }
  }
  
  class inputDialog extends JDialog implements ActionListener {
	  
	  private String[] functions = { "SUM","AVG","COUNT","MAX","MIN"};
	  
	  JTextArea textArea;
	  JTextField attrName;
	  JButton button;
	  JComboBox<String> cbox;
	  JComboBox<String> dataType;
	  /**
	 * 
	 */
	public static final long serialVersionUID = 1360651079735275408L;
	public inputDialog(JFrame parent) {
		
		this.setTitle("New Calculated Measure");
		setLocationRelativeTo(parent);
	    setLayout(new GridLayout(5,1));
	    JLabel attrLabel = new JLabel("Measure Name:");
	    JLabel exprLabel = new JLabel("Expression:");
	    JLabel dataTypeLabel = new JLabel("Data Type:");
	    JLabel cboxLabel = new JLabel("Aggregation Function:");
	    
	    attrName = new JTextField();
	   
	    textArea = new JTextArea(2,10);
	    JScrollPane scrollPane = new JScrollPane(textArea); 
	    

	    cbox = new JComboBox<String>();
	    dataType = new JComboBox<String>();
	    
	    for (int i = 0; i < functions.length; i++)
	        cbox.addItem(functions[i]);
	    
	    for(int i = 0; i < Attribute.numericType.length; i++)
	    	dataType.addItem(Attribute.numericType[i]);
		
	    add(attrLabel);
	    add(attrName);
	    add(cboxLabel);
	    add(cbox);
	    add(dataTypeLabel);
	    add(dataType);
	    add(exprLabel);
	    add(scrollPane);

	    JPanel buttonPane = new JPanel();
	    button = new JButton("OK"); 
	    buttonPane.add(button); 
	    button.addActionListener(this);
	    add(buttonPane, BorderLayout.SOUTH);
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

           
         
  