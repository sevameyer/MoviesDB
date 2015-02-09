import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetMetaData;

public class PeoplePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	JComponent peoplePanel;
	JTable peopleList;
	JScrollPane peopleScroll;
	int selectedIndex;
	JButton addPersonButt;

	public PeoplePanel() {
		Vector<Vector<String>> rows = new Vector<>();
		/* POPULATE THE COLUMNS */
		Vector<String> columns = new Vector<>();
		columns.add("id");
		columns.add("Name");
		columns.add("Profession");

		addPersonButt = new JButton("Add");
		addPersonButt.addActionListener(new ButtonListener());
		DefaultTableModel peopleModel = new DefaultTableModel(rows, columns) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};

		peoplePanel = this;
		peopleList = new JTable(peopleModel);

		/* REMOVE ID COLUMN FROM THE VIEW */
		TableColumn tc = peopleList.getColumnModel().getColumn(0);
		peopleList.removeColumn(tc);
		peopleList.setRowHeight(25);

		peopleScroll = new JScrollPane(peopleList);

		peopleList.addMouseListener(new PeopleClickListener());
		peoplePanel.add(peopleScroll);
	}

	public void displayPeople(ResultSet rs) throws SQLException {
		DefaultTableModel dtm = (DefaultTableModel) peopleList.getModel();
		dtm.getDataVector().removeAllElements();
		parseResultSet(rs, dtm);
	}

	private void parseResultSet(ResultSet rs, DefaultTableModel dtm) throws SQLException {
		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
		int numColumns = rsmd.getColumnCount();
		Vector<String> row = new Vector<>();
		while (rs.next()) {
			row = new Vector<>(numColumns);
			for (int i = 1; i <= numColumns; i++) {
				row.add(rs.getString(i));
			}
			dtm.addRow(row);
		}
	}

	class PeopleClickListener implements MouseListener {
		JTable list;

		@Override
		public void mousePressed(MouseEvent event) {
			list = (JTable) event.getSource();
			if (SwingUtilities.isRightMouseButton(event)) {

				int index = list.rowAtPoint(event.getPoint());
				list.setRowSelectionInterval(index, index);
				selectedIndex = list.getSelectedRow();

				JPopupMenu menu = new JPopupMenu();
				JMenuItem deleteItem = new JMenuItem("Delete");

				deleteItem.addActionListener(new PeopleDeleteListener(list));
				menu.add(deleteItem);
				// moviesList.get
				list.setComponentPopupMenu(menu);

			}

		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

	class PeopleDeleteListener implements ActionListener {
		JTable list;

		public PeopleDeleteListener(JTable list2) {
			this.list = list2;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// selectedIndex = moviesList.getSelectedIndex();
			// moviesList.remove(selectedIndex);
			DefaultTableModel model = (DefaultTableModel) list.getModel();
			selectedIndex = list.getSelectedRow();
			if (selectedIndex != -1) {
				Long id = Long.parseLong((String) model.getValueAt(selectedIndex, 0));
				model.removeRow(selectedIndex);
				 /* DELETE PERSON FROM DATABASE */ 
				Connection conn = DatabaseDriver.open();
				PreparedStatement ps;
					try {
						conn.setAutoCommit(false);
						ps = (PreparedStatement) conn.prepareStatement(Query.DELETEPERSON);
						ps.setLong(1, id);
						ps.executeUpdate();
						conn.commit();
						conn.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
			}

		}
	}

	class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// new AddMovie();

		}

	}
}
