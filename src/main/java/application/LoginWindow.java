package application;

import application.services.LoginService;
import diploma.models.User;
import diploma.models.UserCredentials;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Arrays;

public class LoginWindow<T extends JFrame> extends JDialog implements LoginService {

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    private User currentUser;
    public LoginWindow(T c) {
        super(c, "Logon", true);
        initialize(c);
    }

    private void initialize(T c) {
        setLayout(new GridLayout(4, 2));
        setLocationRelativeTo(c);
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JLabel serverIpLbl = new JLabel("Server");
        add(serverIpLbl);
        JTextField serverIP = new JTextField("localhost:4505");
        add(serverIP);
        add(new JLabel("User:"));
        JTextField userId = new JTextField("Username");
        //set clear if focused
        add(userId);
        add(new JLabel("Password:"));
        JPasswordField userPassword = new JPasswordField("Password");
        add(userPassword);
        userId.addFocusListener(new FieldFocusListener("Username", userId));
        userPassword.addFocusListener(new FieldFocusListener("Password", userPassword));

        JButton proceedLogin = new JButton("Login");
        JButton cancelLogin = new JButton("Cancel");
        add(proceedLogin);
        add(cancelLogin);
        proceedLogin.addActionListener(e -> {

            String message = null;

            while (getCurrentUser() != null && !getCurrentUser().isOnline()) {
                String dialogHeader = "Login error!";
                int jOptionPaneTypeInfo = JOptionPane.ERROR_MESSAGE;
                if (message.isEmpty() || message.equals("Go ahead!")) { //just to demonstrate functionality
                    dialogHeader = "Ok!";
                    jOptionPaneTypeInfo = JOptionPane.INFORMATION_MESSAGE;
                    this.currentUser.setOnline(true);
                    dispose();
                }
                JOptionPane.showMessageDialog(LoginWindow.this, message, dialogHeader, jOptionPaneTypeInfo);
            }
        });
        cancelLogin.addActionListener(e -> {
            dispose();
            this.currentUser = null;
            System.exit(0);
        });
        pack();
        setVisible(true);
    }


    public String loginUser(User user, Inet4Address server, char[] password) {
        //TODO create interface to work with password (hash) and server answer
        try {
            this.currentUser = new User("SomeStubUser", "Empty"
                    , Integer.parseInt("1"));
            UserCredentials userCredentials = new UserCredentials(Integer.parseInt("1"), Arrays.hashCode(password));

            return "ServerAuthorizationStub.checkUserAuthority(userCredentials);";
        } catch (NumberFormatException e) {
            return "Wrong input format. Try again!";
        }
    }


    private class FieldFocusListener implements FocusListener {
        private String defaultValue;
        private JTextField textField;

        FieldFocusListener(String defaultValue, JTextField textField) {
            this.defaultValue = defaultValue;
            this.textField = textField;
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (textField.getText().equals(defaultValue)) {
                textField.setText("");
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (textField.getText().isEmpty()) {
                textField.setText(defaultValue);
            }
        }


    }
    public static void main(String[] args) {
        JDialog dialog = new LoginWindow<>(new MySwingApp());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
