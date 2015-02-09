import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.ldap.Rdn;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * 
 * @author Seva, Spyro A frame for adding a movie with its genre and cast to the
 *         database
 * 
 */
public class AddMovie extends JFrame {

	private JPanel panel;
	private JLabel titleL, yearL, durationL, genreL, personL, professionL;
	private JTextField titleF, yearF, durationF, genreF, personF;
	private JButton cancelB, saveB, plusB;
	private JComboBox<String> profession;
	private JList peopleList;
	private DefaultListModel<String> listModel;
	private JScrollPane scroll;
	private String[] roleArray;

	private String name, role;

	private OnDatabaseChange listener;

	public AddMovie(OnDatabaseChange listener) {

		this.listener = listener;
		roleArray = new String[] { "Actor", "Director", "Producer", "Writer" }; // manually
																				// specify
																				// the
																				// list
																				// of
																				// roles
																				// for
																				// JComboBox
		panel = new JPanel();
		titleL = new JLabel("Title");
		yearL = new JLabel("Year");
		durationL = new JLabel("Duration");
		genreL = new JLabel("Genre");
		personL = new JLabel("Cast");
		professionL = new JLabel("Role");
		titleF = new JTextField();
		yearF = new JTextField();
		durationF = new JTextField();
		genreF = new JTextField();
		personF = new JTextField();
		listModel = new DefaultListModel<String>();
		peopleList = new JList(listModel);
		listModel = new DefaultListModel<>();

		cancelB = new JButton("Cancel");
		saveB = new JButton("Save");
		plusB = new JButton("+");

		profession = new JComboBox<String>(roleArray);

		scroll = new JScrollPane(peopleList);

		panel.setLayout(null);

		/* set the location of objects */
		titleL.setBounds(19, 10, 40, 10);
		titleF.setBounds(19, 25, 100, 20);

		yearL.setBounds(19, 50, 100, 20);
		yearF.setBounds(19, 75, 100, 20);

		durationL.setBounds(19, 100, 100, 20);
		durationF.setBounds(19, 125, 100, 20);

		genreL.setBounds(19, 150, 100, 20);
		genreF.setBounds(19, 175, 100, 20);

		personL.setBounds(170, 10, 40, 10);
		personF.setBounds(170, 25, 100, 20);

		scroll.setBounds(170, 50, 300, 150);

		profession.setBounds(300, 25, 100, 20);
		professionL.setBounds(300, 10, 40, 10);

		plusB.setBounds(420, 25, 50, 20);

		cancelB.setBounds(170, 230, 100, 30);
		saveB.setBounds(280, 230, 100, 30);

		/* add action listener to the buttons */
		plusB.addActionListener(new ButtonListener(this));
		saveB.addActionListener(new ButtonListener(this));
		cancelB.addActionListener(new ButtonListener(this));

		/* a bit of hint for ppl */
		titleF.setToolTipText("Must not be empty");
		yearF.setToolTipText("Must not be empty");
		personF.setToolTipText("Surname, name");
		genreF.setToolTipText("Add multiple genres by separating with \",\"");
		

		/* finally add everything to the panel and frame */
		panel.add(titleL);
		panel.add(personL);

		panel.add(titleF);
		panel.add(personF);

		panel.add(yearL);
		panel.add(profession);
		panel.add(professionL);

		panel.add(yearF);
		panel.add(plusB);

		panel.add(durationL);
		panel.add(durationF);
		panel.add(genreL);
		panel.add(genreF);

		panel.add(cancelB);
		panel.add(saveB);

		panel.add(scroll);

		this.setTitle("Movies");
		this.setEnabled(true);
		this.setLocationRelativeTo(null);
		this.setPreferredSize(new Dimension(500, 300));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);

		this.getContentPane().add(panel);
		this.setVisible(true);
		this.pack();

	}

	/**
	 * 
	 * @author Seva, Spyro a simple action listener class
	 */
	private class ButtonListener implements ActionListener {

		private JFrame frame;

		public ButtonListener(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			/* add a person to the list */
			if (ev.getSource() == plusB) {
				
				name = personF.getText().toString();
				role = profession.getSelectedItem().toString();
				if(!name.equals("")){
					((DefaultListModel<String>) peopleList.getModel()).addElement(name + "-" + role);
					peopleList.invalidate();
					personF.setText("");
				}else{
					JOptionPane.showConfirmDialog(null, "Whoops! Insert a name!");
				}
				

			}

			// cancel the whole shit
			if (ev.getSource() == cancelB) {
				frame.dispose();
			}

			// save everything to the database
			if (ev.getSource() == saveB) {
				String[] person_role;
				long movieId;
				Long[] personId;

				try {
					Connection conn = (Connection) DatabaseDriver.open();
					conn.setAutoCommit(false);

					/* INSERT MOVIE */
					PreparedStatement ps = (PreparedStatement) conn.prepareStatement(Query.INSERTMOVIE, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, titleF.getText().toString());
					ps.setInt(2, Integer.parseInt(durationF.getText()));
					ps.setInt(3, Integer.parseInt(yearF.getText()));

					ps.executeUpdate();
					
					ps = (PreparedStatement) conn.prepareStatement("SELECT LAST_INSERT_ID()");
					ResultSet rs = ps.executeQuery();
					rs.next();
					movieId = rs.getLong(1);

					
					/* GET A MAP WITH PERSON NAME AND ID */
					Map<String, Long> pIds = this.getPersonIdFromSelect(conn);

					/*INSERT PERSONS WHO DO NOT EXIST AND GET PERSON IDs*/
					ps = (PreparedStatement) conn.prepareStatement(Query.INSERTPERSON);

					for (Entry<String, Long> entry : pIds.entrySet()) {
						if (entry.getValue() == -1) {
							ps.setString(1, entry.getKey());
							ps.addBatch();
						}
					}
					ps.executeBatch();

					/* RENEW THE MAP */

					pIds = getPersonIdFromSelect(conn);

					// /*INSERT GENRE WITH MOVIE ID*/
					ps = (PreparedStatement) conn.prepareStatement(Query.INSERTGENRE);
					String[] genres = genreF.getText().split(",");

					for (String genre : genres) {
						ps.setString(2, genre);
						ps.setLong(1, movieId);
						ps.addBatch();
					}

					ps.executeBatch();
					//
					// /*ADD IDs TO CASTANDCREW*/
					
					 ps = (PreparedStatement)conn.prepareStatement(Query.INSERTCAST);

					 for(Entry<String, Long> id : pIds.entrySet()){
						 ps.setLong(1, movieId);
						 ps.setLong(2,id.getValue());

						 for(int i = 0; i < peopleList.getModel().getSize(); i++) {
							 String[] personNRole = ((String) peopleList.getModel().getElementAt(i)).split("-");
							 if (personNRole[0].equals(id.getKey())) {
								 ps.setString(3, personNRole[1]);
								 break;
							 }
						 }
						 ps.addBatch();
					 }
					 ps.executeBatch();
					rs.close();
					ps.close();
					conn.commit();
					listener.onChange();
					frame.dispose();
				} catch (SQLException e) {

					e.printStackTrace();
				} catch (NumberFormatException ae) {
					JOptionPane
							.showMessageDialog(null,
									"Duration or year fields must contain only digits!");
				}

			}

		}

		private Map<String, Long> getPersonIdFromSelect(Connection conn) {
			ResultSet rs = null;
			Map<String, Long> pIds = new HashMap<>();
			try {
				PreparedStatement ps = (PreparedStatement) conn.prepareStatement(Query.SELECTPERSONID);
				for (int i = 0; i < peopleList.getModel().getSize(); i++) {

					String personName = ((String) peopleList.getModel().getElementAt(i)).split("-")[0];
					ps.setString(1, personName);				
					rs = ps.executeQuery();
					boolean next = rs.next();
					if (next) {
						pIds.put(personName, rs.getLong("peopleId"));
					} else {
						pIds.put(personName, -1l);
					}
				}
				rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return pIds;
		}

		/* get IDs from the result set */
		private Long[] getKey(PreparedStatement ps) {

			ResultSet rs;
			Long[] result = {};
			List<Long> list = new ArrayList<Long>();

			try {
				rs = ps.getGeneratedKeys();

				while (rs.next()) {
					list.add(rs.getLong(1));

				}
				result = list.toArray(new Long[list.size()]);
				return result;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;

		}

	}

}
