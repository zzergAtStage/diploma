package diploma;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import diploma.models.*;
import parsing.ParseFeed;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * EarthquakeCityMap
 * Based on
 * UC San Diego Intermediate Software Development MOOC team
 * @author Sergei Brusentsov
 * Date: 01 02 2024
 */
public class EarthquakeCityMap extends PApplet {

    private static final long serialVersionUID = 1L;
    /**
     * This is where to find the local tiles, for working without an Internet connection
     */
    public static String mbTilesString = "blankLight-1-3.mbtiles";
    private static boolean offline = true;
    //feed with magnitude 2.5+ Earthquakes
    private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

    // The files containing city names and info and country names and info
    private String cityFile = "city-data.json";
    private String countryFile = "countries.geo.json";

    // The map
    private UnfoldingMap map;

    // Markers for each city
    private List<Marker> cityMarkers;
    // Markers for each earthquake
    private List<Marker> quakeMarkers;

    // A List of country markers
    private List<Marker> countryMarkers;


    private CommonMarker lastSelected;
    private CommonMarker lastClicked;
    //private Map<Class<? extends CommonMarker>, String> classMap;

    public static void main(String[] args) {
        PApplet.main("diploma.EarthquakeCityMap");
    }

    public void setup() {
        // (1) Initializing canvas and map tiles
        size(900, 700, OPENGL);

        if (offline) {
            map = new UnfoldingMap(this, 200, 50, 650, 600/*, new MBTilesMapProvider(mbTilesString)*/);

        } else {
            map = new UnfoldingMap(this, 200, 50, 650, 600, new OpenStreetMap.OpenStreetMapProvider());

        }
        MapUtils.createDefaultEventDispatcher(this, map);


        // (2) Reading in earthquake data and geometric properties
        //     STEP 1: load country features and markers
        List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
        countryMarkers = MapUtils.createSimpleMarkers(countries);

        //     STEP 2: read in city data
        List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
        cityMarkers = new ArrayList<Marker>();
        for (Feature city : cities) {
            cityMarkers.add(new CityMarker(city));
        }

        //     STEP 3: read in earthquake RSS feed
        List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
        quakeMarkers = new ArrayList<Marker>();

        for (PointFeature feature : earthquakes) {
            //check if LandQuake
            if (isLand(feature)) {
                quakeMarkers.add(new LandQuakeMarker(feature));
            }
            // OceanQuakes
            else {
                quakeMarkers.add(new OceanQuakeMarker(feature));
            }
        }

        // (3) Add markers to map
        map.addMarkers(quakeMarkers);
        map.addMarkers(cityMarkers);


    }

    public void draw() {
        background(0);
        map.draw();
        addKey();

    }

    private void sortAndPrint(int numToPrint) {
        //using stream API
        Marker[] markers = quakeMarkers.stream()
                .sorted()
                .toArray(Marker[]::new);

        //traditional insertion sorting
//
//		Object[] markers = quakeMarkers.toArray();
//		for (int i = 1; i < markers.length; i++) {
//			int currPosition = i;
//
//			while (currPosition > 0 &&
//					(((EarthquakeMarker) markers[currPosition]).compareTo(((EarthquakeMarker)markers[currPosition - 1])) < 0)) {
//				swap(markers, currPosition, currPosition - 1);
//				currPosition -= 1;
//			}
//
//		}

        //choice count of items to be printed
        int itemsToOutput = (numToPrint < markers.length) ? numToPrint : markers.length;
        for (int i = 0; i < itemsToOutput; i++) {
            ;
        }


    }
    //sorting helper method
    private void swap(Object[] markers, int leftValueMin, int rightValueMin) {
        EarthquakeMarker tmp = (EarthquakeMarker) markers[rightValueMin];
        markers[rightValueMin] = markers[leftValueMin];
        markers[leftValueMin] = tmp;
    }

    /**
     * Event handler that gets called automatically when the
     * mouse moves.
     */
    @Override
    public void mouseMoved() {
        // clear the last selection
        if (lastSelected != null) {
            lastSelected.setSelected(false);
            lastSelected = null;

        }
        selectMarkerIfHover(quakeMarkers);
        selectMarkerIfHover(cityMarkers);
    }

    // If there is a marker under the cursor, and lastSelected is null
    // set the lastSelected to be the first marker found under the cursor
    // Make sure you do not select two markers.
    //
    private void selectMarkerIfHover(List<Marker> markers) {
        if (lastSelected != null) return;

        if (lastSelected == null) {
            for (Marker m : markers) {
                CommonMarker marker = (CommonMarker) m;
                if (marker.isInside(this.map, (float) mouseX, (float) mouseY)) {
                    //System.out.println("Marker: <" + marker.getStringProperty("name") + "> x" + mouseX + " y" + mouseY);
                    lastSelected = marker;
                    lastSelected.setSelected(true);
                    return;
                }
            }
        }
    }

    /**
     * The event handler for mouse clicks
     * It will display an earthquake and its threat circle of cities
     * Or if a city is clicked, it will display all the earthquakes
     * where the city is in the threat circle
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mouseClicked() {
        if (lastClicked != null) {
            unhiddenMarkers();
            lastSelected = lastClicked = null;

        } else if (lastClicked == null) {
            Marker currentClicked = this.getMarkerUnderClick(cityMarkers);
            lastClicked = (currentClicked == null) ? (CommonMarker) this.getMarkerUnderClick(quakeMarkers) : (CommonMarker) currentClicked;

            if (lastClicked == null) return;
            lastClicked.setClicked(true);
            hideMarkersOutterRange(lastClicked.getClass());
        }
    }

    private void hideMarkersOutterRange(Class<? extends CommonMarker> markerClass) {

        if (markerClass.equals(LandQuakeMarker.class) || markerClass.equals(OceanQuakeMarker.class)) {
            //All downcast expressions breaks the SOLID principles!!!
            for (Marker marker : cityMarkers) {
                //Hide all cityMarkers out of bounds of threatCircle
                if (marker.getDistanceTo(lastClicked.getLocation()) < ((EarthquakeMarker) lastClicked)
                        .threatCircle()) {
                    marker.setHidden(false);
                } else {
                    marker.setHidden(true);
                }

            }
            //hide all earthquakes
            for (Marker marker : quakeMarkers) {
                if (marker != lastClicked) {
                    marker.setHidden(true);
                }
            }
        } else {
            for (Marker marker : quakeMarkers) {
                if (marker.getDistanceTo(lastClicked.getLocation()) < ((EarthquakeMarker) marker)
                        .threatCircle()) {
                    marker.setHidden(false);
                } else {
                    marker.setHidden(true);
                }
            }
            //hide all other cities
            for (Marker marker : cityMarkers) {
                if (marker != lastClicked) marker.setHidden(true);
            }
        }
    }

    private Marker getMarkerUnderClick(List<? extends Marker> markers) {
        for (Marker marker : markers) {
            if (marker.isInside(map, mouseX, mouseY)
                    && (marker.getClass() == EarthquakeMarker.class
                    || marker.getClass() == LandQuakeMarker.class
                    || marker.getClass() == OceanQuakeMarker.class
                    || marker.getClass() == CityMarker.class)
            ) return marker;
        }
        return null;
    }

    // loop over and unhidden all markers
    private void unhiddenMarkers() {
        for (Marker marker : quakeMarkers) {
            marker.setHidden(false);
        }

        for (Marker marker : cityMarkers) {
            marker.setHidden(false);
        }
    }

    // helper method to draw key in GUI
    private void addKey() {
        fill(255, 250, 240);

        int xbase = 25;
        int ybase = 50;

        rect(xbase, ybase, 180, 250);

        fill(0);
        textAlign(LEFT, CENTER);
        textSize(14);
        text("Легенда", xbase + 25, ybase + 25);

        fill(150, 30, 30);
        int tri_xbase = xbase + 35;
        int tri_ybase = ybase + 50;
        triangle(tri_xbase, tri_ybase - CityMarker.TRI_SIZE, tri_xbase - CityMarker.TRI_SIZE,
                tri_ybase + CityMarker.TRI_SIZE, tri_xbase + CityMarker.TRI_SIZE,
                tri_ybase + CityMarker.TRI_SIZE);

        fill(0, 0, 0);
        textAlign(LEFT, CENTER);
        text("Города", tri_xbase + 15, tri_ybase);

        text("Суша", xbase + 50, ybase + 70);
        text("Морские", xbase + 50, ybase + 90);
        text("Размер ~ Магнитуда", xbase + 25, ybase + 110);

        fill(255, 255, 255);
        ellipse(xbase + 35,
                ybase + 70,
                10,
                10);
        rect(xbase + 35 - 5, ybase + 90 - 5, 10, 10);

        fill(color(255, 0, 0));
        ellipse(xbase + 35, ybase + 140, 12, 12);
        fill(color(0, 0, 255));
        ellipse(xbase + 35, ybase + 160, 12, 12);
        fill(color(255, 255, 0));
        ellipse(xbase + 35, ybase + 180, 12, 12);

        textAlign(LEFT, CENTER);
        fill(0, 0, 0);
        text("Поверхностные", xbase + 50, ybase + 140);
        text("Средняя глубина", xbase + 50, ybase + 160);
        text("Глубокие", xbase + 50, ybase + 180);

        text("Последний час", xbase + 50, ybase + 200);

        fill(255, 255, 255);
        int centerx = xbase + 35;
        int centery = ybase + 200;
        ellipse(centerx, centery, 12, 12);

        strokeWeight(2);
        line(centerx - 8, centery - 8, centerx + 8, centery + 8);
        line(centerx - 8, centery + 8, centerx + 8, centery - 8);

    }

    private boolean isLand(PointFeature earthquake) {
        for (Marker country : countryMarkers) {
            if (isInCountry(earthquake, country)) {
                return true;
            }
        }
        // not inside any country
        return false;
    }

    // prints countries with number of earthquakes
    private void printQuakes() {
        int totalWaterQuakes = quakeMarkers.size();
        for (Marker country : countryMarkers) {
            String countryName = country.getStringProperty("name");
            int numQuakes = 0;
            for (Marker marker : quakeMarkers) {
                EarthquakeMarker eqMarker = (EarthquakeMarker) marker;
                if (eqMarker.isOnLand()) {
                    if (countryName.equals(eqMarker.getStringProperty("country"))) {
                        numQuakes++;
                    }
                }
            }
            if (numQuakes > 0) {
                totalWaterQuakes -= numQuakes;
                //System.out.println(countryName + ": " + numQuakes);
            }
        }
        //System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
    }

    // helper method to test whether a given earthquake is in a given country
    // This will also add the country property to the properties of the earthquake feature if
    // it's in one of the countries.
    // You should not have to modify this code
    private boolean isInCountry(PointFeature earthquake, Marker country) {
        // getting location of feature
        Location checkLoc = earthquake.getLocation();

        // some countries represented it as MultiMarker
        // looping over SimplePolygonMarkers which make them up to use isInsideByLoc
        if (country.getClass() == MultiMarker.class) {

            // looping over markers making up MultiMarker
            for (Marker marker : ((MultiMarker) country).getMarkers()) {

                // checking if inside
                if (((AbstractShapeMarker) marker).isInsideByLocation(checkLoc)) {
                    earthquake.addProperty("country", country.getProperty("name"));

                    // return if is inside one
                    return true;
                }
            }
        }

        // check if inside country represented by SimplePolygonMarker
        else if (((AbstractShapeMarker) country).isInsideByLocation(checkLoc)) {
            earthquake.addProperty("country", country.getProperty("name"));

            return true;
        }
        return false;
    }
}
