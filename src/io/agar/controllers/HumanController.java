package io.agar.controllers;

import io.agar.Agar;
import io.agar.AgarCanvas;

import java.awt.event.*;

public class HumanController extends Controller implements MouseMotionListener, KeyListener {
    private final AgarCanvas canvas;
    private float movementX;
    private float movementY;

    public HumanController(Agar agar, AgarCanvas canvas) {
        super(agar);
        this.canvas = canvas;

        canvas.addKeyListener(this);
        canvas.addMouseMotionListener(this);
    }

    public void tick() {
        agar.movementX = movementX;
        agar.movementY = movementY;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        float xd = e.getX() - canvas.getWidth() / 2;
        float yd = e.getY() - canvas.getHeight() / 2;
        movementX = xd;
        movementY = yd;
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
}
