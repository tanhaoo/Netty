package com.th.netty.s02;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author TanHaooo
 * @date 2021/2/24 21:12
 */
public class ClientFrame extends Frame {
    TextArea ta = new TextArea();
    TextField tf = new TextField();
    Client c = null;

    public static final ClientFrame INSTANCE = new ClientFrame();

    private ClientFrame() {
        this.setSize(600, 400);
        this.setLocation(100, 20);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.send(tf.getText());
                // ta.setText(ta.getText() + tf.getText() + "\n");
                tf.setText("");
            }
        });
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                c.closeConnect();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        ClientFrame frame = ClientFrame.INSTANCE;
        frame.connectToServer();
    }

    public void connectToServer() {
        c = new Client();
        c.connect();
    }

    public void update(String msg) {
        ta.setText(ta.getText() + msg + "\n");
    }

}
