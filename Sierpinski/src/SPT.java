

public class SPT {
	public static void main(String[] args) {
		int order = 4;  // Set the order of the triangle (change this number)
		int height = (int) Math.pow(2, order); // Height of the triangle
		char[][] triangle = new char[height][height * 2 - 1];

		// Initialize the array with spaces
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < (height * 2 - 1); j++) {
				triangle[i][j] = ' ';
			}
		}

		drawSierpinski(triangle, height - 1, (height * 2 - 2) / 2, height, order);

		// Print the triangle
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < (height * 2 - 1); j++) {
				System.out.print(triangle[i][j]);
			}
			System.out.println();
		}
	}

	// Recursive function to draw Sierpinski Triangle
	private static void drawSierpinski(char[][] triangle, int y, int x, int size, int order) {
		if (order == 0) {
			// Draw the single point at the base case
			triangle[y][x] = '*';
		} else {
			int newSize = size / 2;
			// Top triangle
			drawSierpinski(triangle, y, x, newSize, order - 1);
			// Bottom-left triangle
			drawSierpinski(triangle, y + newSize, x - newSize, newSize, order - 1);
			// Bottom-right triangle
			drawSierpinski(triangle, y + newSize, x + newSize, newSize, order - 1);
		}
	}
}
