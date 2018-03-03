import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {

	private static Scanner input = new Scanner(System.in);
	private static DB db;
	private static int student_id;
	private static int faculty_id;
	
	public static void main(String args[]) throws SQLException
	{
	    db = new DB();
	    
		while (true)
			mainMenu();
	}
	
	public static void mainMenu(){
		System.out.println("\r\n" + "Login as [S]tudent, [A]dmin, [F]aculty or [Q]uit");
		System.out.print("Enter Selection: ");
		char selection;
		selection = input.nextLine().charAt(0);
		switch (selection)	{
			case 'F':
				facultyLogin();
				break;
		    case 'S':
				studentLogin();
		    	break;
		    case 'A':
				adminLogin();
		    	break;
		    case 'Q':
		    	System.exit(0);
		    	break;
		    default:
		}
	}
	
	public static void facultyLogin()	{
		System.out.print("Enter faculty ID: ");
		faculty_id = Integer.parseInt(input.nextLine()); //NO ERROR HANDLING NEED TO CHECK FOR INT
		System.out.print("Enter password: ");
		input.nextLine();
		if(db.checkFacultyID(faculty_id))
			facultyMenu();
		else
			System.out.println("Incorrect facultyID and/or password");
	}
	
	
	public static void facultyMenu()	{
		char selection;
		boolean back = false;
		while(!back){
			System.out.println("\r\n" + "[V]iew Persoanal Info, [C]lasses I Teach");
			System.out.println("[S]tudents In My Classes, [B]ack, [Q]uit");
			System.out.println("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch (selection)	{
				case 'V':
					db.facultyPersonalInfo(faculty_id);
	    			break;
	    		case 'C':
	    			db.classesIteach(faculty_id);
		    		break;
	    		case 'S':
	    			db.getStudentsIteach(faculty_id);
	    			break;
	    		case 'B':
	    			back=true;
		    		break;
	    		case 'Q':
	    			db.close();
			    	System.exit(0);
			    	break;
			}
		}
	}
	
	public static void studentLogin(){
		System.out.print("Enter student ID: ");
		student_id = Integer.parseInt(input.nextLine()); //NO ERROR HANDLING NEED TO CHECK FOR INT
		System.out.print("Enter password: ");
		input.nextLine();
		if(db.checkStudentID(student_id))
			studentMenu();
		else
			System.out.println("Incorrect username or password");
	}
	
	public static void studentMenu(){
		char selection;
		boolean back = false;
		while(!back){
			System.out.println("\r\n" + "[K]Search Faculty, [A]dd class, [D]rop, [M]y Enrollment [B]ack, [Q]uit");
			System.out.println("[V]iew my personal info, [S]earch Classes, [L]ist Not Full Classes");
			System.out.println("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch (selection)
			{
				case 'V':
					db.viewStudentInfo(student_id);
	    			break;
	    		case 'S':
	    			searchClassesMenu();
		    		break;
	    		case 'K':
	    			searchFacultyMenu();
	    			break;
	    		case 'D':
	    			dropMenu();
	    			break;
	    		case 'A':
	    			System.out.print("Enter Class Number: ");
					int class_id = Integer.parseInt(input.nextLine());
					db.addClass(class_id, student_id);
					db.getMyEnrollment(student_id);
					break;
	    		case 'M':
	    			db.getMyEnrollment(student_id);
	    			break;
		    	case 'B':
		    		back=true;
		    		break;
			    case 'Q':
			    	db.close();
			    	System.exit(0);
			    	break;
			    case 'L':
			    	listClassMenu();
			    	break;
			    default:
			}
		}
	}
	
	public static void dropMenu()	{
		int classToEnrollIn;
		db.getMyEnrollment(student_id);
		System.out.println("\r\n" + "To Drop a class, enter class_id: ");
		System.out.println("Enter Selection: ");
		classToEnrollIn = Integer.parseInt(input.nextLine());
		
		db.dropClass(student_id, classToEnrollIn);
		db.getMyEnrollment(student_id);
		
		char selection; 
		System.out.println("\r\n" + "Drop another class? [Y]es or [N]o?");
		System.out.println("Enter Selection: ");
		selection = input.nextLine().charAt(0);
		
		if(selection == 'Y')	{
			dropMenu();
		}
	}
	
	//student menu for searching faculty
	public static void searchFacultyMenu()	{
		char selection;
		String fToSearchFor;
		System.out.print("\nSearch Faculty by [N]ame, [D]epartment ");
		System.out.println("Enter Selection: ");
		selection = input.nextLine().charAt(0);
		
		switch(selection)	{
			case 'N':
				System.out.println("Enter Teacher name to search for: ");
				fToSearchFor = input.nextLine();
				db.searchFacultyByNameOrDepartment(fToSearchFor, "", 0);
				break;
			case 'D':
				System.out.println("Enter Department name to search for: ");
				fToSearchFor = input.nextLine();
				db.searchFacultyByNameOrDepartment("", fToSearchFor, 1);
		}
	}
	
	public static void listClassMenu()	{
		char selection ;
		boolean back = false;
		while(!back)	{
			System.out.println("\r\n" + "[D]List Classes by department, [L]ist Not Full Classes by class_id, [B]ack");
			System.out.println("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch(selection)	{
				case 'B':
					back = true;
					break;
				case 'L':
					db.listClassesThatAreNotFull();
					break;
				case 'D':
					listByDepartmentMenu();
					break;
			}
		}
	}
	
	public static void listByDepartmentMenu()	{
		char selection ;
		boolean back = false;
			System.out.println("Search By Department: [M]ath, [C]omputer Science, [E]nglish, [S]cience, [B]ack");
			System.out.println("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch(selection)	{
				case 'M':
					db.listClassesNotFullByDepartment("Math");
				case 'C':
					db.listClassesNotFullByDepartment("Computer Science");
				case 'E':
					db.listClassesNotFullByDepartment("English");
				case 'S':
					db.listClassesNotFullByDepartment("Science");
				case 'B':
					back = true;
			}
	}

	// STUDENT MENU
	private static void searchClassesMenu() {
		char selection;
		boolean back = false;
		while(!back){
			System.out.println("\r\n" + "Search classes by [D]epartment, [N]ame, [I]d, [P]search Faculty by name, [B]ack, [Q]uit");
			System.out.print("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch (selection)	{
				case 'D':
					System.out.print("Enter Department Name: ");
					db.listClassesByDepartment(input.nextLine());
	    			break;
	    		case 'N':
	    			String cToSearchFor;
	    			System.out.print("Class Name to Search For: ");
	    			cToSearchFor = input.nextLine();
	    			db.searchForClassByNameOrNumber(0, cToSearchFor, 1);
		    		break;
	    		case 'P':
	    			String professorName;
	    			System.out.print("Enter Professor's FULL Name: ");
	    			professorName = input.nextLine();
	    			db.searchForClassByInstructor(professorName);
		    		break;
	    		case 'I':
	    			int numberToSearchFor;
	    			System.out.print("Class ID # to Search For: ");
	    			numberToSearchFor = Integer.parseInt(input.nextLine());
	    			db.searchForClassByNameOrNumber(numberToSearchFor, "", 0);
		    		break;
		    	case 'B':
		    		back=true;
		    		break;
			    case 'Q':
			    	db.close();
			    	System.exit(0);
			    	break;
			    default:
			}
		}
	}

	public static void adminLogin(){
		System.out.print("Enter admin ID: ");
		input.nextLine();
		System.out.print("Enter password: ");
		input.nextLine();
		adminMenu();
	}
	
	public static void adminMenu(){
		char selection;
		boolean back = false;
		while(!back){
			System.out.println("\r\n" + "[A]dd faculty, [C]Add Class, [B]ack, [Q]uit, [F]ind");
			System.out.println("[D]elete class, [K]Delete Faculty, [S]earch, A[R]chive]");
			System.out.print("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch (selection)
			{
				case 'R':
					System.out.print("Enter TIMESTAMP yyyy-mm-dd hh:mm:dd ->");
					db.archive(input.nextLine());
					break;
	    		case 'A':
	    			addFacultyMenu();
	    			break;
		    	case 'B':
		    		back=true;
		    		break;
			    case 'Q':
			    	db.close();
			    	System.exit(0);
			    	break;
			    case 'D':
			    	deleteClassMenu();
			    	break;
			    case 'K':
			    	deleteFacultyMenu();
			    	break;
			    case 'S':
			    	adminSearchMenu();
			    	break;
			    case 'F':
			    	findMenu();
			    	break;
			    case 'C':
			    	addClassForm();
			    	break;
			    default:
			}
		}
	}
	
	//ADMIN MENU
	public static void adminSearchMenu()	{
		char selection;
		boolean back = false;
		while(!back){
			System.out.println("\r\n" + "List [A]ll Students, List All [C]lasses, Ac[T]ivity, [B]ack");
			System.out.print("[L]ist students RANGED ids, List classes by [E]Enrollment");
			System.out.println("\n[S]pecial Search To Fulfil Union Requirement");
			System.out.print("\nEnter Selection: ");
			selection = input.nextLine().charAt(0);
			switch (selection)	{
				case 'A':
					db.printAllStudents();
					break;
				case 'B':
					back = true;
					break;
				case 'T':
					db.getStudentsByActivity();
					break;
				case 'C':
					db.printAllClasses();
					break;
				case 'L':
					int max;
					int min;
					System.out.println("Enter Minimum ID Range: ");
					System.out.println("Min Range: ");
					min = Integer.parseInt(input.nextLine());
					
					System.out.println("Enter MAximum ID Range: ");
					System.out.println("Max Range: ");
					max = Integer.parseInt(input.nextLine());
					
					db.printAllStudentsWithinRange(min, max);
					break;
				case 'E':
					db.printClassesByEnrollment();
					break;
				case 'S':
					specialSearchPrompt();
					break;
			}
		}
	}
	
	public static void specialSearchPrompt()	{
		System.out.println("\r\nPrint the instructors who teach students less than ages a"
				+ "\n\t AND students over the age of b");
		int a;
		int b;
		System.out.println("Enter Minimum student Age: ");
		System.out.println("Min age: ");
		a = Integer.parseInt(input.nextLine());
		
		System.out.println("Enter Maximum student Age: ");
		System.out.println("Max age: ");
		b = Integer.parseInt(input.nextLine());
		
		db.unionMethod(a, b);
	}
	
	//ADMIN MENU
	public static void deleteClassMenu()	{
		char selection;
		boolean back = false;
		while(!back)	{
			System.out.println("\r\n" + "Delete class by [I]d, [N]ame, [B]ack");
			System.out.print("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch(selection)	{
			case 'B':
				back = true;
				break;
			case 'I':
				int idToDelete;
				System.out.println("\r\n" + "Enter class ID to delete: ");
				System.out.print("class_id: ");
				idToDelete = Integer.parseInt(input.nextLine());
				
				db.deleteClassByIdOrName(idToDelete, "", 0);
				break;
			case 'N':
				String nameToDelete;
				System.out.println("\r\n" + "Enter class name to delete: ");
				System.out.print("class_id: ");
				nameToDelete = input.nextLine();

				db.deleteClassByIdOrName(0, nameToDelete, 1);
				break;
			}
		}
	}
	
	
	//ADMIN MENU
	public static void deleteFacultyMenu()	{
		char selection;
		boolean back = false;
		while(!back)	{
			System.out.println("\r\n" + "Delete Faculty by [I]d, [B]ack");
			System.out.print("Enter Selection: ");
			selection = input.nextLine().charAt(0);
			switch(selection)	{
			case 'B':
				back = true;
				break;
			case 'I':
				int idToDelete;
				System.out.println("\r\n" + "Enter Faculty ID to delete: ");
				System.out.print("class_id: ");
				idToDelete = Integer.parseInt(input.nextLine());
				
				db.deleteFacultyByIdOrName(idToDelete, "", 0);
				break;
			}
		}
	}
	
	//ADMIN MENU
	public static void addClassForm()	{
		String cName;
		int tID;
		String depart;
		int max_enroll;
		int units;
		System.out.print("Enter Course Name: ");
		cName = input.nextLine();
		System.out.print("Enter Teacher_id: ");
		tID = Integer.parseInt(input.nextLine());
		System.out.print("Enter Department: ");
		depart = input.nextLine();
		System.out.print("Enter Enrollment Cap: ");
		max_enroll = Integer.parseInt(input.nextLine());
		System.out.print("Enter Course Units: ");
		units = Integer.parseInt(input.nextLine());
		
		db.addNewClass(cName, tID, depart, max_enroll, units);
	}
	
	//ADMIN MENU
	public static void findMenu()	{
		char selection;
		System.out.println("\r\n" + "Find: [A]vg Student Age, [K]All Students OUTER JOIN Faculty [B]ack");
		System.out.print("Enter Selection: ");
		selection = input.nextLine().charAt(0);
		switch(selection)	{
			case 'A':
				db.getAverageStudentAge();
				adminMenu();
			case 'K':
				db.outerJoinStudentOnFaculty();
				adminMenu();
		}
	}

	//ADMIN MENU
	private static void addFacultyMenu() {
		System.out.print("Enter faculty name: ");
		String name = input.nextLine();
		System.out.print("Enter age: ");
		int age = Integer.parseInt(input.nextLine());
		db.addFaculty(name, age);
	}
}	