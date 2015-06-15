package io.agar.controllers;

import io.agar.Agar;
import io.agar.AgarCanvas;
import io.agar.Blob;

import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Map;

public class BenAIController extends Controller implements MouseMotionListener, KeyListener, MouseListener {
    private final AgarCanvas canvas;
    private boolean predators = true;
    private boolean prey = true;
    private boolean virus = true;
    private boolean ai = true;
    private float movementX;
    private float movementY;
    private ArrayList<Agar> bots;
    private Proxy[] proxies;

    public BenAIController(Agar agar, AgarCanvas canvas) {
        super(agar);
        this.canvas = canvas;
        this.bots = new ArrayList<Agar>();
        this.proxies = new Proxy[3];
        for (int i = 0; i < 3; i++) {
            proxies[i] = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 8080 + i));
        }

        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    @Override
    public void tick() {
        for (Agar agar : bots) {
            if (agar == null) {
                continue;
            }
            MoveToTargetController bot = (MoveToTargetController) agar.controller;
            bot.targetX = this.agar.xoffset;
            bot.targetY = this.agar.yoffset;
            this.agar.vect.add(new double[]{agar.xoffset, agar.yoffset});
        }


        if (ai) {
            ai();
        } else {
            agar.vect.clear();
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

            if (agar.follow.contains(bl.id)) {
                continue;
            }

            double ddx = (agar.xoffset - bl.x);
            double ddy = (agar.yoffset - bl.y);
            double ddd = Math.sqrt(ddx * ddx + ddy * ddy);
            double ndx = ddx / ddd;
            double ndy = ddy / ddd;
            if (!bl.virus) {
                if (bl.mass >= avgMass * 1.1) {
                    double mx = (ddx - ndx * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 10000;
                    dx += mx;
                    double my = (ddy - ndy * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 10000;
                    dy += my;
                    if (predators) {
                        agar.vect.add(new double[]{mx, my});
                    }
                } else if (avgMass >= bl.mass * 1.15) {
                    double mx = (ddx) / ddd / ddd * avgMass * 50;
                    dx -= mx;
                    double my = (ddy) / ddd / ddd * avgMass * 50;
                    dy -= my;
                    if (prey) {
                        agar.vect.add(new double[]{-mx, -my});
                    }
                }
            } else {
                if (bl.mass <= avgMass) {
                    double mx = (ddx - ndx * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 5000;
                    double my = (ddy - ndy * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 5000;
                    dx += mx;
                    dy += my;

                    if (virus) {
                        agar.vect.add(new double[]{mx, my});
                    }
                }
            }
        }
        double ddist = Math.sqrt(dx * dx + dy * dy);
        agar.movementX = dx / ddist * 1000;
        agar.movementY = dy / ddist * 1000;
//        System.out.println(dx + ", " + dy);
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
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            predators = !predators;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            prey = !prey;
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            virus = !virus;
        } else if (e.getKeyCode() == KeyEvent.VK_B) {
            try {
                int count = bots.size();
                int amount = count / 5;

                Agar agar;

                if (amount >= proxies.length) {
                    System.out.println("No more proxies, starting on localhost");
                    agar = new Agar("167.114.209.35", 443);
                } else {
                    System.out.println("Starting on socks proxy: " + amount);
                    agar = new Agar("167.114.209.35", 443, proxies[amount]);
                }
                MoveToTargetController bot = new MoveToTargetController(agar, this.agar.follow, this.agar.xoffset, this.agar.yoffset);
                agar.setController(bot);
                bots.add(agar);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_Z) {
            ai = !ai;
            agar.vect.clear();
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
