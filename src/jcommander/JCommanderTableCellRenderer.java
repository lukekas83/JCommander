package jcommander;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class JCommanderTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public JCommanderTableCellRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	
    	setText(value != null ?  value.toString() : "");
    	setBorder(BorderFactory.createEmptyBorder());
    	table.setIntercellSpacing(new Dimension(0,0));     	
        super.setHorizontalTextPosition(RIGHT);
        super.setIconTextGap(6);
        Font tf = table.getFont();        
        super.setFont(new Font(tf.getName(), 1 ,tf.getSize())); 
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
