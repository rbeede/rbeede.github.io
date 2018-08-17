/* YahooFolder
 *
 * Just holds the folder name and url
 *
*/

public class YahooFolder {
	protected String strName = "";
	protected String strURL = "";
	
	public void setName(String strName) {
		this.strName = strName;
	}
	
	public void setURL(String strURL) {
		this.strURL = strURL;
	}
	
	public String getName() {
		return this.strName;
	}
	
	public String getURL() {
		return this.strURL;
	}
	
}