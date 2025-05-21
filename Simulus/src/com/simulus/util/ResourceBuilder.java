package com.simulus.util;

import javafx.scene.image.Image;

/**
 * Provides a central access point to all resources used when rendering the simulation or the editor.
 */
public class ResourceBuilder {

    private static Image EWLaneTexture = new Image("/resources/roadtile_eastwest.png");
    private static Image NSLaneTexture = new Image("/resources/roadtile_northsouth.png");
    private static Image doubleNSLaneTexture = new Image("/resources/double_northsouth.png");
    private static Image doubleEWLaneTexture = new Image("/resources/double_eastwest.png");
    private static Image landTexture = new Image("/resources/land.png");
    private static Image dirtTexture= new Image("/resources/dirt.png");
    private static Image grassTexture = new Image("/resources/grass.png");
    private static Image blockTexture = new Image("/resources/block.png");
    private static Image boxjunctionTexture = new Image("/resources/boxjunction.png");
    private static Image waterTexture = new Image("/resources/water.png");
    
    private static Image blockCursor = new Image("/resources/csr_block.png");
    private static Image boxjunctionCursor = new Image("/resources/csr_boxjunction.png");
    private static Image waterCursor = new Image("/resources/csr_water.png");
    private static Image dirtCursor = new Image("/resources/csr_dirt.png");
    private static Image EWLaneCursor = new Image("/resources/csr_eastwest.png");
    private static Image NSLaneCursor = new Image("/resources/csr_northsouth.png");
    private static Image eraseCursor = new Image("/resources/csr_erase.png");
    private static Image grassCursor = new Image("/resources/csr_grass.png");
    private static Image settingsCursor = new Image("/resources/csr_settings.png");
    private static Image deleteIcon = new Image("/resources/delete.png");
    private static Image disketteIcon = new Image("/resources/diskette.png");
    private static Image resizeIcon = new Image("/resources/resize_icon.png");
    private static Image eraseIcon = new Image("/resources/erase.png");
    private static Image folderIcon = new Image("/resources/folder.png");
    private static Image validateIcon = new Image("/resources/validate.png");
    private static Image simulateIcon = new Image("/resources/small_car.png");
    
    private static Image logoSmall = new Image("/resources/logo1_small.png");

	public static Image getEWLaneTexture() {
		return EWLaneTexture;
	}

	public static Image getNSLaneTexture() {
		return NSLaneTexture;
	}

	public static Image getDoubleNSLaneTexture() {
		return doubleNSLaneTexture;
	}

	public static Image getDoubleEWLaneTexture() {
		return doubleEWLaneTexture;
	}

	public static Image getLandTexture() {
		return landTexture;
	}

	public static Image getDirtTexture() {
		return dirtTexture;
	}

	public static Image getGrassTexture() {
		return grassTexture;
	}

	public static Image getBlockTexture() {
		return blockTexture;
	}

	public static Image getBoxjunctionTexture() {
		return boxjunctionTexture;
	}

	public static Image getWaterTexture() {
		return waterTexture;
	}

	public static Image getBlockCursor() {
		return blockCursor;
	}

	public static Image getBoxjunctionCursor() {
		return boxjunctionCursor;
	}

	public static Image getWaterCursor() {
		return waterCursor;
	}

	public static Image getDirtCursor() {
		return dirtCursor;
	}

	public static Image getEWLaneCursor() {
		return EWLaneCursor;
	}

	public static Image getNSLaneCursor() {
		return NSLaneCursor;
	}

	public static Image getEraseCursor() {
		return eraseCursor;
	}

	public static Image getGrassCursor() {
		return grassCursor;
	}

	public static Image getDeleteIcon() {
		return deleteIcon;
	}

	public static Image getDisketteIcon() {
		return disketteIcon;
	}

	public static Image getResizeIcon() {
		return resizeIcon;
	}

	public static Image getEraseIcon() {
		return eraseIcon;
	}

	public static Image getFolderIcon() {
		return folderIcon;
	}

	public static Image getValidateIcon() {
		return validateIcon;
	}

	public static Image getLogoSmall() {
		return logoSmall;
	}  
	
	public static Image getSimulateIcon() {
		return simulateIcon;
	}

	public static Image getSettingsCursor() {
		return settingsCursor;
	}

	public static void setSettingsCursor(Image settingsCursor) {
		ResourceBuilder.settingsCursor = settingsCursor;
	}
}
