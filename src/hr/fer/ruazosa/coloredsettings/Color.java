package hr.fer.ruazosa.coloredsettings;
/**
 * This the Color, it is defined by RGB values (red, green, blue)
 * which are calculated in DrawOnTop class
 * @author Tomislav
 *
 */
public class Color {
	
	/**
	 * Value of red color (0-255)
	 */
	public double red = 0; 
	/**
	 * Value of blue color (0-255)
	 */
	public double blue = 0;
	/**
	 * Value of green color (0-255)
	 */
	public double green = 0;
	
	/**
	 * Calculates the color by the RGB values
	 * @param flash Information if the torch (extra light) is on
	 * @return Returns name of the color (Red, Green, Blue, Yellow, Black, White)
	 */
	 
	public String returnColor (boolean flash){
		if (flash) {
			blue -= 35;
			green -= 45;
			red -= 35;
		}
		String colorName = "Not recognized";
		double wlow = (red + blue + green) / 3 - 10;
		double whigh = wlow + 20;
		if (red > wlow && red < whigh && green > wlow && green < whigh && blue > wlow && blue < whigh && wlow > 80) colorName = "White";
		else if (red > wlow && red < whigh && green > wlow && green < whigh && blue > wlow && blue < whigh && wlow < 30) colorName = "Black";
		else if (red < 110 && green > 60 && blue < 160 && (green > blue) && (green > red)) colorName = "Green";
		else if (red < 135 && green < 160 && blue > 50 && (blue > green) && (blue > red)) colorName = "Blue";
		else if (red > 70 && green < 100 && blue < 120 && (red>blue)) colorName = "Red";
		else if (red > 100 && green > 50 && blue <120) colorName = "Yellow";
		return colorName;
		
	}
}
