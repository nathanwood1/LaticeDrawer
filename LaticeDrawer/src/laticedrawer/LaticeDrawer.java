/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laticedrawer;

import java.awt.Canvas;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import javax.swing.JFrame;

/**
 *
 * @author nathan
 */
public class LaticeDrawer extends Canvas implements Runnable, KeyListener, MouseListener, MouseWheelListener {
    public static int WIDTH = 800; //Width and height. Not final as resizing is supported
    public static int HEIGHT = 600;
    
    public static void main(String[] args) {
        JFrame frame = new JFrame(); //Create and setup JFrame
        frame.setTitle("Latice Drawer");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        
        LaticeDrawer l = new LaticeDrawer();
        l.addMouseListener(l);
        l.addKeyListener(l);
        l.addMouseWheelListener(l);
        frame.addMouseListener(l);
        frame.addKeyListener(l);
        frame.addMouseWheelListener(l);
        
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
    
    int moveAmount = 0;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getUnitsToScroll() >= 0)
            moveAmount++;
        else
            moveAmount--;
    }

    @Override
    public void run() {
        keys = new LinkedList<>();
        
    }
    
}
