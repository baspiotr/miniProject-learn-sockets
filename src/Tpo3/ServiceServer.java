package Tpo3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Piotr on 2015-03-27.
 */
public class ServiceServer {

    private int port;
    private Selector selector;
    private SelectionKey selectionKey;
    private ServerSocketChannel serverChannel;
    private Set keys;
    private Iterator iterator;
    private ArrayList<String> topic = new ArrayList<String>();


    private StringBuffer requestString = new StringBuffer();
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private Charset charset = Charset.forName("ISO-8859-2");


    private ArrayList<SocketChannel> allClientsList = new ArrayList<SocketChannel>();


    private HashMap<String, ArrayList<SocketChannel>> topicsToClientsHashMap;


    public ServiceServer() throws IOException {

        newsMapInit();
        serverInit();

    }


    private void serverInit() throws IOException {


        port = 8190;

        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress("localhost", port));

        serverChannel.configureBlocking(false);

        selector = Selector.open();

        selectionKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        try {
            serverLoop();
        } catch (Exception e) {

        }

    }

    private void serverLoop() throws IOException {

        System.out.println("Server: in Loop");
        while (true) {

            selector.select();
            keys = selector.selectedKeys();
            iterator = keys.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {

                    SocketChannel socketChannel = serverChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                    System.out.println("połączono z klientem");


                    allClientsList.add(socketChannel);

                    sendToSelectedClient(socketChannel, "getTopicListSize");


                    continue;
                }


                if (key.isReadable()) {

                    try {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        serviceRequest(socketChannel);

                    } catch (Exception e) {

                    }

                    continue;
                }
            }
        }
    }


    private void serviceRequest(SocketChannel socketChannel) throws IOException {

        if (!socketChannel.isOpen()) return;

        requestString.setLength(0);
        byteBuffer.clear();

        readLoop:
        while (true) {
            if (socketChannel.isOpen()) {

                int n = socketChannel.read(byteBuffer);
                if (n > 0) {
                    byteBuffer.flip();
                    CharBuffer charBuffer = charset.decode(byteBuffer);
                    while (charBuffer.hasRemaining()) {
                        char c = charBuffer.get();
                        if (c == '\r' || c == '\n') break readLoop;
                        requestString.append(c);
                    }
                }
            }
        }

        System.out.println(requestString);


        StringTokenizer stringTokenizer = new StringTokenizer(requestString.toString());


        String command = "", subject = "";

        command = stringTokenizer.nextToken();
        subject = stringTokenizer.nextToken();

        if (command.equals("subscribe")) {

            System.out.println("zapisz się na " + subject);


            if (!topicsToClientsHashMap.get(subject).contains(socketChannel)) {
                topicsToClientsHashMap.get(subject).add(socketChannel);

            }


        } else if (command.equals("unsubscribe")) {

            System.out.println("wypisz się z" + subject);

            topicsToClientsHashMap.get(subject).remove(socketChannel);

        } else if (command.equals("size")) {


            int sizeOfClientList = Integer.parseInt(subject);

            if (sizeOfClientList < topic.size()) {


                for (int i = sizeOfClientList; i < topic.size(); i++) {


                    System.out.print("WYSYŁAM: = " + topic.get(i) + " ");


                    sendToSelectedClient(socketChannel, "addTopicToComboBox " + topic.get(i));


                    try{
                        Thread.sleep(100);
                    }
                    catch (Exception er){}



                }
                System.out.println();
            }


        } else if (command.equals("close")) {

            System.out.println("Jestem w close");

            for (String x : topic) {

                if (topicsToClientsHashMap.get(x).contains(socketChannel)) {

                    topicsToClientsHashMap.get(x).remove(socketChannel);
                    System.out.println("kasuje");

                }

            }


            allClientsList.remove(socketChannel);



        } else if (command.equals("addTopic")) {


            if (!topic.contains(subject)) {

                topic.add(subject);
                topicsToClientsHashMap.put(subject, new ArrayList<SocketChannel>());

            }


            for (SocketChannel x : allClientsList) {

                    sendToSelectedClient(x, "addTopicToComboBox " + subject);

            }


        } else if (command.equals("news")) {


            int amountOfTokens = stringTokenizer.countTokens();

            String newsContent = "Kategoria: " + subject + ", value= ";

            for (int i = 0; i < amountOfTokens; i++) {

                newsContent += stringTokenizer.nextToken() + " ";

            }

            //  System.out.println("treść newsa to " + newsContent);


            int size = topicsToClientsHashMap.get(subject).size();


            System.out.println("jestem w wysyłaniu size to " + size);


            if (size == 0) {

            } else if (size > 0) {

                for (int j = 0; j < size; j++) {
                    SocketChannel x = topicsToClientsHashMap.get(subject).get(j);
                    System.out.println("j = " + j);
                    sendToSelectedClient(x, newsContent);
                }

            }

            command = "";
            subject = "";
        }

    }


    private void sendToSelectedClient(SocketChannel socketChannel, String subject) throws IOException {

        if (!socketChannel.isOpen()) return;


        System.out.println("send " + subject + "||| to client");


        StringBuffer outStringBuffer = new StringBuffer();

        outStringBuffer.setLength(0);

        outStringBuffer.append(subject);
        outStringBuffer.append('\n');

        ByteBuffer buf = charset.encode(CharBuffer.wrap(outStringBuffer));
        socketChannel.write(buf);


    }


    private void newsMapInit() {

        topicsToClientsHashMap = new HashMap<String, ArrayList<SocketChannel>>();


        topicsToClientsHashMap.put("polityka", new ArrayList<SocketChannel>());
        topicsToClientsHashMap.put("sport", new ArrayList<SocketChannel>());
        topicsToClientsHashMap.put("celebryci", new ArrayList<SocketChannel>());
        topicsToClientsHashMap.put("gotowanie", new ArrayList<SocketChannel>());
        topicsToClientsHashMap.put("randki", new ArrayList<SocketChannel>());


        topic.add("polityka");
        topic.add("sport");
        topic.add("celebryci");
        topic.add("gotowanie");
        topic.add("randki");


    }


    public static void main(String args[]) throws IOException {
        new ServiceServer();

    }


}
