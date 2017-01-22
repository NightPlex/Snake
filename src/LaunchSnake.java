import java.awt.EventQueue;
import javax.swing.JFrame;

/*
 * Launch the snake game
 * 
 * 
 * */

public class LaunchSnake extends JFrame {

	// construction
    public LaunchSnake() {

        add(new SnakeBoard()); // add board to JFrame
        
        setResizable(false);
        pack();
        
        setTitle("Snake v2"); // Title
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    

    public static void main(String[] args) {
        
    	// run the game
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                JFrame ex = new LaunchSnake();
                ex.setVisible(true);                
            }
        });
    }
}