import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private boolean authenticated = false;
    private LoginManager loginManager;

    public LoginDialog(Frame parent) {
        super(parent, "Login/Register", true);
        loginManager = new LoginManager();
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(parent);

        // Set the background color of the dialog to black
        getContentPane().setBackground(Color.BLACK);

        // Input panel with black background and white labels
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Color.BLACK);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        inputPanel.add(usernameLabel);
        JTextField usernameField = new JTextField();
        usernameField.setBackground(Color.DARK_GRAY);
        usernameField.setForeground(Color.WHITE);
        inputPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        inputPanel.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBackground(Color.DARK_GRAY);
        passwordField.setForeground(Color.WHITE);
        inputPanel.add(passwordField);

        add(inputPanel, BorderLayout.CENTER);

        // Buttons panel with black background
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(Color.BLACK);
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Action listener for login
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (loginManager.login(username, password)) {
                authenticated = true;
                dispose(); // Close dialog on successful login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action listener for registration
        registerBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (loginManager.register(username, password)) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
