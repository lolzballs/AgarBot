package io.agar.controllers;

import io.agar.Agar;
import io.agar.Blob;

import java.util.Map;

public class AIController extends Controller {
    protected boolean predators;
    protected boolean prey;
    protected boolean virus;

    public AIController(Agar agar) {
        super(agar);
        this.predators = true;
        this.prey = true;
        this.virus = true;
    }

    @Override
    public void tick() {
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
    }
}
