package io.agar.controllers;

import io.agar.Agar;
import io.agar.AgarCanvas;

import java.awt.event.KeyEvent;
import java.net.Proxy;
import java.util.ArrayList;

public class HackController extends HybridController {
    private ArrayList<Agar> bots;
    private Proxy[] proxies;

    public HackController(Agar agar, AgarCanvas canvas, Proxy[] proxies) {
        super(agar, canvas);
        this.bots = new ArrayList<Agar>();
        this.proxies = proxies;
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

        super.tick();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if (e.getKeyCode() == KeyEvent.VK_B) {
            try {
                int count = bots.size();
                int amount = count / 5;

                Agar agar;

                if (amount >= proxies.length) {
                    System.out.println("No more proxies, starting on localhost");
                    agar = new Agar(this.agar.connection.ip, this.agar.connection.port);
                } else {
                    System.out.println("Starting on socks proxy: " + amount);
                    agar = new Agar(this.agar.connection.ip, this.agar.connection.port, proxies[amount]);
                }
                MoveToTargetController bot = new MoveToTargetController(agar, this.agar.follow, this.agar.xoffset, this.agar.yoffset);
                agar.setController(bot);
                bots.add(agar);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
