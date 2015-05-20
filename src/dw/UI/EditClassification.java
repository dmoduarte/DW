package dw.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.Window.Type;

public class EditClassification extends JDialog {
	private final String NONE = "none";
	private final String TRANS = "transactional";
	private final String COMP = "component";
	private final String CLASS = "classifier";
	private final JPanel contentPanel = new JPanel();
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EditClassification dialog = new EditClassification();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EditClassification() {
		setResizable(false);
		setBounds(100, 100, 534, 373);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			
			{
				DefaultTableModel dtm = new DefaultTableModel(0, 0);
				String header[] = new String[] { "Entity","Classification" };
				dtm.setColumnIdentifiers(header);
				table = new JTable();
				table.setBounds(0, 0, 452-38, 246-38);
				table.setModel(dtm);
				
				
				for (int count = 1; count <= 20; count++) {
			        dtm.addRow(new Object[] { "data", "data", "data",
			                "data", "data", "data" });
				}
				
				TableColumn classificationColumn = table.getColumnModel().getColumn(1);
				JComboBox<String> comboBox = new JComboBox<String>();
				comboBox.addItem(NONE);
				comboBox.addItem(CLASS);
				comboBox.addItem(COMP);
				comboBox.addItem(TRANS);
				classificationColumn.setCellEditor(new DefaultCellEditor(comboBox));
				
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
