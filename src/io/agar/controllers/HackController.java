package io.agar.controllers;

import io.agar.Agar;
import io.agar.AgarCanvas;

import java.awt.event.KeyEvent;
import java.net.Proxy;
import java.util.ArrayList;

public class HackController extends HybridController {
    private final ArrayList<Agar> bots;
    private final Proxy[] proxies;

    public HackController(Agar agar, AgarCanvas canvas, Proxy[] proxies) {
        super(agar, canvas);
        this.bots = new ArrayList<Agar>();
        this.proxies = proxies;
    }

    @Override
    public void tick() {
        synchronized (bots) {
            for (Agar agar : bots) {
                if (agar == null) {
                    continue;
                }
                MoveToTargetController bot = (MoveToTargetController) agar.controller;
                bot.targetX = this.agar.xoffset;
                bot.targetY = this.agar.yoffset;
                this.agar.vect.add(new double[]{agar.xoffset, agar.yoffset});
            }
        }

        super.tick();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if (e.getKeyCode() == KeyEvent.VK_B) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        int count = bots.size();
                        int amount = count / 5; // Five per server

                        Agar botAgar;

                        if (amount >= proxies.length) {
                            System.out.println("No more proxies, starting on localhost");
                            botAgar = new Agar(agar.connection.ip, agar.connection.port, "TheBestHelper");
                        } else {
                            System.out.println("Starting on socks proxy: " + amount);
                            botAgar = new Agar(agar.connection.ip, agar.connection.port, proxies[amount], "TheBestHelper");
                        }
                        MoveToTargetController bot = new MoveToTargetController(botAgar, agar.follow, agar.xoffset, agar.yoffset);
                        botAgar.setController(bot);
                        synchronized (bots) {
                            bots.add(botAgar);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
