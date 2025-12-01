package GUI;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Install FlatLaf for a modern Look & Feel
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("FlatLaf setup failed, continuing with default LAF: " + ex.getMessage());
        }

        // Apply a global UI font (fallback to system font if not available)
        Font uiFont = new Font("Segoe UI", Font.PLAIN, 13);
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, uiFont);
            }
        }

        // Start the login UI
        IDandPassword iDandPassword = new IDandPassword();
        SwingUtilities.invokeLater(() -> new LogInPage(iDandPassword.getLoginInfo()));
    }

    public Main() {
        init();
    }

    public void init() {
        IDandPassword iDandPassword = new IDandPassword();
        SwingUtilities.invokeLater(() -> new LogInPage(iDandPassword.getLoginInfo()));
    }
}
