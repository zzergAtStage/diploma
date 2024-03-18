package diploma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashMap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashMap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		fillRoutes(airports, routes);
		System.out.println("routeList size: " + routeList.size());
		map.addMarkers(airportList);
		map.addMarkers(routeList);
		
	}

	private void fillRoutes(HashMap<Integer, Location> airports, List<ShapeFeature> routes) {
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
			sl.setHidden(true); //by default all routes are hidden
			routeList.add(sl);
		}
	}
	
	
	
	public void draw() {
		background(0);
		map.draw();
		
	}

	
	//mouse events
	
	@Override
	public void mouseMoved() {
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(airportList);
		
		super.mouseMoved();
	}
	
	private void selectMarkerIfHover(List<Marker> markers)
	{
		if (lastSelected != null) return;
		
		if (lastSelected == null) { 
			for (Marker m : markers) {
				CommonMarker marker = (CommonMarker)m;
				if(marker.isInside(this.map, (float)mouseX, (float)mouseY)) {
					lastSelected = marker;
					lastSelected.setSelected(true);
					return;
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void mouseClicked(){
		if ( lastClicked != null) {
			unhideMarkers();
			hideAllRoutes();
			lastSelected = lastClicked = null;
			
		} else if (lastClicked == null){
			CommonMarker currentClicked = lastSelected;//this.getMarkerUnderClick((List<? extends CommonMarker>) cityMarkers);
			lastClicked = ( currentClicked == null) ? this.getMarkerUnderClick(airportList) : currentClicked;
			
			if (lastClicked == null) return;
			
			lastClicked.setClicked(true);
			//hide other airports
			hideOtherMarkers(lastClicked);
			
			//show routes
			showRoutes(lastClicked);
		}
	}

	private void hideOtherMarkers(CommonMarker lastClicked2) {
		for (Marker marker : airportList) {
			if (!(marker == lastClicked2)) marker.setHidden(true);
		}	
	}
	
	private void hideAllRoutes() {
		for (Marker marker : routeList) {
			marker.setHidden(true);
		}
	}
	private void showRoutes(CommonMarker lastClicked2) {
		SimpleLinesMarker marker = null;
		//if the routes doesn't have targeted airport
		try {
			for (Marker route : routeList) {
				marker = (SimpleLinesMarker) route;
				if (route.getProperty("source") != null &&
						lastClicked2.getId().equals((String) route.getProperty("source"))) route.setHidden(false);
			}
		} catch (NullPointerException e) {
			//we still work - because is not critical thing - missing route or destination
			System.out.println("#AirportId = " + lastClicked2.getId() + " #marker: " + marker + "# " + marker.getStringProperty("sourse"));
		}
	}

	private  CommonMarker getMarkerUnderClick(List<? extends Marker> markers) {
		for (Marker marker : markers) {
			if (marker.isInside(map, mouseX, mouseY) 
					&& marker.getClass() == AirportMarker.class
					) return (CommonMarker) marker;
		}
		return null;
	}

	private void unhideMarkers() {
		for(Marker marker : airportList) {
			marker.setHidden(false);
		}
	}

	public static void main(String[] args) {
		PApplet.main("module6.AirportMap");
	}
	
}
