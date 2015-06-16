package io.agar.net;

import io.agar.Agar;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WriteThread extends Thread {
    public final Agar agar;
    public final Connection connection;
    public final DataOutputStream output;

    private int split = 0;
    private int eject = 0;

    public WriteThread(Agar agar, Connection connection, DataOutputStream output) {
        this.agar = agar;
        this.connection = connection;
        this.output = output;
    }

    public void split() {
        ++split;
    }

    public void eject() {
        ++eject;
    }

    public void run() {
        try {
            while (true) {
                for (; eject > 0; --eject) {
                    sendBinary(new byte[]{21});
                }
                for (; split > 0; --split) {
                    sendBinary(new byte[]{17});
                }
                for (; agar.restart; agar.restart = false) {
//                    String s = JOptionPane.showInputDialog("Nickname: ");
                    System.out.println("dead!");
                    String s = agar.username;
                    if (s == null) {
                        System.exit(0);
                    }
                    agar.starting = true;
                    sendInit(s);
                }
                ByteBuffer b = ByteBuffer.allocate(21);
                b.order(ByteOrder.LITTLE_ENDIAN);
                b.put((byte) 16);
                b.putDouble(agar.xoffset + agar.movementX);
                b.putDouble(agar.yoffset + agar.movementY);
                b.putInt(0);
                sendBinary(b.array());
                Thread.sleep(50);
            }
        } catch (Exception e) {
        }
    }
    public void sendBinary(byte[] b) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(6 + b.length);
        buf.put((byte) 0x82);
        buf.put((byte) (0x80 + b.length));
        byte[] mask = new byte[4];
        connection.random.nextBytes(mask);
        buf.put(mask);
        for (int i = 0; i < b.length; ++i) {
            b[i] ^= mask[i % 4];
        }
        buf.put(b);
        output.write(buf.array());
    }

    public void sendInit(String name) throws Exception {
        byte[] bt = new byte[name.length() * 2 + 1];
        for (int i = 0; i < name.length(); ++i) {
            setUint16(bt, i * 2 + 1, name.charAt(i));
        }
        sendBinary(bt);
    }

    public static void setUint32(byte[] bytes, int i, int n) {
        bytes[i] = (byte) (n & 0xFF);
        bytes[i + 1] = (byte) ((n >> 8) & 0xFF);
        bytes[i + 2] = (byte) ((n >> 16) & 0xFF);
        bytes[i + 3] = (byte) ((n >> 24) & 0xFF);
    }

    public static void setUint16(byte[] bytes, int i, int n) {
        bytes[i] = (byte) (n & 0xFF);
        bytes[i + 1] = (byte) ((n >> 8) & 0xFF);
    }
}
