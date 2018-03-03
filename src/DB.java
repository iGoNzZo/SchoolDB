import java.sql.*;

public class DB {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "cs157a";
	//static final String PASS = ; // need YOUR password for mySQL
	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Statement statement = null;

	public DB() throws SQLException {
		try
		{
			Class.forName(JDBC_DRIVER); //Register JDBC Driver
			createDatabase(); 
			createFacultyTable();
			createStudentTable();
			createDepartmentTable();
			createClassTable();
			createEnrollmentTable();
			createArchiveTable();
			createTriggers();
			loadDepartmentDataIntoTable();
			loadFacultyDataIntoTable();
			loadStudentDataIntoTable();
			loadClassDataIntoTable();
			loadEnrollmentDataIntoTable();
			loadArchiveDataIntoTable();
			
			createProcedures();
		}
		catch(SQLException se){se.printStackTrace(); }
		catch(Exception e){ e.printStackTrace(); }
		
	}

	public void close(){
		try{ if(preparedStatement!=null) preparedStatement.close(); }
		catch(SQLException se2){ }// nothing we can do
		
		try{ if(statement!=null) statement.close(); }
		catch(SQLException se2){ }// nothing we can do
		
	    try{ if(conn!=null) conn.close(); }
		catch(SQLException se){ se.printStackTrace(); }
	}
	private static void createDatabase() throws SQLException {

		// Open a connection
		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);


		String queryDrop = "DROP DATABASE IF EXISTS cs";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		// Create a database named CS
		System.out.println("Creating database...");

		String sql = "CREATE DATABASE CS";
		preparedStatement = conn.prepareStatement(sql);
		preparedStatement.executeUpdate();
		System.out.println("Database created successfully...");

		conn = DriverManager.getConnection(DB_URL+"CS", USER, PASS);
	}

	private static void createFacultyTable() throws SQLException {
		String queryDrop = "DROP TABLE IF EXISTS Faculty";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		String createTableSQL = "CREATE TABLE FACULTY("
				+ "id INTEGER NOT NULL AUTO_INCREMENT, "
				+ "name VARCHAR(20), "
				+ "age INTEGER, " 
				+ "PRIMARY KEY (ID))";
		preparedStatement = conn.prepareStatement(createTableSQL);
		preparedStatement.executeUpdate(); 
		System.out.println("Table called FACULTY created successfully...");
	}

	private static void createStudentTable() throws SQLException {
		String queryDrop = "DROP TABLE IF EXISTS Student";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		String createTableSQL = "CREATE TABLE STUDENT("
				+ "student_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ "name VARCHAR(20) NOT NULL, "
				+ "age INTEGER NOT NULL, "
			    + "department VARCHAR(20), "
			    + "units_enrolled_in INTEGER DEFAULT 0, "
			    + "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";

		preparedStatement = conn.prepareStatement(createTableSQL);
		preparedStatement.executeUpdate(); 
		System.out.println("Table called STUDENT created successfully...");
	}
	
	private static void createDepartmentTable() throws SQLException {
		String queryDrop = "DROP TABLE IF EXISTS Department";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		String createTableSQL = "CREATE TABLE DEPARTMENT("
				+ "name VARCHAR(20), "
				+ "PRIMARY KEY (NAME))";
		preparedStatement = conn.prepareStatement(createTableSQL);
		preparedStatement.executeUpdate(); 
		System.out.println("Table called DEPARTMENT created successfully...");
	}
	
	private static void createClassTable() throws SQLException {
		String queryDrop = "DROP TABLE IF EXISTS Class";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		String createTableSQL = "CREATE TABLE Class("
				+ "class_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ "class_name VARCHAR(30), "
				+ "teacher_id INTEGER, "
				+ "department VARCHAR(30), "
				+ "enrollment INTEGER DEFAULT 0, "
				+ "max_enrollment INTEGER, "
				+ "units INTEGER DEFAULT 3, "
				+ "FOREIGN KEY (department) REFERENCES Department(name) "
				+ "ON DELETE RESTRICT "
				+ "ON UPDATE CASCADE, "
				+ "FOREIGN KEY (teacher_id) REFERENCES Faculty(id) "
				+ "ON DELETE RESTRICT "
				+ "ON UPDATE CASCADE);";
		
		preparedStatement = conn.prepareStatement(createTableSQL);
		preparedStatement.executeUpdate(); 
		System.out.println("Table called CLASS created successfully...");
	}
	
	private static void createEnrollmentTable() throws SQLException {
		String queryDrop = "DROP TABLE IF EXISTS Enrollment";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		String createTableSQL = "CREATE TABLE Enrollment("
				+ "class_id INTEGER, "
				+ "student_id INTEGER, "
				+ "primary key (class_id, student_id), "
				+ "FOREIGN KEY (student_id) REFERENCES Student(student_id), "
				+ "FOREIGN KEY (class_id) REFERENCES Class(class_id));";
		
		preparedStatement = conn.prepareStatement(createTableSQL);
		preparedStatement.executeUpdate(); 
		System.out.println("Table called ENROLLMENT created successfully...");
	}
	
	private static void createArchiveTable() throws SQLException {
		String queryDrop = "DROP TABLE IF EXISTS Archive";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);

		String createTableSQL = "CREATE TABLE Archive("
				+ "student_id INTEGER NOT NULL PRIMARY KEY, "
				+ "name VARCHAR(20) NOT NULL, "
				+ "age INTEGER NOT NULL, "
			    + "department VARCHAR(20));";

		preparedStatement = conn.prepareStatement(createTableSQL);
		preparedStatement.executeUpdate(); 
		System.out.println("Table called ARCHIVE created successfully...");
	}
	
	private static void loadFacultyDataIntoTable() throws SQLException {
		String path = System.getProperty("user.dir").replace("\\", "\\\\") + "/faculty.txt";
		System.out.println("Load data from a file faculty.txt");
		String loadDataSQL = "LOAD DATA LOCAL INFILE '" + path + "' INTO TABLE Faculty "
				+ "LINES TERMINATED BY '\r\n'"; // need to add lines terminated if on windows


		preparedStatement = conn.prepareStatement(loadDataSQL);
		preparedStatement.execute(); 
		
		/*Statement statement = conn.createStatement(); 
		ResultSet rs = statement.executeQuery("SELECT * from faculty");
		printResultSetfromFaculty(rs);*/
	}
	
	private static void loadDepartmentDataIntoTable() throws SQLException {
		String path = System.getProperty("user.dir").replace("\\", "\\\\") + "/departments.txt";
		System.out.println("Load data from a file departments.txt");
		String loadDataSQL = "LOAD DATA LOCAL INFILE '" + path + "' INTO TABLE Department "
				+ "LINES TERMINATED BY '\r\n'"; // need to add lines terminated if on windows

		preparedStatement = conn.prepareStatement(loadDataSQL);
		preparedStatement.execute(); 
	}
	
	private static void loadStudentDataIntoTable() throws SQLException {
		String path = System.getProperty("user.dir").replace("\\", "\\\\") + "/students.txt";
		System.out.println("Load data from a file students.txt");
		String loadDataSQL = "LOAD DATA LOCAL INFILE '" + path + "' INTO TABLE Student "
				+ "LINES TERMINATED BY '\r\n'"; // need to add lines terminated if on windows

		preparedStatement = conn.prepareStatement(loadDataSQL);
		preparedStatement.execute(); 
	}
	
	private static void loadClassDataIntoTable() throws SQLException {
		String path = System.getProperty("user.dir").replace("\\", "\\\\") + "/classes.txt";
		System.out.println("Load data from a file classes.txt");
		String loadDataSQL = "LOAD DATA LOCAL INFILE '" + path + "' INTO TABLE class "
				+ "LINES TERMINATED BY '\r\n'"; // need to add lines terminated if on windows

		preparedStatement = conn.prepareStatement(loadDataSQL);
		preparedStatement.execute(); 
	}
	
	private static void loadEnrollmentDataIntoTable() throws SQLException {
		String path = System.getProperty("user.dir").replace("\\", "\\\\") + "/enrollment.txt";
		System.out.println("Load data from a file enrollment.txt");
		String loadDataSQL = "LOAD DATA LOCAL INFILE '" + path + "' INTO TABLE Enrollment "
				+ "LINES TERMINATED BY '\r\n'"; // need to add lines terminated if on windows

		preparedStatement = conn.prepareStatement(loadDataSQL);
		preparedStatement.execute(); 
	}
	
	private static void loadArchiveDataIntoTable() throws SQLException {
		String path = System.getProperty("user.dir").replace("\\", "\\\\") + "/archive.txt";
		System.out.println("Load data from a file archive.txt");
		String loadDataSQL = "LOAD DATA LOCAL INFILE '" + path + "' INTO TABLE Archive "
				+ "LINES TERMINATED BY '\r\n'"; // need to add lines terminated if on windows

		preparedStatement = conn.prepareStatement(loadDataSQL);
		preparedStatement.execute(); 
	}
	
	private static void createProcedures() throws SQLException{
		// to view created procedures in mysql cmd type "SHOW PROCEDURE STATUS WHERE Db=DATABASE();"
		// these should be edited to fit our program
		statement = conn.createStatement();
		String queryDrop = "DROP PROCEDURE IF EXISTS archiveNow";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop); 

		/*String createInParameterProcedure = "CREATE PROCEDURE getFacultyByName(IN facultyName VARCHAR(50)) BEGIN SELECT * FROM Faculty WHERE name=facultyName; END";
		statement.executeUpdate(createInParameterProcedure);
		queryDrop = "DROP PROCEDURE IF EXISTS countByAge";
		stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop);
		
		String createOutParameterProcedure ="CREATE PROCEDURE countByAge(IN retirementAge INT, OUT total INT) BEGIN SELECT count(*) INTO total FROM Faculty WHERE retirementAge < age; END" ;
		statement.executeUpdate(createOutParameterProcedure);*/
		
		String createInParameterProcedure = "CREATE PROCEDURE archiveNow(IN t timestamp) "
				+ "BEGIN " 
				+ "INSERT IGNORE INTO archive (SELECT student_id, name, age, department FROM student WHERE updatedAt <= t);"
				+ "DELETE FROM student WHERE updatedAt <= t;"
				+ "END;";
		statement.executeUpdate(createInParameterProcedure);
	}

	private void createTriggers() throws SQLException {
		String queryDrop = "DROP TRIGGER IF EXISTS enroll_insert";
		Statement stmtDrop = conn.createStatement();
		stmtDrop.execute(queryDrop); 
		statement = conn.createStatement();
		String createTrigger = "CREATE TRIGGER enroll_insert BEFORE INSERT ON Enrollment "
				+ "FOR EACH ROW "
				+ "IF (SELECT 1=1 FROM class WHERE class.class_id = new.class_id) "
				+ "THEN "
				+ "BEGIN "
				+ "UPDATE student "
				+ "SET units_enrolled_in = units_enrolled_in+(Select units from class where class_id=new.class_id) "
				+ "WHERE student.student_id=new.student_id; "
				+ "UPDATE class "
				+ "SET enrollment = enrollment+class.units "
				+ "WHERE class.class_id=new.class_id; "
				+ "END; "
				+ "END IF;"; 
		statement.executeUpdate(createTrigger); 
		
		createTrigger = "CREATE TRIGGER updateTimestampOnEnroll "
				+ "AFTER INSERT ON Enrollment "
				+ "FOR EACH ROW "
				+ "BEGIN "
					+ "UPDATE student SET updatedAt = CURRENT_TIMESTAMP() "			
					+ "WHERE student_id = New.student_id; "
				+ "END;";
		preparedStatement = conn.prepareStatement(createTrigger);
		preparedStatement.execute();	
		
		createTrigger = "CREATE TRIGGER updateEnrolledOnDROP "
				+ "AFTER DELETE ON Enrollment "
				+ "FOR EACH ROW "
				+ "BEGIN "
					+ "UPDATE class SET enrollment = enrollment - 1 "			
					+ "WHERE class_id = Old.class_id; "
				+ "END;";
		preparedStatement = conn.prepareStatement(createTrigger);
		preparedStatement.execute();
		
		createTrigger = "CREATE TRIGGER updateUnitsOnDROP "
				+ "AFTER DELETE ON Enrollment "
				+ "FOR EACH ROW "
				+ "BEGIN "
					+ "UPDATE student SET units_enrolled_in = units_enrolled_in - 3 "			
					+ "WHERE student_id = Old.student_id; "
				+ "END;";
		preparedStatement = conn.prepareStatement(createTrigger);
		preparedStatement.execute();
		
		createTrigger = "CREATE TRIGGER updateTimestampOnDrop "
				+ "AFTER DELETE ON Enrollment "
				+ "FOR EACH ROW "
				+ "BEGIN "
					+ "UPDATE student SET updatedAt = CURRENT_TIMESTAMP() "			
					+ "WHERE student_id = Old.student_id; "
				+ "END;";
		preparedStatement = conn.prepareStatement(createTrigger);
		preparedStatement.execute();	
	}
	
	public void addFaculty(String name, int age){
		String sql = null;
		ResultSet rs = null;
		
		sql = "INSERT INTO Faculty " 
				+ "(name, age) VALUES"
				+ "(?, ?)";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, name);
			preparedStatement.setInt(2, age);
			preparedStatement.executeUpdate();

			Statement statement = conn.createStatement();
			rs = statement.executeQuery("SELECT * from faculty");
			printResultSetfromFaculty(rs);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void printResultSetfromFaculty(ResultSet rs) throws SQLException {
		System.out.println();
		while(rs.next())
		{
			int id = rs.getInt("id"); 
			String name = rs.getString("name"); 
			int age = rs.getInt("age");
			System.out.println("ID:" + id + " Name:" + name + " Age:" + age); 
		}
	}

	public boolean checkStudentID(int id){
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT 1 FROM student WHERE student_id = ?";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();
			if (rs.next())
				return true;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void viewStudentInfo(int student_id) {
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM student WHERE student_id = ?";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, student_id);
			rs = preparedStatement.executeQuery();

			while(rs.next())
			{
				int id = rs.getInt("student_id"); 
				String name = rs.getString("name"); 
				int age = rs.getInt("age");
				String department = rs.getString("department"); 
				int units = rs.getInt("units_enrolled_in");
				System.out.println("\r\n" + "ID: " + id); 
				System.out.println("Name: " + name); 
				System.out.println("Age:" + age); 
				System.out.println("Department:" + department); 
				System.out.println("Units enrolled in:" + units);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void listClassesByDepartment(String s) {
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT department, class_id, class_name, name, enrollment, max_enrollment, units " 
				+ "FROM class JOIN faculty " 
				+ "ON class.teacher_id=faculty.id and department = ?;";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, s);
			rs = preparedStatement.executeQuery();
			System.out.println();
			while(rs.next())
			{
				System.out.println(rs.getString("department") + "-" 
						+ rs.getInt("class_id")
						+ " " + rs.getString("class_name") 
						+ " | Instructor: " + rs.getString("name") 
						+ " | Enrollment: " + rs.getInt("enrollment") 
						+ "/" + rs.getInt("max_enrollment")
						+ " | Units: " + rs.getInt("units"));
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void listClassesThatAreNotFull()	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT class.department, class_id, class_name, faculty.name "
				+ "FROM Class JOIN Faculty "
				+ "ON teacher_id = faculty.id "
				+ "WHERE enrollment < max_enrollment;";
		try	{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();
			System.out.println();
			while(rs.next())	{
				System.out.println(rs.getString("class.department") + "-"
						+ rs.getString("class_id") + " " + rs.getString("class_name")
						+ " | Instructor: " + rs.getString("faculty.name"));
			}
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	public void listClassesNotFullByDepartment(String department)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT class.department, class_id, class_name, faculty.name, enrollment, max_enrollment "
				+ "FROM class right JOIN faculty ON faculty.id = class.teacher_id "
				+ "WHERE enrollment < max_enrollment "
				+ "AND class.department = ?";
		try	{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, department);
			rs = preparedStatement.executeQuery();
			System.out.println();
			while(rs.next())	{
				System.out.println(rs.getString("class.department") + "-"
						+ rs.getString("class_id") + " " + rs.getString("class_name")
						+ " | Instructor: " + rs.getString("faculty.name") + " " 
						+ " | Enrolled: " + rs.getInt("enrollment") 
						+ " class capacity: " + rs.getInt("max_enrollment"));
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void getAverageStudentAge()	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT AVG(averageAge)"
				+ "FROM (Select AVG(age) AS averageAge "
					+ "From student GROUP BY age) myAVG";
		try	{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();
			System.out.println();
			while(rs.next())	{
				System.out.println("Average Student Age: " + rs.getDouble("AVG(averageAge)"));
			}
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	public void outerJoinStudentOnFaculty()	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT student.name, faculty.name, faculty.id "
				+ "FROM student RIGHT OUTER JOIN faculty ON student_id = faculty.id - 1000;";
		try	{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();
			System.out.println();
			while(rs.next())	{
				System.out.println(rs.getString("student.name")
					+ " " + rs.getString("faculty.name") + " " + rs.getInt("faculty.id"));
			}
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	public void addNewClass(String cName, int teach_id, String dept, int max_enroll, int units)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "INSERT INTO Class (class_name, teacher_id, department, max_enrollment, units) "
				+ "VALUES (?, ?, ?, ?, ?)";
		
		try	{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, cName);
			preparedStatement.setInt(2, teach_id);
			preparedStatement.setString(3, dept);
			preparedStatement.setInt(4, max_enroll);
			preparedStatement.setInt(5, units);
			preparedStatement.executeUpdate();
			
			Statement stmt = conn.createStatement();
			rs = statement.executeQuery("SELECT * FROM Class");
			System.out.println();
			while(rs.next())	{
				System.out.println(rs.getInt("class_id") + " " + rs.getString("class_name") + " "
						+ rs.getString("teacher_id") + " " + rs.getString("department") + " "
						+ rs.getInt("enrollment") + " " + rs.getInt("max_enrollment") + " "
						+ rs.getInt("units"));
			}
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}

	public void addClass(int class_id, int student_id) {
		String sql = null;
		ResultSet rs = null;
		
		sql = "INSERT IGNORE INTO Enrollment " 
				+ "(class_id, student_id) VALUES"
				+ "(?, ?)";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, class_id);
			preparedStatement.setInt(2, student_id);
			preparedStatement.executeUpdate();

			Statement statement = conn.createStatement();
			/*
			rs = statement.executeQuery("SELECT * FROM Enrollment");
			System.out.println();
			while(rs.next())	{
				System.out.println("class_id: " + rs.getInt("class_id") + " student_id: " + rs.getInt("student_id"));
			}
			*/
		}catch (SQLException e) {
			//e.printStackTrace();
			System.out.println("Erorr: class_id not found");
		}
	}

	public void archive(String s) {
		String sql = null;
		sql = "CALL archiveNow(?)";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, s);
			preparedStatement.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	/* STUDENT MENU called from searchClassesMenu() 
	 * takes option to perform 1 of 2 queries find by class number or find class by name
	 */
	public void searchForClassByNameOrNumber(int classNum, String className, int option)	{
		String sql = null;
		ResultSet rs = null;
		
		if(option == 0)	{
			sql = "SELECT class_id, class_name, teacher_id, department, enrollment, max_enrollment, units "
				+ "FROM Class WHERE class_id = " + classNum + ";";
		}
		else	{
			System.out.println(className);
			sql = "SELECT class_id, class_name, teacher_id, department, enrollment, max_enrollment, units "
					+ "FROM Class WHERE class_name = ?;";
		}
		
		try	{
			preparedStatement = conn.prepareStatement(sql);
			
			if(option == 1)	{
				preparedStatement.setString(1, className);
			}
			
			rs = preparedStatement.executeQuery();
			System.out.println();
			while(rs.next())	{
				System.out.println(rs.getInt("class_id") + " " + rs.getString("class_name") + " | "
						+ rs.getInt("teacher_id") + " " + rs.getString("department")
						+ " " +  rs.getInt("enrollment") + " " + rs.getInt("max_enrollment")
						+ " " + rs.getInt("units"));
			}
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	/* STUDENT MENU called from search faculty menu 
	 *  takes option to perform 1 of 2 queries, search faculty by faculty name or search by department
	 */
	public void searchFacultyByNameOrDepartment(String teacherName, String department, int option)	{
		String sql = null;
		ResultSet rs = null;
		
		if(option == 0)	{
			sql = "SELECT id, name, age "
				+ "FROM Faculty "
				+ "WHERE name =  ?;";
		}
		else	{
			sql = "SELECT id, name, age, department "
					+ "FROM Faculty JOIN class ON teacher_id = id "
					+ "WHERE department = ?;";
		}
		
		try	{
			preparedStatement = conn.prepareStatement(sql);
			
			if(option == 0)	{
				preparedStatement.setString(1, teacherName);
			}
			else	{
				preparedStatement.setString(1, department);
			}
			
			rs = preparedStatement.executeQuery();
			
			System.out.println();
			if(option == 0)	{
				while(rs.next())	{
				System.out.println(rs.getInt("id") + " " + rs.getString("name") + " "
						+ rs.getInt("age"));
				}
			}
			else	{
				while(rs.next())	{
					System.out.println(rs.getInt("id") + " " + rs.getString("name") + " "
							+ rs.getInt("age") + " " + rs.getString("department"));
					}
			}
			
			
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	/* ADMIN MENU called from delete class menu
	 * takes option to perdorm 1 or 2 deletions, delete by class name or delete by class id
	 */
	public void deleteClassByIdOrName(int id, String name, int option)	{
		String sql = null;
		ResultSet rs = null;
		
		if(option == 0)	{
			sql = "DELETE FROM Class WHERE class_id =  ?;";
		}
		else	{
			sql = "DELETE FROM Class WHERE class_name =  ?;";
		}
		
		try	{
			preparedStatement = conn.prepareStatement(sql);
			
			if(option == 0)	{
				preparedStatement.setInt(1, id);
			}
			else	{
				preparedStatement.setString(1, name);
			}
			
			preparedStatement.executeUpdate();
			
			Statement stmt = conn.createStatement();
			rs = statement.executeQuery("SELECT * FROM Class");
			System.out.println();
			while(rs.next())	{
				System.out.println(rs.getInt("class_id") + " " + rs.getString("class_name") + " "
						+ rs.getString("teacher_id") + " " + rs.getString("department") + " "
						+ rs.getInt("enrollment") + " " + rs.getInt("max_enrollment") + " "
						+ rs.getInt("units"));
			}
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	public void deleteFacultyByIdOrName(int id, String name, int option)	{
		String sql = null;
		ResultSet rs = null;
		
		if(option == 0)	{
			sql = "DELETE FROM Faculty WHERE id =  ?;";
		}
		else	{
			sql = "DELETE FROM Faculty WHERE name =  ?;";
		}
		
		try	{
			preparedStatement = conn.prepareStatement(sql);
			
			if(option == 0)	{
				preparedStatement.setInt(1, id);
			}
			else	{
				preparedStatement.setString(1, name);
			}
			
			preparedStatement.executeUpdate();
			
			Statement statement = conn.createStatement();
			rs = statement.executeQuery("SELECT * from faculty");
			printResultSetfromFaculty(rs);
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	/* ADMIN MENU called from adminSearchMenu()
	 * prints all students
	 */
	public void printAllStudents()	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM student ORDER BY student_id";
		try{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int id = rs.getInt("student_id"); 
				String name = rs.getString("name"); 
				int age = rs.getInt("age");
				String department = rs.getString("department"); 
				int units = rs.getInt("units_enrolled_in");
				System.out.println("\r\n" + "ID: " + id); 
				System.out.println("Name: " + name); 
				System.out.println("Age:" + age); 
				System.out.println("Department:" + department); 
				System.out.println("Units enrolled in:" + units);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/* ADMIN MENU called from adminSearchMenu()
	 * prints all students whose ids are within ranges a and b
	 */
	public void printAllStudentsWithinRange(int a, int b)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM student "
				+ "WHERE student_id >= " + a + " "
				+ "AND student_id <= " + b + " "
				+ "ORDER BY student_id";
		try{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int id = rs.getInt("student_id"); 
				String name = rs.getString("name"); 
				int age = rs.getInt("age");
				String department = rs.getString("department"); 
				int units = rs.getInt("units_enrolled_in");
				System.out.println("\n" + "ID: " + id); 
				System.out.println("Name: " + name); 
				System.out.println("Age:" + age); 
				System.out.println("Department:" + department); 
				System.out.println("Units enrolled in:" + units);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/* ADMIN MENU
	 * prints all classes in class table
	 */
	public void printAllClasses()	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM class ORDER BY enrollment";
		try{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int id = rs.getInt("class_id"); 
				String name = rs.getString("class_name"); 
				int teachID = rs.getInt("teacher_id");
				String department = rs.getString("department"); 
				int enrollment = rs.getInt("enrollment");
				int maxEnrollment = rs.getInt("max_enrollment");
				int units = rs.getInt("units");
				System.out.println("| " + id
						+ " | " + name + " | " + teachID
						+ " | " + department
						+ " | " + enrollment
						+ " | " + maxEnrollment
						+ " | " + units + " | ");
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	public void enrollStudent(int studentID, int classID)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "INSERT INTO Enrollment (class_id, student_id) "
				+ "VALUES (?, ?)";
		
		try	{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, classID);
			preparedStatement.setInt(2, studentID);
			preparedStatement.executeUpdate();
		}catch(SQLException e)	{
			e.printStackTrace();
		}
		
	}
	
	*/
	
	/*
	 * STUDENT MENU allows student to drop a class from enrollment
	 */
	public void dropClass(int studentID, int classToDrop)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "DELETE FROM Enrollment "
				+ "WHERE class_id = ? "
				+ "AND student_id = ?";
		
		try	{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, classToDrop);
			preparedStatement.setInt(2, studentID);
			preparedStatement.executeUpdate();
		}catch(SQLException e)	{
			e.printStackTrace();
		}
	}
	
	/* STUDENT MENU
	 * prints out classes current student is enrolled in
	 */
	public void getMyEnrollment(int studentID)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM Class NATURAL JOIN Enrollment "
				+ "WHERE Enrollment.student_id =  ? "
				+ "ORDER BY Class.class_id ";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, studentID);
			rs = preparedStatement.executeQuery();

			while(rs.next())				{
				int id = rs.getInt("class_id"); 
				String name = rs.getString("class_name"); 
				int teachID = rs.getInt("teacher_id");
				String department = rs.getString("department"); 
				int enrollment = rs.getInt("enrollment");
				int maxEnrollment = rs.getInt("max_enrollment");
				int units = rs.getInt("units");
				System.out.println("| " + id
							+ "\t| " + name
							+ "\t| " + teachID
							+ "\t| " + department
							+ "\t| " + enrollment
							+ "\t| " + maxEnrollment
							+ "\t| " + units + " \t| ");
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/* ADMIN MENU called from adminSearchMenu()
	 * prints all classes ordered by 
	 */
	public void printClassesByEnrollment() {
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT class_id, COUNT(*) as Enrolled FROM enrollment GROUP BY class_id";
		
		/*sql = "SELECT class_id, COUNT(*), teacher_id, department, enrollment, max_enrollment, units"
				+ " FROM class ORDER BY class_id";*/
		try{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();
	
			System.out.println();
			while(rs.next())	{
				int classID = rs.getInt("class_id"); 
				int enrolled = rs.getInt("Enrolled"); 
				
				System.out.println("| " + classID
						+ " | " + enrolled + " |");	
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/* STUDENT MENU called from searchClassesMenu()
	 * searches/prints classes instrcuted by professorName
	 */
	public void searchForClassByInstructor(String professorName) {
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT faculty.id, class_name, class_id, class.department "
				+ "FROM faculty RIGHT JOIN class ON (class.teacher_id = faculty.id) "
				+ "WHERE name = ? "
				+ "ORDER BY class_name ";
		
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setNString(1, professorName);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int teacherID = rs.getInt("faculty.id");
				String teacherName = rs.getString("class_name");
				int classID = rs.getInt("class_id");
				String className = rs.getString("class.department"); 
								
				System.out.println("| " + teacherID
						+ " | " + className
						+ " | " + teacherName + " | ");
						 
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getStudentsByActivity()	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM student ORDER BY updatedAt ASC;";
		
		try{
			preparedStatement = conn.prepareStatement(sql);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int id = rs.getInt("student_id"); 
				String name = rs.getString("name"); 
				int age = rs.getInt("age");
				String department = rs.getString("department"); 
				int units = rs.getInt("units_enrolled_in");
				Timestamp ts = rs.getTimestamp("updatedAt");
				System.out.println("\n" + "ID: " + id); 
				System.out.println("Name: " + name); 
				System.out.println("Age: " + age); 
				System.out.println("Department: " + department); 
				System.out.println("Units enrolled in: " + units);
				System.out.println("UpdatedAt: " + ts);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkFacultyID(int id)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT 1 FROM faculty WHERE id = ?";
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();
			if (rs.next())
				return true;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void facultyPersonalInfo(int id)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM faculty WHERE id = ?";
		
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int fID = rs.getInt("id"); 
				String name = rs.getString("name"); 
				int age = rs.getInt("age");
				System.out.println("\n" + "ID: " + fID); 
				System.out.println("Name: " + name); 
				System.out.println("Age: " + age); 
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void classesIteach(int id)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT * FROM class WHERE teacher_id = ?";
		
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int class_id = rs.getInt("class_id"); 
				String name = rs.getString("class_name"); 
				int teachID = rs.getInt("teacher_id");
				String department = rs.getString("department"); 
				int enrollment = rs.getInt("enrollment");
				int maxEnrollment = rs.getInt("max_enrollment");
				int units = rs.getInt("units");
				System.out.println("| " + class_id
						+ " | " + name + " | " + department
						+ " | " + enrollment + " | " + maxEnrollment
						+ " | " + units + " | ");
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getStudentsIteach(int id)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT class.class_id, class.class_name, student.student_id, "
				+ "student.name, age, units_enrolled_in "
				+ "FROM (class JOIN enrollment ON class.class_id = enrollment.class_id) "
				+ "JOIN student on student.student_id = enrollment.student_id "
				+ "WHERE teacher_id = ? "
				+ "ORDER BY class_id;";
		
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int class_id = rs.getInt("class_id"); 
				String className = rs.getString("class_name"); 
				int studentID = rs.getInt("student_id");
				String studentName = rs.getString("name"); 
				int age = rs.getInt("age");
				int enrolledIn = rs.getInt("units_enrolled_in");
				System.out.println("| " + class_id
						+ " | " + className + " | " + studentID
						+ " | " + studentName + " | " + age
						+ " | " + enrolledIn + " | ");
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void unionMethod(int a, int b)	{
		String sql = null;
		ResultSet rs = null;
		
		sql = "SELECT faculty.id, faculty.name, class_name FROM "
				+ "(((class JOIN enrollment ON  class.class_id = enrollment.class_id) JOIN "
				+ "student ON student.student_id = enrollment.student_id) " 
				+ "JOIN faculty ON class.teacher_id = faculty.id) "
				+ "WHERE student.age < ? "
						+ "UNION " 
			+ "SELECT faculty.id, faculty.name, class_name FROM "
				+ "(((class JOIN enrollment ON  class.class_id = enrollment.class_id) JOIN "
				+ "student ON student.student_id = enrollment.student_id) " 
				+ "JOIN faculty ON class.teacher_id = faculty.id) "
				+ "WHERE student.age > ?;";
		
		try{
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, a);
			preparedStatement.setInt(2, b);
			rs = preparedStatement.executeQuery();

			while(rs.next())	{
				int teacher_id = rs.getInt("id"); 
				String teacher_name = rs.getString("name");
				String class_name = rs.getString("class_name");
				System.out.println("| " + teacher_id + " | " + teacher_name + " | " + class_name + " | ");
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
}