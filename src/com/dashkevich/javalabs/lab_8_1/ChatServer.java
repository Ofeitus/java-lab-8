package com.dashkevich.javalabs.lab_8_1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ChatServer extends Thread {
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());
    public final static int PORT = 8090;

    private final DatagramSocket socket;
    private final List<String> clients;
    private LocalDateTime nextRun;

    public ChatServer() throws IOException {
        socket = new DatagramSocket(PORT);
        clients = new ArrayList<>();
        clients.add("localhost:8091");

        StringBuilder sb = new StringBuilder();
        sb.append("Список клиентов:").append("\n");
        for (String client : clients) {
            sb.append(client).append("\n");
        }
        logger.info(sb.toString());

        nextRun = LocalDateTime.now().withHour(0).withMinute(39).withSecond(0).withNano(0);
        if (LocalDateTime.now().isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        logger.info("Сообщение будет отправлено " + nextRun);
    }

    @Override
    public void run() {
        Duration initialDelay = Duration.between(LocalDateTime.now(), nextRun);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                DatagramPacket packet;
                byte[] data = "Сообщение всем клиентам".getBytes();
                for (String client : clients) {
                    packet = new DatagramPacket(
                            data,
                            data.length,
                            InetAddress.getByName(client.split(":")[0]),
                            Integer.parseInt(client.split(":")[1])
                    );
                    socket.send(packet);

                    nextRun = nextRun.plusDays(1);
                    logger.info("Сообщение отправлено");
                    logger.info("Следующее сообщение " + nextRun);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }, initialDelay.toMillis(), Duration.ofDays(1).toMillis(), TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws Exception {
        ChatServer s = new ChatServer();
        s.start();
    }
}