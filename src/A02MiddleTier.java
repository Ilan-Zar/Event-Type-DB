import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class A02MiddleTier {
	static Statement statement;
	static ResultSet result;
	static Connection con;
	static PreparedStatement ps;
	static String query;

	private static void makeConnection() throws SQLException {
		String url = "/insert path to host here/";
		String uname = "root";
		String password = "/insert password here/";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// establish connection
		con = DriverManager.getConnection(url, uname, password);
		statement = con.createStatement();

	}

	public static String getResponse(String text, String from, String to) throws SQLException {
		//call to seperate method to establish connection
		makeConnection();

		String selectedBox = text;
		String fromDate = from;
		String toDate = to;
		String query;
		String event = "SELECT E.ID, E.Name, E.EventWebLink, E.CFPText\r\n" + 
				"FROM Event AS E\r\n" + "WHERE EXISTS\r\n";
		String eventConference = "(SELECT C.EventID\r\n" + 
				"FROM EventConference AS C\r\n" + 
				"WHERE E.ID = C.EventID)";
		String eventJournal = "(SELECT J.EventID\r\n" + 
				"FROM EventJournal AS J\r\n" + 
				"WHERE E.ID = J.EventID)";
		String eventBook = "(SELECT B.EventID\r\n" + 
				"FROM EventBook AS B\r\n" + 
				"WHERE E.ID = B.EventID)";
		String periodConference = "(SELECT C.EventID, C.EvDate, A.EventID, A.ActivityDate\r\n" + 
				"FROM EventConference AS C, ActivityHappens AS A\r\n" + 
				"WHERE E.ID = C.EventID AND A.EventID = C.EventID AND (A.ActivityDate BETWEEN '" + fromDate + "' AND '" + toDate + "' OR C.EvDate BETWEEN '" + fromDate + "' AND '" + toDate + "')\r\n" + 
				")";
		String periodJournal = "(SELECT J.EventID, A.EventID, A.ActivityDate\r\n" + 
				"FROM EventJournal AS J, ActivityHappens AS A\r\n" + 
				"WHERE E.ID = J.EventID AND A.EventID = J.EventID AND A.ActivityDate BETWEEN '" + fromDate + "' AND '" + toDate + "'\r\n" + 
				")";
		String periodBook = "(SELECT B.EventID, A.EventID, A.ActivityDate\r\n" + 
				"FROM EventBook AS B, ActivityHappens AS A\r\n" + 
				"WHERE E.ID = B.EventID AND A.EventID = B.EventID AND A.ActivityDate BETWEEN '" + fromDate + "' AND '" + toDate + "'\r\n" + 
				")";
		
		if(fromDate != null && toDate != null) {
			switch(selectedBox) {
			case "Period":
				query = "SELECT * FROM Events";
				break;
			
			case "Period,EventConference":
				query = event + periodConference + ";";
				break;
			case "Period,EventJournal":
				query = event + periodJournal + ";";
				break;
			case "Period,EventBook":
				query = event + periodBook + ";";
				break;
			case "Period,EventConference,EventJournal":
				query = event + periodConference +
						"OR\r\n" + 
    					"EXISTS\r\n" +
						periodJournal + ";";
				break;
			case "Period,EventConference,EventBook":
				query = event + periodConference +
						"OR\r\n" + 
						"EXISTS\r\n" +
						periodBook + ";";
				break;
			case "Period,EventJournal,EventBook":
				query = event + periodJournal +
						"OR\r\n" + 
						"EXISTS\r\n" +
						periodBook + ";";
				break;
			case "Period,EventConference,EventJournal,EventBook":
				query = event + periodConference + 
						"OR\r\n" + 
						"EXISTS\r\n" + 
						periodJournal + 
						"OR\r\n" + 
						"EXISTS\r\n" + 
						periodBook +";";
				break;
			default:
				query = "";
				break;
			}
		} else {
	    	switch(selectedBox){
    		case "Events":
    			query = "SELECT * FROM Events";
    			break;
    			
    		case "Events,EventConference":
    			query = event + eventConference + ";";
    			break;
    			
    		case "Events,EventJournal":
    			query = event + eventJournal + ";";
    			break;
    			
    		case "Events,EventBook":
    			query = event + eventBook + ";";
    			break;
    			
    		case "Events,EventConference,EventJournal":
    			query = event + eventConference +
    					"OR\r\n" + 
    					"EXISTS\r\n" + 
    					eventJournal + ";";
    			break;
    			
    		case "Events,EventConference,EventBook":
    			query = event + eventConference + 
    					"OR\r\n" + 
    					"EXISTS\r\n" + 
    					eventBook + ";";
    			break;
    		
    		case "Events,EventJournal,EventBook":
    			query = event + eventJournal + 
    					"OR\r\n" + 
    					"EXISTS\r\n" + 
    					eventBook + ";";
    			break;
    			
    		case "Events,EventConference,EventJournal,EventBook":
    			query = event + eventConference + 
    					"OR\r\n" + 
    					"EXISTS\r\n" + 
    					eventJournal + 
    					"OR\r\n" + 
    					"EXISTS\r\n" + 
    					eventBook +";";
    			break;
    		default:
    			query = "";
    			break;
	    	}
		}
			

		//send the query
		try  {
			result = statement.executeQuery(query);
		} catch (SQLException e) {
			return "Query: \n" + query +"\n" + "Output: \n" + "empty set";
		}

		// get the number of rows in the resultset
		java.sql.ResultSetMetaData rsmd = result.getMetaData();
		int column_count = rsmd.getColumnCount();

		// loop through rows and columns
		String resultText = "";
		while (result.next()) {
			for (int i = 1; i <= column_count; i++) {
				resultText += result.getString(i) + " ";

			}
			resultText += "\n";
			
		}
		
		//if there was a result print it
		String output = "Query: \n" + query +"\n" + "Output: \n";
		if(!resultText.isEmpty()) {
			output +=  resultText; 
		} else {
			output +=  "Empty set"; 
		}
		
		
		return output;

	}

}
