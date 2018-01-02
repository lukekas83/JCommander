package jcommander;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

public class JDialogUtils {
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
}
