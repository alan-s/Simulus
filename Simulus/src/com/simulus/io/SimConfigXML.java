package com.simulus.io;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.simulus.MainApp;


/**
 * Used by the Simulus suite of applications, ({@link MainApp})
 * for importing and exporting a Simulus simulation file which contains simulation parameters.  XML data format used.
 * 
 *
 */
public class SimConfigXML {

	File inputXmlFile;
	File outputXmlFile;

	NodeList nList;
	Node nNode;
	Element eElement;

	String simName;
	String simCreationDate;
	String simDescription;
	String simAuthor;

	double noCars;
	double tickRate;
	double spawnRate;
	double maxSpeedCars;
	double carTruckRatio;
	boolean debugMode;
	double recklessNormRatio;
	String carColourOption;
	String carColour;
	String truckColourOption;
	String truckColour;

	

	public SimConfigXML() {

	}

	/**
	 * Reads a Simulus XML simulation file. Error thrown if the file does not match expected schema
	 * 
	 * @param inputFile the simulation file
	 */
	public void readXML(String inputFile) {

		try {

			inputXmlFile = new File(inputFile);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName("sim_details");
			nNode = nList.item(0);
			eElement = (Element) nNode;

			simName = eElement.getElementsByTagName("name").item(0)
					.getTextContent();
			simCreationDate = eElement.getElementsByTagName("date").item(0)
					.getTextContent();
			simDescription = eElement.getElementsByTagName("description")
					.item(0).getTextContent();
			simAuthor = eElement.getElementsByTagName("author").item(0)
					.getTextContent();

			nList = doc.getElementsByTagName("sim_specs");
			nNode = nList.item(0);
			eElement = (Element) nNode;

			noCars = Double.parseDouble(eElement
					.getElementsByTagName("no_of_cars").item(0)
					.getTextContent());
			tickRate = Double.parseDouble(eElement
					.getElementsByTagName("tickrate").item(0).getTextContent());
			spawnRate = Double.parseDouble(eElement.getElementsByTagName("spawnrate")
							.item(0).getTextContent());
			maxSpeedCars = Double.parseDouble(eElement
					.getElementsByTagName("max_speed_cars").item(0)
					.getTextContent());
			carTruckRatio = Double.parseDouble(eElement
					.getElementsByTagName("car_truck_ratio").item(0)
					.getTextContent());
			debugMode = Boolean.parseBoolean(eElement
					.getElementsByTagName("debug").item(0).getTextContent());
			
			recklessNormRatio = Double.parseDouble(eElement
					.getElementsByTagName("reckless_norm_ratio").item(0).getTextContent());
			
			nList = doc.getElementsByTagName("colour_specs");
			nNode = nList.item(0);
			eElement = (Element) nNode;
			
			carColourOption = eElement.getElementsByTagName("car_colour_option").item(0)
					.getTextContent();
						
			carColour = eElement.getElementsByTagName("car_colour").item(0)
			.getTextContent();
			
			truckColourOption = eElement.getElementsByTagName("truck_colour_option").item(0)
					.getTextContent();
			
			truckColour = eElement.getElementsByTagName("truck_colour").item(0)
					.getTextContent();
			
		} catch (Exception e) {
			System.err
					.println("Check that the input file exisits and that it matches XML simulation schema.");
			System.err.println("Error reported:");
			e.printStackTrace();
		}

	}

	/**
	 * Outputs a Simulus simulation file with the passed parameters
	 * 
	 * @param outputFile the file path to save the output including a filename
	 * @param nameIn a name for the simulation
	 * @param dateIn the date and time the simulation was last saved
	 * @param descIn a description for the simulation file
	 * @param authIn the author of the simulation settings file
	 * @param noCarsIn the number of cars used in the simulation
	 * @param tickRateIn the tick rate used in the simulation
	 * @param spawnRateIn the spawn rate used in the simulation
	 * @param maxSpeedIn the maximum speed of cars in the simulation
	 * @param carTruckRatioIn the ration of cars to trucks in the simulation
	 * @param debugIn if the simulation should run in debug mode
	 * @param recklessNormRatioIn the ratio of reckless to normal cars in the simulation
	 * @param carColourOptionIn the source of colour for cars in the simulation
	 * @param carColourIn the custom car colour when the user defined option us used in the simulation
	 * @param truckColourOptionIn the source of colour for trucks in the simulation
	 * @param truckColourIn the custom truck colour when the user defined option us used in the simulation
	 */
	public void writeXML(String outputFile, String nameIn, String dateIn,
			String descIn, String authIn, double noCarsIn, double tickRateIn,
			double spawnRateIn, double maxSpeedIn, double carTruckRatioIn,
			boolean debugIn, double recklessNormRatioIn, String carColourOptionIn, String carColourIn, 
			String truckColourOptionIn, String truckColourIn) {

		try {

			outputXmlFile = new File(outputFile);

			simName = nameIn;
			simCreationDate = dateIn;
			simDescription = descIn;
			simAuthor = authIn;

			noCars = noCarsIn;
			tickRate = tickRateIn;
			spawnRate = spawnRateIn;
			maxSpeedCars = maxSpeedIn;
			carTruckRatio = carTruckRatioIn;
			debugMode = debugIn;
			recklessNormRatio = recklessNormRatioIn; 
			carColourOption = carColourOptionIn;
			carColour = carColourIn;
			truckColourOption = truckColourOptionIn;
			truckColour = truckColourIn;

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root element - simulation
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("simulation");
			doc.appendChild(rootElement);

			// create a comment with team tag
			Comment comment = doc
					.createComment("Team Simulus Traffic Simulator Simulation File");
			doc.insertBefore(comment, rootElement);

			// sim_details
			Element simDetails = doc.createElement("sim_details");
			rootElement.appendChild(simDetails);

			Element name = doc.createElement("name");
			name.appendChild(doc.createTextNode(simName));
			simDetails.appendChild(name);

			Element date = doc.createElement("date");
			date.appendChild(doc.createTextNode(simCreationDate));
			simDetails.appendChild(date);

			Element description = doc.createElement("description");
			description.appendChild(doc.createTextNode(simDescription));
			simDetails.appendChild(description);

			Element author = doc.createElement("author");
			author.appendChild(doc.createTextNode(simAuthor));
			simDetails.appendChild(author);

			// sim_specs
			Element simSpecs = doc.createElement("sim_specs");
			rootElement.appendChild(simSpecs);

			Element eNoCars = doc.createElement("no_of_cars");
			eNoCars.appendChild(doc.createTextNode(Double.toString(noCars)));
			simSpecs.appendChild(eNoCars);

			Element eTickRate = doc.createElement("tickrate");
			eTickRate
					.appendChild(doc.createTextNode(Double.toString(tickRate)));
			simSpecs.appendChild(eTickRate);

			Element eSpawnRate = doc.createElement("spawnrate");
			eSpawnRate.appendChild(doc.createTextNode(Double
					.toString(spawnRate)));
			simSpecs.appendChild(eSpawnRate);

			Element eMaxSpeedCars = doc.createElement("max_speed_cars");
			eMaxSpeedCars.appendChild(doc.createTextNode(Double
					.toString(maxSpeedCars)));
			simSpecs.appendChild(eMaxSpeedCars);

			Element eCartruckRatio = doc.createElement("car_truck_ratio");
			eCartruckRatio.appendChild(doc.createTextNode(Double
					.toString(carTruckRatio)));
			simSpecs.appendChild(eCartruckRatio);

			Element eDubug = doc.createElement("debug");
			eDubug.appendChild(doc.createTextNode(Boolean.toString(debugMode)));
			simSpecs.appendChild(eDubug);
			
			Element eRecklessRatio = doc.createElement("reckless_norm_ratio");
			eRecklessRatio.appendChild(doc.createTextNode(String.valueOf(recklessNormRatio)));
			simSpecs.appendChild(eRecklessRatio);
			
			Element colourSpecs = doc.createElement("colour_specs");
			rootElement.appendChild(colourSpecs);
			
			Element eCarColourOption = doc.createElement("car_colour_option");
			eCarColourOption.appendChild(doc.createTextNode(carColourOption));
			colourSpecs.appendChild(eCarColourOption);
			
			Element eCarColour = doc.createElement("car_colour");
			eCarColour.appendChild(doc.createTextNode(carColour));
			colourSpecs.appendChild(eCarColour);
			
			Element eTruckColourOption = doc.createElement("truck_colour_option");
			eTruckColourOption.appendChild(doc.createTextNode(truckColourOption));
			colourSpecs.appendChild(eTruckColourOption);
			
			Element eTruckColour = doc.createElement("truck_colour");
			eTruckColour.appendChild(doc.createTextNode(truckColour));
			colourSpecs.appendChild(eTruckColour);	
			

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outputXmlFile);

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}

	}

	public String toString() {

		return "No of Cars: " + noCars + "\n" + "Tickrate: " + tickRate + "\n"
				+ "Spawn Rate: " + spawnRate + "\n"
				+ "Max Speed of Cars: " + maxSpeedCars + "\n"
				+ "Car to Truck Ratio: " + carTruckRatio + "\n"
				+ "Reckless Normal Car Ratio: " + recklessNormRatio + "\n"
				+ "Debug Mode: " + debugMode + "\n"
				+ "Car Colour Option: " + carColourOption + "\n"
				+ "Car Colour: " + carColour + "\n"
				+ "Truck Colour Option: " + truckColourOption + "\n"
				+ "Truck Colour: " + truckColour;
	}

	public String getSimName() {
		return simName;
	}

	public String getSimCreationDate() {
		return simCreationDate;
	}

	public String getSimDescription() {
		return simDescription;
	}

	public String getSimAuthor() {
		return simAuthor;
	}

	public double getNoCars() {
		return noCars;
	}

	public double getTickRate() {
		return tickRate;
	}

	public double getSpawnRate() {
		return spawnRate;
	}

	public double getMaxSpeedCars() {
		return maxSpeedCars;
	}

	public double getCarTruckRatio() {
		return carTruckRatio;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public double getRecklessNormRatio() {
		return recklessNormRatio;
	}

	public String getCarColourOption() {
		return carColourOption;
	}

	public String getCarColour() {
		return carColour;
	}

	public String getTruckColourOption() {
		return truckColourOption;
	}

	public String getTruckColour() {
		return truckColour;
	}
	



}
