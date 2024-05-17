package com.dashkevich.javalabs.lab_8_1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;

class MessageReceiver implements Runnable {
    private final Logger logger = Logger.getLogger(MessageReceiver.class.getName());

    private final DatagramSocket sock;
    private final byte[] buf;

    MessageReceiver(DatagramSocket s) {
        sock = s;
        buf = new byte[1024];
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                sock.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                logger.info(received);
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}

public class ChatClient {
    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());
    public final static int PORT = 8091;

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(PORT);
        MessageReceiver r = new MessageReceiver(socket);
        Thread rt = new Thread(r);
        rt.start();
        logger.info("Клиент ожидает сообщения на " + socket.getLocalSocketAddress());
    }
}
