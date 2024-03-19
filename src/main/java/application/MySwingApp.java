package application;

import diploma.AirportMap;
import diploma.EarthquakeCityMap;
import diploma.LifeExpectancy;
import processing.core.PApplet;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MySwingApp extends JFrame {
    final static Dimension defaultDimension = new Dimension(1200, 900);
    final static Dimension defaultMapsFrameDimension = new Dimension(900, 700);
    JButton btnMakeWindow;
    JButton btnMakeAirPorts;
    JTabbedPane tabbedPane;

    JButton btnAirports;
    JButton btnEarthquakes;

    JButton btnCountriesLiveExpectancies;
    Map<String, ControlFrame> cfList = new HashMap<>();

    public MySwingApp() {
        //configure screens
        SwingUtilities.invokeLater(() -> {
            try {
                // Set System L&F
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                     IllegalAccessException e) {
                e.printStackTrace();
            }
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] screens = ge.getScreenDevices();
            GraphicsDevice defaultScreen;
            if (screens.length >= 2) defaultScreen = screens[1];
            else {
                defaultScreen = screens[screens.length - 1];
            }
            Dimension screenSize = defaultScreen.getDefaultConfiguration().getBounds().getSize();
            // Create a menu bar
            JMenuBar menuBar = new JMenuBar();

            // Create a "File" menu
            JMenu fileMenuItem = new JMenu("File");

            // Create a "User Login" menu item
            JMenuItem userLoginMenuItem = new JMenuItem("User Login");
            userLoginMenuItem.addActionListener(e -> {
                //TODO: Handle user login action here (replace with your logic)

            });

            // Add the menu item to the "File" menu
            fileMenuItem.add(userLoginMenuItem);

            // Add the "File" menu to the menu bar
            menuBar.add(fileMenuItem);

            // Set the menu bar for the frame
            setJMenuBar(menuBar);

            setPreferredSize(defaultDimension);

            this.setLayout(new BorderLayout());
            // create a tab base
            cfList.put("Earthquakes",
                    ControlFrame.create(this, "Earthquakes", defaultMapsFrameDimension,
                            new EarthquakeCityMap(), defaultScreen));
            cfList.put("AirportMap",
                    ControlFrame.create(this, "AirportMap", defaultMapsFrameDimension,
                            new AirportMap(), defaultScreen));

            cfList.put("Live",
                    ControlFrame.create(this, "Live", defaultMapsFrameDimension,
                            new LifeExpectancy(), defaultScreen));
            // Create a buttons panel
            JPanel buttonsPanel = new JPanel();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            GridBagLayout gbl = new GridBagLayout();
            buttonsPanel.setLayout(gbl);
            gbc.gridx = 0;
            gbc.gridy = 0;
            btnEarthquakes = createButtons(buttonsPanel, gbc, "Switch Earthquake app", 0);
            gbc.gridx = 0;
            gbc.gridy = 1;
            btnAirports = createButtons(buttonsPanel, gbc, "Switch Airports app", 1);
            gbc.gridx = 0;
            gbc.gridy = 2;
            btnCountriesLiveExpectancies = createButtons(buttonsPanel, gbc,
                    "Switch live expectancy app", 2);




            // Create a dropdown field
            JComboBox<String> dropdown = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3"});

            // Add dropdown field in the middle
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2; // Span across two columns
            gbc.anchor = GridBagConstraints.CENTER; // Align component in the center
            buttonsPanel.add(dropdown, gbc);

            add(buttonsPanel, BorderLayout.WEST);

            tabbedPane = new JTabbedPane();
            JPanel earthquakePanel = createMapPanel(cfList.get("Earthquakes").getPapplet());
            tabbedPane.addTab("Earthquakes", earthquakePanel);
            // Add Airport Map
            JPanel airportPanel = createMapPanel(cfList.get("AirportMap").getPapplet());
            tabbedPane.addTab("Airports", airportPanel);

            // Live expectancy map
            JPanel liveExpectancy = createMapPanel(cfList.get("Live").getPapplet());
            tabbedPane.addTab("Live", liveExpectancy);

            add(tabbedPane, BorderLayout.CENTER);

            this.setLocation(defaultScreen.getDefaultConfiguration().getBounds().getLocation());
            setDefaultLookAndFeelDecorated(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setVisible(true);
            cfList.forEach((key, value) -> value.getPapplet().redraw());

        });

    }

    public static void main(String[] args) {
        new MySwingApp();
    }

    private JButton createButtons(JPanel buttonsPanel, GridBagConstraints gridBagConstraints,
                                  String tabLabel, int tabOrder) {
        JButton button = new JButton(tabLabel);
        button.addActionListener(e -> tabbedPane.setSelectedIndex(tabOrder));
        buttonsPanel.add(button, gridBagConstraints);
        return button;
    }

    private JPanel createMapPanel(PApplet map) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(map, BorderLayout.CENTER);
        return panel;
    }
}
