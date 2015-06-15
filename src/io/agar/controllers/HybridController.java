package io.agar.controllers;

import io.agar.Agar;
import io.agar.AgarCanvas;

import java.awt.event.*;

public class HybridController extends AIController implements MouseMotionListener, KeyListener, MouseListener {
    private final AgarCanvas canvas;
    private boolean ai = true;
    private float movementX;
    private float movementY;

    public HybridController(Agar agar, AgarCanvas canvas) {
        super(agar);
        this.canvas = canvas;

        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    @Override
    public void tick() {
        if (ai) {
            super.tick();
        } else {
            agar.movementX = movementX;
            agar.movementY = movementY;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!ai) {
            agar.vect.clear();
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
