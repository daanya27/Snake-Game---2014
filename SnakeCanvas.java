import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import acm.program.*;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class snakeCanvas extends GraphicsProgram implements Runnable, KeyListener {

//	Defines screen width and height
	private final int BOX_HEIGHT = 15;
	private final int BOX_WIDTH = 15;
	private final int GRID_HEIGHT = 25;
	private final int GRID_WIDTH = 50;
	
//	Sets initial score to 0
	private int score = 0;
	
//	Sets initial direction to no direction AKA 0
	private int direction = Direction.NO_DIRECTION;
	
//	Creates snake and fruit points
	private LinkedList<Point> snake;
	private Point fruit;
	
//	Creates high score file
//	private String highScore = "";
	
//	Creates override thread to constantly loop in background and gG to be parameter for paint
	private Thread runThread;
	
//	Booleans dealing with menus
	private boolean isInMenu = true;
	private boolean isInDirections = false;
	private boolean isInEndGame = false;
	private boolean won = false;
	
//	Double buffering so program stops blinking 
	public void update(Graphics g) {
		Graphics dBuffGraphics;
		BufferedImage offscreen = null;
		Dimension dBuffDimension = this.getSize();
		offscreen = new BufferedImage(dBuffDimension.width, dBuffDimension.height, BufferedImage.TYPE_INT_ARGB);
		dBuffGraphics = offscreen.getGraphics();
		dBuffGraphics.setColor(this.getBackground());
		dBuffGraphics.fillRect(0, 0, dBuffDimension.width, dBuffDimension.height);
		dBuffGraphics.setColor(this.getForeground());
		paint(dBuffGraphics);
//		Put what was just made onto screen
		g.drawImage(offscreen, 0, 0, this);
	}
	
	public void paint(Graphics g) {
//		If nothing is running, start this current thread aka snakeCanvas
		if (runThread == null) {
//			Sets preferred size in case it fails in Applet
			this.setPreferredSize(new Dimension(1000, 500));
//			Tells snakeCanvas that it is its own key listener
			this.addKeyListener(this);
			runThread = new Thread(this);
			runThread.start();			
		}
		if (isInMenu == true) {
			drawMenu(g);
		}else if (isInDirections == true){
			drawDirections(g);
		}else if (isInEndGame == true) {
			drawEndScreen(g);
		}else{
//			Uses if statement so that snake is not reset every time paint is called in update
			if (snake == null) { 
				snake = new LinkedList<Point>();
				newSnake();
				placeFruit();
			}
//			if (highScore.equals("")) {
//				highScore = this.getHighScore();
//			}
			g.clearRect(0, 0, GRID_WIDTH * BOX_WIDTH + 5 , GRID_HEIGHT * BOX_HEIGHT + 150);
			drawGrid(g);
			drawSnake(g);
			drawFruit(g);
			drawScore(g);
		}
	}

//	Constantly runs in background
	@Override
	public void run() {
		while (true) {
			if (!isInMenu && !isInDirections && !isInEndGame) {
				move();
			}
			repaint();
//			Basically buffers program so it's not moving speed of light
			pause();
		}
	}
	
//	Draws menu
	public void drawMenu(Graphics g) {
		score = 0;
		Image menuImage = null;
		if (menuImage == null) {
			try {
				URL imagePath = snakeCanvas.class.getResource("snakeStartMenu.png");
				menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
			}catch(Exception e){
//				If image does not exist
				e.printStackTrace();
			}
		}
		g.drawImage(menuImage, 0, 0, 750, 500, this);
	}
	
	public void drawDirections(Graphics g) {
		Image directionsImage = null;
		if (directionsImage == null) {
			try {
				URL imagePath = snakeCanvas.class.getResource("snakeDirections.png");
				directionsImage = Toolkit.getDefaultToolkit().getImage(imagePath);
			}catch(Exception e){
//				If image does not exist
				e.printStackTrace();
			}
		}
		g.drawImage(directionsImage, 0, 0, 750, 500, this);
	}
	
	public void drawEndScreen(Graphics g) {
		if (won == true) {
			Image winImage = null;
			if (winImage == null) {
				try {
					URL imagePath = snakeCanvas.class.getResource("snakeYouWin.png");
					winImage = Toolkit.getDefaultToolkit().getImage(imagePath);
				}catch(Exception e){
//					If image does not exist
					e.printStackTrace();
				}
			}
			g.drawImage(winImage, 0, 0, 750, 500, this);
		}else if (won == false) {
			Image loseImage = null;
			if (loseImage == null) {
				try {
					URL imagePath = snakeCanvas.class.getResource("snakeYouLose.png");
					loseImage = Toolkit.getDefaultToolkit().getImage(imagePath);
				}catch(Exception e){
//					If image does not exist
					e.printStackTrace();
				}
			}
			g.drawImage(loseImage, 0, 0, 750, 500, this);
			g.setColor(Color.WHITE);
			Font myFont = new Font("Serif", Font.PLAIN, 130);
			g.setFont(myFont);
			g.drawString("" + score, 450, 380);
			g.setColor(Color.BLACK);
		}
	}
	
//	Draws grid
	public void drawGrid(Graphics g) {	
//		Draws outer rectangle of grid
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, GRID_WIDTH * BOX_WIDTH, GRID_HEIGHT * BOX_HEIGHT);
//		Draws vertical lines
		for (int x = BOX_WIDTH; x < GRID_WIDTH * BOX_WIDTH; x += BOX_WIDTH) {
			g.setColor(Color.BLACK);
			g.drawLine (x, 0, x, GRID_HEIGHT * BOX_HEIGHT);
		}
//		Draws horizontal lines
		for(int y = BOX_HEIGHT; y < GRID_HEIGHT * BOX_HEIGHT; y += BOX_HEIGHT) {
			g.setColor(Color.BLACK);
			g.drawLine (0, y, GRID_WIDTH * BOX_WIDTH, y);
		}
	}
	
//	Draws snake
	public void drawSnake(Graphics g) {
//		Makes snake green
		g.setColor(Color.GREEN);
		for (Point p : snake) {
			g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
//		Sets color back to black for the rest of the code
		g.setColor(Color.BLACK);
	}
	
	public void drawFruit(Graphics g) {
//		Creates red fruit; uses (x,y,n,n) because it needs to set fruit at correct increments of boxes, not just randomly on lines
		g.setColor(Color.RED);
		g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		g.setColor(Color.BLACK);
	}
	
//	Show score on screen
	public void drawScore(Graphics g) {
		g.drawString("Score: " + score, 0, GRID_HEIGHT * BOX_HEIGHT + 20);
//		g.drawString("High Score: " + highScore, 0, GRID_HEIGHT * BOX_HEIGHT + 33);
		g.drawString("The escape key will take you back to the menu, and then you may choose to resume your game.", 0, GRID_HEIGHT * BOX_HEIGHT + 50);
	}
	
	public void placeFruit() {
		Random random = new Random();
		int randomX = random.nextInt(GRID_WIDTH);
		int randomY = random.nextInt(GRID_HEIGHT);
		Point randomFruit = new Point(randomX, randomY);
		while (snake.contains(randomFruit)) {
			randomX = random.nextInt(GRID_WIDTH);
			randomY = random.nextInt(GRID_HEIGHT);
			randomFruit = new Point(randomX, randomY);
		}
		fruit = randomFruit;
	}
	
	public void newSnake() {
//		Generates default snake when game lost and resets score too
//		score = 0;
		snake.clear();
		snake.add(new Point(0, 2));
		snake.add(new Point(0, 1));
		snake.add(new Point(0, 0));
		direction = Direction.NO_DIRECTION;
	}
	
	public void resetScore() {
		score = 0;
	}
	
//	ONLY TO BE USED WHEN adding additional points, cannot be used as initial switch block
	public void switchCase(Point head, Point newPoint, Point addPoint) {
		switch (direction) {
		case Direction.NORTH:
			newPoint = new Point (head.x, head.y - 1);
			break;
		case Direction.SOUTH:
			newPoint = new Point (head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point (head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point (head.x + 1, head.y);
			break;
		}
		snake.push(addPoint);
	}

	public void move() {
//		Sets head point as the first point in the linked list (peekFirst)
		Point head = snake.peekFirst();
		Point newPoint = head;
//		Says that if direction changes, place new point in that direction
		switch (direction) {
		case Direction.NORTH:
			newPoint = new Point (head.x, head.y - 1);
			break;
		case Direction.SOUTH:
			newPoint = new Point (head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point (head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point (head.x + 1, head.y);
			break;
		}
//		Takes of last point so that it doesn't run into itself at where the tail used to be
		if (this.direction != Direction.NO_DIRECTION) {
			snake.remove(snake.peekLast());
		}
//		Specifies what to do if snake head hits fruit, wall. or itself
		if (newPoint.equals (fruit)) {
			score += 10;
			Point addPoint = (Point) newPoint.clone(); 
//			Calls move method again so it essentially adds two heads
			switchCase(head, newPoint, addPoint);
			switchCase(head, newPoint, addPoint);
			switchCase(head, newPoint, addPoint);
			placeFruit();
		} else if (newPoint.x < 0 || newPoint.x > GRID_WIDTH - 1) {
//			if snake goes out of bounds horizontally
			isInEndGame = true;
			won = false;
//			checkScore();
			newSnake();
			return;
		} else if (newPoint.y < 0 || newPoint.y > GRID_HEIGHT - 1) {
//			if snake goes out of bounds vertically
			isInEndGame = true;
			won = false;
//			checkScore();
			newSnake();
			return;
		} else if (snake.contains(newPoint)) {
//			if snake hits itself
			if (snake.size() > 3){
				isInEndGame = true;
				won = false;
//				checkScore();
			}
			newSnake();
			return;
		} else if (snake.size() == (GRID_WIDTH * GRID_HEIGHT)) {
//			if user wins game
			isInEndGame = true;
			won = true;
			newSnake();
			resetScore();
			return;
		}
//		If none of the above happens, then it will put another point ahead in the same direction
		snake.push(newPoint);	
	}

	public void pause() {
		try{
			Thread.currentThread();
			Thread.sleep(100);
		}catch(Exception e){
			e.printStackTrace();
		}
	}	

//	public String getHighScore() {
//		FileReader readFile = null;
//		BufferedReader reader = null;
//		try{
//			readFile = new FileReader("highScore.dat");
//			reader = new BufferedReader(readFile);
//			return reader.readLine();
//		}catch (Exception e){
//			return "Nobody: 0";
//		}finally{
//			try{
//				if (reader != null){
//					reader.close();
//				}
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//	}
	
//	public void checkScore() {
//		if (highScore.equals("")){
//			return;
//		}
//		if (score > (Integer.parseInt(highScore.split(":")[1]))){
////			User has set new record
//			String name = JOptionPane.showInputDialog("Congratulations, you set a new high score! Enter your name: ");
//			highScore = name + ":" + score;
////			Create file writer
//			File scoreFile = new File("highScore.dat");
////			Checks if score file already exists or not
//			if (!scoreFile.exists()) {
//				try {
//					scoreFile.createNewFile();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			FileWriter writeFile = null;
//			BufferedWriter writer = null;
//			try {
//				writeFile = new FileWriter(scoreFile);
//				writer = new BufferedWriter(writeFile);
//				writer.write(this.highScore);
//			}catch (Exception e){
////				Catch errors
//			}finally{
//				try{
//					if (writer != null) {
//						writer.close();
//					}
//				}catch (Exception e){
//					
//				}
//			}
//		}
//	}
	
//	Whenever arrow keys pressed, it takes precedence over rest of code
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()){
//		(VK = Virtual key)
		case KeyEvent.VK_UP:
			if (!isInMenu && !isInDirections && !isInEndGame && direction != Direction.SOUTH) {
				direction = Direction.NORTH;
			}
		case KeyEvent.VK_DOWN:
			if (!isInMenu && !isInDirections && !isInEndGame && direction != Direction.NORTH) {
				direction = Direction.SOUTH;
			}
			break;
		case KeyEvent.VK_LEFT:
			if (!isInMenu && !isInDirections && !isInEndGame && direction != Direction.EAST){
				direction = Direction.WEST;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (!isInMenu && !isInDirections && !isInEndGame && direction != Direction.WEST) {
				direction = Direction.EAST;
			}
			break;
		case KeyEvent.VK_ENTER:
			if (isInMenu == true) {
				isInMenu = false;
				repaint();
			}
			if (isInDirections == true) {
				isInDirections = false;
				repaint();
			}
			if (isInEndGame == true) {
				isInEndGame = false;
				won = false;
				isInMenu = true;
				repaint();
			}
			break;
		case KeyEvent.VK_SPACE:
			if (isInMenu == true) {
				isInMenu = false;
				isInDirections = true;
				repaint();
			}
			break;
		case KeyEvent.VK_ESCAPE:
			isInMenu = true;
			break;
		}
	}
}
