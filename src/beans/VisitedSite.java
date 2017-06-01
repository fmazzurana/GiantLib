package beans;

public class VisitedSite {
	
	// properties...
	// ...from the browsers history
	private String lastVisit;	// in the format %Y-%m-%d %H:%M:%S
	private int typed;			// typed into the address bar (1) or not (0)
	private String url;
	private String title;		// could be empty
	// ...internal computed
	private String browser;
	private String profile;
	private String domain;

	// getters & setters
	public String getLastVisit() {
		return lastVisit;
	}
	public void setLastVisit(String lastVisit) {
		this.lastVisit = lastVisit;
	}

	public int getTyped() {
		return typed;
	}
	public void setTyped(int typed) {
		this.typed = typed;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	// publics
	public void setDomainFromUrl() {
		if (url == null || url.isEmpty())
			domain = "";
		else {
			// url: http://<domain>/<extra-path>
			domain = url;
			int p = domain.indexOf("//");
			if (p != -1)
				domain = domain.substring(p+2);
			p = domain.indexOf("/");
			if (p != -1)
				domain = domain.substring(0, p);
		}
	}
}
