
    		public final class Query {

    			/* INSERT QUERIES */
    			public static final String INSERTMOVIE  = "INSERT INTO movies"
    								+ "(movie_Title, duration, yr) VALUES"
    								+ "(?,?,?)"
    								+ "ON DUPLICATE KEY UPDATE "
    								+ "movie_Title = VALUES(movie_Title),"
    								+ "duration = VALUES (duration),"
    								+ "yr = VALUES(yr),"
    								+ "movieId = LAST_INSERT_ID(movieId)";

    			public static final String INSERTPERSON = "INSERT INTO people "
    								+ "(personName) VALUES"
    								+ "(?)";

    			public static final String INSERTCAST   = "INSERT INTO castandcrew "
    								+ "(movieId, peopleId, roleId) "
    								+ "SELECT movieId, peopleId, roleId "
    								+ "FROM movies , people , roles  "
    								+ "WHERE movieId=? "
    								+ "AND peopleId=? "
    								+ "AND profession=? ";

    			public static final String INSERTGENRE  = "INSERT INTO genre "
    								+ "(movieId, genre)"
    								+ "VALUES(?, ?)";

    			public static final String INSERTROLE   = "INSERT INTO roles "
    								+ "(profession)"
    								+ "VALUES(?)";


    			/* DELETE QUERIES */
    			public static final String DELETETMOVIE = "DELETE FROM movies "
    								+ "WHERE movieId=?";

    			public static final String DELETEPERSON = "DELETE FROM people "
    								+ "WHERE peopleId=?";

    			public static final String DELETEROLE   = "DELETE FROM roles "
    								+ "WHERE roleI=?";

    			public static final String DELETEGENRE  = "DELETE FROM genre "
    								+ "WHERE genreId=?";

    			public static final String DELETEGENREMOVIEID  = "DELETE FROM genre "
						+ "WHERE movieId=? AND genre=?";

    			/* UPDATE QUERIES */
    			public static final String UPDATEMOVIE  = "UPDATE movies "
    								+ "SET"
    								+ "movie_Title=?"
    								+ "duration=?"
    								+ "yr=?"
    								+ "WHERE movieId=?";

    			public static final String UPDATEPERSON = "UPDATE person "
    								+ "SET"
    								+ "personName=?"
    								+ "WHERE peopleId=?";

    			public static final String UPDATEROLE   = "UPDATE roles "
    								+ "SET"
    								+ "profession=?"
    								+ "WHERE roleId=?";

    			public static final String UPDATEGENRE  = "UPDATE genre "
    								+ "SET"
    								+ "genre=?"
    								+ "WHERE genreId=?"
    								+ "AND movieId=?";
    			
    			/* SELECT QUERIES */
    			public static final	String SELECTMOVIESGENREASC = "select "
    					+ "movies.movieId as id, "
    					+ "movie_Title as title, "
    					+ "movies.duration, movies.yr, "
    					+ "COALESCE(group_concat(genre.genre separator ' / '), 'N/A') as genre "
    					+ "from movies "
    					+ "left join genre on movies.movieId = genre.movieId "
    					+ "group by id, title, duration, yr ORDER BY title ASC";

    			public static final String SELECTPERSONID = "SELECT peopleId FROM people WHERE personName=?";
    			
    			public static final String SELECTCASTANDCREW = "SELECT c.peopleId, p.personName, r.profession "
    					+ "from castandcrew c "
    					+ "inner join people p on c.peopleId = p.peopleId " 
    					+ "inner join roles r on c.roleId = r.roleId "
    			 		+ "where c.movieId = ? ";

    			/*SEARCH QUERIES */
    			public static final String SEARCHMOVIETITLELIKE = "SELECT movies.movie_Title, "
    					+ "movies.yr, "
    					+ "movies.duration, "
    					+ "genre.genre, "
    					+ "people.personName, "
    					+ "roles.profession "
    					+ "FROM castandcrew "
    					+ "INNER JOIN movies ON castandcrew.movieId = movies.movieId AND movies.movie_Title LIKE ? "
    					+ "LEFT JOIN genre ON castandcrew.movieId = genre.movieId "
    					+ "INNER JOIN people ON castandcrew.peopleId = people.peopleId "
    					+ "INNER JOIN roles ON castandcrew.roleId = roles.roleId";

    			public static final String SEARCHMOVIETITLEREGEX = "SELECT movies.movie_Title, "
    					+ "movies.yr, "
    					+ "movies.duration, "
    					+ "genre.genre, "
    					+ "people.personName, "
    					+ "roles.profession "
    					+ "FROM castandcrew "
    					+ "INNER JOIN movies ON castandcrew.movieId = movies.movieId AND movies.movie_Title REGEXP ? "
    					+ "LEFT JOIN genre ON castandcrew.movieId = genre.movieId "
    					+ "INNER JOIN people ON castandcrew.peopleId = people.peopleId "
    					+ "INNER JOIN roles ON castandcrew.roleId = roles.roleId";

    			public static final String SEARCHPERSONLIKE = "SELECT people.personName, roles.profession, "
    					+"movies.movie_Title, "
    				    +"movies.duration, "
    				    +"movies.yr "
    				+"FROM castandcrew "
    				+"INNER JOIN movies ON castandcrew.movieId = movies.movieId "
    				+"INNER JOIN people ON castandcrew.peopleId = people.peopleId AND people.personName LIKE ? "
    				+"INNER JOIN roles ON castandcrew.roleId = roles.roleId";

    			public static final String SEARCHPERSONREGEX = "SELECT people.personName, roles.profession, "
    					+"movies.movie_Title, "
    				    +"movies.duration, "
    				    +"movies.yr "
    				+"FROM castandcrew "
    				+"INNER JOIN movies ON castandcrew.movieId = movies.movieId "
    				+"INNER JOIN people ON castandcrew.peopleId = people.peopleId AND people.personName REGEXP ? "
    				+"INNER JOIN roles ON castandcrew.roleId = roles.roleId";
    			
    			
    			public static final String SEARCHGENRE = 
    					"SELECT "
    					+ "movies.movie_Title, "
    				    + "movies.duration,"
    				    + " movies.yr "
    				    + "FROM movies "
    				    + "INNER JOIN genre ON movies.movieId = genre.movieId AND genre.genre = ?";
    			
    			public static final String SEARCHGENREBYYEAR = 
    					"SELECT "
    					+ "movies.movie_Title, "
    				    + "movies.duration,"
    				    + " movies.yr "
    				    + "FROM movies "
    				    + "INNER JOIN genre ON movies.movieId = genre.movieId AND genre.genre LIKE ? "
    				    + "WHERE movies.yr = ?";
    		}