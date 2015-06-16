package io.agar;

import io.agar.controllers.lIAOAIController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class lIAOAgar {
    public static void main(String[] args) throws Exception {
        Agar agar = new Agar("localhost", 443);
        AgarCanvas canvas = new AgarCanvas(agar);

        agar.setController(new lIAOAIController(agar, canvas));

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
}
