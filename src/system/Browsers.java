package system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import beans.VisitedSite;
import commons.MyException;
import commons.Utils;
import database.DbException;
import database.DbSQLite;

public class Browsers {

	// --------------------------------------------------------------------------------------------
	// Types
	// --------------------------------------------------------------------------------------------
	private static enum knownBrowsers  { FIREFOX /* 0 */, CHROME /* 1 */, EXPLORER /* 2 */ };
	
	// --------------------------------------------------------------------------------------------
	// Constants
	// --------------------------------------------------------------------------------------------
	private static final String CMD_LISTBROWSERS  = "tools\\list_browsers.bat";
	
	// --------------------------------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------------------------------
	private List<knownBrowsers> browsersList;
	//public List<String> messages;
	
	// --------------------------------------------------------------------------------------------
	// Public methods
	// --------------------------------------------------------------------------------------------
	public Browsers() {
		browsersList = new ArrayList<knownBrowsers>();
	}
	
	/**
	 * Detects the browsers installed into the system.
	 * 
	 * @return A messages list containing unhandled browsers
	 * @throws MyException
	 */
	public List<String> detectInstalled() throws MyException {
		List<String> messages = new ArrayList<String>();
		browsersList.clear();
		
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(CMD_LISTBROWSERS);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {
				switch (line.toLowerCase()) {
				case "firefox":
					browsersList.add(knownBrowsers.FIREFOX);
					break;
				case "google chrome":
					browsersList.add(knownBrowsers.CHROME);
					break;
				case "iexplore":
					browsersList.add(knownBrowsers.EXPLORER);
					break;
				default:
					messages.add("Browsers list: unhandled " + line);
				}
			}
            int exitVal = pr.waitFor();
            if (exitVal != 0)
				throw new MyException(String.format("Browsers list: command exited with error code %d", exitVal));
		} catch (IOException | InterruptedException ex) {
			throw new MyException("Browsers list: error running list_browsers command: " + ex.getMessage());
		}
		return messages;
	}
	
	/**
	 * Detects the browser history for a given day for all handled browsers
	 * 
	 * @param day
	 * @throws DbException
	 */
	public List<VisitedSite> detectBrowsersHistory(String day) throws MyException {
		List<VisitedSite> sites = new ArrayList<VisitedSite>();
		for (knownBrowsers browser : browsersList) {
			switch (browser) {
			case FIREFOX:
				detectFirefoxHistory(sites, day);
				break;
			case CHROME:
				detectChromeHistory(sites, day);
				break;
			case EXPLORER:
				detectExplorerHistory(sites, day);
				break;
			}
		}
		return sites;
	}
	
	// --------------------------------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------------------------------
	/**
	 * Specialized methods to detect the sites history of a given day
	 *  
	 * @param sites List to be filled with the history
	 * @param day Day to search for
	 * @throws MyException
	 */
	private void detectFirefoxHistory(List<VisitedSite> sites, String day) throws MyException {
		String appdata = System.getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles";
		
		// Gets the profiles list
		File[] profiles = Utils.listDirectory(new File(appdata));
		if (profiles == null) {
			throw new MyException("Firefox: no profiles found");
		}
		
		// for each profile...
		for (File p : profiles) {
			String dbFile = p.getAbsolutePath() + "\\places.sqlite";
			String sqlCmd = "select strftime('%Y-%m-%d %H:%M:%S', last_visit_date/1000000, 'unixepoch') as lastVisit, typed, url, ifnull(title,'') as title, " +
							"'Firefox' as browser, '" + p.getName() + "' as profile " +
							"from moz_places where strftime('%Y-%m-%d', last_visit_date/1000000, 'unixepoch') = '" + day + "' order by last_visit_date;";
			
			extract_history(sites, dbFile, sqlCmd);
		}
	}

	private void detectChromeHistory(List<VisitedSite> sites, String day) throws MyException {
		String dbFile = System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Default\\History";
		String sqlCmd = "select strftime('%Y-%m-%d %H:%M:%S', last_visit_time/1000000-11644473600, 'unixepoch') as lastVisit, " +
						"typed_count as typed, url, ifnull(title,'') as title, " +
						"'Chrome' as browser, '' as profile " +
						"from urls where strftime('%Y-%m-%d', last_visit_time/1000000-11644473600, 'unixepoch') = '" + day + "' order by last_visit_time;";

		extract_history(sites, dbFile, sqlCmd);
	}

	private void detectExplorerHistory(List<VisitedSite> sites, String day) throws MyException {
		// TODO
	}
	
	/**
	 * Queries a SQLite database file to extract the sites history
	 * 
	 * @param sites List to be filled with the history
	 * @param dbFile The db filename
	 * @param sqlCmd The sql command to be executed
	 * @throws MyException
	 */
	private void extract_history(List<VisitedSite> sites, String dbFile, String sqlCmd) throws MyException {
		try {
			DbSQLite db = new DbSQLite(dbFile);
			List<VisitedSite> list = db.select(sqlCmd, VisitedSite.class);
			if (list != null)
				sites.addAll(list);
		} catch (DbException e) {
			throw new MyException("Error reading history file", e);
		}
	}
}
