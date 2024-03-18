package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import diploma.AirportMap;
import diploma.EarthquakeCityMap;
import processing.core.PApplet;

public class MySwingApp extends JFrame{
    final static Dimension defaultDimension = new Dimension(200, 300);
    final static Dimension defaultMapsFrameDimension = new Dimension(900, 700);
    JButton btnMakeWindow;
    JButton btnMakeAirPorts;
    ControlFrame cf;

    public MySwingApp(){
        //configure screens
        SwingUtilities.invokeLater(() -> {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] screens = ge.getScreenDevices();
            GraphicsDevice defaultScreen;
            if (screens.length >= 2) defaultScreen = screens[1];
            else {
                defaultScreen = screens[screens.length - 1];
            }
            Dimension screenSize = defaultScreen.getDefaultConfiguration().getBounds().getSize();

            setPreferredSize(defaultDimension);

            this.setLayout(new BorderLayout());

            btnMakeWindow = new JButton("Start Earthquake app");
            btnMakeWindow.addActionListener(e -> cf = new ControlFrame("Earthquakes", defaultMapsFrameDimension , new EarthquakeCityMap(), defaultScreen));
            add(btnMakeWindow, BorderLayout.SOUTH);

            btnMakeAirPorts = new JButton("Start Airports app");
            btnMakeAirPorts.addActionListener(e -> cf = new ControlFrame("AirportMap", defaultMapsFrameDimension, new AirportMap(), defaultScreen));
            add(btnMakeAirPorts, BorderLayout.NORTH);
            btnMakeAirPorts = new JButton("Start LifeExpectancy app");
            btnMakeAirPorts.addActionListener(e -> cf = new ControlFrame("Life expectancy Map", defaultMapsFrameDimension, new AirportMap(), defaultScreen));
            add(btnMakeAirPorts, BorderLayout.NORTH);
            this.setLocation(defaultScreen.getDefaultConfiguration().getBounds().getLocation());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setVisible(true);

        });

    }

    public class ControlFrame extends JFrame {
        private final PApplet papplet;

        public ControlFrame(String title,Dimension dimension, PApplet localFrame, GraphicsDevice screen) {
            this.setLayout(new BorderLayout());
            papplet = localFrame;
            // So we can resize the frame to get the sketch canvas size reqd.
            papplet.frame = this;
            setResizable(true);
            setTitle(title);
            setLocation(screen.getDefaultConfiguration().getBounds().getLocation());
            setPreferredSize(dimension);

            add(papplet, BorderLayout.CENTER);
            papplet.init();

            JButton btnEarthquakes = new JButton("Refresh data");
            btnEarthquakes.addActionListener(e -> papplet.redraw());
            add(btnEarthquakes, BorderLayout.SOUTH);
            pack();
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        new MySwingApp();
    }
}