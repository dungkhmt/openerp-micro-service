package localsearch.domainspecific.vehiclerouting.vrp.utils.googlemaps;

import java.io.DataInputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




import java.net.URLEncoder;

import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class GoogleMapsQuery {

	/**
	 * @param args
	 */
	public static final int SPEED = 40;// average speed is 40km/h
	public static final double RATIO = 1.5;// ratio between gMap distance and
											// Havsine distance

	public GoogleMapsQuery() {
	}

	public double getApproximateTravelTimeSecond(double lat1, double long1,
			double lat2, double long2) {
		double t = computeDistanceHaversine(lat1, long1, lat2, long2);
		t = t * 3600.0 / SPEED;
		t = t * RATIO;
		return t;
	}

	public double getApproximateDistanceMeter(double lat1, double long1,
			double lat2, double long2) {
		double t = computeDistanceHaversine(lat1, long1, lat2, long2) * 1000;
		t = t * RATIO;
		return t;
	}

	public double computeDistanceHaversine(double lat1, double long1,
			double lat2, double long2) {
		double SCALE = 1;
		double PI = 3.14159265;
		long1 = long1 * 1.0 / SCALE;
		lat1 = lat1 * 1.0 / SCALE;
		long2 = long2 * 1.0 / SCALE;
		lat2 = lat2 * 1.0 / SCALE;

		double dlat1 = lat1 * PI / 180;
		double dlong1 = long1 * PI / 180;
		double dlat2 = lat2 * PI / 180;
		double dlong2 = long2 * PI / 180;

		double dlong = dlong2 - dlong1;
		double dlat = dlat2 - dlat1;

		double aHarv = Math.pow(Math.sin(dlat / 2), 2.0) + Math.cos(dlat1)
				* Math.cos(dlat2) * Math.pow(Math.sin(dlong / 2), 2.0);
		double cHarv = 2 * Math.atan2(Math.sqrt(aHarv), Math.sqrt(1.0 - aHarv));

		double R = 6378.137; // in km

		return R * cHarv * SCALE; // in km

	}

	public LatLng getCoordinate(String address) {
		URL url = null;
		try {
			url = new URL(
					"http://maps.google.com/maps/api/geocode/xml?address="
							+ URLEncoder.encode(address, "UTF-8")
							+ "&sensor=false");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		HttpURLConnection urlConn = null;
		try {
			// URL connection channel.
			urlConn = (HttpURLConnection) url.openConnection();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Let the run-time system (RTS) know that we want input.
		urlConn.setDoInput(true);

		// Let the RTS know that we want to do output.
		urlConn.setDoOutput(true);
		// No caching, we want the real thing.
		urlConn.setUseCaches(false);
		try {
			urlConn.setRequestMethod("POST");
		} catch (ProtocolException ex) {
			ex.printStackTrace();
		}
		try {
			urlConn.connect();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		DataOutputStream output = null;
		DataInputStream input = null;
		try {
			output = new DataOutputStream(urlConn.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Get response data.
		String str = null;
		try {
			PrintWriter out = new PrintWriter(new FileWriter(
					"http-post-log.txt"));
			input = new DataInputStream(urlConn.getInputStream());
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(input);
				doc.getDocumentElement().normalize();

				NodeList nl = doc.getElementsByTagName("geometry");
				Node nod = nl.item(0);
				Element e = (Element) nod;
				if (e == null) {
					return null;
				}

				nl = e.getElementsByTagName("location");
				nod = nl.item(nl.getLength() - 1);
				
				e = (Element) nod;
				nl = e.getElementsByTagName("lat");
				nod = nl.item(0);

				String d_s = nod.getChildNodes().item(0).getNodeValue();
				double lat = Double.valueOf(d_s);

				nl = e.getElementsByTagName("lng");
				nod = nl.item(0);
				d_s = nod.getChildNodes().item(0).getNodeValue();
				double lng = Double.valueOf(d_s);

				return new LatLng(lat, lng);
			} catch (Exception e) {
				e.printStackTrace();
			}

			input.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public double getDistance(double lat1, double lng1, double lat2, double lng2) {
		URL url = null;
		try {
			url = new URL(
					"http://maps.google.com/maps/api/directions/xml?origin="
							+ lat1 + "," + lng1 + "&destination=" + lat2 + ","
							+ lng2 + "&sensor=false&units=metric");
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}

		HttpURLConnection urlConn = null;
		try {
			// URL connection channel.
			urlConn = (HttpURLConnection) url.openConnection();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Let the run-time system (RTS) know that we want input.
		urlConn.setDoInput(true);

		// Let the RTS know that we want to do output.
		urlConn.setDoOutput(true);

		// No caching, we want the real thing.
		urlConn.setUseCaches(false);

		try {
			urlConn.setRequestMethod("POST");
		} catch (ProtocolException ex) {
			ex.printStackTrace();
		}

		try {
			urlConn.connect();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		DataOutputStream output = null;
		DataInputStream input = null;

		try {
			output = new DataOutputStream(urlConn.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Get response data.
		String str = null;
		double d = -1;
		try {
			PrintWriter out = new PrintWriter(new FileWriter(
					"http-post-log.txt"));
			input = new DataInputStream(urlConn.getInputStream());
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(input);
				doc.getDocumentElement().normalize();

				NodeList nl = doc.getElementsByTagName("leg");
				Node nod = nl.item(0);
				Element e = (Element) nod;
				if (e == null) {
					return -1;
				}
				nl = e.getElementsByTagName("step");
				nl = e.getElementsByTagName("distance");
				nod = nl.item(nl.getLength() - 1);
				
				e = (Element) nod;
				nl = e.getElementsByTagName("text");
				nod = nl.item(0);

				String d_s = nod.getChildNodes().item(0).getNodeValue();
				int idx = d_s.indexOf("km");
				if (idx < 0) {
					idx = d_s.indexOf("m");
					if (idx == -1) {
						return -1;
					}
					d_s = d_s.substring(0, idx);
					d = Double.valueOf(d_s) * 0.001; // convert into km
				} else {
					d_s = d_s.substring(0, idx);
					d = Double.valueOf(d_s);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			input.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return d;
	}

	public int getTravelTime(double lat1, double lng1, double lat2,
			double lng2, String mode) {
		// try to probe maximum 20 times
		int t = -1;
		int maxTrials = 2;
		for (int i = 0; i < maxTrials; i++) {
			t = getTravelTimeOnePost(lat1, lng1, lat2, lng2, mode);
			if (t > -1)
				break;
		}

		return t;
	}

	private int getTravelTimeOnePost(double lat1, double lng1, double lat2,
			double lng2, String mode) {

		URL url = null;
		try {
			url = new URL(
					"http://maps.google.com/maps/api/directions/xml?origin="
							+ lat1 + "," + lng1 + "&destination=" + lat2 + ","
							+ lng2 + "&sensor=false&units=metric");
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}

		HttpURLConnection urlConn = null;
		try {
			// URL connection channel.
			urlConn = (HttpURLConnection) url.openConnection();
		} catch (IOException ex) {
			System.out.println("openConnection failed");
			ex.printStackTrace();
		}

		// Let the run-time system (RTS) know that we want input.
		urlConn.setDoInput(true);

		// Let the RTS know that we want to do output.
		urlConn.setDoOutput(true);

		// No caching, we want the real thing.
		urlConn.setUseCaches(false);

		try {
			urlConn.setRequestMethod("POST");
		} catch (ProtocolException ex) {
			ex.printStackTrace();
		}

		try {
			urlConn.connect();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		DataOutputStream output = null;
		DataInputStream input = null;

		try {
			output = new DataOutputStream(urlConn.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Get response data.
		String str = null;
		int duration = -1;// in seconds
		try {
			input = new DataInputStream(urlConn.getInputStream());
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(input);

				doc.getDocumentElement().normalize();

				NodeList nl = doc.getElementsByTagName("leg");
				Node nod = nl.item(0);
				Element e = (Element) nod;
				if (e == null) {
					return -1;
				}
				nl = e.getElementsByTagName("duration");
				nod = nl.item(nl.getLength() - 1);
				
				e = (Element) nod;
				nl = e.getElementsByTagName("value");
				nod = nl.item(0);

				e = (Element) nod;

				duration = Integer.valueOf(e.getChildNodes().item(0)
						.getNodeValue());

			} catch (Exception e) {
				e.printStackTrace();
			}

			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return duration;
	}

	public Direction getDirection(double lat1, double lng1, double lat2,
			double lng2, String mode) {
		Direction direction = null;
		ArrayList<StepDirection> steps = new ArrayList<StepDirection>();
		URL url = null;
		int durations = 0;
		int distances = 0;
		try {
			url = new URL(
					"http://maps.google.com/maps/api/directions/xml?origin="
							+ lat1 + "," + lng1 + "&destination=" + lat2 + ","
							+ lng2 + "&sensor=false&units=metric&mode=" + mode);
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
		System.out.println("URL: " + url);

		HttpURLConnection urlConn = null;
		try {
			// URL connection channel.
			urlConn = (HttpURLConnection) url.openConnection();
		} catch (IOException ex) {
			System.out.println("openConnection failed");
			ex.printStackTrace();
		}

		// Let the run-time system (RTS) know that we want input.
		urlConn.setDoInput(true);
		urlConn.setDoOutput(true);
		// No caching, we want the real thing.
		urlConn.setUseCaches(false);

		try {
			urlConn.setRequestMethod("POST");
		} catch (ProtocolException ex) {
			ex.printStackTrace();
		}

		try {
			urlConn.connect();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		DataOutputStream output = null;
		DataInputStream input = null;
		try {
			output = new DataOutputStream(urlConn.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Get response data.
		String str = null;
		try {
			input = new DataInputStream(urlConn.getInputStream());
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
				Document doc = builder.parse(input);
				doc.getDocumentElement().normalize();

				// read steps
				NodeList nl = doc.getElementsByTagName("step");
				int szLocation = nl.getLength();
				String lat_start_location;
				String lng_start_location;

				String lat_end_location;
				String lng_end_location;

				int duration;
				float distance;
				String modeStep;
				String html_instruction;

				for (int i = 0; i < szLocation; i++) {
					// read start locations
					NodeList nlStart = doc
							.getElementsByTagName("start_location");
					Element e = (Element) nlStart.item(i);
					lat_start_location = e.getElementsByTagName("lat").item(0)
							.getChildNodes().item(0).getNodeValue();
					lng_start_location = e.getElementsByTagName("lng").item(0)
							.getChildNodes().item(0).getNodeValue();

					// read end locations
					NodeList nlEnd = doc.getElementsByTagName("end_location");
					e = (Element) nlEnd.item(i);
					lat_end_location = e.getElementsByTagName("lat").item(0)
							.getChildNodes().item(0).getNodeValue();
					lng_end_location = e.getElementsByTagName("lng").item(0)
							.getChildNodes().item(0).getNodeValue();

					// read duration
					NodeList nlDuration = doc.getElementsByTagName("duration");
					e = (Element) nlDuration.item(i);
					duration = Integer.parseInt(e.getElementsByTagName("value")
							.item(0).getChildNodes().item(0).getNodeValue());
					durations += duration;
					
					// read distance
					NodeList nlDistance = doc.getElementsByTagName("distance");
					e = (Element) nlDistance.item(i);
					distance = Float.parseFloat(e.getElementsByTagName("value")
							.item(0).getChildNodes().item(0).getNodeValue());
					distances += distance;
					
					// read mode
					NodeList nlModeStep = doc
							.getElementsByTagName("travel_mode");
					e = (Element) nlModeStep.item(i);
					modeStep = e.getChildNodes().item(0).getNodeValue();
					
					// read html instruction
					NodeList nlHTML_instructions = doc
							.getElementsByTagName("html_instructions");
					e = (Element) nlHTML_instructions.item(i);
					html_instruction = e.getChildNodes().item(0).getNodeValue();
					
					StepDirection step = new StepDirection(lat_start_location,
							lng_start_location, lat_end_location,
							lng_end_location, duration, distance, modeStep,
							html_instruction);
					steps.add(step);
				}

				// read start address
				String startAdd = null;
				String endAdd = null;
				if (doc.getElementsByTagName("start_address") != null) {
					NodeList nlStartAdd = doc
							.getElementsByTagName("start_address");
					Element e = (Element) nlStartAdd.item(0);
					if (e != null) {
						startAdd = e.getChildNodes().item(0).getNodeValue();
					}

					// read end address
					NodeList nlEndAdd = doc.getElementsByTagName("end_address");
					e = (Element) nlEndAdd.item(0);
					if (e != null) {
						endAdd = e.getChildNodes().item(0).getNodeValue();
					}
				}
				
				direction = new Direction(steps, startAdd, endAdd, lat1, lng1,
						lat2, lng2, durations, distances, mode);
			} catch (Exception e) {
				e.printStackTrace();
			}

			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return direction;
	}



	public void extractCoordinate(String fn){
		try{
			Scanner in = new Scanner(new File(fn));
			while(true){
				String addr = in.nextLine();
				if(addr.equals("END")) break;
				LatLng ll = getCoordinate(addr);
				String p = "NULL";
				if(ll != null)
					p = ll.toString();
				System.out.println(addr + " : " + p);
			}
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		GoogleMapsQuery G = new GoogleMapsQuery();
		//G.getDirection(21, 105, 21.01, 105, "driving");
		G.extractCoordinate("addr.txt");
	}
}
