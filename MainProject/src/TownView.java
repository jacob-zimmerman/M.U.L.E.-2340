
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;

/**
 * @author Team 7
 * Class for the in-town view of the town
 */
public class TownView implements Serializable{
	
	private static final long serialVersionUID = 2L;
	private static final int NORTH = 0;
	private static final int EAST = 1;
	private static final int SOUTH = 2;
	private static final int WEST = 3;
	private static final int FRAME_DELAY = 3;
	private static final int NUM_FRAMES = 4; // number of frames per animation cycle
	
	private float progress = 0.0f;
	private Player player;
	private Turn currentTurn;
	private JLabel playerImage;
	private JLabel townImage;
	private int playerWidth;
	private int playerHeight;
	private int playerX=250;
	private int tempPlayerX=250;
	private int playerY=180;
	private int tempPlayerY=180;
	private int townWidth;
	private int townHeight;
	private JPanel panel; 
	private JFrame frame;
	private JFrame pubframe;
	private JFrame storeframe;
	private JFrame loframe;
	private TownNotificationPanel townNotifyPanel;
	int pubX=28;
	int pubY=32;
	int storeX=28;
	int storeY=272;//242;
	int landOfficeX=348;
	int landOfficeY=242;
	int otherX=348;
	int otherY=32;
	int width=166;
	int heightT=78;//108;
	int heightB=110;//140;
	static final int SPEED = 2;
	private ImageIcon currentFrame;
	private int currentDirection;
	private int frameNum;
	
	/* store values */
//	private static int foodQuantity = 16;
//	private static int energyQuantity = 16;
//	private static int muleQuantity = 25;
//	private static int smithoreQuantity = 0;
//	private final int FOOD_COST = 30;
//	private final int ENERGY_COST = 25;
//	private final int SMITHORE_COST = 50;
//	private final int FOOD_MULE_COST = 125;
//	private final int ENERGY_MULE_COST = 150;
//	private final int SMITHORE_MULE_COST = 175;
	
	/**
	 * TownView constructor
	 * 
	 * 
	 * @param player player to enter the town
	 */
	public TownView(Turn currentTurn){
		playerWidth=28;//playerIcon.getIconWidth();
		playerHeight=42;//playerIcon.getIconHeight();
		if(currentTurn!=null&&currentTurn.getPlayer()!=null&&currentTurn.getPlayer().getImage()!=null){
			this.currentTurn = currentTurn;
			this.player = currentTurn.getPlayer();
			//Sets the widths and heights for the player and the town. If the player is set to the correct width odd things happen. 
			//If the player is set to 28x42 it works normally except near the edges.
			//ImageIcon currentFrame = player.getImage();
			currentDirection = SOUTH;
			frameNum = 0;
			ImageIcon currentFrame = player.getSFrames()[frameNum];
			playerImage = new JLabel(currentFrame);
			
			townNotifyPanel = new TownNotificationPanel(currentTurn);
			ImageIcon townIcon = new ImageIcon(getClass().getClassLoader().getResource("resources/townview.jpg"));
			townImage = new JLabel(townIcon);
			townWidth=townIcon.getIconWidth();
			townHeight=townIcon.getIconHeight();
		}
	}
	
	/**
	 * @return the town notification panel
	 */
	public TownNotificationPanel getTownNotificationPanel()
	{
		return townNotifyPanel;
	}
	
	/**
	 * @return the pub frame for the game
	 */
	public JFrame getPubFrame()
	{
		return pubframe;
	}
	
	/**
	 * @return the store frame for the game
	 */
	public JFrame getStoreFrame()
	{
		return storeframe;
	}
	
	/**
	 * displays initial town with player in the center of the board
	 * @param frame 
	 */
	public void displayTownSquare(){
		frame=new GameFrame(currentTurn.getRound().getGame());
		
		townNotifyPanel.setPreferredSize(new Dimension(townWidth, 30));
		//creates layeredPane
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setOpaque(false);
		layeredPane.setPreferredSize(new Dimension(townWidth, townHeight));
		
		//sets bounds on images so they can be drawn
		townImage.setBounds(0,0,townWidth, townHeight);
		playerImage.setBounds(playerX,playerY,playerX+playerWidth,playerY+playerHeight);
		
		//adds images to the layeredPane
		layeredPane.add(townImage,JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(playerImage,JLayeredPane.POPUP_LAYER);
		
		//creates a new JPanel, and adds the layered pane to it
		panel = new JPanel();
		panel.add(layeredPane);
		
		//adds the pane to the frame, the JFrame has a grid layout but changing the layout does not seem to help the size problem
		frame.setSize(new Dimension(townWidth, townHeight + 30));
		panel.setLayout(new GridLayout(1,1));
		
		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setPreferredSize(new Dimension(townWidth, townHeight+30));
		wrapperPanel.add(panel, BorderLayout.SOUTH);
		wrapperPanel.add(townNotifyPanel, BorderLayout.NORTH);
		frame.add(wrapperPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animate();
	}
	
	/**
	 * updates player's location
	 * 
	 * @ return if the frame can be updated
	 */
	public boolean updatePlayer(){
	
	    if (!frame.isVisible()) {
	        return false;
	    }
		//calculate new location
		boolean touchingInner =checkInnerBoundaries();
		boolean touchingOuter =checkOuterBoundaries();

		if(!touchingInner&&!touchingOuter){
			playerX=tempPlayerX;
			playerY=tempPlayerY;
		}
		else{
			tempPlayerX=playerX;
			tempPlayerY=playerY;
		}
		
		playerImage.setBounds(playerX,playerY,playerX+playerWidth,playerY+playerHeight);
		if(checkForSpecificLocation(pubX,pubY+heightT,width, 10)){
			pubview();
			return false;
		}
		if(checkForSpecificLocation(storeX,storeY-20,width, 30)){
			storeview();
			return false;
		}
		return true;
	}
	
	/**
	 * @return if the player is out of bounds
	 */
	private boolean checkOuterBoundaries(){
		//if the code is running correctly then the 404 should be replaced by townHeight and the 534 should be replaced by townWidth
		//the 10s should be replaced with 0
		boolean touching=false;
		
		if(tempPlayerX+playerWidth>523){
			touching=true;
		}
		if(tempPlayerX<22){
			touching=true;
		}
		if(tempPlayerY+playerHeight>395){
			touching=true;
		}
		if(tempPlayerY<22){
			touching=true;
		}
		return touching;
	}

	/**
	 * @return if the player is in the shop
	 */
	private boolean checkInnerBoundaries(){
		return (checkForSpecificLocation(pubX, pubY, width, heightT)||
				checkForSpecificLocation(storeX, storeY, width, heightB)||
				checkForSpecificLocation(landOfficeX, landOfficeY, width, heightB)||
				checkForSpecificLocation(otherX, otherY, width, heightT));
	}
	
	/**
	 * checks if the player is inside a specific location and updates players location to not be inside the specific location
	 * @param locX
	 * @param locY
	 * @param locWidth
	 * @param locHeight
	 */
	public boolean checkForSpecificLocation(int locX,int locY,int locWidth, int locHeight){
		//This is a very painful read through, basically it checks if the corner of the player overlaps with the corner of the location
		//or if the player is hitting one of the sides of the location
		//I believe this code to be correct
		boolean touching=false;
		/*checking if corners are overlapping */
		if(tempPlayerX+playerWidth>locX&&tempPlayerX<locX){
			if(tempPlayerY+playerHeight>locY&&tempPlayerY<locY){//top right corner of loc
				touching=true;
			}
			if(tempPlayerY<locY+locHeight&&tempPlayerY+playerHeight>locY+locHeight){//bottom right corner of loc
				touching=true;
			}
			
		}
		
		if(tempPlayerX<locX+locWidth&&tempPlayerX+playerWidth>locX+locWidth){
			if(tempPlayerY+playerHeight>locY&&tempPlayerY<locY){//top left corner of loc
				touching=true;
			}
			if(tempPlayerY<locY+locHeight&&tempPlayerY+playerHeight>locY+locHeight){//bottom left corner of loc
				touching=true;
			}
			
		}
		/*checking if the player is overlapping the location from the size*/
		if(tempPlayerY+playerHeight<locY+locHeight&&tempPlayerY>locY){
			if(tempPlayerX+playerWidth>locX&&tempPlayerX<locX){//right side of loc
				touching=true;
			}
			
		    if(tempPlayerX+playerWidth>locX+locWidth&&tempPlayerX<locX+locWidth){//left side of loc
		    	touching=true;
		    }
		}
		if(tempPlayerX+playerWidth<locX+locWidth&&tempPlayerX>locX){
			if(tempPlayerY+playerHeight>locY&&tempPlayerY<locY){//top side of loc
				touching=true;
			}
			
		    if(tempPlayerY+playerHeight>locY+locHeight&&tempPlayerY<locY+locHeight){//bottom side of loc
		    	touching=true;
		    }
		}
		return touching;
	
	}	
	
	
	/**
	 * moves the player the location
	 * @param playerX
	 * @param playerY
	 */
	public void movePlayer(int playerX,int playerY){
		this.playerX=playerX;
		this.playerY=playerY;
		this.tempPlayerX=playerX;
		this.tempPlayerY=playerY;
	
	}
	/**
	 * @return the player's width
	 */	
	public int getPlayerWidth(){
		return playerWidth;
	}
	/**
	 * @return the player's height
	 */
	public int getPlayerHeight(){
		return playerHeight;
	}
	
	/**
	 * moves player around the screen
	 */
	private void animate() {
		//sets a KeyListener to listen for directions
		KeyStroke stroke = new KeyStroke();
		if(frame==null)
			return;
		frame.addKeyListener(stroke);
		
		//animates the player moving across the screen at a certain fps
		//also has an unused timer feature that could be used later on in the project.
		final int animationTime = 1000;
        int fps = 30;
        int delay = 1000 / fps;
        final long start = System.currentTimeMillis();
        final Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
        	
        	
            public void actionPerformed(ActionEvent e) {
                final long now = System.currentTimeMillis();
                final long elapsed = now - start;
                progress = (float) elapsed / animationTime;

                if(!updatePlayer()){
                	timer.stop();
                }
                if (elapsed >= animationTime) {
                 //   timer.stop();
                }
                
                
            }
            
            
        });
        timer.start();
    }
	
	/**
	 * handles the movement for the player while inside the pub
	 */
	private void pubAnimate() {
		//sets a KeyListener to listen for directions
		KeyStroke stroke = new KeyStroke();
		if(pubframe==null)
			return;
		pubframe.addKeyListener(stroke);
		
		//animates the player moving across the screen at a certain fps
		//also has an unused timer feature that could be used later on in the project.
		final int animationTime = 1000;
        int fps = 30;
        int delay = 1000 / fps;
        final long start = System.currentTimeMillis();
        final Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final long now = System.currentTimeMillis();
                final long elapsed = now - start;
                progress = (float) elapsed / animationTime;
                if(!updatePlayerPub()){
                	timer.stop();
                }
            }
        });
        timer.start();
    }
	
	/**
	 * Displays the frame for the pub
	 * 
	 * @return if the pub can be displayed
	 */
	private boolean pubview(){
		
		frame.setVisible(false);
		
		pubframe =new GameFrame(currentTurn.getRound().getGame());
		
		ImageIcon pubIcon = new ImageIcon(getClass().getClassLoader().getResource("resources/pubview.jpg"));
		JLabel pubImage = new JLabel(pubIcon);
		int pubWidth=pubIcon.getIconWidth();
		int pubHeight=pubIcon.getIconHeight();
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setOpaque(false);
		layeredPane.setPreferredSize(new Dimension(pubWidth, pubHeight));
		
		//sets bounds on images so they can be drawn
		pubImage.setBounds(0,0,pubWidth, pubHeight);
		tempPlayerX=260;
		tempPlayerY=332;
		playerImage.setBounds(playerX,playerY,playerX+playerWidth,playerY+playerHeight);
		
		//adds images to the layeredPane
		layeredPane.add(pubImage,JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(playerImage,JLayeredPane.POPUP_LAYER);
		
		//creates a new JPanel, and adds the layered pane to it
		JPanel pubpanel = new JPanel();
		pubpanel.add(layeredPane);

		JPanel pubWrapperPanel = new JPanel(new BorderLayout());
		pubWrapperPanel.setPreferredSize(new Dimension(townWidth, townHeight+30));
		pubWrapperPanel.add(pubpanel, BorderLayout.SOUTH);
		pubWrapperPanel.add(townNotifyPanel, BorderLayout.NORTH);
		pubframe.add(pubWrapperPanel);
		
        pubframe.pack();
		pubframe.setLocationRelativeTo(null);
        pubframe.setVisible(true);
        pubAnimate();
        
		return true;
	}
	
	/**
	 * Handles everything that happens when player gambles at the pub
	 */
	private void gamble() {
		int timeRemaining = currentTurn.getSecondsLeft();
		int currentRound = currentTurn.getRound().getRoundNumber(); // a number representing the current round
		
		// calculates time bonus
		int timeBonus = 50; // if time is less than or equal to 12, timeBonus = 50
		if (timeRemaining > 12) // if time is between 12 and 25, timeBonus = 100
			timeBonus = 100;
		if (timeRemaining > 25) // if time is between 25 and 37, timeBonus = 150
			timeBonus = 150;
		if (timeRemaining > 37) // if time is between 37 and 50, timeBonus = 200
			timeBonus = 200;
		// calculates the round bonus
		int roundBonus = 50; // if it is round 1,2, or 3, roundBonus = 50
		if (currentRound > 3) // if it is round 3,4,5,6, or 7, roundBonus = 100
			roundBonus = 100;
		if (currentRound > 7) // if it is round 8,9,10, or 11, roundBonus = 150
			roundBonus = 150;
		if (currentRound > 11) // if it is round 12, roundBonus = 200
			roundBonus = 200;
		
		// calculates the money bonus; Money Bonus = Round Bonus * random b/t 0 and Time Bonus
		int moneyBonus = (int) (roundBonus * (Math.random() * timeBonus));
		if (moneyBonus > 250) // you cannot earn more than 250 from gambling
			moneyBonus = 250;
		
		player.changeMoney(moneyBonus); // adds money from gambling to the player
		
		// notify the player of how much money they have made
		JOptionPane.showMessageDialog(frame, "You made $" + moneyBonus + " from gambling! Your turn is now over.");
		
		// end the player's turn
		currentTurn.endTurn();
	}
	
	/**
	 * Checks how the player can move around inside the pub
	 * 
	 * @return true if the player is outside the boundaries
	 */
	private boolean updatePlayerPub(){
		boolean touchingOuter =checkOuterBoundaries();
        if (!pubframe.isVisible()) {
            return false;
        }
        
		if(!touchingOuter){
			playerX=tempPlayerX;
			playerY=tempPlayerY;
		}
		else{
			tempPlayerX=playerX;
			tempPlayerY=playerY;
		}
		
		playerImage.setBounds(playerX,playerY,playerX+playerWidth,playerY+playerHeight);
		if(checkForSpecificLocation(170,140,200,130)){//gamble
			pubframe.setVisible(false);
			gamble();
			
			return false;
		}
		if(checkForSpecificLocation(168,388,216,10)){//exit
			returnToTown(pubframe);
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * Animates the player while inside the store
	 */
	private void storeAnimate() {
		//sets a KeyListener to listen for directions
		KeyStroke stroke = new KeyStroke();
		if(storeframe==null)
			return;
		storeframe.addKeyListener(stroke);
		
		//animates the player moving across the screen at a certain fps
		//also has an unused timer feature that could be used later on in the project.
		final int animationTime = 1000;
        int fps = 30;
        int delay = 1000 / fps;
        final long start = System.currentTimeMillis();
        final Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final long now = System.currentTimeMillis();
                final long elapsed = now - start;
                progress = (float) elapsed / animationTime;
                if(!updatePlayerStore()){
                	timer.stop();
                }
            }
            
        });
        timer.start();
    }
	
	/**
	 * Displays the store view for the game
	 * 
	 * @return true if the store can be displayed
	 */
	private boolean storeview(){
		
		frame.setVisible(false);
		
		storeframe =new GameFrame(currentTurn.getRound().getGame());
		
		ImageIcon storeIcon = new ImageIcon(getClass().getResource("resources/storeview.jpg"));
		JLabel storeImage = new JLabel(storeIcon);
		int storeWidth=storeIcon.getIconWidth();
		int storeHeight=storeIcon.getIconHeight();
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setOpaque(false);
		layeredPane.setPreferredSize(new Dimension(storeWidth, storeHeight));
		
		//sets bounds on images so they can be drawn
		storeImage.setBounds(0,0,storeWidth, storeHeight);
		tempPlayerX=260;
		tempPlayerY=40;
		playerImage.setBounds(playerX,playerY,playerX+playerWidth,playerY+playerHeight);
		
		//adds images to the layeredPane
		layeredPane.add(storeImage,JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(playerImage,JLayeredPane.POPUP_LAYER);
		
		//creates a new JPanel, and adds the layered pane to it
		JPanel storepanel = new JPanel();
		storepanel.add(layeredPane);
				
		JPanel storeWrapperPanel = new JPanel(new BorderLayout());
		storeWrapperPanel.setPreferredSize(new Dimension(townWidth, townHeight+30));
		storeWrapperPanel.add(storepanel, BorderLayout.SOUTH);
		storeWrapperPanel.add(townNotifyPanel, BorderLayout.NORTH);
		storeframe.add(storeWrapperPanel);
		
		storeframe.pack();
		storeframe.setLocationRelativeTo(null);
		storeframe.setVisible(true);
		
        storeAnimate();
        
		return true;
	}
	
	/**
	 * updates the player movement within the store
	 * 
	 * @return true if the player can continue moving
	 */
	private boolean updatePlayerStore(){
	
		boolean touchingOuter = checkOuterBoundaries();
		
        if (!storeframe.isVisible()) {
            return false;
        }
        
		if(!touchingOuter){
			playerX=tempPlayerX;
			playerY=tempPlayerY;
		}
		else{
			tempPlayerX=playerX;
			tempPlayerY=playerY;
		}
		
		playerImage.setBounds(playerX,playerY,playerX+playerWidth,playerY+playerHeight);
		if(checkForSpecificLocation(170,140,200,130)){//buy!
			buy();
			tempPlayerX=260;
			tempPlayerY=40;
		//	return false;
		}
		if(checkForSpecificLocation(168,0,216,30)){//exit
			returnToTown(storeframe);
			return false;
		}
		return true;
	}
	
	
	/**
	 * removes the frame left over from the places in town
	 */
	public void removeSquare(){
		if(loframe!=null){
			loframe.setVisible(false);
		}
		if(storeframe!=null){
			storeframe.setVisible(false);
		}
		if(pubframe!=null){
			pubframe.setVisible(false);
		}
		if(frame!=null){
			frame.setVisible(false);
		}
	}
	
	
	/**
	 * Handles all of the logic for buying things inside of the store and updating the player
	 */
	private void buy() {
		Store store = new Store(true);
		
		String cancel="Cancel";
		Object[] options = {"Buy",
        					"Sell", cancel};
		int n = JOptionPane.showOptionDialog(frame,
			"Would you like to buy or sell?",
			"Store",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,     //do not use a custom Icon
			options,  //the titles of buttons
			options[0]); //default button title
		
		if (n==0) {//BUY
			String mule ="Mule costs 100 (base price)";
			String food ="Food costs 30";
			String energy="Energy costs 25";
			String ore="Ore costs 50";
			
			Object[] possibilitiesBuy = {food,energy,ore,mule};
			String buy = (String)JOptionPane.showInputDialog(
			                    storeframe,
			                    "What would you like to buy?",
			                    "Customized Dialog",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilitiesBuy,
			                    "");
			if(buy==null){
			}
			else if (buy.equals(mule)) {
				String foodMule =  "Food Mule                                  100+25=125";
				String energyMule ="Energy Mule                               100+50=150";
				String oreMule =   "Ore Mule                                    100+75=175";
	               
				
				Object[] possibilitiesMule = {foodMule, energyMule, oreMule};
				String s = (String)JOptionPane.showInputDialog(
				                    storeframe,
				                    "Which kind of mule would you like to buy?",
				                    "Customized Dialog",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilitiesMule,
				                    "");
				if(s==null){
				}
				else if (s.equals(foodMule)) {
					if (player.canPurchase(store.getFOOD_MULE_COST()) && store.getMuleQuantity() > 0 && !player.hasMule()) {
				        player.changeMoney(-store.getFOOD_MULE_COST());
				        Mule m = new Mule(player, Mule.FOOD);
				        m.setOwner(player);
				        player.setMule(m);
				        currentTurn.getRound().getGame().setMule(m);
				        store.setMuleQuantity(store.getMuleQuantity()-1);
				    } else {
				        if (store.getMuleQuantity() <= 0) {
				            JOptionPane.showMessageDialog(storeframe, "Store is out of mules.");
				        } else if (player.hasMule()) {
				            JOptionPane.showMessageDialog(storeframe, "You already have mule!");
				        } else {
				            JOptionPane.showMessageDialog(storeframe, "You don't have enough money to buy that.");
				        }
				    }
				} else if (s.equals(energyMule)) {
					if (player.canPurchase(store.getENERGY_MULE_COST()) && store.getMuleQuantity() > 0 && !player.hasMule()) {
				        player.changeMoney(-store.getENERGY_MULE_COST());
				        Mule m = new Mule(player, Mule.ENERGY);
				        m.setOwner(player);
				        player.setMule(m);
				        currentTurn.getRound().getGame().setMule(m);
				        store.setMuleQuantity(store.getMuleQuantity()-1);
				    } else {
				        if (store.getMuleQuantity() <= 0) {
				            JOptionPane.showMessageDialog(storeframe, "Store is out of mules.");
				        } else if (player.hasMule()) {
				            JOptionPane.showMessageDialog(storeframe, "You already have mule!");
				        } else {
				            JOptionPane.showMessageDialog(storeframe, "You don't have enough money to buy that.");
				        }
				    }
				} else if (s.equals(oreMule)) {
					if (player.canPurchase(store.getSMITHORE_MULE_COST()) && store.getMuleQuantity() > 0 && !player.hasMule()) {
				        player.changeMoney(-store.getSMITHORE_MULE_COST());
				        Mule m = new Mule(player, Mule.SMITHORE);
				        m.setOwner(player);
				        player.setMule(m);
				        currentTurn.getRound().getGame().setMule(m);
				        store.setMuleQuantity(store.getMuleQuantity()-1);
				    } else {
				        if (store.getMuleQuantity() <= 0) {
				            JOptionPane.showMessageDialog(storeframe, "Store is out of mules.");
				        } else if (player.hasMule()) {
				            JOptionPane.showMessageDialog(storeframe, "You already have mule!");
				        } else {
				            JOptionPane.showMessageDialog(storeframe, "You don't have enough money to buy that.");
				        }
				    }
				}
			} else if (buy.equals(food)) {
				if (player.canPurchase(store.getFOOD_COST()) && store.getFoodQuantity() > 0) {
				    player.changeMoney(-store.getFOOD_COST());
				    player.setFood(player.getFood() + 1);
				    store.setFoodQuantity(store.getFoodQuantity()-1);
				} else {
				    if (store.getFoodQuantity() <= 0) {
				        JOptionPane.showMessageDialog(storeframe, "Store is out of food.");
				    } else {
				        JOptionPane.showMessageDialog(storeframe, "You don't have enough money to buy that.");
				    }
				}
			} else if (buy.equals(energy)) {
				if (player.canPurchase(store.getENERGY_COST()) && store.getEnergyQuantity() > 0) {
				    player.changeMoney(-store.getENERGY_COST());
				    player.setEnergy(player.getEnergy() + 1);
				    store.setEnergyQuantity(store.getEnergyQuantity()-1);
				} else {
				    if (store.getEnergyQuantity() <= 0) {
				        JOptionPane.showMessageDialog(storeframe, "Store is out of energy.");
				    } else {
				        JOptionPane.showMessageDialog(storeframe, "You don't have enough money to buy that.");
				    }
				}
			} else if (buy.equals(ore)) {
				if (player.canPurchase(store.getSMITHORE_COST()) && store.getSmithoreQuantity() > 0) {
				    player.changeMoney(-store.getSMITHORE_COST());
				    player.setSmithore(player.getSmithore() + 1);
				    store.setSmithoreQuantity(store.getSmithoreQuantity()-1);
				} else {
				    if (store.getSmithoreQuantity() <= 0) {
				        JOptionPane.showMessageDialog(storeframe, "Store is out of smithore.");
				    } else {
				        JOptionPane.showMessageDialog(storeframe, "You don't have enough money to buy that.");
				    }
				}
			} else {//cancel
				
			}
		} else if (n == 1){//SELL
			
			String mule ="Mule sells for 100 (base price)";
			String food ="Food sells for 30";
			String energy="Energy sells for 25";
			String ore="Ore sells for 50";
			
			Object[] possibilitiesBuy = {food,energy,ore};
			String sell = (String)JOptionPane.showInputDialog(
			                    storeframe,
			                    "What would you like to sell?",
			                    "Customized Dialog",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilitiesBuy,
			                    "");
			
			if(sell==null){
				
			}
		/*	else if (sell.equals(mule)) {
				String foodMule =  "Food Mule                                  100+25=125";
				String energyMule ="Energy Mule                               100+50=150";
				String oreMule =   "Ore Mule                                    100+75=175";
	               
				
				Object[] possibilitiesMule = {foodMule,energyMule,oreMule};
				String s = (String)JOptionPane.showInputDialog(
				                    storeframe,
				                    "Which kind of mule would you like to sell?",
				                    "Customized Dialog",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilitiesMule,
				                    "");
				if (s.equals(foodMule)) {
					System.out.println("Selling Food Mule!!");
					if (player.hasMule() && player.getMule().getType() == Mule.FOOD) {
					    player.changeMoney(FOOD_MULE_COST);
					    player.setMule(null);
					    muleQuantity++;
					} else {
				        JOptionPane.showMessageDialog(storeframe, "You don't have a food mule!");
					}
				} else if (s.equals(energyMule)) {
					System.out.println("Selling Energy Mule!!");
					if (player.hasMule() && player.getMule().getType() == Mule.ENERGY) {
					    player.changeMoney(ENERGY_MULE_COST);
					    player.setMule(null);
					    muleQuantity++;
					} else {
				        JOptionPane.showMessageDialog(storeframe, "You don't have a energy mule!");
					}
				} else if (s.equals(oreMule)) {
					System.out.println("Selling Ore Mule!!");
					if (player.hasMule() && player.getMule().getType() == Mule.SMITHORE) {
					    player.changeMoney(SMITHORE_MULE_COST);
					    player.setMule(null);
					    muleQuantity++;
					} else {
				        JOptionPane.showMessageDialog(storeframe, "You don't have a smithore mule!");
					}
				}
			} */else if(sell.equals(food)) {
				if (player.getFood() > 0) {
				    player.changeMoney(store.getFOOD_COST());
				    player.setFood(player.getFood() - 1);
				    store.setFoodQuantity(store.getFoodQuantity()+1);
				} else {
				    JOptionPane.showMessageDialog(storeframe, "You don't have any food!");
				}
			} else if(sell.equals(energy)) {
				if (player.getEnergy() > 0) {
				    player.changeMoney(store.getENERGY_COST());
				    player.setEnergy(player.getEnergy() - 1);
				    store.setEnergyQuantity(store.getEnergyQuantity()+1);
				} else {
				    JOptionPane.showMessageDialog(storeframe, "You don't have any energy!");
				}
			} else if(sell.equals(ore)) {
				if (player.getSmithore() > 0) {
				    player.changeMoney(store.getSMITHORE_COST());
				    player.setSmithore(player.getSmithore() - 1);
				    store.setSmithoreQuantity(store.getSmithoreQuantity()+1);
				} else {
				    JOptionPane.showMessageDialog(storeframe, "You don't have any smithore!");
				}
			} else {//cancel
				
			}
		} else {//cancel
			
		}
	}

	/**
	 * returns the game screen to the town
	 * 
	 * @param locFrame the frame that is currently displayed
	 * @return true
	 */
	public boolean returnToTown(JFrame locFrame){
		locFrame.setVisible(false);
		tempPlayerX=250;
		tempPlayerY=180;
		displayTownSquare();
		return true;
	}
	
	/**
	 * Sets frameNum equal to the next frame and updates current animation frame
	 */
	private void nextFrame() {
		frameNum++;
		frameNum = frameNum % (FRAME_DELAY*NUM_FRAMES);
		int n = frameNum/FRAME_DELAY; // get the number corresponding to the index of the animation frame
		// get next frame based on direction the player is facing
		if (currentDirection == NORTH) currentFrame = player.getNFrames()[n];
		else if (currentDirection == EAST) currentFrame = player.getEFrames()[n];
		else if (currentDirection == SOUTH) currentFrame = player.getSFrames()[n];
		else currentFrame = player.getWFrames()[n];
		
		playerImage.setIcon(currentFrame);
	}
	
	/**
	 * @author Team 7
	 * KeyStroke class to move the player
	 */
	private class KeyStroke implements KeyListener{
		
		/**
		 * @param e KeyEvent
		 */
		public void keyTyped(KeyEvent e){}
		/**
		 * Listens for key strokes.
		 * Updates the player location and current animation frame.
		 * Monitors which direction the player is going in, currently this is being used to decide information about 
		 * the movement of the player around the corners of locations in the checkForSpecificLocation() method.
		 * Given time a better method of moving around corners should be devised.
		 * 
		 * @param e KeyEvent
		 */
		public void keyPressed(KeyEvent e){
			
			 if(KeyEvent.VK_UP==e.getKeyCode()||KeyEvent.VK_W==e.getKeyCode()){
				 tempPlayerY-=SPEED;
				 // if we changed directions, set to default 
				 if (currentDirection != NORTH) {
					 currentDirection = NORTH;
					 frameNum = 0;
					 currentFrame = player.getNFrames()[0];
				 }
				 // if we did not change directions, go to next frame
				 else {
					 nextFrame();
				 }
			 }
			 
			 else if(KeyEvent.VK_DOWN==e.getKeyCode()||KeyEvent.VK_S==e.getKeyCode()){
				 tempPlayerY+=SPEED;
				 
				// if we changed directions, set to default 
				 if (currentDirection != SOUTH) {
					 currentDirection = SOUTH;
					 frameNum = 0;
					 currentFrame = player.getSFrames()[0];
				 }
				 // if we did not change directions, go to next frame
				 else {
					 nextFrame();
				 }
			 }
			 
			 else if(KeyEvent.VK_RIGHT==e.getKeyCode()||KeyEvent.VK_D==e.getKeyCode()){
				 tempPlayerX+=SPEED;
				// if we changed directions, set to default 
				 if (currentDirection != EAST) {
					 currentDirection = EAST;
					 frameNum = 0;
					 currentFrame = player.getEFrames()[0];
				 }
				 // if we did not change directions, go to next frame
				 else {
					 nextFrame();
				 }
			 }
			 
			 else if(KeyEvent.VK_LEFT==e.getKeyCode()||KeyEvent.VK_A==e.getKeyCode()){
				 tempPlayerX-=SPEED;
				// if we changed directions, set to default 
				 if (currentDirection != WEST) {
					 currentDirection = WEST;
					 frameNum = 0;
					 currentFrame = player.getWFrames()[0];
				 }
				 // if we did not change directions, go to next frame
				 else {
					 nextFrame();
				 }
			 }
		 }
		
		/**
		 * When key is released, change animation frame to stationary frame
		 * @param e KeyEvent
		 */
		 public void keyReleased(KeyEvent e){
			 // reset to standing position facing north
			 if (currentDirection == NORTH) {
				 frameNum = 0;
				 currentFrame = player.getNFrames()[0];
			 }
			 // reset to standing position facing east
			 if (currentDirection == EAST) {
				 frameNum = 0;
				 currentFrame = player.getEFrames()[0];
			 }
			 // reset to standing position facing south
			 if (currentDirection == SOUTH) {
				 frameNum = 0;
				 currentFrame = player.getSFrames()[0];
			 }
			 // reset to standing position facing west
			 if (currentDirection == WEST) {
				 frameNum = 0;
				 currentFrame = player.getWFrames()[0];
			 }
			 
		 }
	}
	
	/**
     * Changes the current turn
     * 
     * @param the current turn
     */
	public void changeTurn(Turn currentTurn) {
		
		frame.setVisible(false);
		if(pubframe!=null){
			pubframe.setVisible(false);
		}
		if(storeframe!=null){
			storeframe.setVisible(false);
		}
		if(loframe!=null){
			loframe.setVisible(false);
		}
		this.currentTurn = currentTurn;
		this.player = currentTurn.getPlayer();
		ImageIcon playerIcon = player.getImage();
		playerImage = new JLabel(playerIcon);		
		townNotifyPanel = new TownNotificationPanel(currentTurn);
	}
	

	
}
