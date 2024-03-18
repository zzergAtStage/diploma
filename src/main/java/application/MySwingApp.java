package application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import diploma.EarthquakeCityMap;
import processing.core.PApplet;

public class MySwingApp extends JFrame{

    JButton btnMakeWindow;
    JButton btnMakeAirPorts;
    ControlFrame cf;

    public MySwingApp(){
        this.setLayout(new BorderLayout());

        btnMakeWindow = new JButton("Start Earthquake app");
        btnMakeWindow.addActionListener(e -> cf = new ControlFrame("Earthquakes", 800,600));
        add(btnMakeWindow, BorderLayout.SOUTH);

        btnMakeAirPorts = new JButton("Start Airports app");
        btnMakeAirPorts.addActionListener(e -> cf = new ControlFrame("AirportMap", 800,600));
        add(btnMakeAirPorts, BorderLayout.SOUTH);


        pack();
        setVisible(true);
    }

    public class ControlFrame extends JFrame {
        private EarthquakeCityMap papplet;
        private JButton btnEarthquakes;

        public ControlFrame(String title, int w, int h) {
            this.setLayout(new BorderLayout());
            papplet = new EarthquakeCityMap();//TODO: create beans
            // So we can resize the frame to get the sketch canvas size reqd.
            papplet.frame = this;
            setResizable(true);
            setTitle(title);
            setLocation(100, 100);
            add(papplet, BorderLayout.CENTER);
            papplet.init();
            papplet.height = 600;
            papplet.width = 800;
            btnEarthquakes = new JButton("Refresh data");
            btnEarthquakes.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    papplet.redraw();
                };

            });
            add(btnEarthquakes, BorderLayout.SOUTH);
            pack();
            setVisible(true);
        }
    }

    public static void main(String[] args) {
        new MySwingApp();
    }
}