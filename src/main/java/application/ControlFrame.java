package application;

import processing.core.PApplet;

import javax.swing.*;
import java.awt.*;

public class ControlFrame{
    private final PApplet papplet;

    private ControlFrame(JFrame frame,
                         String title,
                         Dimension dimension,
                         PApplet localFrame,
                         GraphicsDevice screen) {
        papplet = localFrame;
        papplet.frame = frame;
        papplet.init();
    }

    public static ControlFrame create(JFrame frame,String title, Dimension dimension, PApplet localFrame, GraphicsDevice screen) {
        return new ControlFrame(frame, title, dimension, localFrame, screen);
    }

    public PApplet getPapplet() {
        return papplet;
    }
}
