import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.PreparedStatement;

public class MoviesMain {
	public static void main(String[] args) {

		new MovieFrame();
/*		JFrame main_frame = new JFrame();
		JTabbedPane tabbedPane = new JTabbedPane();

		main_frame.setTitle("Movies");
		main_frame.setEnabled(true);
		main_frame.setLocationRelativeTo(null);

		main_frame.setPreferredSize(new Dimension(550, 485));
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.setResizable(false);
		tabbedPane.addTab("Movies", new MoviesPanel());
		tabbedPane.addTab("People", new PeoplePanel());

		main_frame.getContentPane().add(tabbedPane, BorderLayout.WEST);

		main_frame.setVisible(true);

		main_frame.pack();
*/
		// MoviesDB db = new MoviesDB();
		// db.getConnection();

	}
}
