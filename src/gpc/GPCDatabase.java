package gpc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import beans.VisitedSite;
import database.DbException;
import database.DbMySql;
import database.DbParamsList;


public class GPCDatabase extends DbMySql {

	private final static String dtFormat = "yyyy-MM-dd";

	// --------------------------------------------------------------------------------------------
	// Constructor
	// --------------------------------------------------------------------------------------------
	/**
	 * Constructor: Creates the db object.
	 * 
	 * @throws DBException
	 */
	public GPCDatabase() throws DbException {
		super("gpc");
	}

	public GPCDatabase(String url, int port, String usr, String pwd, String extras) throws DbException {
		super("gpc", url, port, usr, pwd, extras);
	}
	
	
	// --------------------------------------------------------------------------------------------
	// Methods on MESSAGES
	// --------------------------------------------------------------------------------------------
	/**
	 * Inserts a list of messages (if it's not empty)
	 * 
	 * @param messages
	 * @throws DbException
	 */
	public void messagesInsert(List<String> messages) throws DbException {
		if (!messages.isEmpty()) {
			DbParamsList params = new DbParamsList();
			LocalDateTime dt_day = LocalDateTime.now();
			String day = dt_day.format(DateTimeFormatter.ofPattern(dtFormat));
			params.add(day);
			params.add("");
			for (String message : messages) {
				params.modify(1, message);
				super.callProcedure("p_msgInsert", params);
			}
		}
	}
	
	/**
	 * Lists the messages for a given day (sorted by time)
	 * 
	 * @param day
	 * @return
	 * @throws DbException
	 */
	public List<String> messagesList(String day) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(day);
		return super.callProcedure("p_msgList", params, String.class);
	}
	
	// --------------------------------------------------------------------------------------------
	// Methods on CALENDAR
	// --------------------------------------------------------------------------------------------
	/**
	 * Retrieves the list of the days to be scanned till yesterday (included)
	 * 
	 * @return A string list containing the retrieved days
	 * @throws DbException
	 */
	public List<String> calendarDays2Scan() throws DbException {
		List<String> days = new ArrayList<String>();
		
		// gets the list of the days to be rescanned: old 'not scanned' or 'scanned with errors'
		List<String> tmp1 = super.callProcedure("p_calNotScannedDays", String.class);
		if (tmp1 != null && tmp1.size() > 0)
			days.addAll(tmp1);
		
		// gets the last day scanned...
		LocalDateTime dt_day;
		LocalDateTime dt_yday = LocalDateTime.now().plusDays(-1);
		String yday = dt_yday.format(DateTimeFormatter.ofPattern(dtFormat));
		List<String> tmp2 = super.callProcedure("p_calLastScannedDay", String.class);
		if (tmp2 == null || tmp2.size() == 0) {
			// ...not found: yesterday is used
			dt_day = dt_yday;
		} else {
			// ...found: gets the day after
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dtFormat+" HH:mm:ss");
			dt_day = LocalDateTime.parse(tmp2.get(0)+" 10:00:00", formatter).plusDays(1);
		}
		// adds to the list the days from the last till yesterday
		String day = dt_day.format(DateTimeFormatter.ofPattern(dtFormat));
		while (day.compareTo(yday) <= 0) {
			if (!days.contains(day))
				days.add(day);
			
			dt_day = dt_day.plusDays(1);
			day = dt_day.format(DateTimeFormatter.ofPattern(dtFormat));
		}
		
		return days;
	}
	
	/**
	 * Updates the day's scan column, also setting the message column 
	 * 
	 * @param day
	 * @param scanDate
	 * @param message
	 * @throws DbException
	 */
	public void calendarUpdateScan(String day, String scanDate, String message) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(day);
		params.add(scanDate);
		params.add(message);
		super.callProcedure("p_calUpdateScan", params);
	}

	/**
	 * Checks if a day is scanned
	 * 
	 * @param day
	 * @return
	 * @throws DbException
	 */
	public boolean calendarCheckScannedDay(String day) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(day);
		return super.execFunctionRetInt("f_calCheckScannedDay", params) > 0;
	}
	
	// --------------------------------------------------------------------------------------------
	// Methods on SITES
	// --------------------------------------------------------------------------------------------
	/**
	 * Inserts a site record
	 * 
	 * @param site
	 * @throws DbException
	 */
	public void sitesInsert(VisitedSite site) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(site.getLastVisit());
		params.add(site.getTyped());
		params.add(site.getUrl());
		params.add(site.getTitle());
		params.add(site.getBrowser());
		params.add(site.getProfile());
		params.add(site.getDomain());
		super.callProcedure("p_sitesInsert", params);
	}
	
	/**
	 * Lists the sites for a given day (sorted by time)
	 * 
	 * @param day
	 * @return
	 * @throws DbException
	 */
	public List<VisitedSite> sitesList(String day) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(day);
		return super.callProcedure("p_sitesList", params, VisitedSite.class);
	}
	
	/**
	 * Lists the unique domains visited in a given day
	 * 
	 * @param day
	 * @return
	 * @throws DbException
	 */
	public List<String> sitesListDomains(String day) throws DbException {
		DbParamsList params = new DbParamsList();
		params.add(day);
		return super.callProcedure("p_sitesListDomains", params, String.class);
	}
}
