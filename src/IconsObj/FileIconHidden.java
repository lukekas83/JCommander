package IconsObj;

import java.net.URL;

import javax.swing.ImageIcon;

public class FileIconHidden {
	private static FileIconHidden fileIcon;
	private javax.swing.Icon icon;
	
	protected FileIconHidden(URL iconFileName) {
		icon = new ImageIcon(iconFileName);
	}
	
	public static FileIconHidden getInstance(URL iconFileName) {
		if(fileIcon == null) {
			fileIcon = new FileIconHidden(iconFileName);
		}
		return fileIcon;
	}
	
	public javax.swing.Icon getIcon() {
		return icon;
	}
}