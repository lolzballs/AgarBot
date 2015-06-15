package io.agar.controllers;

import io.agar.Agar;
import io.agar.Blob;

import java.util.ArrayList;
import java.util.Map;

public class MoveToTargetController extends Controller {
    public double targetX;
    public double targetY;
    private final ArrayList<Integer> follow;

    public MoveToTargetController(Agar agar, ArrayList<Integer> follow, double targetX, double targetY) {
        super(agar);
        this.targetX = targetX;
        this.targetY = targetY;
        this.follow = follow;
    }

    @Override
    public void tick() {
        double dstX = targetX;
        double dstY = targetY;
        double xd = dstX - agar.xoffset;
        double yd = dstY - agar.yoffset;
        double dist = Math.sqrt(xd * xd + yd * yd);
        double dx = xd / dist * 100;
        double dy = yd / dist * 100;
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

            if (follow.contains(bl.id)) {
                continue;
            }

            double ddx = (agar.xoffset - bl.x);
            double ddy = (agar.yoffset - bl.y);
            double ddd = Math.sqrt(ddx * ddx + ddy * ddy);
            double ndx = ddx / ddd;
            double ndy = ddy / ddd;
            if (!bl.virus) {
                if (bl.mass >= avgMass * 1.25) {
                    double mx = (ddx - ndx * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 10000;
                    dx += mx;
                    double my = (ddy - ndy * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 10000;
                    dy += my;
                } else if (avgMass >= bl.mass * 1.25) {
                    double mx = (ddx) / ddd / ddd * 10;
                    dx -= mx;
                    double my = (ddy) / ddd / ddd * 10;
                    dy -= my;
                }
            } else {
                if (bl.mass <= avgMass) {
                    double mx = (ddx - ndx * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 5000;
                    double my = (ddy - ndy * bl.mass) / (ddd - bl.mass) / (ddd - bl.mass) * 5000;
                    dx += mx;
                    dy += my;
                }
            }
        }
        double ddist = Math.sqrt(dx * dx + dy * dy);
        agar.movementX = dx / ddist * 1000;
        agar.movementY = dy / ddist * 1000;
    }
}
