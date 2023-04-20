import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.ArrayList;
import java.util.Random;

import java.awt.event.KeyAdapter;              
import java.awt.event.KeyEvent;


@SuppressWarnings("serial")
public class SnakeBoard extends JPanel {

	public static void main (String[]args) {

		JFrame frame = new JFrame("Snake!");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SnakeBoard());
		frame.pack();
		frame.setVisible(true);

	}
	Random rand = new Random();
	
	private int DELAY = 60;
	private int keysPressedInFrame = 0;
	
	private int foodAvailable = 1;
	private int foodReplacementRate = 1;
	private int foodToSpawn = foodAvailable;
	private int snekGrowthRate = 5;
	
	private int squareSize = 25;
	private int squareSeparation = squareSize * 12/10;
	private int squareIndent = squareSize/10;
	
	private int gridWidth;
	private int gridHeight;
	private int[][] gameGridX;
	private int[][] gameGridY;
	private ArrayList<ArrayList<Integer>> gridStatus = new ArrayList<>();
	private int openSpaces = 0;
	
	private int headX;
	private int headY;
	private int snekLength;
	private boolean readyToGrow = false;
	private int snekToGrow = 0;
	
	private boolean dead = false;
	private boolean resetGame = false;
	
	private char queueDirection = 'n';
	private char newDirection = 'n';
	private char direction = 'n';

	private final Color background = new Color(rand.nextInt(100) + 25, rand.nextInt(100) + 25, rand.nextInt(100) + 25);
	private final Color border = new Color(rand.nextInt(155) + 100, rand.nextInt(155) + 100, rand.nextInt(155) + 100);
	private final Color body = new Color(rand.nextInt(155) + 100, rand.nextInt(155) + 100, rand.nextInt(155) + 100);
	private final Color food = new Color(rand.nextInt(155) + 100, rand.nextInt(155) + 100, rand.nextInt(155) + 100);
	
	ActionListener timerListener = new TimerListener();
	Timer timer = new Timer(DELAY, timerListener);
	JButton keyboardInput = new JButton();
	public SnakeBoard()
	{
		int initWidth = 800;
		int initHeight = 500;
		setPreferredSize(new Dimension(initWidth, initHeight));
		this.setDoubleBuffered(true);
		
		keyboardInput.addKeyListener(new KeyInput());
		keyboardInput.setOpaque(false);
		keyboardInput.setContentAreaFilled(false);
		keyboardInput.setBorderPainted(false);
		
		this.add(keyboardInput);
		

		gridHeight = initHeight / squareSeparation;
		gridWidth = initWidth / squareSeparation;

		for (int i = 0; i < gridHeight; i++) {
			gridStatus.add(new ArrayList<Integer>());
			for (int j = 0; j < gridWidth; j++) {
				gridStatus.get(i).add(0);
				openSpaces++;
			}
		}

		headX = 2;
		headY = 2;
		gridStatus.get(headY).set(headX, 1);
		snekLength = 1;
		openSpaces -= snekLength;

		spawnFood();

		timer.start();
	}

	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		gridHeight = getHeight() / squareSeparation;
		gridWidth = getWidth() / squareSeparation;

		gameGridX = new int[gridHeight][gridWidth];
		gameGridY = new int[gridHeight][gridWidth];
		
		snekUpdate();
		spawnFood();
		
		direction = newDirection;
		
		if (keysPressedInFrame > 1) {
			newDirection = queueDirection;
			queueDirection = 'n';
		}
		
		if (dead) {
			newDirection = 'n';
		}

		g.setColor(background);
		g.fillRect(0, 0, width, height);
		g.setColor(body);
		
		for (int i = 0; i < gridHeight; i++) {
			int xCoordinate = (getWidth() % squareSeparation) / 2;
			if (gridStatus.size() - 1 < i) {
				gridStatus.add(new ArrayList<Integer>());
			}
			int yCoordinate = (getHeight() % squareSeparation) / 2 + (squareSeparation * i);
			for (int j = 0; j < gridWidth; j++) {

				if (gridStatus.get(i).size() - 1 < j) {
					gridStatus.get(i).add(0);
				}

				gameGridX[i][j] = xCoordinate;
				gameGridY[i][j] = yCoordinate;

				if (i == 0 || j == 0 || i == gridHeight - 1|| j == gridWidth - 1) {
					g.setColor(border);
					g.fillRect(gameGridX[i][j], gameGridY[i][j], squareSeparation, squareSeparation);
				}
				
				if (gridStatus.get(i).get(j) > 0) {
					if (!dead) {
						g.setColor(body);
					} else {
						g.setColor(Color.red);
					}
					g.fillRect(gameGridX[i][j] + squareIndent, gameGridY[i][j] + squareIndent, squareSize, squareSize);
						
				} else if (gridStatus.get(i).get(j) == -1 && i < gridHeight - 1 && j < gridWidth - 1) {
					g.setColor(food);
					g.fillRect(gameGridX[i][j] + squareIndent, gameGridY[i][j] + squareIndent, squareSize, squareSize);
				}

				xCoordinate += squareSeparation;

			}
		}
		
		Toolkit.getDefaultToolkit().sync();
		keysPressedInFrame = 0;
		
		if (resetGame) {
			resetGame = false;
			timer.stop();
			newGame();
			
		}
		if (dead) {
			resetGame = true;
		}
	}

	public void moveSnek(int oldHeadY, int oldHeadX) {

		int Y = oldHeadY;
		int X = oldHeadX;
		int position = 1;

		while (gridStatus.get(Y).get(X) > 0) {

			if (Y != gridHeight - 1) {
				if (gridStatus.get(Y + 1).get(X) == position + 1) {
					gridStatus.get(Y).set(X, position + 1);
					Y += 1;
					position += 1;
				} 
			}
			if (Y != 0){
				if (gridStatus.get(Y - 1).get(X) == position + 1) {
					gridStatus.get(Y).set(X, position + 1);
					Y -= 1;
					position += 1;
				}
			} 
			if (X != gridWidth - 1) {
				if (gridStatus.get(Y).get(X + 1) == position + 1) {
					gridStatus.get(Y).set(X, position + 1);
					X += 1;
					position += 1;
				}
			} 
			if (X != 0) { 
				if (gridStatus.get(Y).get(X - 1) == position + 1) {
					gridStatus.get(Y).set(X, position + 1);
					X -= 1;
					position += 1;
				}

			}

			if (position == snekLength){
				gridStatus.get(Y).set(X, 0);
			}

		}

		if(snekToGrow > 0 && readyToGrow == true) {
			gridStatus.get(Y).set(X, position + 1);
			position++;
			snekLength++;
			snekToGrow--;
			openSpaces--;
			if (snekToGrow == 0) {
				readyToGrow = false;
			}
		} else if (snekToGrow > 0) {
			readyToGrow = true;
		}

	}

	public void snekUpdate() {
		
		switch(newDirection) {
		case 'u':
			
				int oldHeadY = headY;
				headY -= 1;
				
				
				if (gridStatus.get(headY).get(headX) > 0) {
					dead = true;
				} else if (gridStatus.get(headY).get(headX) == -1) {
					foodToSpawn += foodReplacementRate;
					snekToGrow += snekGrowthRate;
				}
				
				if (!dead) {
					gridStatus.get(headY).set(headX, gridStatus.get(headY + 1).get(headX));
					moveSnek(oldHeadY, headX);
				}
				
				if (headY == 0) {
					dead = true;
				}
			
			break;

		case 'd':
				
				oldHeadY = headY;
				headY += 1;

				if (gridStatus.get(headY).get(headX) > 0) {
					dead = true;
				} else if (gridStatus.get(headY).get(headX) == -1) {
					foodToSpawn += foodReplacementRate;
					snekToGrow += snekGrowthRate;
				}
				
				if (!dead) {
				gridStatus.get(headY).set(headX, gridStatus.get(headY - 1).get(headX));
				moveSnek(oldHeadY, headX);
				}
				
				if (headY == gridHeight - 1) {
					dead = true;
				}
				
			break;
			
		case 'l':
			
				int oldHeadX = headX;
				headX -= 1;
				
				if (gridStatus.get(headY).get(headX) > 0) {
					dead = true;
				} else if (gridStatus.get(headY).get(headX) == -1) {
					foodToSpawn += foodReplacementRate;
					snekToGrow += snekGrowthRate;
				}
				
				if (!dead) {
					gridStatus.get(headY).set(headX, gridStatus.get(headY).get(headX + 1));
					moveSnek(headY, oldHeadX);
				}
				
				if (headX == 0) {
					dead = true;
				}
				
			break;
		case 'r':
		
				oldHeadX = headX;
				headX += 1;
				
				if (gridStatus.get(headY).get(headX) > 0) {
					dead = true;
				} else if (gridStatus.get(headY).get(headX) == -1) {
					foodToSpawn += foodReplacementRate;
					snekToGrow += snekGrowthRate;
				}
				
				if (!dead) {
					gridStatus.get(headY).set(headX, gridStatus.get(headY).get(headX - 1));
					moveSnek(headY, oldHeadX);
				}
				
				if (headX == gridWidth - 1) {
					dead = true;
				}
				
			break;
		}

	}

	public void spawnFood() {

		while (foodToSpawn > 0) {

			int randomY = rand.nextInt(gridHeight - 2) + 1;
			int randomX = rand.nextInt(gridWidth - 2) + 1;

			if (gridStatus.get(randomY).get(randomX) == 0) {
				gridStatus.get(randomY).set(randomX, -1);
				
				foodToSpawn--;
				openSpaces--;
			}
			
		}

	}

	public void newGame() {
		
		dead = false;
		newDirection = 'n';
		openSpaces = 0;
		
		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				gridStatus.get(i).set(j, 0);
				openSpaces++;
			}
		}
		
		foodToSpawn = foodAvailable;
		headX = 2;
		headY = 2;
		gridStatus.get(headY).set(headX, 1);
		snekLength = 1;
		openSpaces -= snekLength;
		snekToGrow = 0;

		spawnFood();
		
		timer.start();
	}

	public class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			
			repaint();
			
		}
	}
		
	public class KeyInput extends KeyAdapter { 
		
		public void keyPressed(KeyEvent e) {
		
			switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				if (keysPressedInFrame == 0) {
					newDirection = 'u';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'u';
				}
				keysPressedInFrame++;
				break;
			case KeyEvent.VK_UP:
				if (keysPressedInFrame == 0) {
					newDirection = 'u';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'u';
				}
				keysPressedInFrame++;
				break;
			case KeyEvent.VK_S: 
				if (keysPressedInFrame == 0) {
					newDirection = 'd';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'd';
				}
				keysPressedInFrame++;
				break;
			case KeyEvent.VK_DOWN:
				if (keysPressedInFrame == 0) {
					newDirection = 'd';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'd';
				}
				keysPressedInFrame++;
				break;
			case KeyEvent.VK_A: 
				if (keysPressedInFrame == 0) {
					newDirection = 'l';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'l';
				}
				keysPressedInFrame++;
				break;
			case KeyEvent.VK_LEFT:
				if (keysPressedInFrame == 0) {
					newDirection = 'l';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'l';
				}
				keysPressedInFrame++;
				break;
			case KeyEvent.VK_D: 
				if (keysPressedInFrame == 0) {
					newDirection = 'r';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'r';
				}
				keysPressedInFrame++;
				break;
			case KeyEvent.VK_RIGHT:
				if (keysPressedInFrame == 0) {
					newDirection = 'r';
				} else if (keysPressedInFrame >= 1){
					queueDirection = 'r';
				}
				keysPressedInFrame++;
				break;
			}
			
			if ((direction == 'd' && newDirection == 'u' && snekLength != 1)
					 || (direction == 'u' && newDirection == 'd' && snekLength != 1)
					 || (direction == 'l' && newDirection == 'r' && snekLength != 1)
					 || (direction == 'r' && newDirection == 'l' && snekLength != 1)) {
						
						newDirection = direction;
			}
		}
	}
}