/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laticedrawer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import static javax.swing.SwingUtilities.convertPointFromScreen;

/**
 *
 * @author nathan
 */
public class LaticeDrawer extends Canvas implements Runnable, KeyListener, MouseListener, MouseWheelListener {
    public static int WIDTH = 800; //Width and height. Not final as resizing is supported
    public static int HEIGHT = 600;
    
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true"); //In case you are on Linux
        
        JFrame frame = new JFrame(); //Create and setup JFrame
        frame.setTitle("Latice Drawer");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor( new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor")); //Set a blank curor
        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                WIDTH = frame.getWidth(); //In case the user resizes the window
                HEIGHT = frame.getHeight();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                
            }

            @Override
            public void componentShown(ComponentEvent e) {
                
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                
            }
        });
        
        LaticeDrawer l = new LaticeDrawer(); //Setup listeners
        l.addMouseListener(l);
        l.addKeyListener(l);
        l.addMouseWheelListener(l);
        frame.addMouseListener(l);
        frame.addKeyListener(l);
        frame.addMouseWheelListener(l);
        frame.add(l);
        
        frame.setVisible(true);
        l.run();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    
    LinkedList<Integer> keys;

    @Override
    public void keyPressed(KeyEvent e) {
        if (!keys.contains(e.getKeyCode()))
            keys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (keys.contains(e.getKeyCode()))
            keys.remove((Integer) e.getKeyCode());
    }
    
    public boolean isKeyDown(int code) {
        return keys.contains(code);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    boolean mouseDownInit = false;
    boolean mouseDown = false;
    int mouseCode = -1;

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDownInit = true;
        mouseDown = true;
        mouseCode = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
        mouseCode = e.getButton();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    static int moveAmount = 0;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getUnitsToScroll() >= 0)
            moveAmount--;
        else
            moveAmount++;
    }

    @Override
    public void run() {
        keys = new LinkedList<>(); //Setup arrays
        lines = new LinkedList[2];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new LinkedList<>();
        }
        long prevNano = System.nanoTime(); //Timing
        while (true) {
            long now = System.nanoTime();
            double delta = (double) (now - prevNano) / 1000000000;
            prevNano = now;
            render(delta); //Render
        }
    }
    
    public void render(double delta) {
        BufferStrategy bs = getBufferStrategy(); //Get the BufferStrategy
        if (bs == null) {
            createBufferStrategy(3); //Create it if it doesn't exsist
            return;
        }
        
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        
        g.setColor(Color.BLACK); //Fill background with black
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        mouse = MouseInfo.getPointerInfo().getLocation();
        convertPointFromScreen(mouse, this);
        
        if (isKeyDown(KeyEvent.VK_SHIFT)) {
            mouse.x = (int) Math.round(mouse.x / 25d) * 25;
            mouse.y = (int) Math.round(mouse.y / 25d) * 25;
        }
        
        g.setColor(Color.WHITE);
        g.drawLine(mouse.x, mouse.y - 10, mouse.x, mouse.y + 10);
        g.drawLine(mouse.x - 10, mouse.y, mouse.x + 10, mouse.y);
        
        if (mouseDownInit) {
            switch (mouseCode) {
                case MouseEvent.BUTTON1:
                    if (lines[type].isEmpty())
                        lines[type].add(new Line(type));
                    else
                        if (lines[type].getLast().completed)
                            lines[type].add(new Line(type));
                    lines[type].getLast().addPoint(mouse);
                    break;
                case MouseEvent.BUTTON3:
                    type = (type + 1) % lines.length;
                case MouseEvent.BUTTON2: //No break because chaning state should also finish any curves
                    if (!lines[type].isEmpty())
                        lines[type].getLast().completed = true;
                    break;
            }
            mouseDownInit = false;
        }
        
        for (LinkedList<Line> list : lines)
            list.stream().forEach((l) -> {
                l.render(g);
            });
        
        g.dispose();
        bs.show();
    }
    
    LinkedList<Line>[] lines;
    static Point mouse = null;
    static int type = 0;
    
    public static class Line {
        double detail = 10;
        boolean completed = false;
        int type;
        
        LinkedList<Point> points;
        
        public Line(int type) {
            this.type = type;
            points = new LinkedList<>();
        }
        
        public void addPoint(Point p) {
            points.add(p);
        }
        
        public void render(Graphics g) {
            if (type == 0) {
                if(!completed) {
                    points.add(mouse);
                    detail += moveAmount;
                    moveAmount = 0;
                }
                
                g.setColor(Color.WHITE);
                for (int i = 1; i < points.size(); i++)
                    g.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y);
                
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
                    for (int j = 0; j < detail; j++)
                        g.drawLine(a.x + (int) (aXDiff * j), a.y + (int) (aYDiff * j), b.x + (int) (bXDiff * j), b.y + (int) (bYDiff * j));
                }
                
                if(!completed)
                    points.remove(mouse);
            } else if (type == 1) {
                if(!completed) {
                    points.add(mouse);
                    detail += moveAmount;
                    moveAmount = 0;
                }

                if (points.size() > 2) {
                    g.setColor(Color.WHITE);
                    Point[] p = points.toArray(new Point[points.size()]);
                    for (int j = 0; j < 1000; j++) {
                        int i = realMod(j, (points.size() + 1));
                        g.drawLine(
                                p[realMod(i - 1, p.length)].x, p[realMod(i - 1, p.length)].y,
                                p[realMod(i, p.length)].x, p[realMod(i, p.length)].y);
                        if (i != j) {
                            int a = realMod(i - 1, p.length);
                            int b = realMod(i, p.length);
                            double xD = (p[b].x - p[a].x) / detail;
                            double yD = (p[b].y - p[a].y) / detail;
                            p[a] = new Point(p[a].x + (int) xD, p[a].y + (int) yD);
                        }
                    }
                }
                
                if(!completed)
                    points.removeLast();
            }
        }
    }
    
    public static int realMod(int val, int diviser) {
//        System.out.print(val + " % " + diviser + " = ");
        int r = val;
        while (r >= diviser)
            r -= diviser;
        while (r < 0)
            r += diviser;
//        System.out.println(r);
        return r;
    }
    
}
