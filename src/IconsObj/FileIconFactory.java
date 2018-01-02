package IconsObj;

import icons.FooIcon;

import java.io.File;


public class FileIconFactory {
	private static final String FOLDER_ICON = "Folder.png";
	private static final String FOLDER_ICON_HIDDEN = "FolderHidden.png";
	private static final String FOLDER_UP_ICON = "Up.png";
	private static final String FILE_ICON = "File.png";
	private static final String FILE_ICON_HIDDEN = "FileHidden.png";
	
	private static final FooIcon fc = new FooIcon();
	
	public static javax.swing.Icon getIconForFile(File file) {
		if(file == null)
			return FolderUpIcon.getInstance(fc.getClass().getResource(FOLDER_UP_ICON)).getIcon();
		else if(file.isDirectory()) {
			if(file.isHidden())
				return FolderIconHidden.getInstance(fc.getClass().getResource(FOLDER_ICON_HIDDEN)).getIcon();
				
			return FolderIcon.getInstance(fc.getClass().getResource(FOLDER_ICON)).getIcon();
		}
		else
			if(file.isHidden())
				return FileIconHidden.getInstance(fc.getClass().getResource(FILE_ICON_HIDDEN)).getIcon();
		
			return FileIcon.getInstance(fc.getClass().getResource(FILE_ICON)).getIcon();	
	}
}