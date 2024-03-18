package application;

import processing.core.PApplet;

import javax.swing.*;

public class EarthquakeCityMapWithMenuSwing extends PApplet {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Earthquake City Map with Menu (Swing)");

            // Create a menu bar
            JMenuBar menuBar = new JMenuBar();

            // Create a "File" menu
            JMenu fileMenu = new JMenu("File");

            // Create a "User Login" menu item
            JMenuItem userLoginMenuItem = new JMenuItem("User Login");
            userLoginMenuItem.addActionListener(e -> {
                // Handle user login action here (replace with your logic)
                System.out.println("User clicked on User Login!");
            });

            // Add the menu item to the "File" menu
            fileMenu.add(userLoginMenuItem);

            // Add the "File" menu to the menu bar
            menuBar.add(fileMenu);

            // Set the menu bar for the frame
            frame.setJMenuBar(menuBar);

            // Set other frame properties (size, close operation, etc.)
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // Initialize your EarthquakeCityMap sketch
            PApplet.runSketch(new String[]{"EarthquakeCityMapWithMenuSwing"}, new EarthquakeCityMapWithMenuSwing());
        });
    }

    public void settings() {
        size(800, 600);
        // Additional setup for your EarthquakeCityMap
    }

    public void setup() {
        // Your setup code here
    }

    public void draw() {
        // Your drawing code here
    }
}
