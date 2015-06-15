package io.agar.controllers;

import io.agar.Agar;
import io.agar.AgarCanvas;
import io.agar.Blob;

import java.awt.event.*;
import java.util.Map;

public class lIAOAIController extends Controller implements MouseMotionListener, KeyListener, MouseListener {
    private final AgarCanvas canvas;
    private boolean ai = true;
    private float movementX;
    private float movementY;

    public lIAOAIController(Agar agar, AgarCanvas canvas) {
        super(agar);
        this.canvas = canvas;

        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    @Override
    public void tick() {
        if (ai) {
            ai();
        } else {
            agar.movementX = movementX;
            agar.movementY = movementY;
        }
    }

    private void ai() {
        double dstX = 5000;
        double dstY = 5000;
        double xd = dstX - agar.xoffset;
        double yd = dstY - agar.yoffset;
        double dist = Math.sqrt(xd * xd + yd * yd);
        double dx = xd / dist;
        double dy = yd / dist;
        double avgMass = 0;
        for (int i = 0; i < agar.follow.size(); ++i) {
            if (agar.blobs.get(agar.follow.get(i)) != null) {
                avgMass += agar.blobs.get(agar.follow.get(i)).mass;
            }
        }
        avgMass /= agar.follow.size();
        agar.vect.clear();
        for (Map.Entry<Integer, Blob> sets : agar.blobs.entrySet()) {
            Blob bl = sets.getValue();
            if(agar.follow.contains(bl.id)){
                continue;
            }
            double ddx = (agar.xoffset - bl.x);
            double ddy = (agar.yoffset - bl.y);
            double ddd = Math.sqrt(ddx * ddx + ddy * ddy);
            double ndx = ddx / ddd;
            double ndy = ddy / ddd;
            if (!bl.virus) {
                if (bl.mass >= avgMass * 1.15) {
                    dx += (ddx - ndx * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 1000;
                    dy += (ddy - ndy * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 1000;
                    agar.vect.add(new double[]{(ddx - ndx * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 1000, (ddy - ndy * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 1000});
                } else if (avgMass >= bl.mass * 1.15) {
                    dx -= (ddx) / (ddd) / (ddd) * 250;
                    dy -= (ddy) / (ddd) / (ddd) * 250;
                    agar.vect.add(new double[]{-ddx / ddd / ddd * 250, -ddy / ddd / ddd * 250});
                }
            } else {
                if (bl.mass >= avgMass * 1.15) {
                    dx += (ddx - ndx * avgMass) / (ddd - avgMass) / (ddd - avgMass) * 500;
                    dy += (ddy - ndy * avgMass) / (ddd - avgMass) / (ddd - avgMass) * 500;
                    agar.vect.add(new double[]{(ddx - ndx * avgMass) / (ddd - avgMass) / (ddd - avgMass) * 500, (ddy - ndy * avgMass) / (ddd - avgMass) / (ddd - avgMass) * 500});
                }
            }
        }
        double ddist = Math.sqrt(dx * dx + dy * dy);
        agar.movementX = dx / ddist * 1000;
        agar.movementY = dy / ddist * 1000;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!ai) {
            float xd = e.getX() - canvas.getWidth() / 2;
            float yd = e.getY() - canvas.getHeight() / 2;
            movementX = xd;
            movementY = yd;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            agar.connection.writeThread.eject();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            agar.connection.writeThread.split();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ai = false;
        agar.vect.clear();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ai = true;
    }
}
