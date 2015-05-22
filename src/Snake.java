//**************************************************
// A-number : A01872123
// Problem : HW13
// Description : A game called "Snake"
//**************************************************


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class Snake extends JFrame implements ActionListener, ItemListener,                                             
                                             KeyListener, MouseListener
{

	private static final int WIDTH = 736;
	private static final int HEIGHT = 704;
	
	private Container pane;
	
	private JLabel[] snake = new JLabel[432];
	private JLabel food;
	private JLabel[] bomb = new JLabel[4];
	private JLabel boom;
	private JButton cMode, wMode, bMode;
	private ButtonGroup SpeedGroup;
	
	private final int moveDELAY = 150;
	private final int bombDELAY = 10000;
	private final int DELAY2 = 1/100;

	Timer timerMove;
	Timer timerCheck;
	Timer timerBomb;

	private JMenuBar menuMB = new JMenuBar();
	private JMenu gameM, speedM, helpM;
	private JMenuItem exitI, newgameI;
	private JMenuItem ssI, smI, sfI, helpI;
	private JRadioButton ssR, smR, sfR;
	
	private JTextField scoreTF;
	Font bigFont = new Font("Serif", Font.PLAIN, 20);
	
	char direction;
	int xFood, yFood;
	int[] xBomb = new int[4]; 
	int[] yBomb = new int[4];
	int[] x = new int[432];
	int[] y = new int[432];
	int gameScore, count, bombCount;
	int gameLevel = 0;
	int gamePause = 0;
	int gameStop = 0;
	int snakeSize;
	
	private JLabel sBG = new JLabel(new ImageIcon("image/sBG.jpg"));
	private JLabel BG = new JLabel(new ImageIcon("image/BG1.jpg"));
	private JLabel BG2 = new JLabel(new ImageIcon("image/BG2.jpg"));
	private JLabel TOP = new JLabel(new ImageIcon("image/top.png"));
	private JLabel PauseBG = new JLabel(new ImageIcon("image/pauseBG.png"));
	private JLabel OverBG = new JLabel(new ImageIcon("image/overBG.png"));
	private JLabel WinBG = new JLabel(new ImageIcon("image/winBG.png"));
	
	String str = "In this game, you need to control the snake to " +
			"eat the food, and get the highest score. You can not hit " +
			"the wall, and yourself.";
	
	public Snake()
	{
		super("Snake Game");
		
		timerMove = new Timer(moveDELAY, new TimerEventHandler());
		timerBomb = new Timer(bombDELAY, new TimerEventHandler3());
		timerCheck = new Timer(DELAY2, new TimerEventHandler2());
		
		pane = getContentPane();
		pane.setLayout(null);
		pane.setBackground(Color.LIGHT_GRAY);
		
		BG.setSize(730,550);
		BG2.setSize(730,550);
		sBG.setSize(730,750);
		PauseBG.setSize(730,550);
		TOP.setSize(730,100);
		OverBG.setSize(730,550);
		WinBG.setSize(730,550);
		
		setJMenuBar(menuMB);
		setMenu();
		
		scoreTF = new JTextField();
		scoreTF.setFont(bigFont);
		scoreTF.setEditable(false);
		scoreTF.setSize(100,30);
		
		food = new JLabel(new ImageIcon("image/apple.png"));
		food.setSize(30,30);
		
		boom = new JLabel(new ImageIcon("image/boom.png"));
		boom.setSize(90,90);
		
		cMode = new JButton(new ImageIcon("image/classic.png"));
		cMode.setSize(200,200);
		cMode.setContentAreaFilled(false);
		cMode.setBorderPainted(false);
		cMode.addActionListener(this);
		
		wMode = new JButton(new ImageIcon("image/hardMode.png"));
		wMode.setSize(200,200);
		wMode.setContentAreaFilled(false);
		wMode.setBorderPainted(false);
		wMode.addActionListener(this);
		
		bMode = new JButton(new ImageIcon("image/bombMode.png"));
		bMode.setSize(200,200);
		bMode.setContentAreaFilled(false);
		bMode.setBorderPainted(false);
		bMode.addActionListener(this);
		
		pane.add(PauseBG);
		pane.add(OverBG);
		pane.add(WinBG);
		pane.add(boom);
		
		for(int i=0; i<432; i++)
		{
			snake[i] = new JLabel(new ImageIcon("image/android.png"));
			snake[i].setSize(30,30);
			snake[i].setLocation(1000,1000);
			pane.add(snake[i]);
		}
		
		for(int i=0; i<4; i++)
		{
			bomb[i] = new JLabel(new ImageIcon("image/bomb.png"));
			bomb[i].setSize(30,30);
			bomb[i].setLocation(1000,1000);
			pane.add(bomb[i]);
		}
		

		pane.add(food);
		pane.add(cMode);
		pane.add(wMode);
		pane.add(bMode);
		pane.add(scoreTF);
		pane.add(TOP);
		pane.add(sBG);
		pane.add(BG);
		pane.add(BG2);
		
		start();
		//classicMode();
		
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	public void setMenu()
	{
		gameM = new JMenu("Game");
		menuMB.add(gameM);
		speedM = new JMenu("Speed");
		menuMB.add(speedM);
		helpM = new JMenu("Help");
		menuMB.add(helpM);
			
		newgameI = new JMenuItem("New Game");
		gameM.add(newgameI);
		newgameI.addActionListener(this);
		
		exitI = new JMenuItem("Exit");
		gameM.add(exitI);
		exitI.addActionListener(this);
		
		helpI = new JMenuItem("Help");
		helpM.add(helpI);
		helpI.addActionListener(this);
		
		ssR = new JRadioButton("Slow");
		speedM.add(ssR);
		ssR.addItemListener(this);
		
		smR = new JRadioButton("Normal");
		speedM.add(smR);
		smR.addItemListener(this);
		
		sfR = new JRadioButton("Fast");
		speedM.add(sfR);
		sfR.addItemListener(this);
		
		SpeedGroup = new ButtonGroup();
		SpeedGroup.add(ssR);
		SpeedGroup.add(smR);
		SpeedGroup.add(sfR);
		
	}
	
	
	public void start()
	{
		removeAll();
		gameLevel = 0;
		gameStop = 0;
		gamePause = 0;
		
		sBG.setLocation(0,0);
		cMode.setLocation(30,200);
		wMode.setLocation(260,200);
		bMode.setLocation(490,200);
		
		repaint();
	}
	
	//Pause the game
	public void pauseGame()
	{
		if(gameLevel != 0)
		{
			PauseBG.setLocation(0,100);
			timerMove.stop();
			timerCheck.stop();
			timerBomb.stop();
		}
	}
	
	//continue the game
	public void continueGame()
	{
		PauseBG.setLocation(1000,1000);
		timerMove.start();
		timerCheck.start();
		if(gameLevel == 3)
			timerBomb.start();
	}
	
	//remove all the element in the game board
	public void removeAll()
	{
		BG.setLocation(1000,1000);
		BG2.setLocation(1000,1000);
		sBG.setLocation(1000,1000);
		PauseBG.setLocation(1000,1000);
		TOP.setLocation(1000,1000);
		OverBG.setLocation(1000,1000);
		WinBG.setLocation(1000,1000);
		boom.setLocation(1000,1000);
		
		for(int i=0; i<432; i++)
		{
			snake[i].setLocation(1000,1000);
		}
		
		for(int i=0; i<4; i++)
		{
			bomb[i].setLocation(1000,1000);
		}
		
		food.setLocation(1000,1000);
		
		scoreTF.setLocation(1000,1000);
		
		cMode.setLocation(1000,1000);
		wMode.setLocation(1000,1000);
		bMode.setLocation(1000,1000);
		
		timerMove.stop();
		timerCheck.stop();
		timerBomb.stop();
		this.removeKeyListener(this);
	}
	
	//Classic game mode
	public void classicMode()
	{
		removeAll();
		
		gameLevel = 1;
		count = 0;
		gameScore = 0;
		gameStop = 0;
		snakeSize = 3;
		scoreTF.setText(String.format("%d", gameScore));
		
		timerMove.setDelay(150);
		
		TOP.setLocation(0,0);
		BG.setLocation(0,100);
		scoreTF.setLocation(110,62);
		
		x[0] = 305;
		y[0] = 345;
		
		for(int i =1; i <3; i++)
		{
			x[i] = x[i-1] - 30;
			y[i] = y[i-1];
		}
		
		snake[0].setLocation(x[0],y[0]);
		snake[1].setLocation(x[1],y[1]);
		snake[2].setLocation(x[2],y[2]);		
	
		randomFood();
		
		direction = 'R';
		
		repaint();
		timerMove.start();
		timerCheck.start();
		
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		this.addMouseListener(this);
	}
	
	//Hard game mode
	public void hardMode()
	{
		removeAll();
		
		gameLevel = 2;
		count = 0;
		gameScore = 0;
		gameStop = 0;
		snakeSize = 3;
		scoreTF.setText(String.format("%d", gameScore));
		
		timerMove.setDelay(150);
		
		TOP.setLocation(0,0);
		BG2.setLocation(0,100);
		scoreTF.setLocation(110,62);
		
		x[0] = 305;
		y[0] = 345;
		
		for(int i =1; i <3; i++)
		{
			x[i] = x[i-1] - 30;
			y[i] = y[i-1];
		}
		
		snake[0].setLocation(x[0],y[0]);
		snake[1].setLocation(x[1],y[1]);
		snake[2].setLocation(x[2],y[2]);		
	
		randomFood();
		
		direction = 'R';
		
		repaint();
		timerMove.start();
		timerCheck.start();
		
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		this.addMouseListener(this);
	}
	
	//Bomb game mode
	public void bombMode()
	{
		removeAll();
		bombCount = 0;
		gameLevel = 3;
		count = 0;
		gameScore = 0;
		gameStop = 0;
		snakeSize = 3;
		scoreTF.setText(String.format("%d", gameScore));
		
		timerMove.setDelay(150);
		
		TOP.setLocation(0,0);
		BG.setLocation(0,100);
		scoreTF.setLocation(110,62);
		
		x[0] = 305;
		y[0] = 345;
		
		for(int i =1; i <3; i++)
		{
			x[i] = x[i-1] - 30;
			y[i] = y[i-1];
		}
		
		snake[0].setLocation(x[0],y[0]);
		snake[1].setLocation(x[1],y[1]);
		snake[2].setLocation(x[2],y[2]);		
	
		randomFood();
		randomBomb();
		
		
		direction = 'R';
		
		repaint();
		timerMove.start();
		timerCheck.start();
		timerBomb.start();
		
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		this.addMouseListener(this);
	}
	
	//Make the snake move
	public void move()
	{
		int size = count + 2;
		int x1 = x[0];
		int y1 = y[0];
		
		if(direction == 'R')
    	{
			x[0] = x[0] + 30;
    	}
    	
    	if(direction == 'L')
    	{
    		x[0] = x[0] - 30;
    	}
    	
    	if(direction == 'U')
    	{
    		y[0] = y[0] - 30;
    	}
    	
    	if(direction == 'D')
    	{
    		y[0] = y[0] + 30;
    	}
    	
    	checkWin();
    	
		if(checkEnd())
    	{
			x[0] = x1;
			y[0] = y1;
    		gameOver();
    	}
		else
		{
			for(int i = size; i>0; i--)
			{
				x[i] = x[i-1];
				y[i] = y[i-1];
				snake[i].setLocation(x[i],y[i]);
			}
			
			if(count > -2)
			{
				x[1] = x1;
				y[1] = y1;
				
				snake[1].setLocation(x[1],y[1]);
			}
			
			snake[0].setLocation(x[0],y[0]);
			
    		repaint();
		}
	}
	
	//Score
	public void score()
	{
		if(gameLevel == 1)
		{
			gameScore = 30 * count;
			scoreTF.setText(String.format("%d", gameScore));
		}
		if(gameLevel == 2)
		{
			gameScore = 60 * count;
			scoreTF.setText(String.format("%d", gameScore));
		}
		if(gameLevel == 3)
		{
			gameScore = 30 * count;
			if(gameScore < 0)
				gameScore = 0;
			scoreTF.setText(String.format("%d", gameScore));
		}
		
	}
	
	//Food control
	public void randomFood()
	{
		boolean check = true;
		int xtemp=0, ytemp=0;
		
		if(gameLevel == 1 || gameLevel == 3)
		{
			while(check)
			{
				check = false;
	
				xtemp = ((int)(Math.random() * 23) +1) * 30 -25;
				ytemp = ((int)(Math.random() * 16) +1) * 30 +75;
			     
				if (xtemp != xFood && ytemp != yFood)
				{
				    for(int i=0; i<=(count+2);i++)
				    {
					   	if(xtemp == x[i] && ytemp == y[i])
					   		check = true;
				    }
				}
				
				if(gameLevel == 3)
				{
					for(int i=0; i<4;i++)
				    {
					   	if(xtemp == xBomb[i] && ytemp == yBomb[i])
					   		check = true;
				    }
				}
			}
		}
		else if(gameLevel == 2)
		{
			while(check)
			{
				check = false;
	
				xtemp = ((int)(Math.random() * 23) +1) * 30 -25;
				ytemp = ((int)(Math.random() * 16) +1) * 30 +75;
			     
				if (xtemp != xFood && ytemp != yFood)
				{
				    for(int i=0; i<=(count+2);i++)
				    {
					   	if(xtemp == x[i] && ytemp == y[i])
					   		check = true;
				    }
				}
				
				if(xtemp < 5 || xtemp > 695)
					check = true;
			   	if(ytemp < 105 || ytemp > 615)
			   		check = true;
			   	if(xtemp ==95 && ytemp< 375)
			   		check = true;
			   	if(xtemp ==605 && ytemp> 350)
			   		check = true;
			   	if(xtemp < 275 && ytemp == 525)
			   		check = true;
				if(xtemp > 425 && ytemp == 195)
					check = true;
			}
		}
		
	    xFood = xtemp;
	    yFood = ytemp;

	    food.setLocation(xFood,yFood);
		
		repaint();
	}
	
	public void randomBomb()
	{
		boolean check;
		int xtemp =0, ytemp=0;
		
		if(bombCount>=0 && bombCount<= 3)
		{
			for (int i =0; i<=bombCount; i++)
			{
				check = true;
				while(check)
				{
					check = false;
		
					xtemp = ((int)(Math.random() * 23) +1) * 30 -25;
					ytemp = ((int)(Math.random() * 16) +1) * 30 +75;
				     
					if (xtemp != xFood && ytemp != yFood)
					{
					    for(int j=0; j<=(count+2);j++)
					    {
						   	if(xtemp == x[j] && ytemp == y[j])
						   		check = true;
					    }
					}
				}
	
	
			    xBomb[i] = xtemp;
			    yBomb[i] = ytemp;
	
			    bomb[i].setLocation(xBomb[i],yBomb[i]);
			}

			repaint();
		}
	}
	
	public void hitBomb()
	{
		int size = count + 2;
		
		for(int i= size; i> (size-2); i--)
		{
			snake[i].setLocation(1000,1000);
		}
			
		count = count - 2;
		
		for(int i = 0; i<=bombCount; i++)
		{
			if(xBomb[i] == x[0] && yBomb[i] == y[0])
			{
				xBomb[i] = 1000;
				yBomb[i] = 1000;
				bomb[i].setLocation(xBomb[i],yBomb[i]);
			}
		}
		
		boom.setLocation(x[0]-30,y[0]-30);
		score();
		repaint();
	}
	
	
	public boolean checkEnd()
	{
		boolean checkEnd = false;

		if(gameLevel == 1 || gameLevel == 3)
		{
			for(int i=1; i<=(count+2);i++)
		    {
			   	if(x[0] == x[i] && y[0] == y[i])
			   		checkEnd = true;
		    }
			
			if(x[0] < 5 || x[0] > 695)
				checkEnd = true;
		   	else if(y[0] < 105 || y[0] > 615)
		   		checkEnd = true;
		}
		else if(gameLevel == 2)
		{
			for(int i=1; i<=(count+2);i++)
		    {
			   	if(x[0] == x[i] && y[0] == y[i])
			   		checkEnd = true;
		    }
			
			if(x[0] < 5 || x[0] > 695)
				checkEnd = true;
		   	else if(y[0] < 105 || y[0] > 615)
		   		checkEnd = true;
		   	else if(x[0] ==95 && y[0]< 375)
				checkEnd = true;
		   	else if(x[0] ==605 && y[0]> 350)
				checkEnd = true;
		   	else if(x[0] < 275 && y[0] == 525)
				checkEnd = true;
			else if(x[0] > 425 && y[0] == 195)
				checkEnd = true;
		}
		
		return checkEnd;
	}
	
	public void gameOver()
	{
		gameStop = 1;
		
		timerMove.stop();
		timerCheck.stop();
		timerBomb.stop();
		removeKeyListener(this);

		OverBG.setLocation(0,100);
		repaint();
	}
	
	public void winGame()
	{
		timerMove.stop();
		timerCheck.stop();
		removeKeyListener(this);
		
		WinBG.setLocation(0,100);
		repaint();
	}
	
	public void checkWin()
	{
		if(gameLevel == 1)
		{
			if(count == 428)
				winGame();
		}
		else if(gameLevel == 2)
		{
			if(count == 428)
				winGame();
		}
		else if(gameLevel == 3)
		{
			if(count == 429)
				winGame();
		}
	}
	
	public double FindRadius(int x, int y, int xC, int yC)
	{
		double radius;
		
		radius = Math.sqrt(Math.pow((x - xC), 2.0)+Math.pow((y - yC), 2.0));
		
		return radius;
	}
	
	public int checkButton(int x, int y)
	{
		int num = 0;

		if(gamePause ==0 && gameLevel !=0)
		{
			if(FindRadius(x,y,476,100) <= 34)
			{
				num = 1;
			}
			else if(FindRadius(x,y,574,100) <= 34)
			{
				num = 2;
			}
			else if(FindRadius(x,y,678,100) <= 34)
			{
				num = 3;
			}
		}
		
		if(gamePause == 1 || gameStop == 1)
		{
			if(FindRadius(x,y,217,503) <= 49)
			{
				if(gameStop == 1)
					num = 7;
				else 
					num = 4;
			}
			else if(FindRadius(x,y,364,503) <= 49)
			{
				num = 5;
			}
			else if(FindRadius(x,y,517,503) <= 49)
			{
				num = 6;
			}
		}
		
		return num;
	}

	private class TimerEventHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	move();
        	boom.setLocation(1000,1000);
		   	repaint();
		   	
        	if(x[0] == xFood && y[0] == yFood)
           	{
           		count++;
           		score();
           		randomFood();
           		move();
           	}
        	

        	int size = count + 3;
		   	for(int i=0; i<=bombCount;i++)
        	{
        		if(x[0] == xBomb[i] && y[0] == yBomb[i])
        		{
        			if(size <= 2)
        			{
        				boom.setLocation(x[0]-30,y[0]-30);
        				repaint();
        				gameOver();
        			}
        			else
        			{
        				hitBomb();
        				move();
        			}
        		}
        	}

		   
        	
        }
    }
		 
	private class TimerEventHandler2 implements ActionListener
    {
		public void actionPerformed(ActionEvent e)
		{
			checkEnd();
			
			if(gameM.isSelected()||speedM.isSelected()||helpM.isSelected())
			{
				if(gamePause == 0)
				{
					pauseGame();
					gamePause = 1;
				}
			}
			
		   	if(x[0] == xFood && y[0] == yFood)
		   	{
		   		count++;
		   		score();
		   		randomFood();
		   		move();
		   	}
		   	
		  
		   	int size = count + 3;
		   	for(int i=0; i<=bombCount;i++)
        	{
        		if(x[0] == xBomb[i] && y[0] == yBomb[i])
        		{
        			if(size <= 2)
        			{
        				boom.setLocation(x[0]-30,y[0]-30);
        				repaint();
        				gameOver();
        			}
        			else
        			{
        				hitBomb();
        				move();
        			}
        		}
        	}
		}
    }
	
	private class TimerEventHandler3 implements ActionListener
    {
       public void actionPerformed(ActionEvent e)
       {
       		if(gameLevel == 3)
       		{

       			if(count >=30)
   	   			{
   	   				bombCount = 1;
   	       		}
       			if(count >=60)
   	   			{
   	   				bombCount = 2;
   	       		}
       			if(count >=120)
   	   			{
   	   				bombCount = 3;
   	       		}
       			
       			randomBomb();
       		}
       }
    }
		 
	public void keyPressed(KeyEvent ke) 
	{
		if(gamePause == 0)
		{
			if (ke.getKeyCode() == KeyEvent.VK_UP) 
			{
				if(direction != 'D' && direction != 'U')
				{
					direction ='U';
					move();
				}
			}
			else if (ke.getKeyCode() == KeyEvent.VK_DOWN) 
			{
				if(direction != 'U' && direction != 'D')
				{
					direction ='D';
					move();
				}
			} 
			else if (ke.getKeyCode() == KeyEvent.VK_LEFT) 
			{
				if(direction != 'R' && direction != 'L')
				{
					direction ='L';
					move();
				}
			}
			else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) 
			{
				if(direction != 'L' && direction != 'R')
				{
					direction ='R';
					move();
				}
			}
		}
			
			
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) 
		{
			if(gamePause == 0)
			{
				pauseGame();
				gamePause = 1;
			}
			else if(gamePause == 1)
			{
				continueGame();
				gamePause = 0;
			}
		}
		 
			
	}
	
	public void keyReleased(KeyEvent arg0) {}

	
	public void keyTyped(KeyEvent arg0) {}
	
	
	public void mousePressed(MouseEvent e) 
	{
		if(checkButton(e.getX(),e.getY()) == 1)
		{
			if(timerMove.getDelay() <= 1000)
				timerMove.setDelay(timerMove.getDelay() + 50);
		}
		else if(checkButton(e.getX(),e.getY()) == 2)
		{
			if(timerMove.getDelay() >= 50)
				timerMove.setDelay(timerMove.getDelay() - 30);
		}
		else if(checkButton(e.getX(),e.getY()) == 3)
		{
			if(gamePause == 0)
			{
				pauseGame();
				gamePause = 1;
			}
		}
		else if(checkButton(e.getX(),e.getY()) == 4)
		{
			if(gamePause == 1)
			{
				continueGame();
				gamePause = 0;
			}
		}
		else if(checkButton(e.getX(),e.getY()) == 5)
		{
			start();
		}
		else if(checkButton(e.getX(),e.getY()) == 6)
		{
			System.exit(0);
		}
		else if(checkButton(e.getX(),e.getY()) == 7)
		{
			if(gameLevel == 1)
				classicMode();
			else if(gameLevel == 2)
				hardMode();
			else if(gameLevel == 3)
				bombMode();
		}
	
		
	}
	
	public void mouseClicked(MouseEvent e) 
	{
	}
	
	public void mouseReleased(MouseEvent e) 
	{
	}
	
	public void mouseEntered(MouseEvent e) {}


	public void mouseExited(MouseEvent e) {}

	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == newgameI)
		{
			start();
		}	
		else if(e.getSource() == exitI)
		{
			System.exit(0);
		}
		else if(e.getSource() == helpI)
		{
			JOptionPane.showMessageDialog(null, str,"How to play",
					              JOptionPane.INFORMATION_MESSAGE);
		}
		else if (e.getSource() == cMode)
		{
			classicMode();
		}
		else if (e.getSource() == wMode)
		{
			hardMode();
		}
		else if (e.getSource() == bMode)
		{
			bombMode();
		}
	}

	public void itemStateChanged(ItemEvent e) 
	{
		if(e.getSource() == ssR)
		{
			timerMove.setDelay(500);
		}
		else if(e.getSource() == smR)
		{
			timerMove.setDelay(150);
		}
		else if(e.getSource() == sfR)
		{
			timerMove.setDelay(70);
		}
		
	}

	public static void main(String[] args) 
	{
		
		Snake demoObj = new Snake();

	}

	
	

	

}




//import the image
class ImagePanel extends JPanel {

	  private Image img;

	  public ImagePanel(String img) {
	    this(new ImageIcon(img).getImage());
	  }

	  public ImagePanel(Image img) {
	    this.img = img;
	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);
	  }

	  public void paintComponent(Graphics g) {
	    g.drawImage(img, 0, 0, null);
	  }
}