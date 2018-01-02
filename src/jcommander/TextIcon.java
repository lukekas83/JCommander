package jcommander;

public class TextIcon {
    private String text;
    private javax.swing.Icon icon;
    
    public TextIcon(String text, javax.swing.Icon icon) {
        this.text = text;
        this.icon = icon;
    }    

    public javax.swing.Icon getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
