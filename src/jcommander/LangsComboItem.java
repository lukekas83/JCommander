package jcommander;

public class LangsComboItem {
	private String[] lang;
	private String fullName;
	
	public LangsComboItem(String[] lang, String fullName) {
		this.lang = lang;
		this.fullName = fullName;
	}
	
	public String[] getLang() {
		return lang;
	}	
	
	public String toString() {
		return fullName;
	}
}
