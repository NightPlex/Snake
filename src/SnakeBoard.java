import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;



/*
 * Board with JPanel for the game. Uses the ActionListener..
 * 
 * */

public class SnakeBoard extends JPanel implements ActionListener {

	// Stores the x and y values of all points with snake. In other words joints.
    private final int x[] = new int[GlobalConfig.ALL_DOTS_COUNT];
    private final int y[] = new int[GlobalConfig.ALL_DOTS_COUNT];

    // amount of total "dots" : head + joints
    private int dots;
    //apple coords
    private int apple_x;
    private int apple_y;

    
    //moving direction: only one can be true
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    
    // game status: true = game is going on; false = game over.
    private boolean gameStatus = true;

    
    private Timer timer;
    
    //join image
    private Image dot;
    //apple image
    private Image apple;
    //head image
    private Image head;

    // Construct the board with key listener, and JPanel settings
    public SnakeBoard() {
    	
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(GlobalConfig.BOARD_WIDTH, GlobalConfig.BOARD_HEIGHT));
        loadImages(); // load images for the game
        initGame(); // starts the game
    }
    //Set ImageIcons to be the game Images..
    private void loadImages() {

        ImageIcon iid = new ImageIcon(new ImageIcon("img/dot.png").getImage().getScaledInstance(GlobalConfig.DOT_SIZE, GlobalConfig.DOT_SIZE, Image.SCALE_DEFAULT)); // resize them according to the size of the preferred dot
        dot = iid.getImage();

        ImageIcon iia = new ImageIcon(new ImageIcon("img/apple.png").getImage().getScaledInstance(GlobalConfig.DOT_SIZE, GlobalConfig.DOT_SIZE, Image.SCALE_DEFAULT));
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon(new ImageIcon("img/head.png").getImage().getScaledInstance(GlobalConfig.DOT_SIZE, GlobalConfig.DOT_SIZE, Image.SCALE_DEFAULT));
        head = iih.getImage();
    }

    
    // Start the game
    private void initGame() {

        dots = 3; // head and 2 joints

        for (int i = 0; i < dots; i++) { // loop "dots" many times to create dots after head.
            x[i] = 50 - i * 10; // 10 pixels in between each dot.
            y[i] = 50; // as default we move right, no need to do anything here
        }

        randomApple(); // Give me random apple position

        timer = new Timer(GlobalConfig.DELAY, this);
        timer.start();
    }

    // Override the paint method..
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    // Draw the snake and its joints, if gameOver is false, end the game and draw game over.
    private void doDrawing(Graphics g) {
        
        if (gameStatus) {

            g.drawImage(apple, apple_x, apple_y, this); // draw apple

            
            // draw head and joints, head is in the 0 position.
            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.drawImage(head, x[i], y[i], this);
                } else {
                    g.drawImage(dot, x[i], y[i], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }        
    }
    
    //simple game over graphics..
    private void gameOver(Graphics g) {
        
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14); 
        FontMetrics metr = getFontMetrics(small); // get font  rendering

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (GlobalConfig.BOARD_WIDTH - metr.stringWidth(msg)) / 2, GlobalConfig.BOARD_HEIGHT / 2);
    }

    // Check if joint head collides with apple
    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++; // add joint
            randomApple(); // create new random apple
        }
    }

    // Move to the direction of the config.
    private void move() {

    	
    	// we move all its joints forward in chain
        for (int i = dots; i > 0; i--) {
            x[i] = x[(i - 1)];
            y[i] = y[(i - 1)];
        }
        
        // Move one dot-size towards true direction

        if (leftDirection) {
            x[0] -= GlobalConfig.DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += GlobalConfig.DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= GlobalConfig.DOT_SIZE;
        }

        if (downDirection) {
            y[0] += GlobalConfig.DOT_SIZE;
        }
    }

    
    // check if snake has hit walls or himself. Collision.
    private void checkCollision() {

        for (int i = dots; i > 0; i--) {

            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) { // if snake is bigger than 4 dots
            												  //and is in same position that its joints
            												  //End the game
                gameStatus = false;
            }
        }

        if (y[0] >= GlobalConfig.BOARD_HEIGHT) { // If head goes out of the boundaries end the game
        	gameStatus = false;
        }

        if (y[0] < 0) { // if y position is less than 0
        	gameStatus = false;
        }

        if (x[0] >= GlobalConfig.BOARD_WIDTH) { // If head goes out of the boundaries end the game
        	gameStatus = false;
        }

        if (x[0] < 0) { // if x position is less than 0
        	gameStatus = false;
        }
        
        if(!gameStatus) { // end timer if game has stopped
            timer.stop();
        }
    }

    private void randomApple() {

        int random = (int) (Math.random() * GlobalConfig.RAND_POS); // generate random number
        apple_x = ((random * GlobalConfig.DOT_SIZE)); // multiply it with dot size

        random = (int) (Math.random() * GlobalConfig.RAND_POS); // generate random number
        apple_y = ((random * GlobalConfig.DOT_SIZE));
    }

    
    // Every action performed
    public void actionPerformed(ActionEvent e) {

        if (gameStatus) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    
    // Simple class to get the keyboard input
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}