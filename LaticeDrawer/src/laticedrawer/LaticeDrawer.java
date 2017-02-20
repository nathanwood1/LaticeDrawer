/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laticedrawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import sjcc.SJCC;

/**
 *
 * @author nathan
 */
public class LaticeDrawer extends SJCC implements Runnable, KeyListener, MouseListener, MouseWheelListener {

    public static Font font;

    public static void main(String[] args) {
        if (System.getProperty("os.name").equals("Linux")) {
            System.setProperty("sun.java2d.opengl", "true");
        }

        LaticeDrawer laticeDrawer = new LaticeDrawer();
        laticeDrawer.TITLE = "Latice Drawer";
        laticeDrawer.WIDTH = 800;
        laticeDrawer.HEIGHT = 600;
        laticeDrawer.RESIZEABLE = true;
        font = Font.getFont(Font.MONOSPACED);
        laticeDrawer.start();
    }

    boolean saveCombPressed = false;
    boolean colorKeyComb = false;

    public LaticeDrawer() {
        super();
        lines = new LinkedList[2];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new LinkedList<>();
        }
    }

    public void render(Graphics2D g_s, double delta) {
        Graphics2D[] g = new Graphics2D[]{g_s};

        int mA = getMouseWheel();
        moveAmount = mA == 0 ? 0 : (mA > 0 ? 1 : -1);

        if (getKey(KeyEvent.VK_ESCAPE) > 0) {
            System.exit(0);
        }

        if (getKey(KeyEvent.VK_F11) > 0) {
            this.toggleFullScreen();
        }

        BufferedImage img = null;
        if (getKey(KeyEvent.VK_CONTROL) >= 0 && getKey(KeyEvent.VK_S) >= 0) {
            if (!saveCombPressed) {
                saveCombPressed = true;
                img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                g[0] = img.createGraphics();
            }
        } else {
            saveCombPressed = false;
        }

        g[0].setColor(Color.BLACK);
        g[0].fillRect(0, 0, WIDTH, HEIGHT);

        mouse = currentMousePoint();

        if (getKey(KeyEvent.VK_SHIFT) >= 0) {
            mouse.x = (int) Math.round(mouse.x / 25d) * 25;
            mouse.y = (int) Math.round(mouse.y / 25d) * 25;
        }

        if (img == null) {
            g[0].setColor(Color.WHITE);
            g[0].setFont(font);
            String str = "Mode: [" + (type + 1) + "], Cursor:[" + mouse.x + "/" + WIDTH + ", " + mouse.y + "/" + HEIGHT + "], Snapping:[" + (getKey(KeyEvent.VK_SHIFT) >= 0 ? "ON" : "OFF") + "], Detail: [" + (lines[type].isEmpty() ? 10 : (int) Math.abs(lines[type].getLast().detail)) + "], Color: [[";
            g[0].drawString(str, 0, 12);
            g[0].setColor(selectedColour);
            int width = g[0].getFontMetrics().stringWidth(str);
            str = "■";
            g[0].drawString(str, width, 12);
            width += g[0].getFontMetrics().stringWidth(str);
            g[0].setColor(Color.WHITE);
            str = "], R:" + selectedColour.getRed() + ", G:" + selectedColour.getGreen() + ", B:" + selectedColour.getBlue() + ", A:" + selectedColour.getAlpha() + "]";
            g[0].drawString(str, width, 12);
            g[0].setColor(selectedColour);
            g[0].drawLine(mouse.x, mouse.y - 10, mouse.x, mouse.y + 10);
            g[0].drawLine(mouse.x - 10, mouse.y, mouse.x + 10, mouse.y);
        }

        if (getKey(KeyEvent.VK_CONTROL) > 0 && getKey(KeyEvent.VK_C) > 0) {
            if (!colorKeyComb) {
                colorKeyComb = true;
                Color c = JColorChooser.showDialog(this, "Choose Colour", selectedColour);
                if (c != null) {
                    selectedColour = c;
                }
            }
        } else {
            colorKeyComb = false;
        }

        if (mousePressed()) {
            switch (mouseButton()) {
                case MouseEvent.BUTTON1:
                    if (lines[type].isEmpty()) {
                        lines[type].add(new Line(type));
                    } else if (lines[type].getLast().completed) {
                        lines[type].add(new Line(type));
                    }
                    lines[type].getLast().addPoint(mouse);
                    break;
                case MouseEvent.BUTTON3:
                    type = (type + 1) % lines.length;
                case MouseEvent.BUTTON2:
                    if (!lines[type].isEmpty()) {
                        lines[type].getLast().completed = true;
                    }
                    break;
            }
        }

        for (LinkedList<Line> list : lines) {
            list.stream().forEach((l) -> {
                l.render(g[0]);
            });
        }

        g[0].dispose();
        if (img != null) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileFilter(new FileNameExtensionFilter("Image File (*.png)", "png"));
            if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String f_s = jfc.getSelectedFile().toString();
                if (f_s.split("\\.").length == 1) {
                    f_s += ".png";
                } else {
                    String[] strs = f_s.split("\\.");
                    strs[strs.length - 1] = "png";
                    f_s = String.join(".", strs);
                }
                File f = new File(f_s);
                int response = JOptionPane.YES_OPTION;
                if (f.exists()) {
                    response = JOptionPane.showConfirmDialog(this, "The file \'" + f + "\' already exsists.\nDo you want to overwrite?", "Overwrite?", JOptionPane.YES_NO_OPTION);
                }
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        ImageIO.write(img, "png", f);
                    } catch (IOException ex) {
                        Logger.getLogger(LaticeDrawer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    LinkedList<Line>[] lines;
    static Point mouse = null;
    static int type = 0;
    static Color selectedColour = Color.WHITE;
    static int moveAmount = 0;

    public static class Line {

        double detail = 10;
        boolean completed = false;
        int type;
        Color selectedColour;

        LinkedList<Point> points;

        public Line(int type) {
            this.type = type;
            points = new LinkedList<>();
            selectedColour = LaticeDrawer.selectedColour;
        }

        public void addPoint(Point p) {
            points.add(p);
        }

        public void render(Graphics g) {
            if (!completed) {
                selectedColour = LaticeDrawer.selectedColour;
            }
            if (type == 0) {
                if (!completed) {
                    points.add(mouse);
                    detail = Math.max(0, moveAmount + detail);
                    moveAmount = 0;
                }

                g.setColor(selectedColour);
                for (int i = 1; i < points.size(); i++) {
                    g.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y);
                }

                for (int i = 2; i < points.size(); i++) {
                    Point a = points.get(i - 2);
                    Point b = points.get(i - 1);
                    Point c = points.get(i);
                    double aXDiff = b.x - a.x;
                    double aYDiff = b.y - a.y;
                    double bXDiff = c.x - b.x;
                    double bYDiff = c.y - b.y;
                    aXDiff /= detail;
                    aYDiff /= detail;
                    bXDiff /= detail;
                    bYDiff /= detail;
                    for (int j = 0; j < detail; j++) {
                        g.drawLine(a.x + (int) (aXDiff * j), a.y + (int) (aYDiff * j), b.x + (int) (bXDiff * j), b.y + (int) (bYDiff * j));
                    }
                }

                if (!completed) {
                    points.removeLast();
                }
            } else if (type == 1) {
                if (!completed) {
                    points.add(mouse);
                    detail += moveAmount;
                    if (detail == 0 && moveAmount == -1) {
                        detail = -2;
                    } else if (detail == 0 && moveAmount == 1) {
                        detail = 2;
                    }
                    moveAmount = 0;
                }

                g.setColor(selectedColour);
                for (int i = 0; i < points.size(); i++) {
                    g.drawLine(points.get(realMod(i - 1, points.size())).x, points.get(realMod(i - 1, points.size())).y, points.get(i).x, points.get(i).y);
                }

                if (points.size() > 2) {
                    Point[] p_s = points.toArray(new Point[points.size()]);
                    Point2D.Double[] p = new Point2D.Double[p_s.length];
                    for (int i = 0; i < p_s.length; i++) {
                        p[i] = new Point2D.Double(p_s[i].x, p_s[i].y);
                    }
                    for (int j = 0; j < 100 * Math.abs(detail) * points.size(); j++) {
                        int i = realMod(j, (points.size())) + 1;
                        if (detail >= 0) {
                            g.drawLine(
                                    (int) p[realMod(i - 1, p.length)].x, (int) p[realMod(i - 1, p.length)].y,
                                    (int) p[realMod(i, p.length)].x, (int) p[realMod(i, p.length)].y);
                        }
                        int a = realMod(i - 1, p.length);
                        int b = realMod(i, p.length);
                        double xD = (p[b].x - p[a].x) / Math.abs(detail);
                        double yD = (p[b].y - p[a].y) / Math.abs(detail);
                        Point2D.Double p2 = p[a];
                        p[a] = new Point2D.Double(p[a].x + xD, p[a].y + yD);
                        if (detail < 0) {
                            g.drawLine((int) p[a].x, (int) p[a].y, (int) p2.x, (int) p2.y);
                        }
                    }
                }

                if (!completed) {
                    points.removeLast();
                }
            }
        }
    }

    public static int realMod(int val, int diviser) {
        int r = val;
        while (r >= diviser) {
            r -= diviser;
        }
        while (r < 0) {
            r += diviser;
        }
        return r;
    }

}
