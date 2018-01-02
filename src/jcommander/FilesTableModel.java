package jcommander;


import java.io.File;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import IconsObj.FileIconFactory;

public class FilesTableModel extends AbstractTableModel  {
	private static final long serialVersionUID = 1L;
	private String[] columnNames = 
	{
			JCommander.bundle.getString("FilesTableModel.Name"),
			JCommander.bundle.getString("FilesTableModel.Ext"),
			JCommander.bundle.getString("FilesTableModel.Size"),
			JCommander.bundle.getString("FilesTableModel.Date"),			
			JCommander.bundle.getString("FilesTableModel.Atrr")		
	};
	private HashMap<String, Integer> absolutePathIdx = null;
	private Object[][] data;
	private boolean[] rowSelected;
	private int offset = 0;
	private long totalBytesInFiles;
	private int totalfilesCounted;
	private int totalfilesDirsCounted;
	
	@SuppressWarnings("deprecation")
	public FilesTableModel(File[] files, File root) {
		offset = 0;
		java.util.Arrays.sort(files, new FileComparator());
		absolutePathIdx = new HashMap<String, Integer>();
		
		if(root.getParentFile() != null) {			
			offset = 1;
			data = new Object[files.length + offset][columnNames.length + 2];
			data[0][0] = new TextIcon("...", FileIconFactory.getIconForFile(null));
			data[0][2] = "";
			data[0][5] = root.getParentFile();
			data[0][6] = 0;
			absolutePathIdx.put(root.getParentFile().getAbsolutePath(), 0);
		}
		else
			data = new Object[files.length + offset][columnNames.length + 2];
		
		rowSelected = new boolean[data.length];
			
		for(int f = 0 ; f < files.length ; f++) {			
			data[f + offset][0] = new TextIcon(getFileName(files[f]), FileIconFactory.getIconForFile(files[f]));
			data[f + offset][1] = getFileExt(files[f]);
			data[f + offset][2] = files[f].isFile() ? NumberFormat.getInstance().format(files[f].length()) : "<DIR>";
			data[f + offset][3] = new Date(files[f].lastModified()).toLocaleString();
			data[f + offset][4] = buildAtributes(files[f]);
			data[f + offset][5] = files[f];
			data[f + offset][6] = 0; 
			absolutePathIdx.put(files[f].getAbsolutePath(), f + offset);
			rowSelected[f + offset] = false;
			if(files[f].isFile()) {
				totalBytesInFiles += files[f].length();
				totalfilesCounted += 1;
			}
			else if(files[f].isDirectory())
				totalfilesDirsCounted += 1;
		}		
	}	
	
	private String getFileExt(File file) {
		String ext = "";
		if(file.isFile()) {
			String fileName = file.getName();
			int lidx = -1;
			if((lidx = fileName.lastIndexOf(".")) != -1) {
				if(lidx == 0)
					ext = "";
				else
					ext = fileName.substring(lidx+1);
			}
		}
		
		return ext;
	}

	private String getFileName(File file) {		
		String fileName = file.getName();
		String filen = fileName;
		if(file.isFile()) {
			int lidx = -1;
			if((lidx = fileName.lastIndexOf(".")) != -1) {
				filen = fileName.substring(0, lidx);
				if(filen.length() == 0)
					filen = fileName;
			}
			else
				filen = fileName;
		}
		
		return filen;
	}
	
	private String buildAtributes(File file) {
		StringBuilder sb = new StringBuilder();
				
		if(file.canExecute())
			sb.append("x");
		
		if(file.canRead())
			sb.append("r");
		
		if(file.canWrite())
			sb.append("w");		
		
		return sb.toString();
	}
	
	@Override
	public int getColumnCount() {		
		return columnNames.length;
	}

	@Override
	public int getRowCount() {		
		return data.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	@Override
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = newValue;
	}
	
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public int getFileIndex(String absolutePath) {
    	if(absolutePathIdx.containsKey(absolutePath))
    		return absolutePathIdx.get(absolutePath);
    	else
    		return 0;
    }
    
    public void setFileSelected(int rowIdx, boolean isSelected) {
    	rowSelected[rowIdx] = isSelected;
    }
    
    public boolean isFileSelected(int rowIdx) {
    	return rowSelected[rowIdx];
    }
    
    public long getTotalBytesInFiles() {
    	return getTotalBytesInDirs() + totalBytesInFiles;
    }
    
    public int getTotalFilesCounted() {
    	return totalfilesCounted;
    }
    
    public int getTotalDirsCounted() {
    	return totalfilesDirsCounted;
    }
    
    private long getTotalBytesInDirs() {
    	long counted = 0;
    	for(int f = 0 ; f < data.length ; f++) {
    		if(!data[f][2].toString().equals("<DIR>")) {
    			counted += Long.parseLong(data[f][6].toString());
    		}
		}	
    	return counted;
    }
    
    public int getSelectedDirsCounted() {
    	int counted = 0;
    	for(int f = 0 ; f < data.length ; f++) {
    		if(rowSelected[f] && ((File)data[f][5]).isDirectory())
    			counted += 1;
    	}
    	return counted;
    	
    }
    
    public long[] getSelectedFilesAndBytesCounted() {
    	long counted[] = new long[2];
    	counted[0] = 0;
    	counted[1] = 0;
    	for(int f = 0 ; f < data.length ; f++) {
    		if(rowSelected[f]) {
    			if(((File)data[f][5]).isFile()) {
    				++counted[0];
    				counted[1] += ((File)data[f][5]).length(); 
    			}
    			else if(((File)data[f][5]).isDirectory()) {
    				if(!data[f][2].toString().equals("<DIR>")) {
    					counted[1] += Long.parseLong(data[f][6].toString()); 
    				}
    			}
    		}	
    	}
    	return counted;
    }
    
	class FileComparator implements Comparator<File> {
		public int compare(File f1, File f2) {			
			if(f1.isDirectory() && !f2.isDirectory()) {
				return -1;
			}
			else if(!f1.isDirectory() && f2.isDirectory()) {
				return 1;
			}			
			return getFileName(f1).toUpperCase().compareTo(getFileName(f2).toUpperCase() );
		}
	}    
}