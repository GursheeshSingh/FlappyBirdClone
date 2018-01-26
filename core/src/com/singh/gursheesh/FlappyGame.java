package com.singh.gursheesh;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyGame extends ApplicationAdapter {
    private static final String TAG = "FlappyGame";

    private SpriteBatch batch;
    private BitmapFont font;

    private Texture gameOver;
    private Texture background;
    private Texture pipeFaceDown;
    private Texture pipeFaceUp;
    private Texture[] birds;

    private int flapstate = BIRD_WING_UP;

    private float birdY = 0;

    private float birdVelocity;
    private float gravity = 2;

    private Circle birdCircle;

    private int scoringPipe;

    private float pipeVelocity = 10;

    private float[] pipeX = new float[NUMBER_OF_PIPES];
    private float[] pipeOffset = new float[NUMBER_OF_PIPES];

    private Rectangle[] pipeFaceUpRectangles;
    private Rectangle[] pipeFaceDownRectangles;

    private int gameState = START;
    private int gameScore;

    private final static int NUMBER_OF_PIPES = 4;
    private static float DISTANCE_BETWEEN_PIPES;

    private Random randomGenerator;

    private static float SCREEN_HEIGHT;
    private static float SCREEN_WIDTH;

    private static float PIPE_WIDTH;
    private static float PIPE_HEIGHT;

    private static float BIRD_WIDTH;
    private static float BIRD_HEIGHT;

    private static final int PIPES_GAP = 500;

    private static final int BIRD_WING_UP   = 0;
    private static final int BIRD_WING_DOWN = 1;

    private static float SCREEN_BOTTOMMOST = 0;
    private static float SCREEN_LEFTMOST   = 0;
    private static float SCREEN_RIGHTMOST;
    private static float SCREEN_TOPMOST;

    private static final int START = 0;
    private static final int PLAY  = 1;
    private static final int OVER  = 2;

    @Override
    public void create() {

        SCREEN_WIDTH  = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();

        SCREEN_RIGHTMOST = SCREEN_WIDTH;
        SCREEN_TOPMOST   = SCREEN_HEIGHT;

        DISTANCE_BETWEEN_PIPES = SCREEN_WIDTH / 2;

        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        gameOver     = new Texture("gameover.png");
        background   = new Texture("bg.png");
        pipeFaceDown = new Texture("toptube.png");
        pipeFaceUp   = new Texture("bottomtube.png");

        PIPE_WIDTH  = pipeFaceDown.getWidth();
        PIPE_HEIGHT = pipeFaceDown.getHeight();

        birds = new Texture[2];
        birds[BIRD_WING_UP]   = new Texture("bird.png");
        birds[BIRD_WING_DOWN] = new Texture("bird2.png");

        BIRD_WIDTH  = birds[0].getWidth();
        BIRD_HEIGHT = birds[0].getHeight();

        birdCircle = new Circle();

        randomGenerator = new Random();

        pipeFaceUpRectangles   = new Rectangle[NUMBER_OF_PIPES];
        pipeFaceDownRectangles = new Rectangle[NUMBER_OF_PIPES];

        initializingGame();

    }

    private void initializingGame() {

        birdVelocity = 0;
        gameScore    = 0;
        scoringPipe  = 0;

        birdY = (SCREEN_HEIGHT - birds[BIRD_WING_UP].getHeight()) / 2;

        for (int i = 0; i < NUMBER_OF_PIPES; i++) {
            pipeOffset[i] = (randomGenerator.nextFloat() - 0.5F) * (SCREEN_HEIGHT - PIPES_GAP - 200);

            pipeX[i] = (SCREEN_WIDTH - PIPE_WIDTH) / 2 + SCREEN_WIDTH + (i * DISTANCE_BETWEEN_PIPES);

            pipeFaceDownRectangles[i] = new Rectangle();
            pipeFaceUpRectangles[i]   = new Rectangle();
        }
    }

    @Override
    public void render() {

        batch.begin();

        batch.draw(background,
                SCREEN_LEFTMOST, SCREEN_BOTTOMMOST,
                SCREEN_WIDTH, SCREEN_HEIGHT);

        if (gameState == PLAY) {

            if (Gdx.input.justTouched()) {
//                Bird Jumps
                birdVelocity = -30;
            }

            if (pipeX[scoringPipe] < SCREEN_WIDTH / 2) {

                gameScore = gameScore + 1;
                Gdx.app.log("Scored ", String.valueOf(gameScore));

                changePipe();
            }

            for (int i = 0; i < NUMBER_OF_PIPES; i++) {

                if (isPipeOffScreen(pipeX[i])) {

                    pipeX[i] = pipeX[i] + NUMBER_OF_PIPES * DISTANCE_BETWEEN_PIPES;
                    pipeOffset[i] = (randomGenerator.nextFloat() - 0.5F) * (SCREEN_HEIGHT - PIPES_GAP - 200);
                } else {

                    pipeX[i] = pipeX[i] - pipeVelocity;
                }


                batch.draw(pipeFaceDown,
                        pipeX[i],
                        (SCREEN_HEIGHT / 2) + (PIPES_GAP / 2) + pipeOffset[i]
                );
                batch.draw(pipeFaceUp,
                        pipeX[i],
                        (SCREEN_HEIGHT / 2) - (PIPES_GAP / 2) - PIPE_HEIGHT + pipeOffset[i]
                );

                pipeFaceDownRectangles[i]
                        .set(pipeX[i], (SCREEN_HEIGHT / 2) + (PIPES_GAP / 2) + pipeOffset[i]
                                , PIPE_WIDTH, PIPE_HEIGHT);

                pipeFaceUpRectangles[i]
                        .set(pipeX[i], (SCREEN_HEIGHT / 2) - (PIPES_GAP / 2) - PIPE_HEIGHT + pipeOffset[i]
                                , PIPE_WIDTH, PIPE_HEIGHT);

            }

            if (isBirdInScreen(birdY)) {
//                    +ve Velocity = FALL DOWN
//                    -ve Velocity = JUMP
                birdVelocity = birdVelocity + gravity;
                birdY = birdY - birdVelocity;

            } else if (isBirdOffScreen(birdY)) {
                gameState = OVER;
            }

        } else if (gameState == OVER) {

            batch.draw(gameOver,
                    (SCREEN_WIDTH - gameOver.getWidth()) / 2,
                    (SCREEN_HEIGHT - gameOver.getHeight()) / 2);

            if (Gdx.input.justTouched()) {

                gameState = PLAY;
                initializingGame();

            }

        } else if (gameState == START) {

            if (Gdx.input.justTouched()) {
                gameState = PLAY;
            }

        }

        if (flapstate == BIRD_WING_UP)
            flapstate = BIRD_WING_DOWN;
        else
            flapstate = BIRD_WING_UP;

        batch.draw(birds[flapstate],
                (SCREEN_WIDTH - BIRD_WIDTH) / 2,
                birdY);

        font.draw(batch, String.valueOf(gameScore), 100, 200);

        birdCircle.set(SCREEN_WIDTH / 2, birdY + BIRD_HEIGHT / 2, BIRD_WIDTH / 2);

        for (int i = 0; i < NUMBER_OF_PIPES; i++) {

            if (birdCollide(pipeFaceDownRectangles[i],pipeFaceUpRectangles[i])) {

                gameState = OVER;
                System.out.println("Collision");

            }

        }

        batch.end();

    }

    private void changePipe() {
        if (scoringPipe < NUMBER_OF_PIPES - 1) {
            scoringPipe++;
        } else {
            scoringPipe = 0;
        }
    }

    private boolean isPipeOffScreen(float x) {
        return (x < -PIPE_WIDTH);
    }

    private boolean isBirdInScreen(float y) {
        return (y > SCREEN_BOTTOMMOST && y < SCREEN_TOPMOST);
    }

    private boolean isBirdOffScreen(float y) {
        return (y <= SCREEN_BOTTOMMOST || y >= SCREEN_TOPMOST);
    }

    private boolean birdCollide(Rectangle pipeFaceDown,Rectangle pipeFaceUp){
        return ( Intersector.overlaps(birdCircle, pipeFaceDown) || Intersector.overlaps(birdCircle, pipeFaceUp) );
    }
}
