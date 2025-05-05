package src;

import javax.swing.*;

import java.awt.event.*;

public class LoginGUI extends JFrame{
    public static void main(String[] args) {
        JFrame frame = new JFrame("Productivity_Calculator");
        JLabel userLabel = new JLabel("Username : ");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password: ");
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        userLabel.setBounds(30, 30, 80, 25);
        userField.setBounds(120, 30, 150, 25);
        passLabel.setBounds(30, 70, 80, 25);
        passField.setBounds(120, 70, 150, 25);
        loginBtn.setBounds(100, 110, 80, 30);

        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(userLabel);
        frame.add(userField);
        frame.add(passLabel);
        frame.add(passField);
        frame.add(loginBtn);

        loginBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText();
                char[] password = passField.getPassword();

                LoginLogic val = new LoginLogic();
                Result res = val.validateLogin(user, password);

                if(res.res == 100){
                    JOptionPane.showMessageDialog(null,"No such user found !!");
                }
                if(res.res == 10){
                    JOptionPane.showMessageDialog(null, "Wrong pasword");
                }
                else if(res.res == 1){
                    JOptionPane.showMessageDialog(null, "Login Successful");
                    new ProductivityDashboard(res.username);
                    frame.setVisible(false);
                }
            }
        });
    }
}