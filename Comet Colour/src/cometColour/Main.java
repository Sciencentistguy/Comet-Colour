package cometColour;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {
		int[][][] pixels = loadImageFromFile("res/comet.png");
		int[][][] bricks = basicBrickPicking(pixels);
		writeImage(pixelsToImage(bricks), "res/output.png");

		bom("res/output.png", "res/bom.txt");
	}

	private static void bom(String image, String bomPath) {
		int[][][] pixels = loadImageFromFile(image);

		Map<String, Integer> bom = new HashMap<>();

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {

				String colour = "";

				for (Colours col : Colours.values()) {
					if (col.getR() == pixels[i][j][0] && col.getG() == pixels[i][j][1] && col.getB() == pixels[i][j][2]) {
						colour = col.getName();
						break;
					}
				}

				if (colour.equals(""))
					throw new IllegalArgumentException("Pixel (" + i + ", " + j + ") is not in the colour set!");

				if (bom.containsKey(colour))
					bom.put(colour, bom.get(colour) + 1);
				else
					bom.put(colour, 1);
			}
		}

		StringBuilder outStr = new StringBuilder();

		int total = 0;

		for (String colour : bom.keySet()) {
			total += bom.get(colour);
			outStr.append(colour).append(": ").append(bom.get(colour)).append("pcs\n");
		}

		outStr.append("Total: ").append(total).append("pcs");

		try {
			FileWriter outFile = new FileWriter(bomPath);
			outFile.write(outStr.toString());
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static int colDiff(Color c1, Color c2) {

		int diffR = c1.getRed() - c2.getRed();
		int diffG = c1.getGreen() - c2.getGreen();
		int diffB = c1.getBlue() - c2.getBlue();

		int diff2A = Math.abs(diffR - diffG);
		int diff2B = Math.abs(diffG - diffB);
		int diff2C = Math.abs(diffB - diffR);

		return diff2A + diff2B + diff2C;
	}

	private static int[][][] basicBrickPicking(int[][][] pixels) {
		int dim = 50;

		int[][][] bricks = new int[dim][dim][3];

		final int PIXELS_PER_BRICK = pixels.length / dim;

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				float r = 0;
				float g = 0;
				float b = 0;

				for (int pixI = 0; pixI < PIXELS_PER_BRICK; pixI++) {
					for (int pixJ = 0; pixJ < PIXELS_PER_BRICK; pixJ++) {

						int x_coord = ((i * pixels.length) / dim) + pixI;
						int y_coord = ((j * pixels.length) / dim) + pixJ;

						int pr = pixels[x_coord][y_coord][0];
						int pg = pixels[x_coord][y_coord][1];
						int pb = pixels[x_coord][y_coord][2];

						r += pr;
						g += pg;
						b += pb;

					}
				}
				float ppbSquared = PIXELS_PER_BRICK * PIXELS_PER_BRICK;
				r /= ppbSquared;
				g /= ppbSquared;
				b /= ppbSquared;

				r = Math.round(r);
				g = Math.round(g);
				b = Math.round(b);

				Colours closest = null;

				for (Colours col : Colours.values()) {
					if (closest == null) {
						closest = col;
						continue;
					}

					Color c = new Color((int) r, (int) g, (int) b);
					int diffCol = colDiff(c, new Color(col.getR(), col.getG(), col.getB()));
					int diffClosest = colDiff(c, new Color(closest.getR(), closest.getG(), closest.getB()));

					float difCol = Math.abs(r - col.getR()) + Math.abs(g - col.getG()) + Math.abs(b - col.getB());
					float difClosest = Math.abs(r - closest.getR()) + Math.abs(g - closest.getG()) + Math.abs(b - closest.getB());

					if (diffCol < diffClosest) {
						closest = col;
					}

				}

				assert closest != null; // if Colours.values() is empty, closest is null, then NullPointerException.
				bricks[i][j][0] = closest.getR();
				bricks[i][j][1] = closest.getG();
				bricks[i][j][2] = closest.getB();
			}
		}

		return bricks;
	}

	private static List<Color> getBackgrounds(int[][][] pixels) {
		HashMap<Color, Integer> clustering = new HashMap<>();
		final float DIFF = 0.15f;

		for (int[][] column : pixels) {
			for (int row = 0; row < pixels[0].length; row++) {

				float r = column[row][0] / (float) 255;
				float g = column[row][1] / (float) 255;
				float b = column[row][2] / (float) 255;

				if (clustering.isEmpty()) {
					clustering.put(new Color(r, g, b), 1);
					continue;
				}

				Color closest = null;
				for (Color c : clustering.keySet()) {

					float diffCol = Math.abs(r - (c.getRed() / (float) 255)) + Math.abs(g - (c.getGreen() / (float) 255)) + Math.abs(b - (c.getBlue() / (float) 255));

					if (diffCol <= DIFF) {
						clustering.put(c, clustering.get(c) + 1);
						closest = c;
						break;
					}

				}

				if (closest == null) clustering.put(new Color(r, g, b), 1);

			}
		}

		List<Color> backgrounds = new ArrayList<>();
		Color temp = clustering.keySet().stream().max(Comparator.comparing(clustering::get)).orElseThrow();
		clustering.remove(temp);
		backgrounds.add(temp);
		temp = clustering.keySet().stream().max(Comparator.comparing(clustering::get)).orElseThrow();
		clustering.remove(temp);
		backgrounds.add(temp);

		return backgrounds;
	}

	private static int[][][] loadImageFromFile(String filepath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		/*	pixels[x][y][0] -> (x,y) R val
		 *  pixels[x][y][1] -> (x,y) G val
		 *  pixels[x][y][2] -> (x,y) B val
		 */

		int[][][] pixels;

		if (image.getWidth() <= image.getHeight())
			pixels = new int[image.getWidth()][image.getWidth()][3];
		else
			pixels = new int[image.getHeight()][image.getHeight()][3];

		for (int col = 0; col < pixels.length; col++) {
			for (int row = 0; row < pixels[0].length; row++) {
				pixels[col][row][0] = (0xFF0000 & image.getRGB(col, row)) >> 16;
				pixels[col][row][1] = (0x00FF00 & image.getRGB(col, row)) >> 8;
				pixels[col][row][2] = 0x0000FF & image.getRGB(col, row);
			}
		}
		return pixels;
	}

	private static void writeImage(BufferedImage image, String filepath) {
		File file = new File(filepath);
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage pixelsToImage(int[][][] pixels) {
		BufferedImage toRet = new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
		for (int col = 0; col < pixels.length; col++) {
			for (int row = 0; row < pixels[0].length; row++) {
				toRet.setRGB(col, row, (pixels[col][row][0] << 16 | pixels[col][row][1] << 8) | pixels[col][row][2]);
			}
		}
		return toRet;
	}

}
