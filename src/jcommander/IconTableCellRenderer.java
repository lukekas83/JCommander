package jcommander;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;

public class IconTableCellRenderer extends JCommanderTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public IconTableCellRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
    	
        if (value instanceof TextIcon) {
            setIcon(((TextIcon) value).getIcon());
            setText(((TextIcon) value).getText());
        } else {
            setText((value == null) ? "" : value.toString());
            setIcon(null);
        }
        super.setHorizontalTextPosition(RIGHT);
        super.setIconTextGap(6);    
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());            
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        if(((FilesTableModel)table.getModel()).isFileSelected(row))
        	setForeground(Color.red);        
        
        return this;
    }
}
