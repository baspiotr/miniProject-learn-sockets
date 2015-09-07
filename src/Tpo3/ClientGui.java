package Tpo3;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Piotr on 2015-03-28.
 */
public class ClientGui {


    private JFrame frame;
    public JButton sendSubscribeButton, sendUnsubscribeButton;
    private JTextArea newsTextArea;


    private JComboBox<String> subjectComboBox;
    private JLabel infoOneLabel, infoTwolabel;
    private JPanel panelOne, panelTwo;


    public ClientGui() {


        guiInit();


    }


    private ComboBoxRenderer renderer;

    private void guiInit() {


        frame = new JFrame();
        frame.setLayout(new GridLayout(2, 1));
        frame.setSize(900, 400);

        sendSubscribeButton = new JButton("Subscribe");
        sendUnsubscribeButton = new JButton("Unsubscribe");
        subjectComboBox = new JComboBox<String>();


        newsTextArea = new JTextArea();


        DefaultCaret caret = (DefaultCaret) newsTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollNewsTextArea = new JScrollPane();
        scrollNewsTextArea.setViewportView(newsTextArea);


        infoOneLabel = new JLabel("Wpisz temat który Cię interesuje. Kliknij subscribe aby dostawać wiadomości o tej tematyce, lub Unsubscribe aby zrezygnować z ich otrzymywania.");
        infoTwolabel = new JLabel("Poniżej wyświetlane są przychodzące wiadomości.");


        panelOne = new JPanel(new GridLayout(4, 1));
        panelTwo = new JPanel(new GridLayout(2, 1));

        panelOne.add(infoOneLabel);


        subjectComboBox.addItem("polityka");
        subjectComboBox.addItem("sport");
        subjectComboBox.addItem("celebryci");
        subjectComboBox.addItem("gotowanie");
        subjectComboBox.addItem("randki");


        panelOne.add(subjectComboBox);

        panelOne.add(sendSubscribeButton);
        panelOne.add(sendUnsubscribeButton);


        frame.add(panelOne);

        panelTwo.add(infoTwolabel);
        panelTwo.add(scrollNewsTextArea);

        frame.add(panelTwo);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                try {
                    Client.sendToServer("close me");

                    Thread.sleep(200);
                    System.exit(0);
                } catch (IOException eg) {

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            // }
        });


        frame.setVisible(true);


    }



    public String getSubjectComboBoxSelectedValue() {
        return subjectComboBox.getSelectedItem().toString();
    }


    public JComboBox<String> getSubjectComboBox() {
        return subjectComboBox;
    }


    public JTextArea getNewsTextArea() {
        return newsTextArea;
    }


    public JButton getSendUnsubscribeButton() {
        return sendUnsubscribeButton;
    }


    public JButton getSendSubscribeButton() {
        return sendSubscribeButton;
    }


    public JFrame getFrame() {
        return frame;
    }


    public static void main(String args[]) {

        new ClientGui();

    }


}
