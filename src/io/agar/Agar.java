package io.agar;

import io.agar.controllers.Controller;
import io.agar.controllers.HackController;
import io.agar.net.Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;

public class Agar {
    public final HashMap<Integer, Blob> blobs = new HashMap<Integer, Blob>();
    public final ArrayList<Integer> follow = new ArrayList<Integer>();
    public final ArrayList<IdName> leaderboard = new ArrayList<IdName>();
    public final ArrayList<double[]> vect = new ArrayList<double[]>();
    public final Connection connection;
    public final String username;

    public double xmin;
    public double ymin;
    public double xmax;
    public double ymax;
    public double xoffset;
    public double yoffset;
    public double scale = 1;
    public boolean restart = true;
    public boolean starting = false;
    public Controller controller;

    public double movementX = 0;
    public double movementY = 0;

    public Agar(String ip, int port, String username) throws IOException {
        this.connection = new Connection(this, ip, port);
        this.username = username;
    }

    public Agar(String ip, int port, Proxy proxy, String username) throws IOException {
        this.connection = new Connection(this, ip, port, proxy);
        this.username = username;
    }

    public static void main(String[] args) throws Exception {
        Proxy[] proxies = new Proxy[3];
        for (int i = 0; i < 3; i++) {
            proxies[i] = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 8080 + i));
        }

        Agar agar = new Agar("167.114.209.35", 1502, "lolzballs");
        AgarCanvas canvas = new AgarCanvas(agar);

        agar.setController(new HackController(agar, canvas, proxies));

        final JFrame frame = new JFrame("Agar.io");
        canvas.setPreferredSize(new Dimension(800, 600));
        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        new Timer(25, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.repaint();
            }
        }).start();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
