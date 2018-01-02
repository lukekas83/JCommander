package jcommander;


import javax.swing.*;
import java.awt.*;

public class JcDialogs {
    private JFrame DialogFrame;

    public JcDialogs() {
        DialogFrame = new JFrame();
    }

    public void ShowErrorDialog(String message) {
        JOptionPane.showMessageDialog(DialogFrame,
                message,
                "Error Message.",
                JOptionPane.ERROR_MESSAGE);
    }

    public void ShowErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(DialogFrame,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    public void ShowInfoDialog(String message) {
        JOptionPane.showMessageDialog(DialogFrame,
                message,
                "Message.",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean ShowYesNoDialog(String message) {
        if (JOptionPane.showConfirmDialog(DialogFrame,
                message,
                "Question.",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    public static void setCenterLocation(Component frame) {
        Dimension screenSize;
        try {
            Toolkit tk = Toolkit.getDefaultToolkit();
            screenSize = tk.getScreenSize();
        } catch (AWTError awe) {
            screenSize = new Dimension(640, 480);
        }
        Dimension frameSize = frame.getSize();
        /* Fill screen if the screen is smaller that qtfSize. */
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        /* Center the screen */
        int x = screenSize.width / 2 - frameSize.width / 2;
        int y = screenSize.height / 2 - frameSize.height / 2;
        frame.setLocation(x, y);
    }

    public static void setBottomRightCorner(Component frame, JFrame parentFrame) {
        /* bottom right corner */
        int x = parentFrame.getWidth() - (frame.getWidth() + 40);
        int y = parentFrame.getHeight() - (frame.getHeight() + 40);
        frame.setLocation(x, y);
    }
}
