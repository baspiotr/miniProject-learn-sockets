package Tpo3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Piotr on 2015-03-27.
 */
public class Client {


    private static SocketChannel channel;
    private ByteBuffer inByteBuffer = ByteBuffer.allocateDirect(1024);
    private static Charset charset = Charset.forName("ISO-8859-2");
    private ClientGui gui;
    String messageStr = "";

    ArrayList<String> subscribeList =new ArrayList<String>();

    private ArrayList<String> topicsList = new ArrayList<String>();

    public Client() throws IOException, InterruptedException {

        topicListInit();

        connectToServer();
        gui = new ClientGui();
        guiController();

        readFromChennel();

    }

    private void topicListInit() {

        topicsList.add("polityka");
        topicsList.add("sport");
        topicsList.add("celebryci");
        topicsList.add("gotowanie");
        topicsList.add("randki");

    }

    private void guiController() {

try {

    gui.getSubjectComboBox().addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {


            String s = (String) gui.getSubjectComboBox().getSelectedItem();

            System.out.println(s);

            System.out.println(subscribeList.toString() + "ascascas");
if(subscribeList.contains(s)){

                gui.getSendSubscribeButton().setVisible(false);//setEnabled(false);

            }else{

                gui.getSendSubscribeButton().setVisible(true);

            }

        }
    });
}catch (Exception ert){

}
        gui.getSendSubscribeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                messageStr = "";
                messageStr = "subscribe ";


                messageStr += gui.getSubjectComboBoxSelectedValue();

                subscribeList.add(gui.getSubjectComboBoxSelectedValue());

                try {
                    sendToServer(messageStr);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


            }
        });


        gui.getSendUnsubscribeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                messageStr = "";
                messageStr = "unsubscribe ";

                messageStr += gui.getSubjectComboBoxSelectedValue();


                if (subscribeList.contains(gui.getSubjectComboBoxSelectedValue())) {

                    subscribeList.remove(gui.getSubjectComboBoxSelectedValue());

                }

                try {
                    sendToServer(messageStr);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void readFromChennel() throws IOException {

        StringBuffer result = new StringBuffer();
        CharBuffer cbuf;

        while (true) {

            inByteBuffer.clear();

            try {
                int readBytes = channel.read(inByteBuffer);


                if (readBytes == -1) {
                    System.out.println("Błąd, kanał po stronie serwera został zamknięty");
                    break;
                } else {
                    inByteBuffer.flip();

                    cbuf = charset.decode(inByteBuffer);
                    result.append(cbuf);

                    if (result.length() != 0) {

                        System.out.println("dostałem " + result.toString());


                        if (result.toString().contains("getTopicListSize")) {

                            sendToServer("size " + topicsList.size());

                        }
                        if (result.toString().contains("addTopicToComboBox")) {

                            StringTokenizer st = new StringTokenizer(result.toString());

                            String topic;
                            topic = st.nextToken();
                            topic = st.nextToken();

                            System.out.println("Dodaje " + topic + " do combobox");

                            gui.getSubjectComboBox().addItem(topic);

                        }

                        if (!result.toString().contains("getTopicListSize") && !result.toString().contains("addTopicToComboBox")) {
                            gui.getNewsTextArea().append(result.toString() + '\n');
                        }

                        System.out.println("Przyszło od serwara " + result.toString());

                        result.setLength(0);
                    }

                }
            } catch (IOException ex) {
                ex.printStackTrace();

                channel.close();
                gui.getFrame().dispatchEvent(new WindowEvent(gui.getFrame(), WindowEvent.WINDOW_CLOSING));
            }
        }

    }


    public static void sendToServer(String messageToSend) throws IOException {

        StringBuffer outStringBuffer = new StringBuffer();

        outStringBuffer.setLength(0);

        outStringBuffer.append(messageToSend);
        outStringBuffer.append('\n');

        ByteBuffer buf = charset.encode(CharBuffer.wrap(outStringBuffer));
        channel.write(buf);
    }

    private void connectToServer() throws IOException, InterruptedException {

        channel = SocketChannel.open();
        channel.configureBlocking(false);

        channel.connect(new InetSocketAddress("localhost", 8190));

        System.out.println("Connecting to server: ");

        while (!channel.finishConnect()) {
            System.out.print(".");
            Thread.sleep(200);
        }
        System.out.println("CONNECTED");

    }


    public static void main(String args[]) throws IOException, InterruptedException {
        new Client();
    }

}
