import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox<String> cb_selection;
	private JButton bt_submit;
	private JTextField tf_searchTerms;
	private JTextField tf_yearTerm;
	private final String[] searchTypes = { "Movie by LIKE",
											"Movie by regex",
											"Person by LIKE", 
											"Person by regex", 
											"Genre "};
	private JTable searchTable;

	public SearchPanel() {
		cb_selection = new JComboBox<>(searchTypes);
		tf_searchTerms = new JTextField();

		tf_yearTerm = new JTextField();
		tf_searchTerms.setPreferredSize(new Dimension(150,25));
		tf_searchTerms.setToolTipText("Type part of the movie title allong with \'%\', where required...");

		tf_yearTerm.setEditable(true);
		tf_yearTerm.setPreferredSize(new Dimension(150,25));

		bt_submit = new JButton("Submit");
		searchTable = new JTable();
		searchTable.setRowHeight(25);

		bt_submit.addActionListener(new SearchListener());

		JScrollPane tablescroll = new JScrollPane(searchTable);

		add(cb_selection);
		add(tf_searchTerms);
		add(tf_yearTerm);
		add(bt_submit);
		add(tablescroll);

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

	public class SearchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			int selectedItem = cb_selection.getSelectedIndex();
			try {
				Connection conn = DatabaseDriver.open();
				PreparedStatement ps;
				ResultSet rs;
				switch (selectedItem) {
				case 0: case 1: // MOVIES
					Vector<String> cols = new Vector<String>();
					cols.add("Title");
					cols.add("Year");
					cols.add("Duration");
					cols.add("Genre");
					cols.add("Cast");
					cols.add("Profession");
					String movieTitle = tf_searchTerms.getText();
					conn.setAutoCommit(false);
					if (selectedItem == 0) {
						ps = (PreparedStatement) conn.prepareStatement(Query.SEARCHMOVIETITLELIKE);
					} else {
						ps = (PreparedStatement) conn.prepareStatement(Query.SEARCHMOVIETITLEREGEX);
					}
					ps.setString(1, movieTitle);
					rs = ps.executeQuery();

					searchTable.setModel(new DefaultTableModel(new Vector<String>(), cols));

					parseResultSet(rs, (DefaultTableModel) searchTable.getModel());

					conn.commit();
					conn.close();
					rs.close();
					ps.close();
					break;
				case 2 : case 3: // PERSON
					
					Vector<String> person_cols = new Vector<String>();
					person_cols.add("Person");
					person_cols.add("Profession");
					person_cols.add("Title");
					person_cols.add("Duration");
					person_cols.add("Year");

					String personName = tf_searchTerms.getText();
					conn.setAutoCommit(false);
					if (selectedItem == 2) {
						ps = (PreparedStatement) conn.prepareStatement(Query.SEARCHPERSONLIKE);
					} else {
						ps = (PreparedStatement) conn.prepareStatement(Query.SEARCHPERSONREGEX);
					}
					ps.setString(1, personName);
					rs  = ps.executeQuery();

					searchTable.setModel(new DefaultTableModel(new Vector<String>(), person_cols));

					parseResultSet(rs, (DefaultTableModel) searchTable.getModel());

					conn.commit();
					conn.close();
					rs.close();
					ps.close();					
					break;
				case 4: // GENRE
					
					
					Vector<String> genre = new Vector<String>();
					genre.add("Title");
					genre.add("Duration");
					genre.add("Year");

					String genreName = tf_searchTerms.getText();
					String movieYear = tf_yearTerm.getText().toString();
					conn.setAutoCommit(false);
					
					if(movieYear.equals("")){
						ps = (PreparedStatement) conn.prepareStatement(Query.SEARCHGENRE);
						ps.setString(1, genreName);
					
					}else{
						ps = (PreparedStatement) conn.prepareStatement(Query.SEARCHGENREBYYEAR);
						ps.setString(1, genreName);
						ps.setInt(2, Integer.parseInt(movieYear));
					}
					
					
					rs = ps.executeQuery();

					searchTable.setModel(new DefaultTableModel(new Vector<String>(), genre));

					parseResultSet(rs, (DefaultTableModel) searchTable.getModel());

					conn.commit();
					conn.close();
					rs.close();
					ps.close();	
					
					break;
				
				default:
					break;
				}
			}catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Seriously?");
			
			} catch (SQLException  e) {
				if (e instanceof MySQLSyntaxErrorException) {
					JOptionPane.showMessageDialog(null, "Whoops! Something went south with your query!");
				} else {
					e.printStackTrace();
				}
			}
		}
	}
}
