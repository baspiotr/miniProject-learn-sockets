package Tpo3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by Piotr on 2015-03-29.
 */
public class InfoSend {

    private SocketChannel channel;
    private Charset charset = Charset.forName("ISO-8859-2");

    public InfoSend() throws IOException {


        connectToServer();
        guiInit();


    }


    private void guiInit() {

        final JFrame frame = new JFrame("Send news to server");

        frame.setLayout(new GridLayout(2, 1));
        frame.setSize(400, 400);


        JPanel mainPanel = new JPanel(new GridLayout(3, 1));


        JPanel addTopicPanel = new JPanel(new GridLayout(2, 1));
        final JTextField addTopicTextFiled = new JTextField();
        JButton addTopicButton = new JButton("Dodaj temat");

        addTopicPanel.add(addTopicTextFiled);
        addTopicPanel.add(addTopicButton);


        final JTextArea textArea = new JTextArea();
        final JButton sendButton = new JButton("Send");
        final JComboBox<String> comboBox = new JComboBox<String>();


        comboBox.addItem("polityka");
        comboBox.addItem("sport");
        comboBox.addItem("celebryci");
        comboBox.addItem("gotowanie");
        comboBox.addItem("randki");

        mainPanel.add(textArea);
        mainPanel.add(comboBox);
        mainPanel.add(sendButton);

        frame.add(mainPanel);
        frame.add(addTopicPanel);


        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                try {
                    sendToServer("close me");

                    Thread.sleep(200);
                    System.exit(0);
                } catch (IOException eg) {

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //  }
        });


        addTopicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                comboBox.addItem(addTopicTextFiled.getText().toString());


                String msg = "addTopic " + addTopicTextFiled.getText().toString();

                try {
                    sendToServer(msg);
                } catch (IOException e1) {

                }

            }
        });


        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String message = "news ";

                message += comboBox.getSelectedItem().toString() + " ";

                message += textArea.getText().toString();

                System.out.println(message);

                try {
                    sendToServer(message);
                } catch (Exception ex) {
                    ex.printStackTrace();

                    try {
                        connectToServer();
                        sendToServer(message);

                    } catch (Exception ec) {
                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    }
                }
            }
        });


    }


    private void connectToServer() throws IOException {

        channel = SocketChannel.open();
        channel.configureBlocking(false);

        channel.connect(new InetSocketAddress("localhost", 8190));

        System.out.println("Connecting to server: ");

        while (!channel.finishConnect()) {
            System.out.print(".");
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
        }
        System.out.println("CONNECTED");


    }

    private void sendToServer(String messageToSend) throws IOException {

        StringBuffer outStringBuffer = new StringBuffer();

        outStringBuffer.setLength(0);

        outStringBuffer.append(messageToSend);
        outStringBuffer.append('\n');

        ByteBuffer buf = charset.encode(CharBuffer.wrap(outStringBuffer));
        channel.write(buf);


    }

    public static void main(String args[]) throws IOException {


        new InfoSend();

    }


}
