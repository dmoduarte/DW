package dw.UI;

import dw.cadmdm.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView.TableCell;
import javax.swing.JTable;
import javax.swing.JScrollPane;


public class EditClassification extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2510253025608887469L;
	private final JPanel contentPanel = new JPanel();
	private JTable table;

	/*
	public static void main(String[] args) {
		try {
			EditClassification dialog = new EditClassification();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	public EditClassification(List<Table> list) {
		setResizable(false);
		setBounds(100, 100, 534, 373);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			
			{
				DefaultTableModel dtm = new DefaultTableModel(0, 0);
				String header[] = new String[] { "Entity","Set To Transaction" };
				dtm.setColumnIdentifiers(header);
				table = new JTable(){ 
					public void changeSelection(int row, int column, boolean toggle, boolean extend) {
						 super.changeSelection(row, column, toggle, extend);
						 if (editCellAt(row, column))
						 {
						 Component editor = getEditorComponent();
						 editor.requestFocusInWindow();
						 }
					 }};
				table.setBounds(0, 0, 452-38, 246-38);
				table.setModel(dtm);
				
				int row = 0;
				for (Table t : list) {
					dtm.addRow(new Object[]{t.getName()});
					JCheckBox checkBox = new JCheckBox();
					checkBox.setSelected(false);
					row++;
				}
				
				TableColumn classificationColumn = table.getColumnModel().getColumn(1);
				JCheckBox checkBox = new JCheckBox();
				checkBox.setSelected(false);
				//classificationColumn.setCellEditor(new CheckBoxEditor(checkBox));
				
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setBounds(38, 38, 452, 246);
				contentPanel.add(scrollPane);
				scrollPane.add(table);
				scrollPane.setViewportView(table);
				
			}
		}
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
	}

}
class CheckBoxEditor extends DefaultCellEditor implements ActionListener {

private static final long serialVersionUID = 1L;
private JCheckBox checkBox;

private int row;
private int column;
private Table t;

public CheckBoxEditor(JCheckBox checkBox, Table t) {
    super(checkBox);
    this.t = t;
    this.checkBox = checkBox;
    this.checkBox.addActionListener(this);
}

@Override
public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
    this.row = row;
    this.column = column;
    checkBox.setSelected((Boolean) value);
    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
}

public void itemStateChanged(ActionEvent e) {
    this.fireEditingStopped();
    System.out.println("Item Changed " + row + " value is: " + checkBox.isSelected());
    if(checkBox.isSelected())
    	t.setToTransaction();
    else
    	t.setToDefault();
}

@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	
}

}
