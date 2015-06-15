package io.agar.net;

import io.agar.Agar;
import io.agar.Blob;
import io.agar.IdName;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ReadThread extends Thread {
    public final Agar agar;
    public final Connection connection;
    public final DataInputStream input;

    public ReadThread(Agar agar, Connection connection, DataInputStream input) throws IOException {
        this.agar = agar;
        this.connection = connection;
        this.input = input;
    }

    public static String readString(ByteBuffer b) {
        String s = "";
        for (char c = 0; (c = (char) b.getShort()) != 0; ) {
            s += c;
        }
        return s;
    }

    public static String bs2str(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            String s = Integer.toHexString(b[i] & 0xFF).toUpperCase();
            if (s.length() == 1) {
                sb.append('0');
            }
            sb.append(s);
            sb.append(' ');
        }
        return sb.toString();
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] b = readBinary(input);
//                System.out.println(bs2str(b));
                ByteBuffer buffer = ByteBuffer.allocate(b.length);
                buffer.put(b);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.flip();
                if (buffer.get() == (byte) 240) {
                    System.out.println(buffer);
                }
                synchronized (agar.blobs) {
                    switch (b[0]) {
                        case 16:
                            updateNodes(buffer);
                            break;
                        case 17:
                            updatePositionAndSize(buffer);
                            break;
                        case 20:
                            clearAllNodes();
                            break;
                        case 21: // UNKNOWN!
                            short qa = buffer.getShort();
                            short ra = buffer.getShort();
                            break;
                        case 32:
                            addNode(buffer);
                            break;
                        case 49:
                            updateLeaderboard(buffer);
                            break;
                        case 50: // Teams Leaderboard, not implemented
//                            int count = buffer.getInt();
//                            float[] floats = new float[count];
//                            for (int i = 0; i < count; i++) {
//                                floats[i] = buffer.getFloat();
//                            }
                            break;
                        case 64:
                            setBorder(buffer);
                            break;
                    }
                    if (agar.follow.size() == 0 && !agar.restart && !agar.starting) {
                        agar.restart = true;
                    }
                    if (agar.controller != null) {
                        agar.controller.tick();
                    }
                    updateOffset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOffset() {
        if (agar.follow.size() > 0) {
            int avergX = 0;
            int avergY = 0;
            int totalMass = 0;
            boolean set = false;
            for (int i = 0; i < agar.follow.size(); ++i) {
                Blob blob = agar.blobs.get(agar.follow.get(i));
                if (blob != null) {
                    avergX += blob.x;
                    avergY += blob.y;
                    totalMass += blob.mass;
                    set = true;
                }
            }
//            System.out.println(totalMass);
            if (set) {
                agar.xoffset = avergX / agar.follow.size();
                agar.yoffset = avergY / agar.follow.size();
            }
        }
    }

    private void updateLeaderboard(ByteBuffer buffer) {
        synchronized (agar.leaderboard) {
            agar.leaderboard.clear();
            int cont = buffer.getInt();
            for (int i = 0; i < cont; ++i) {
                int id = buffer.getInt();
                agar.leaderboard.add(new IdName(readString(buffer), id));
            }
        }
    }

    private void addNode(ByteBuffer buffer) {
        int a = buffer.getInt();
        agar.follow.add(a);
        agar.starting = false;
    }

    private void clearAllNodes() {

    }

    // NOT CALLED
    private void updatePositionAndSize(ByteBuffer buffer) {
        float P = buffer.getFloat();
        float Q = buffer.getFloat();
        float R = buffer.getFloat();
        System.out.println(P + ", " + Q + ", " + R);
    }

    private void updateNodes(ByteBuffer buffer) {
        short d = buffer.getShort();
        for (int i = 0; i < d; ++i) {
            int na = buffer.getInt();
            int nb = buffer.getInt();
        }
        for (int dd = 0; (dd = buffer.getInt()) != 0; ) {
            Blob blob = agar.blobs.get(dd);
            if (blob == null) {
                blob = new Blob();
                agar.blobs.put(dd, blob);
            }
            blob.id = dd;
            blob.x = buffer.getShort();
            blob.y = buffer.getShort();
            blob.mass = buffer.getShort();
            int red = buffer.get();
            int green = buffer.get();
            int blue = buffer.get();
            int clr = (red & 0xFF) << 16 | ((green & 0xFF) << 8) | (blue & 0xFF);
            blob.color = clr;
            byte f = buffer.get();
            String newName = readString(buffer);
            if (!newName.equals("")) {
                blob.name = newName;
            }
            if (f == 1) {
                blob.virus = true;
            }
        }
        int count = buffer.getInt();
        for (int i = 0; i < count; ++i) {
            int id = buffer.getInt();
            agar.follow.remove((Integer) id);
            agar.blobs.remove(id);
        }
    }

    public void setBorder(ByteBuffer buffer) {
        agar.xmin = buffer.getDouble();
        agar.ymin = buffer.getDouble();
        agar.xmax = buffer.getDouble();
        agar.ymax = buffer.getDouble();
        agar.xoffset = (agar.xmin + agar.xmax) / 2;
        agar.yoffset = (agar.ymin + agar.ymax) / 2;
    }

    public byte[] readBinary(DataInputStream input) throws Exception {
        input.read();
        int len = input.read();
        int actualLen = len;
        if (len == 126) {
            actualLen = input.readUnsignedShort();
        }
        byte[] data = new byte[actualLen];
        input.readFully(data);
        return data;
    }
}
