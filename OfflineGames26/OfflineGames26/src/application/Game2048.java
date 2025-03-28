package application;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game2048 {

    private static final int CELL_SIZE = 64;

    public void start(Stage myStage) {
        myStage.setTitle("Game 2048");

        FlowPane rootNode = new FlowPane();
        myStage.setResizable(false);
        myStage.setOnCloseRequest(event -> Platform.exit());

        Game game = new Game();
        Scene myScene = new Scene(rootNode, game.getWidth(), game.getHeight());
        myStage.setScene(myScene);

        myScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                game.resetGame();
            }

            if (!game.canMove() || (!game.win && !game.canMove())) {
                game.lose = true;
            }

            if (!game.win && !game.lose) {
                switch (event.getCode()) {
                    case LEFT -> game.left();
                    case RIGHT -> game.right();
                    case DOWN -> game.down();
                    case UP -> game.up();
                    case H -> game.showHint();
                }
            }
            game.relocate(330, 390);
        });

        rootNode.getChildren().add(game);
        myStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                GraphicsContext gc = game.getGraphicsContext2D();
                gc.setFill(Color.rgb(135, 206, 235)); // Sky blue
                gc.fillRect(0, 0, game.getWidth(), game.getHeight());

                for (int y = 0; y < 4; y++) {
                    for (int x = 0; x < 4; x++) {
                        Cell cell = game.getCells()[x + y * 4];
                        int value = cell.number;
                        int xOffset = offsetCoors(x);
                        int yOffset = offsetCoors(y);

                        gc.setFill(cell.getBackground());
                        gc.fillRoundRect(xOffset, yOffset, CELL_SIZE, CELL_SIZE, 14, 14);
                        gc.setFill(cell.getForeground());

                        final int size = value < 100 ? 32 : value < 1000 ? 28 : 24;
                        gc.setFont(Font.font("Verdana", FontWeight.BOLD, size));
                        gc.setTextAlign(TextAlignment.CENTER);

                        String s = String.valueOf(value);

                        if (value != 0)
                            gc.fillText(s, xOffset + CELL_SIZE / 2, yOffset + CELL_SIZE / 2 - 2);

                        if (game.win || game.lose) {
                            gc.setFill(Color.rgb(255, 255, 255));
                            gc.fillRect(0, 0, game.getWidth(), game.getHeight());
                            gc.setFill(Color.rgb(78, 139, 202));
                            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
                            if (game.win) {
                                gc.fillText("You win!", 95, 150);
                            }
                            if (game.lose) {
                                gc.fillText("Game over!", 150, 130);
                                gc.fillText("You lose!", 160, 200);
                            }
                            gc.setFont(Font.font("Verdana", FontWeight.LIGHT, 16));
                            gc.setFill(Color.rgb(128, 128, 128));
                            gc.fillText("Press ESC to play again", 110, 270);
                        }
                        gc.setFont(Font.font("Verdana", FontWeight.LIGHT, 18));
                        gc.fillText("Score: " + game.score, 200, 350);
                    }
                }

                // Display hint if available
                if (game.hint != null) {
                    gc.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
                    gc.setFill(Color.BLUE);
                    gc.fillText("Hint: Try moving " + game.hint, 150, 380);
                }
            }
        }.start();
    }

    private static int offsetCoors(int arg) {
        return arg * (16 + 64) + 16;
    }

    public static class Game extends Canvas {

        private Cell[] cells;
        boolean win = false;
        boolean lose = false;
        int score = 0;
        String hint = null; // To store the current hint

        public Cell[] getCells() {
            return cells;
        }

        public Game() {
            super(330, 390);
            setFocused(true);
            resetGame();
        }

        void resetGame() {
            score = 0;
            win = false;
            lose = false;
            hint = null;
            cells = new Cell[4 * 4];
            for (int cell = 0; cell < cells.length; cell++) {
                cells[cell] = new Cell();
            }
            addCell();
            addCell();
        }

        private void addCell() {
            List<Cell> list = availableSpace();
            if (!availableSpace().isEmpty()) {
                int index = (int) (Math.random() * list.size()) % list.size();
                Cell emptyCell = list.get(index);
                emptyCell.number = Math.random() < 0.9 ? 2 : 4;
            }
        }

        private List<Cell> availableSpace() {
            List<Cell> list = new ArrayList<>(16);
            for (Cell c : cells)
                if (c.isEmpty())
                    list.add(c);
            return list;
        }

        private boolean isFull() {
            return availableSpace().size() == 0;
        }

        private Cell cellAt(int x, int y) {
            return cells[x + y * 4];
        }

        protected boolean canMove() {
            if (!isFull()) return true;
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    Cell cell = cellAt(x, y);
                    if ((x < 3 && cell.number == cellAt(x + 1, y).number) ||
                            (y < 3) && cell.number == cellAt(x, y + 1).number) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean compare(Cell[] line1, Cell[] line2) {
            if (line1 == line2) {
                return true;
            }
            if (line1.length != line2.length) {
                return false;
            }

            for (int i = 0; i < line1.length; i++) {
                if (line1[i].number != line2[i].number) {
                    return false;
                }
            }
            return true;
        }

        private Cell[] rotate(int angle) {
            Cell[] tiles = new Cell[4 * 4];
            int offsetX = 3;
            int offsetY = 3;
            if (angle == 90) {
                offsetY = 0;
            } else if (angle == 270) {
                offsetX = 0;
            }

            double rad = Math.toRadians(angle);
            int cos = (int) Math.cos(rad);
            int sin = (int) Math.sin(rad);
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    int newX = (x * cos) - (y * sin) + offsetX;
                    int newY = (x * sin) + (y * cos) + offsetY;
                    tiles[(newX) + (newY) * 4] = cellAt(x, y);
                }
            }
            return tiles;
        }

        private Cell[] moveLine(Cell[] oldLine) {
            LinkedList<Cell> list = new LinkedList<>();
            for (Cell cell : oldLine) {
                if (!cell.isEmpty()) {
                    list.addLast(cell);
                }
            }

            if (list.size() == 0) {
                return oldLine;
            } else {
                Cell[] newLine = new Cell[4];
                while (list.size() != 4) {
                    list.add(new Cell());
                }
                for (int j = 0; j < 4; j++) {
                    newLine[j] = list.removeFirst();
                }
                return newLine;
            }
        }

        private Cell[] mergeLine(Cell[] oldLine) {
            LinkedList<Cell> list = new LinkedList<>();
            for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
                int num = oldLine[i].number;
                if (i < 3 && oldLine[i].number == oldLine[i + 1].number) {
                    num *= 2;
                    score += num;
                    if (num == 2048) {
                        win = true;
                    }
                    i++;
                }
                list.add(new Cell(num));
            }

            if (list.size() == 0) {
                return oldLine;
            } else {
                while (list.size() != 4) {
                    list.add(new Cell());
                }
                return list.toArray(new Cell[4]);
            }
        }

        private Cell[] getLine(int index) {
            Cell[] result = new Cell[4];
            for (int i = 0; i < 4; i++) {
                result[i] = cellAt(i, index);
            }
            return result;
        }

        private void setLine(int index, Cell[] re) {
            System.arraycopy(re, 0, cells, index * 4, 4);
        }

        public void left() {
            boolean needAddCell = false;
            for (int i = 0; i < 4; i++) {
                Cell[] line = getLine(i);
                Cell[] merged = mergeLine(moveLine(line));
                setLine(i, merged);
                if (!needAddCell && !compare(line, merged)) {
                    needAddCell = true;
                }
            }
            if (needAddCell) {
                addCell();
            }
        }

        public void right() {
            cells = rotate(180);
            left();
            cells = rotate(180);
        }

        public void up() {
            cells = rotate(270);
            left();
            cells = rotate(90);
        }

        public void down() {
            cells = rotate(90);
            left();
            cells = rotate(270);
        }

        public void showHint() {
            int maxScore = -1;
            String bestMove = null;

            // Clone game state and test each move
            for (String move : new String[]{"LEFT", "RIGHT", "UP", "DOWN"}) {
                Game copy = cloneGame();
                Cell[] originalState = copy.cells.clone(); // Save the initial state

                switch (move) {
                    case "LEFT" -> copy.left();
                    case "RIGHT" -> copy.right();
                    case "UP" -> copy.up();
                    case "DOWN" -> copy.down();
                }

                // Check if the move actually changes the board state
                boolean boardChanged = !compareBoards(originalState, copy.cells);
                if (boardChanged && (copy.score > maxScore)) {
                    maxScore = copy.score;
                    bestMove = move;
                }
            }

            // Update hint with the best valid move
            hint = bestMove != null ? bestMove : "No valid moves!";
        }

        // Helper method to compare two board states
        private boolean compareBoards(Cell[] board1, Cell[] board2) {
            for (int i = 0; i < board1.length; i++) {
                if (board1[i].number != board2[i].number) {
                    return false;
                }
            }
            return true;
        }

        private Game cloneGame() {
            Game clone = new Game();
            for (int i = 0; i < cells.length; i++) {
                clone.cells[i] = new Cell(cells[i].number);
            }
            clone.score = this.score;
            clone.win = this.win;
            clone.lose = this.lose;
            return clone;
        }

    }

    public static class Cell {
        int number;

        public Cell() {
            this.number = 0;
        }

        public Cell(int number) {
            this.number = number;
        }

        public boolean isEmpty() {
            return number == 0;
        }

        public Color getBackground() {
            return switch (number) {
                case 0 -> Color.rgb(255, 255, 204);  // Light yellow for empty cells
                case 2 -> Color.rgb(255, 255, 102);  // Bright yellow
                case 4 -> Color.rgb(255, 204, 102);  // Orange
                case 8 -> Color.rgb(255, 153, 51);   // Deep orange
                case 16 -> Color.rgb(255, 102, 102); // Red
                case 32 -> Color.rgb(255, 51, 153);  // Pink
                case 64 -> Color.rgb(204, 51, 255);  // Purple
                case 128 -> Color.rgb(102, 153, 255); // Blue
                case 256 -> Color.rgb(102, 255, 204); // Aqua
                case 512 -> Color.rgb(102, 255, 102); // Green
                case 1024 -> Color.rgb(153, 255, 51); // Lime green
                case 2048 -> Color.rgb(255, 255, 0);  // Gold
                default -> Color.rgb(0, 0, 0);        // Black for higher numbers
            };
        }

        public Color getForeground() {
            return number < 16 ? Color.rgb(119, 110, 101, 1.0) : Color.rgb(249, 246, 242, 1.0);
        }
    }
}
