package io.agar;

import io.agar.controllers.AIController;
import io.agar.controllers.BenAIController;
import io.agar.controllers.MoveToTargetController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BenAgar {
    public static void main(String[] args) throws Exception {
        Agar agar = new Agar("167.114.209.35", 443);

        AgarCanvas canvas = new AgarCanvas(agar);

        agar.setController(new BenAIController(agar, canvas));

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

//        for (int j = 0; j < 4; j++) {
//            Agar agar = new Agar("167.114.209.35", 443);
//            agar.setController(new AIController(agar));
//        }
    }
}
