package io.agar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class AgarCanvas extends JPanel implements MouseWheelListener {
    public final Agar agar;

    public AgarCanvas(Agar agar) {
        this.agar = agar;
        setFocusable(true);
        addMouseWheelListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        synchronized (agar.blobs) {
            ArrayList<Blob> rblobs = new ArrayList<Blob>();
            for (Map.Entry<Integer, Blob> blob : agar.blobs.entrySet()) {
                rblobs.add(blob.getValue());
            }
            Collections.sort(rblobs);
            for (int i = 0; i < rblobs.size(); ++i) {
                Blob b = rblobs.get(i);
                Color c = new Color(b.color);
                Color stroke = new Color((int) (c.getRed() / 1.1), (int) (c.getGreen() / 1.1), (int) (c.getBlue() / 1.1));

                if (b.virus) {
                    Path2D.Double path = new Path2D.Double();
                    double xc = agar.xoffset * agar.scale - getWidth() / 2;
                    double yc = agar.yoffset * agar.scale - getHeight() / 2;
                    double amountToAdd = 1.025;
                    path.moveTo(b.x * agar.scale - xc, (b.y + b.mass * amountToAdd) * agar.scale - yc);
                    int slices = 72;
                    for (int j = 1; j <= slices; ++j) {
                        double add = j % 2 == 0 ? amountToAdd : 1 / amountToAdd;
                        double xx = Math.sin(Math.toRadians(360.0 / slices * j)) * (b.mass * add);
                        double yy = Math.cos(Math.toRadians(360.0 / slices * j)) * (b.mass * add);
                        path.lineTo((xx + b.x) * agar.scale - xc, (yy + b.y) * agar.scale - yc);
                    }
                    g2d.setColor(c);
                    g2d.fill(path);

                    g2d.setStroke(new BasicStroke(5));
                    g2d.setColor(stroke);
                    g2d.draw(path);
                } else {
                    g2d.setColor(c);
                    int x = (int) ((b.x - b.mass - agar.xoffset) * agar.scale + getWidth() / 2);
                    int y = (int) ((b.y - b.mass - agar.yoffset) * agar.scale + getHeight() / 2);
                    int diameter = (int) (b.mass * 2 * agar.scale);
                    g2d.fillOval(x, y, diameter, diameter);

                    g2d.setStroke(new BasicStroke(5));
                    g2d.setColor(stroke);
                    g2d.drawOval(x, y, diameter, diameter);
                }
                g2d.setColor(Color.black);
                g2d.setFont(new Font("Ubuntu", Font.BOLD, (int) ((b.mass / 4 + 15) * agar.scale)));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (int) ((b.x - agar.xoffset) * agar.scale + getWidth() / 2 - fm.stringWidth(b.name) / 2);
                int y = (int) ((b.y - agar.yoffset) * agar.scale + getHeight() / 2 + (fm.getAscent() + fm.getDescent()) / 3);

                g2d.drawString(b.name, x + 1, y + 1);
                g2d.drawString(b.name, x + 1, y - 1);
                g2d.drawString(b.name, x - 1, y + 1);
                g2d.drawString(b.name, x - 1, y - 1);

                g2d.setColor(Color.white);
                g2d.drawString(b.name, x, y);
            }
            int mw = getWidth() / 2;
            int mh = getHeight() / 2;
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(Color.black);
            for (int i = 0; i < agar.vect.size(); ++i) {
                double[] vec = agar.vect.get(i);
                g2d.drawLine(mw, mh, (int) (vec[0] * agar.scale * 50 + mw), (int) (vec[1] * agar.scale * 50 + mh));
            }
        }
        synchronized (agar.leaderboard) {
            g2d.setColor(new Color(0, 0, 0, 102));
            g2d.fillRect(getWidth() - 205, 10, 195, agar.leaderboard.size() * 24 + 60);
            g2d.setFont(new Font("Ubuntu", Font.BOLD, 30));
            g2d.setColor(Color.WHITE);
            g2d.drawString("Leaderboard", getWidth() - 200, 50);
            g2d.setFont(new Font("Ubuntu", Font.BOLD, 20));
            for (int i = 0; i < agar.leaderboard.size(); ++i) {
                String name = agar.leaderboard.get(i).name;
                int id = agar.leaderboard.get(i).id;
                if(agar.follow.contains(id)){
                    g2d.setColor(new Color(0xFFAAAA));
                }
                else{
                    g2d.setColor(new Color(0xFFFFFF));
                }
                if (name.equals("")) {
                    name = "An unnamed cell";
                }
                String s = (i + 1) + ". " + name;
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(s, getWidth() - 200 + (185 - fm.stringWidth(s)) / 2, i * 24 + 80);
            }
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        agar.scale /= 1 + e.getPreciseWheelRotation() / 10;
    }
}