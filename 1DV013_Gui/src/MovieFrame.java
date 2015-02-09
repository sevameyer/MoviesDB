import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.PreparedStatement;

public class MovieFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane = new JTabbedPane();
	private MoviesPanel moviesPanel = new MoviesPanel();
	private PeoplePanel peoplePanel = new PeoplePanel();
	private SearchPanel searchPanel = new SearchPanel();
	private JTable moviesTable;
	private String rowObject;

	public MovieFrame() {
		setTitle("Movies");
		setEnabled(true);
		setLocationRelativeTo(null);

		setPreferredSize(new Dimension(550, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		tabbedPane.addTab("Movies", moviesPanel);
		tabbedPane.addTab("People", peoplePanel);
		tabbedPane.addTab("Search", searchPanel);

		moviesTable = moviesPanel.getMoviesTable();
		moviesTable.addMouseListener(new MoviesClickListener());

		getContentPane().add(tabbedPane);

		setVisible(true);

		pack();

	}

	class MoviesClickListener implements MouseListener {
		JTable list;

		@Override
		public void mousePressed(MouseEvent event) {
			list = (JTable) event.getSource();
			if (SwingUtilities.isRightMouseButton(event)) {
				rowObject = (String) moviesPanel.getRowFromTable(
						event.getPoint(), 0);

				JPopupMenu menu = new JPopupMenu();
				JMenuItem deleteItem = new JMenuItem("Delete");
				JMenuItem showCrewItem = new JMenuItem("Show cast and crew");
				JMenuItem addGenre = new JMenuItem("Add a genre");
				JMenuItem removeGenre = new JMenuItem("Remove a genre");

				deleteItem.addActionListener(new MoviesRightClickListener());
				showCrewItem.addActionListener(new MoviesRightClickListener());
				addGenre.addActionListener(new MoviesRightClickListener());
				removeGenre.addActionListener(new MoviesRightClickListener());

				menu.add(showCrewItem);
				menu.add(deleteItem);
				menu.add(addGenre);
				menu.add(removeGenre);
				moviesTable.setComponentPopupMenu(menu);
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

	class MoviesRightClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// selectedIndex = moviesList.getSelectedIndex();
			// moviesList.remove(selectedIndex);
			JMenuItem menuItem = (JMenuItem) e.getSource();
			String item = menuItem.getText().toString();
			try {
				if (item.equals("Delete")) {
					Long id = Long.parseLong(rowObject);
					DefaultTableModel dtm = (DefaultTableModel) moviesTable
							.getModel();
					dtm.removeRow(moviesTable.getSelectedRow());
					/* DELETE FROM DATABASE */
					Connection conn = DatabaseDriver.open();
					PreparedStatement ps;

					conn.setAutoCommit(false);
					ps = (PreparedStatement) conn
							.prepareStatement(Query.DELETETMOVIE);
					ps.setLong(1, id);
					ps.executeUpdate();
					conn.commit();
					conn.close();

				} else if (item.equals("Show cast and crew")) {
					Connection conn = DatabaseDriver.open();
					PreparedStatement ps;

					conn.setAutoCommit(false);
					ps = (PreparedStatement) conn
							.prepareStatement(Query.SELECTCASTANDCREW);
					Long id = Long.parseLong(rowObject);
					ps.setLong(1, id);
					ResultSet rs = ps.executeQuery();
					peoplePanel.displayPeople(rs);
					tabbedPane.setSelectedIndex(1);
					conn.commit();
					conn.close();

				} else if (item.equals("Add a genre")) {

					String genreName = JOptionPane.showInputDialog(null, "Type in the genre");
					if(genreName.equals("")){
						return;
					}
					Connection conn = DatabaseDriver.open();
					PreparedStatement ps;
						conn.setAutoCommit(false);
						ps = (PreparedStatement) conn
								.prepareStatement(Query.INSERTGENRE);
						Long id = Long.parseLong(rowObject);
						ps.setLong(1, id);
						ps.setString(2, genreName);
						ps.executeUpdate();
						
						conn.commit();
						conn.close();
						ps.close();
						moviesPanel.onChange();
				

				} else if (item.equals("Remove a genre")) {
					Connection conn = DatabaseDriver.open();
					PreparedStatement ps;
					conn.setAutoCommit(false);
					ps = (PreparedStatement) conn.prepareStatement("SELECT genre FROM genre WHERE movieId=" + Long.parseLong(rowObject));
					ResultSet rs = ps.executeQuery();
					List<String> genres = new ArrayList<>();
					while (rs.next()) {
						genres.add(rs.getString(1));
					}
					rs.close();
					ps.close();
					String whichGenre = (String) JOptionPane.showInputDialog(null,
					        "What is your favorite pizza?",
					        "Favorite Pizza",
					        JOptionPane.QUESTION_MESSAGE, 
					        null,
					        genres.toArray(new String[genres.size()]), "");

					ps = (PreparedStatement) conn.prepareStatement(Query.DELETEGENREMOVIEID);
					ps.setLong(1, Long.parseLong(rowObject));
					ps.setString(2, whichGenre);
					ps.executeUpdate();
					conn.commit();
					conn.close();
					moviesPanel.onChange();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
