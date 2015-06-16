package io.agar.net;

import io.agar.Agar;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Random;

public class Connection {
    public final Agar agar;
    public final String ip;
    public final int port;
    public final Random random;

    public final ReadThread readThread;
    public final WriteThread writeThread;

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    public Connection(Agar agar, String ip, int port) throws IOException {
        this.agar = agar;
        this.ip = ip;
        this.port = port;

        this.random = new Random();
        this.socket = new Socket(ip, port);
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.readThread = new ReadThread(agar, this, input);
        this.writeThread = new WriteThread(agar, this, output);

        output.write(("GET ws://" + ip + ":" + port + "/ HTTP/1.1\r\n" +
                "Host: " + ip + ":" + port + "\r\n" +
                "Connection: Upgrade\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Upgrade: websocket\r\n" +
                "Origin: http://agar.io\r\n" +
                "Sec-WebSocket-Version: 13\r\n" +
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/40.0.2214.111 Chrome/40.0.2214.111 Safari/537.36\r\n" +
                "Accept-Encoding: gzip, deflate, sdch\r\n" +
                "Accept-Language: en-GB,en-US;q=0.8,en;q=0.6\r\n" +
                "Sec-WebSocket-Key: 7rSl9kibWk3oIQg2GZglVA==\r\n" +
                "Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits\r\n\r\n"
        ).getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String str;
        while ((str = reader.readLine()).length() != 0) {
            System.out.println(str);
        }

        writeThread.sendBinary(new byte[]{-2, 4, 0, 0, 0});
        writeThread.sendBinary(new byte[]{-1, 0x29, 0x28, 0x28, 0x28});
        readThread.start();
        writeThread.start();
    }

    public Connection(Agar agar, String ip, int port, Proxy proxy) throws IOException {
        this.agar = agar;
        this.ip = ip;
        this.port = port;

        this.random = new Random();
        this.socket = new Socket(proxy);
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.readThread = new ReadThread(agar, this, input);
        this.writeThread = new WriteThread(agar, this, output);

        socket.connect(new InetSocketAddress(ip, port));

        output.write(("GET ws://" + ip + ":" + port + "/ HTTP/1.1\r\n" +
                "Host: " + ip + ":" + port + "\r\n" +
                "Connection: Upgrade\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Upgrade: websocket\r\n" +
                "Origin: http://agar.io\r\n" +
                "Sec-WebSocket-Version: 13\r\n" +
                "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/40.0.2214.111 Chrome/40.0.2214.111 Safari/537.36\r\n" +
                "Accept-Encoding: gzip, deflate, sdch\r\n" +
                "Accept-Language: en-GB,en-US;q=0.8,en;q=0.6\r\n" +
                "Sec-WebSocket-Key: 7rSl9kibWk3oIQg2GZglVA==\r\n" +
                "Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits\r\n\r\n"
        ).getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String str;
        while ((str = reader.readLine()).length() != 0) {
            System.out.println(str);
        }

        writeThread.sendBinary(new byte[]{-2, 4, 0, 0, 0});
        writeThread.sendBinary(new byte[]{-1, 0x29, 0x28, 0x28, 0x28});
        readThread.start();
        writeThread.start();
    }
}