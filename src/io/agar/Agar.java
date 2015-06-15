package io.agar;

import io.agar.controllers.Controller;
import io.agar.controllers.HybridController;
import io.agar.net.Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;

public class Agar {
    public final HashMap<Integer, Blob> blobs = new HashMap<Integer, Blob>();
    public final ArrayList<Integer> follow = new ArrayList<Integer>();
    public final ArrayList<IdName> leaderboard = new ArrayList<IdName>();
    public final ArrayList<double[]> vect = new ArrayList<double[]>();
    public final Connection connection;

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

    public Agar(String ip, int port) throws IOException {
        this.connection = new Connection(this, ip, port);
    }

    public Agar(String ip, int port, Proxy proxy) throws IOException {
        this.connection = new Connection(this, ip, port, proxy);
    }

    public static void main(String[] args) throws Exception {
        Agar agar = new Agar("167.114.209.36", 443);
        AgarCanvas canvas = new AgarCanvas(agar);

        agar.setController(new HybridController(agar, canvas));

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
