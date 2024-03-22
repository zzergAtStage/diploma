package diploma;

import application.services.AuthenticationServiceImpl;
import application.models.User;
import sun.net.ConnectionResetException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LoginWindow<T extends JFrame> extends JDialog {


    private User currentUser;
    private JPanel contentPane;

    public LoginWindow(T c) {
        super(c, "Logon", true);
        initialize(c);
    }

//    public static void main(String[] args) {
//        JDialog dialog = new LoginWindow<>(new MySwingApp());
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void initialize(T c) {
        setLayout(new GridLayout(4, 2));
        setBounds(450, 190, 1014, 597);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(4,2));
        setLocationRelativeTo(c);
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JLabel serverIpLbl = new JLabel("Server");
        contentPane.add(serverIpLbl);
        JTextField serverIP = new JTextField("localhost:8443");
        serverIP.setColumns(10);
        contentPane.add(serverIP);
        contentPane.add(new JLabel("User:"));
        JTextField userId = new JTextField("user");
        //set clear if focused
        contentPane.add(userId);
        contentPane.add(new JLabel("Password:"));
        JPasswordField userPassword = new JPasswordField("Password");
        contentPane.add(userPassword);
        userId.addFocusListener(new FieldFocusListener("Username", userId));
        userPassword.addFocusListener(new FieldFocusListener("Password", userPassword));

        JButton proceedLogin = new JButton("Login");
        JButton cancelLogin = new JButton("Cancel");
        contentPane.add(proceedLogin);
        contentPane.add(cancelLogin);
        proceedLogin.addActionListener(e -> {
            this.currentUser = new User(userId.getText(), userPassword.getPassword());
            this.currentUser.setAuthenticated(false);
            boolean authResult = false;
            String dialogHeader = "Connection error";
            String dialogMessage = "В доступе отказано. Неавторизованным пользователям \nдоступны данные только за последнюю неделю!";
            try {
                authResult = AuthenticationServiceImpl.getInstance()
                        .authenticateUser(serverIP.getText(), currentUser.getUserName(), currentUser.getPassword());
            } catch (ConnectionResetException exception) {
                dialogHeader = "Connection error";
            }


            int jOptionPaneTypeInfo = JOptionPane.ERROR_MESSAGE;
            if (authResult) {
                dialogHeader = "Соединение принято сервером";
                dialogMessage = "Вам предоставлен доступ к полному спектру данных сервера.";
                jOptionPaneTypeInfo = JOptionPane.INFORMATION_MESSAGE;
                this.currentUser.setAuthenticated(true);
                dispose();
            }
            JOptionPane.showMessageDialog(LoginWindow.this,
                    dialogMessage, dialogHeader, jOptionPaneTypeInfo);
        });
        cancelLogin.addActionListener(e -> {
            dispose();
            this.currentUser = null;
        });
        pack();
        setVisible(true);
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
}
