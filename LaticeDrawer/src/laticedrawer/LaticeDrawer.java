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
import javax.swing.JFrame;

/**
 *
 * @author nathan
 */
public class LaticeDrawer extends Canvas implements KeyListener, MouseListener, MouseWheelListener {
    public static int WIDTH = 800; //Width and height. Not final as resizing is supported
    public static int HEIGHT = 600;
    
    public static void main(String[] args) {
        JFrame frame = new JFrame(); //Create and setup JFrame
        frame.setTitle("Latice Drawer");
        frame.setSize(WIDTH, HEIGHT);
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
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        
    }
    
}
