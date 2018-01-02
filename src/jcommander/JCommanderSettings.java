package jcommander;

public class JCommanderSettings {
    public static final String ConfigFileName = "jcommandercfg.xml";   
	private String LocaleArg1;
    private String LocaleArg2;
    private int    Side;
    private String fontName;
    /*http://jira.codehaus.org/browse/XSTR-76?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel
     * font style dla PLAIN wynosi 0 czyli defualt dla int'a, nie nastepuje wtedy serializacja tej zmiennej */
    private int	   fontStyle = -1;
    private int    fontSize;
    private String styleName; /*Metal ; Nimbus ; CDE/Motif ; Windows ; Windows Classic */
    
    public int getSide() {
    	return Side;    	
    }
    
    public void setSide(int Side) {
    	this.Side = Side;
    }
    
    public void setLocaleArg1(String localeArg1) {
		LocaleArg1 = localeArg1;
	}
    
	public String getLocaleArg1() {
		return LocaleArg1;
	}
	
	public void setLocaleArg2(String localeArg2) {
		LocaleArg2 = localeArg2;
	}
	
	public String getLocaleArg2() {
		return LocaleArg2;
	}
	
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	
	public String getFontName() {
		return fontName;
	}
	
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	public int getFontStyle() {
		return fontStyle;
	}
	
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	
	public int getFontSize() {
		return fontSize;
	}

	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}

	public String getStyleName() {
		return styleName;
	}
}
