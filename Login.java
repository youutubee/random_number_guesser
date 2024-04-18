import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class LoginFrame extends JFrame {
    private static final String USER_INFO_FILENAME = "user_information.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginFrame() {
        super("Login");
        initializeComponents();
        setupLayout();
        setupActions();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(usernameField);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);
        mainPanel.add(new JLabel());
        mainPanel.add(loginButton);
        mainPanel.add(new JLabel());
        mainPanel.add(signUpButton);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupActions() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    dispose(); // Close the login frame
                    new gui().setVisible(true); // Open the main GUI application
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignUpDialog signUpDialog = new SignUpDialog(LoginFrame.this);
                signUpDialog.setVisible(true);
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        File userInfoFile = new File(USER_INFO_FILENAME);
        if (userInfoFile.exists()) {
            try (FileReader reader = new FileReader(USER_INFO_FILENAME)) {
                UserInfo userInfo = gson.fromJson(reader, UserInfo.class);
                if (userInfo != null && userInfo.getUsername() != null) {
                    return userInfo.getUsername().equals(username) && userInfo.getPassword().equals(password);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            askForNewAccount();
        }
        return false;
    }

    private void askForNewAccount() {
        int option = JOptionPane.showConfirmDialog(null, "No account found. Do you want to create a new account?",
                "Create New Account", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            SignUpDialog signUpDialog = new SignUpDialog(LoginFrame.this);
            signUpDialog.setVisible(true);
        } else {
            System.exit(0);
        }
    }

    public void saveUserInfo(String username, String password, String name, double initialAmount) {
        UserInfo userInfo = new UserInfo(username, password, name, initialAmount);
        try (FileWriter writer = new FileWriter(USER_INFO_FILENAME)) {
            gson.toJson(userInfo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}

class SignUpDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField initialAmountField;
    private JButton signUpButton;

    public SignUpDialog(JFrame parent) {
        super(parent, "Sign Up", true);
        initializeComponents();
        setupLayout();
        setupActions();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        nameField = new JTextField(20);
        initialAmountField = new JTextField(20);
        signUpButton = new JButton("Sign Up");
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(usernameField);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);
        mainPanel.add(new JLabel("Name:"));
        mainPanel.add(nameField);
        mainPanel.add(new JLabel("Initial Amount:"));
        mainPanel.add(initialAmountField);
        mainPanel.add(new JLabel());
        mainPanel.add(signUpButton);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void setupActions() {
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String name = nameField.getText();
                double initialAmount = Double.parseDouble(initialAmountField.getText());

                LoginFrame parentFrame = (LoginFrame) getParent();
                parentFrame.saveUserInfo(username, password, name, initialAmount);
                dispose();
                JOptionPane.showMessageDialog(parentFrame, "Account created successfully! You can now log in.");
            }
        });
    }
}

class UserInfo {
    private String username;
    private String password;
    private String name;
    private double initialAmount;

    public UserInfo(String username, String password, String name, double initialAmount) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.initialAmount = initialAmount;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public double getInitialAmount() {
        return initialAmount;
    }
}
