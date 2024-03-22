package diploma;

import application.UserRepository;
import application.models.NoUsersRegistered;
import application.services.AuthenticationService;
import application.services.AuthenticationServiceImpl;
import application.services.Publisher;
import application.services.UserEventListener;
import processing.core.PApplet;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DesktopMonitoringApp extends JFrame implements UserEventListener {
    private final UserRepository userRepository =  UserRepository.getInstance();
    private final AuthenticationService authService = AuthenticationServiceImpl.getInstance();
    final static Dimension defaultDimension = new Dimension(1200, 900);
    final static Dimension defaultMapsFrameDimension = new Dimension(900, 700);
    JButton btnMakeWindow;
    JButton btnMakeAirPorts;
    JTabbedPane tabbedPane;

    JButton btnAirports;
    JButton btnEarthquakes;

    JButton btnCountriesLiveExpectancies;
    protected JLabel userStatusLabel;

    Map<String, ControlFrame> cfList = new HashMap<>();

    public DesktopMonitoringApp() {
        Publisher.getInstance().subscribe(Publisher.LOGON, this);
        Publisher.getInstance().subscribe(Publisher.LOGOUT, this);

        //configure screens
        SwingUtilities.invokeLater(() -> {
            try {
                // Set System L&F
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                     IllegalAccessException e) {
                Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
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
               new LoginWindow<>(this);
            });
            // Create a "User Login" menu item
            JMenuItem userLogoffMenuItem = new JMenuItem("User Logoff");
            userLogoffMenuItem.addActionListener(e -> {

                try {
                    authService.logOffUser(userRepository.getFirstUser());
                } catch (NoUsersRegistered ex) {
                    String dialogHeader = "Ошибка пользовательских данных";
                    String dialogMessage = "Пользователь не прошел авторизацию";
                    int jOptionPaneTypeInfo = JOptionPane.INFORMATION_MESSAGE;
                    JOptionPane.showMessageDialog(this,
                            dialogMessage, dialogHeader, jOptionPaneTypeInfo);
                }
            });

            // Add the menu item to the "File" menu
            fileMenuItem.add(userLoginMenuItem);
            fileMenuItem.add(userLogoffMenuItem);

            // Add the "File" menu to the menu bar
            menuBar.add(fileMenuItem);

            // Set the menu bar for the frame
            setJMenuBar(menuBar);
            //user state
            userStatusLabel = new JLabel("Пользователь не авторизован");
            userStatusLabel.setForeground(Color.RED);
            userStatusLabel.setVisible(true);
            JPanel notificationPane = new JPanel();
            notificationPane.add(userStatusLabel);
            getContentPane().add(notificationPane,BorderLayout.NORTH);


            //this.setLayout(new BorderLayout());
            setPreferredSize(defaultDimension);
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

            getContentPane().add(buttonsPanel, BorderLayout.WEST);

            tabbedPane = new JTabbedPane();
            JPanel earthquakePanel = createMapPanel(cfList.get("Earthquakes").getPapplet());
            tabbedPane.addTab("Earthquakes", earthquakePanel);
            // Add Airport Map
            JPanel airportPanel = createMapPanel(cfList.get("AirportMap").getPapplet());
            tabbedPane.addTab("Airports", airportPanel);

            // Live expectancy map
            JPanel liveExpectancy = createMapPanel(cfList.get("Live").getPapplet());
            tabbedPane.addTab("Live", liveExpectancy);

            getContentPane().add(tabbedPane, BorderLayout.CENTER);

            this.setLocation(defaultScreen.getDefaultConfiguration().getBounds().getLocation());
            setDefaultLookAndFeelDecorated(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setVisible(true);
            cfList.forEach((key, value) -> value.getPapplet().redraw());

        });

    }

    public static void main(String[] args) {
        new DesktopMonitoringApp();
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

    @Override
    public void update(String updateType, String message) {
        if (updateType.equals(Publisher.LOGON)){
            userStatusLabel.setText(message);
            userStatusLabel.setVisible(false);
        } else {
            userStatusLabel.setText(message);
            userStatusLabel.setVisible(true);
        }
    }
}
