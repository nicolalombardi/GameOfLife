package com.nicola.gameoflife;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Timer;
import com.nicola.gameoflife.Game.Cell;

public class Main extends ApplicationAdapter implements InputProcessor {
    public static int FIELD_WIDTH, FIELD_HEIGHT, WIDTH, HEIGHT, VERT_OFFS;
    public static int squareSize = 10, sizeIncrements = 1;
    SpriteBatch batch;
    ShapeRenderer shape;
    Font font;


    Game game;
    Timer timer;
    Timer.Task timerTask;
    private int[] mousePos = new int[2];
    private int[] previousDragPosition = new int[2];
    private boolean mouseDragging = false;
    private int mouseButtonDown = -1;
    private Color background = new Color(0.2f, 0.2f, 0.2f, 1);
    private boolean pause = true;
    private boolean showHelp = true;
    //Parameters
    private int simulationSpeed = 20, speedIncrements = 2;
//    private boolean cellBorder = false;
    private boolean drawGrid = true;



    @Override
    public void create() {
        VERT_OFFS = 50;
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        FIELD_WIDTH = Gdx.graphics.getWidth();
        FIELD_HEIGHT = Gdx.graphics.getHeight() - VERT_OFFS;

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        font = new Font();

        game = new Game(FIELD_WIDTH / squareSize, FIELD_HEIGHT / squareSize);
        timer = new Timer();
        timerTask = new Timer.Task() {
            @Override
            public void run() {
                game.update();
            }
        };
        timer.scheduleTask(timerTask, 0f, simulationSpeed / 100f);
        timer.stop();
        Patterns.load();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int[][] grid = game.getGrid();

        //Draws the cells
        shape.begin(ShapeType.Filled);
        for (int i = 0; i < Game.dimX; i++) {
            for (int j = 0; j < Game.dimY; j++) {
                if (grid[i][j] == Game.ALIVE) {
                    shape.setColor(1, 1, 1, 1);
                    shape.rect(i * squareSize, j * squareSize, squareSize, squareSize);
                }

            }
        }
        shape.end();


        //Draws the text info at the top
        batch.begin();


        if (pause)
            font.getFont(20, Color.RED).draw(batch, "STATUS: " + (pause ? "STOPPED" : "RUNNING"), 10, HEIGHT - 15);
        else
            font.getFont(20, Color.GREEN).draw(batch, "STATUS: " + (pause ? "STOPPED" : "RUNNING"), 10, HEIGHT - 15);


        font.getFont(20, Color.WHITE).draw(batch, "GENERATIONS: " + game.getGenerations(), 330, HEIGHT - 15);

        font.getFont(20, Color.WHITE).draw(batch, "SPEED: " + 1f / (simulationSpeed / 100f) + " GEN/s", 590, HEIGHT - 15);

        batch.end();

        //Draws the separation line at the top
        shape.begin(ShapeType.Filled);
        shape.setColor(Color.ORANGE);
        shape.rect(0, HEIGHT-45, WIDTH, 3);
        shape.end();

        // Draw the grid
        if(drawGrid && squareSize > 1) {
            shape.begin(ShapeType.Filled);
            shape.setColor(Color.BLACK);

            for (int i = 0; i < Game.dimX; i++) {
                shape.rect(i * squareSize, 0, 1, Game.dimY * squareSize);
            }
            for (int j = 0; j < Game.dimY + 1; j++) {
                shape.rect(0, j * squareSize, WIDTH, 1);
            }

            shape.end();
        }


        //Draws the little square around the mouse
        if (mousePos[1] < FIELD_HEIGHT / squareSize) {
            shape.begin(ShapeType.Line);
            shape.setColor(Color.ORANGE);
            shape.rect(mousePos[0] * squareSize + 1, mousePos[1] * squareSize, squareSize - 1, squareSize -1);
            shape.end();
        }


        //Draws the help menu
        if (showHelp) {
            int topY = HEIGHT - VERT_OFFS - 20;
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shape.begin(ShapeType.Filled);
            shape.setColor(0, 0, 0, 0.5f);
            shape.rect(530, topY - 17*11, 260, 17*11);
            shape.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            batch.begin();
            font.getFont(11, Color.WHITE).draw(batch,
                    "Controls\n" +
                            "\n" +
                            "Left Click: Toggle cell state\n" +
                            "Left mouse drag: Paint alive cells\n" +
                            "Right mouse drag: Paint dead cells\n" +
                            "Spacebar: Pause\n" +
                            "R: Reset the grid\n" +
                            "N: Generate a new random grid\n" +
                            "G: Toggle grid\n" +
                            "Numbers: Load patterns\n" +
                            "Scroll Wheel: Control speed\n" +
                            "Down Arrow: Reduce grid size\n" +
                            "Up Arrow: Increase grid size\n\n" +
                            "H: Show/Hide this menu",
                    550, topY - 16);

            batch.end();

        }
    }


    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        //Pause the game
        if (keycode == Input.Keys.SPACE) {
            pause = !pause;
            if (pause)
                timer.stop();
            else
                timer.start();
        }
        //Reset the grid
        else if (keycode == Input.Keys.R) {
            game.reset();
            timer.stop();
            pause = true;
        }
        //Generate a random grid
        else if (keycode == Input.Keys.N) {
            game.generateRandom(15);
            timer.stop();
            pause = true;
        }
        //Load pattern
        else if (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9){
            Pattern p = Patterns.getPattern(keycode);
            if(p != null) {
                timer.stop();
                pause = true;
                game.setPattern(p);
            }
        }
        //Increases the grid size
        else if (keycode == Input.Keys.UP) {
            pause = true;
            timer.stop();
            squareSize += sizeIncrements;
            game = new Game(FIELD_WIDTH / squareSize, FIELD_HEIGHT / squareSize);
            game.generateRandom(15);
        }
        //Decreases the grid size
        else if (keycode == Input.Keys.DOWN) {
            if (squareSize - sizeIncrements > 0) {
                pause = true;
                timer.stop();
                squareSize -= sizeIncrements;
                game = new Game(FIELD_WIDTH / squareSize, FIELD_HEIGHT / squareSize);
                game.generateRandom(15);
            }

        }

        // Toggle the background grid
        else if (keycode == Input.Keys.G) {
            drawGrid = !drawGrid;
        }
        //Toggles the help menu
        else if (keycode == Input.Keys.H) {
            showHelp = !showHelp;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseButtonDown = button;
        if (pause) {
            if (button == Input.Buttons.LEFT && screenY > VERT_OFFS) {
                game.reverseCell(getCellFromScreenCoords(screenX, screenY));
            }

        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mouseDragging = false;
        mouseButtonDown = -1;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        System.out.println(pointer);
        Cell c = getCellFromScreenCoords(screenX, screenY);
        mousePos[0] = c.x();
        mousePos[1] = c.y();
        if(!pause) {
            return false;
        }
        boolean newCellState = mouseButtonDown == Input.Buttons.LEFT;
        // If it's not the first event of a drag
        if(mouseDragging){
            int fromX = Math.min(previousDragPosition[0], screenX);
            int toX = Math.max(previousDragPosition[0], screenX);
            for(int x = fromX; x <= toX; x++){
                int y = Math.round(Utils.getYFromXOfLine(previousDragPosition[0], previousDragPosition[1], screenX, screenY, x));
                paintOnCoords(x, y, newCellState);
            }
            int fromY = Math.min(previousDragPosition[1], screenY);
            int toY = Math.max(previousDragPosition[1], screenY);
            for(int y = fromY; y < toY; y++){
                int x = Math.round(Utils.getXFromYOfLine(previousDragPosition[0], previousDragPosition[1], screenX, screenY, y));
                paintOnCoords(x, y, newCellState);
            }
        }else{
            paintOnCoords(screenX, screenY, newCellState);
        }
        previousDragPosition[0] = screenX;
        previousDragPosition[1] = screenY;
        mouseDragging = true;

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Cell c = getCellFromScreenCoords(screenX, screenY);
        mousePos[0] = c.x();
        mousePos[1] = c.y();
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount < 0) {
            //Reduces the time between each generation thus augmenting the speed
            if (simulationSpeed - speedIncrements > 0) {
                simulationSpeed -= speedIncrements;
                timer.clear();
                timer.scheduleTask(timerTask, 0f, simulationSpeed / 100f);
            }

        } else {
            //Increases the time between each generation thus reducing the speed
            simulationSpeed += speedIncrements;
            timer.clear();
            timer.scheduleTask(timerTask, 0f, simulationSpeed / 100f);
        }
        return false;
    }

    private Cell getCellFromScreenCoords(int screenX, int screenY){
        int cellX = screenX / squareSize;
        int cellY = (FIELD_HEIGHT - (screenY - VERT_OFFS)) / squareSize;
        return new Cell(cellX, cellY);
    }

    private void paintOnCoords(int x, int y, boolean alive){
        Cell c = getCellFromScreenCoords(x, y);
        if(c.x() > -1 && c.x() < Game.dimX && c.y() > -1 && c.y() < Game.dimY){
            game.setCell(c, alive);
        }
    }

}
