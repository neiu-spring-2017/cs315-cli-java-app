import java.sql.*;
import java.util.*;

public class ConnectionToDb
{
	public static void main(String[]args)
	{
		Scanner in = new Scanner(System.in);
		Connection conn = null;
		String tableName = "AquariumFish";

		try {
	    	conn = DriverManager.getConnection(
				Config.HOST + Config.DATABASE +
				"?user=" + Config.USERNAME +
				"&password=" + Config.PASS
			);

			displayMenu();
			boolean run = true;
			do {
				System.out.print("Enter a command: ");
				int input = in.nextInt();
				switch (input) {
					case 1:
						selectAll(conn, tableName);
						break;
					case 2:
						insertFish(conn, tableName);
						break;
					case 3:
						deleteFish(conn, tableName);
						break;
					case 4:
						displayMenu();
						break;
					case 5:
						run = false;
						break;
					default:

				}
			} while (run);
			System.out.println("\nThanks for using the AquariumFish table.");

		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		} catch (InputMismatchException ex) {
			System.out.println("Command not found... Terminating");
		}
	}

	public static void deleteFish(Connection conn, String table) throws SQLException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("\nPlease enter the id of the fish you wish to delete.");
		try {
			int id = in.nextInt();
			int response = deleteId(conn, table, id);
			if (response == 1) {
				System.out.println("\nSuccessfully deleted fish!\n");
			} else {
				System.out.println("\nError deleting fish.\n");
			}
		} catch (InputMismatchException ex) {
			System.out.println("Improper value for id...");
		}
	}

	public static void insertFish(Connection conn, String table) throws SQLException
	{
		Scanner in = new Scanner(System.in);
		String[] vals = new String[5];
		String[] cols = {"common name", "scientific name", "type",
							"aggressive flag", "max size"};
		System.out.println();
		for (int i = 0; i < vals.length; i++) {
			System.out.println("Please enter the " + cols[i]);
			vals[i] = in.nextLine();
		}

		int result = insertTo(conn, table, vals);
		if (result == 1) {
			System.out.println("\nSuccessfully inserted fish!\n");
		} else {
			System.out.println("\nError inserting fish.\n");
		}
	}

	public static void selectAll(Connection conn, String table) throws SQLException
	{
		String[] cols = new String[0];
		ResultSet rs = selectFrom(conn, table, cols, "");
		printResults(rs);
	}

	public static void displayMenu()
	{
		System.out.println("\n_____________________________________________________");
		System.out.printf("| %-50s |\n", "Welcome to the AquariumFish table demo!");
		System.out.printf("| %50s |\n", "");
		System.out.printf("| %-50s |\n", "Options: (type the option number then hit enter)");
		System.out.printf("| %50s |\n", "");
		System.out.printf("| %-16d .... %28s |\n", 1, "Select All");
		System.out.printf("| %-16d .... %28s |\n", 2, "Insert New Entry");
		System.out.printf("| %-16d .... %28s |\n", 3, "Delete Fish");
		System.out.printf("| %-16d .... %28s |\n", 4, "Display Menu");
		System.out.printf("| %-16d .... %28s |\n", 5, "Exit");
		System.out.println("-----------------------------------------------------\n");
	}

	public static int deleteId(Connection conn, String table, int id) throws SQLException
	{
		Statement stmt = conn.createStatement();

		String sql = "DELETE FROM " + table + " WHERE id='" + id + "'";

		return stmt.executeUpdate(sql);
	}

	public static int insertTo(Connection conn, String table, String[] values) throws SQLException
	{
		Statement stmt = conn.createStatement();

		String sql = "INSERT INTO " + table + " VALUES(null, ";
		for (int i = 0; i < values.length; i++) {
			if (i < values.length - 2)
				sql += "'" + values[i] + "'";
			else //last 2 columns are integers
				sql += values[i];
			if (i != values.length - 1) {
				sql += ", ";
			} else {
				sql += ")";
			}
		}
		return stmt.executeUpdate(sql);
	}

	public static ResultSet selectFrom(Connection conn, String table,String[] cols, String where) throws SQLException
	{
		Statement stmt = conn.createStatement();

		String sql = "SELECT ";
		if (cols.length > 0) {
			for (int i = 0; i < cols.length; i++) {
				sql += cols[i];
				if (i != cols.length - 1) {
					sql += ", ";
				} else {
					sql += " ";
				}
			}
		} else {
			sql += "* ";
		}
		sql += "FROM " + table;
		if (where.length() > 0) {
			sql += " WHERE " + where;
		}

		//Execute statement and return a resultset
		return stmt.executeQuery(sql);
	}

	/**
	 *  Helper method to print ResultSet
	 */
	public static void printResults(ResultSet rs) throws SQLException
	{
		//Get the meta data for the table
		ResultSetMetaData rm = rs.getMetaData();
		String[] colNames = new String[rm.getColumnCount()];

		//Display data in console for now
		System.out.print("\n| ");
		for (int i = 1; i <= colNames.length; i++) {
			colNames[i - 1] = rm.getColumnName(i);
			String tempF = "%-" + colNames[i - 1].length() + "s | ";
			System.out.printf(tempF, colNames[i-1]);
		}
		System.out.print("\n-------------------------------------------------------------------------\n");
		//Loop through result rows
		while (rs.next()) {
			System.out.print("| ");
			//Grab print values for each column
			for (String name : colNames) {
				String tempF = "%-" + name.length() + "s | ";
				String tempN = rs.getString(name);
				if (tempN.length() > name.length())
					tempN = tempN.substring(0, name.length() - 2) + "..";
				System.out.printf(tempF, tempN);
			}
			System.out.println();
		}
	}
}
