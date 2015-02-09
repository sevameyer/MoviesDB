import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
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

public class MoviesPanel extends JPanel implements OnDatabaseChange {
	private static final long serialVersionUID = 1L;

	JComponent moviesPanel;
	JTable moviesList;
	JScrollPane moviesScroll;
	int selectedIndex;
	JButton addMovieButt;

	public MoviesPanel() {

		Vector<Vector<String>> rows = new Vector<>();

		/* POPULATE THE COLUMNS */
		Vector<String> columns = new Vector<>();
		columns.add("id");
		columns.add("Title");
		columns.add("Duration");
		columns.add("Year");
		columns.add("Genre");

		/* MAKE TABLE NON EDITABLE */
		DefaultTableModel moviesModel = new DefaultTableModel(rows, columns) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		addMovieButt = new JButton("Add");
		addMovieButt.addActionListener(new ButtonListener());

		moviesPanel = this;

		moviesList = new JTable(moviesModel);

		onChange();
		/* REMOVE ID COLUMN FROM THE VIEW */
		TableColumn tc = moviesList.getColumnModel().getColumn(0);
		moviesList.removeColumn(tc);
		moviesList.setRowHeight(25);
		moviesScroll = new JScrollPane(moviesList);

		moviesPanel.add(moviesScroll);
		moviesPanel.add(addMovieButt, BorderLayout.EAST);

	}

	public JTable getMoviesTable() {
		return this.moviesList;
	}

	public Object getRowFromTable(Point point, int column) {

		DefaultTableModel dtm = (DefaultTableModel) moviesList.getModel();
		int selectedIndex = moviesList.rowAtPoint(point);
		moviesList.setRowSelectionInterval(selectedIndex, selectedIndex);
		return dtm.getValueAt(selectedIndex, column);
	}

	class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			new AddMovie(MoviesPanel.this);

		}

	}

	@Override
	public void onChange() {

		/* POPULATE THE ROWS */
		Connection conn = DatabaseDriver.open();
		PreparedStatement ps;
		DefaultTableModel dtm = (DefaultTableModel) moviesList.getModel();
		dtm.getDataVector().removeAllElements();
		try {
			ps = (PreparedStatement) conn
					.prepareStatement(Query.SELECTMOVIESGENREASC);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			Vector<String> row;
			while (rs.next()) {
				row = new Vector<>(numColumns);
				for (int i = 1; i <= numColumns; i++) {
					row.add(rs.getString(i));
				}
				/* ADD THE ROW DATA */
				dtm.addRow(row);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}