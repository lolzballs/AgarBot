package io.agar;

public class Blob implements Comparable<Blob> {
    public int id;
    public String name = "";
    public int x = 0;
    public int y = 0;
    public int mass = 0;
    public int color = 0;
    public boolean virus = false;

    public String getColorString() {
        String s = Integer.toHexString(color).toUpperCase();
        while (s.length() < 6) {
            s = "0" + s;
        }
        return s;
    }

    public String toString() {
        return name + "(x=" + x + " y=" + y + " m=" + mass + " c=0x" + getColorString() + ")";
    }

    public int compareTo(Blob a) {
        return mass - a.mass;
    }
}