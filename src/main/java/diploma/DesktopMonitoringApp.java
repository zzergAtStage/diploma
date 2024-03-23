package diploma;

import application.UserRepository;
import application.models.NoUsersRegistered;
import application.models.User;
import application.services.AuthenticationService;
import application.services.AuthenticationServiceImpl;
import application.services.Publisher;
import application.services.UserEventListener;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import processing.core.PApplet;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Основной класс десктопного приложения.
 *  Позволяет управлять пользователем, авторизацией на WEB-сервисе и отображать
 *  карты разного типа.
 *
 */
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
        //subscribe app to publisher
        Publisher.getInstance().subscribe(Publisher.LOGON, this);
        Publisher.getInstance().subscribe(Publisher.LOGOUT, this);

        //configure screens
        SwingUtilities.invokeLater(() -> {
            try {
                // Set System L&F
                UIManager.setLookAndFeel(
                        new FlatIntelliJLaf());
            } catch (UnsupportedLookAndFeelException e) {
                Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
            }
            UIManager.put( "TabbedPane.showTabSeparators", true );
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
            JMenu userAuthority = new JMenu("Управление пользователем");

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
            JMenuItem closeAppMenuItem = new JMenuItem("Exit");
            closeAppMenuItem.addActionListener(e -> {
                try {
                    User user = userRepository.getFirstUser();
                    authService.logOffUser(user);
                } catch (NoUsersRegistered ex) {
                    ;
                }
                dispose();

            });
            JMenuItem userAuthorityItem = new JMenuItem("Управление пользователем");
            // Create an ActionListener for the menu item
            userAuthorityItem.addActionListener(e -> {
                // Create the confirmation dialog
                int dialogResult = JOptionPane.showConfirmDialog(this,
                        "Для управления ролью пользователя перейти на внешний сайт?",
                        "Управление правами пользователя",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == JOptionPane.YES_OPTION) {
                    // User wants to open external site
                    try {
                        Desktop.getDesktop().browse(new URI("https://localhost:8443/"));
                    } catch (IOException | URISyntaxException ex) {
                        // Handle exception if opening URL fails
                        JOptionPane.showMessageDialog(null, "Невозможно открыть внешний сайт.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            // Add the menu item to the "File" menu
            fileMenuItem.add(userLoginMenuItem);
            fileMenuItem.add(userLogoffMenuItem);
            fileMenuItem.add(closeAppMenuItem);
            userAuthority.add(userAuthorityItem);
            // Add the "File" menu to the menu bar
            menuBar.add(fileMenuItem);
            menuBar.add(userAuthority);
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
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(5, 5, 5, 5);

            GridBagLayout gbl = new GridBagLayout();
            buttonsPanel.setLayout(gbl);


            // Create a dropdown field
            JComboBox<String> dropdown = new JComboBox<>(new String[]{"Все", "Наземные землетрясения",
                    "Подводные землетрясения"});
            dropdown.addActionListener(e -> {
                int selectedItem = dropdown.getSelectedIndex();
                EarthquakeCityMap map = (EarthquakeCityMap) cfList.get("Earthquakes").getPapplet();
                map.setExternalFilter(selectedItem);
            });


            // Add dropdown field in the middle
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2; // Span across two columns
            gbc.anchor = GridBagConstraints.NORTH; // Align component in the center
            buttonsPanel.add(dropdown, gbc);

            getContentPane().add(buttonsPanel, BorderLayout.WEST);

            tabbedPane = new JTabbedPane();
            JPanel earthquakePanel = createMapPanel(cfList.get("Earthquakes").getPapplet());
            tabbedPane.addTab("Карта землетрясений", earthquakePanel);
            // Add Airport Map
            JPanel airportPanel = createMapPanel(cfList.get("AirportMap").getPapplet());

            tabbedPane.addTab("Аэропорты мира и их маршруты", airportPanel);

            // Live expectancy map
            JPanel liveExpectancy = createMapPanel(cfList.get("Live").getPapplet());

            tabbedPane.addTab("Средняя продолжительность жизни", liveExpectancy);
            setTabbedEnabled(false);
            getContentPane().add(tabbedPane, BorderLayout.CENTER);

            this.setLocation(defaultScreen.getDefaultConfiguration().getBounds().getLocation());
            setDefaultLookAndFeelDecorated(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            pack();
            setVisible(true);
            cfList.forEach((key, value) -> value.getPapplet().redraw());

        });

    }

    private void setTabbedEnabled(boolean setAs) {
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setEnabledAt(1, setAs);
        tabbedPane.setEnabledAt(2, setAs);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
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
            setTabbedEnabled(true);
        } else {
            userStatusLabel.setText(message);
            userStatusLabel.setVisible(true);
            setTabbedEnabled(false);
        }
    }
}
