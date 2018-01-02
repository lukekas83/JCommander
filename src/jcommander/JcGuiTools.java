package jcommander;

import javax.swing.*;

import java.awt.*;
import java.awt.datatransfer.*;

public class JcGuiTools {
	private Font font = null;
	
	public JcGuiTools(Font font) {
		this.font = font;
	}
	
	public JcGuiTools() {
		this(new JLabel().getFont());
	}
	
    private void setFont(JMenuBar menu_bar) {
        if (menu_bar != null && menu_bar.getMenuCount() > 0) {            
            for (int m = 0; m < menu_bar.getMenuCount(); m++) {
                for (int mi = 0; mi < menu_bar.getMenu(m).getItemCount(); mi++) {
                    menu_bar.getMenu(m).setFont(font);
                    if (menu_bar.getMenu(m).getItem(mi) != null) {
                        menu_bar.getMenu(m).getItem(mi).setFont(font);
                    }
                }
            }
        }
    }

    private void setFont(JPopupMenu popup_menu) {
        if (popup_menu != null && popup_menu.getComponentCount() > 0) {            
            for (int mi = 0; mi < popup_menu.getComponentCount(); mi++) {
                if (popup_menu.getComponent(mi) != null) {
                    popup_menu.getComponent(mi).setFont(font);
                }
            }
        }
    }

    private void MenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        java.awt.datatransfer.Clipboard clipbd = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
        Object parent = ((JMenuItem) evt.getSource()).getParent();
        MyJPopupMenu pm = (MyJPopupMenu) parent;
        if (((MyJMenuItem) evt.getSource()).getItemType() == MyJMenuItem.COPY) {
            String selection = pm.getParentSelectedText();
            if (selection == null) {
                return;
            }
            StringSelection clipString = new StringSelection(selection);
            clipbd.setContents(clipString, clipString);
        } else if (((MyJMenuItem) evt.getSource()).getItemType() == MyJMenuItem.CUT) {
            String selection = pm.getParentSelectedText();
            if (selection == null) {
                return;
            }
            StringSelection clipString = new StringSelection(selection);
            clipbd.setContents(clipString, clipString);
            pm.replaceParentTextSelection("");
        } else if (((MyJMenuItem) evt.getSource()).getItemType() == MyJMenuItem.PASTE) {
            java.awt.datatransfer.Transferable clipData = clipbd.getContents(pm);
            try {
                String clipString = (String) clipData.getTransferData(DataFlavor.stringFlavor);
                pm.replaceParentTextSelection(clipString);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private JPopupMenu buildPopupMenu(Object parent) {
        MyJPopupMenu popupMenu = new MyJPopupMenu(parent);
        popupMenu.add(new MyJMenuItem("Copy", MyJMenuItem.COPY));
        popupMenu.add(new MyJMenuItem("Cut", MyJMenuItem.CUT));
        popupMenu.add(new MyJMenuItem("Paste", MyJMenuItem.PASTE));

        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            if (font != null) {
                ((JMenuItem) popupMenu.getComponent(i)).setFont(font);
            }
            ((JMenuItem) popupMenu.getComponent(i)).addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MenuItemActionPerformed(evt);
                }
            });
        }
        return popupMenu;
    }

    private void addPopupMenuToJTextComponent(Object JTextComponent) {
        if (JTextComponent instanceof JComponent) {
            if (JTextComponent instanceof JTextField) {
                ((JTextField) JTextComponent).setComponentPopupMenu(buildPopupMenu(JTextComponent));
            }
            for (int i = 0; i < ((JComponent) JTextComponent).getComponentCount(); i++) {
                addPopupMenuToJTextComponent(((JComponent) JTextComponent).getComponent(i));
            }
        }
    }

    public void setFont(Object object) {
        if (object instanceof JComponent) {
        	Font _font = new Font(font.getFontName(), font.getStyle(), font.getSize());
            if (object instanceof JLabel) {
                Font _org_font = ((JComponent) object).getFont();                
                if (_org_font.isBold()) {
                	_font = new Font(font.getFontName(), Font.BOLD, font.getSize());
                }
            }
            ((JComponent) object).setFont(_font);
            setFont(((JComponent) object).getComponentPopupMenu());
            for (int i = 0; i < ((JComponent) object).getComponentCount(); i++) {
                setFont(((JComponent) object).getComponent(i));
            }
        }
    }
    
    public void setEnabled(Object object, boolean enabled) {
        if (object instanceof JFrame) {
        	((JComponent)object).setEnabled(enabled);
        }
    }

    public void setGUI(javax.swing.JFrame frame) {
        setFont(frame.getContentPane());
        addPopupMenuToJTextComponent(frame.getContentPane());
        setFont(frame.getJMenuBar());
    }

    public void setGUI(javax.swing.JDialog frame) {
        setFont(frame.getContentPane());
        addPopupMenuToJTextComponent(frame.getContentPane());
        setFont(frame.getJMenuBar());
    }

    public void addPopupMenuToJTextComponent(JTextField jtext_component) {
        ((JTextField) jtext_component).setComponentPopupMenu(buildPopupMenu(jtext_component));
    }

    class MyJPopupMenu extends JPopupMenu {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object parent;

        public MyJPopupMenu(Object parent) {
            super();
            this.parent = parent;
        }

        public String getParentSelectedText() {
            return ((JTextField) parent).getSelectedText();
        }

        public void setParentText(String txt) {
            ((JTextField) parent).setText(txt);
        }

        public void replaceParentTextSelection(String new_text) {
            ((JTextField) parent).replaceSelection(new_text);
        }
    }

    class MyJMenuItem extends JMenuItem {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int ItemType;
        public static final int COPY = 0;
        public static final int CUT = 1;
        public static final int PASTE = 2;

        public MyJMenuItem(String text, int ItemType) {
            super(text);
            this.ItemType = ItemType;
        }

        public int getItemType() {
            return ItemType;
        }
    }
}
