import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Math.*;

public class Maxwell extends JFrame implements ActionListener {

    // big mama foundational display
    JFrame display;
    
    //button stuff
    JPanel buttonPanel;
    JButton addParticlesButton;
    JButton resetButton;

    //this will be the actual lil' game environment
    PlayArea game;

    //temp display stuff
    JPanel tempDisplay;
    JLabel leftTemp;
    JLabel rightTemp;

    
    //timer and click counter for calculations l8r
    Timer clickTimer;
    int clicks = 0;
    
    //counts number of times Daemon Gate opens
    int openCount;

    //creates two arrays of hot/fast and cold/slow particles
    HawtParticle[] fast;
    ChillParticle[] slow;
    int fastCount;
    int slowCount;

    //interval for position updates
    double changeT = 0.025; 

    
    // MaxwellDaemon default constructor
    public Maxwell() 
    {
    	
        display = new JFrame("Maxwell's Daemon");
        display.setSize(1000, 600);
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        openCount = 0;

        clickTimer = new Timer((int)(1000 * changeT), this);
        clickTimer.start();

        fastCount = 0;
        slowCount = 0;
        fast = new HawtParticle[1000];
        slow = new ChillParticle[1000];
        addParticles();

        //builds the playArea
        game = new PlayArea();
        display.add(game);

        //making the button panel
        buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color( 0, 204, 204 ));
        addParticlesButton = new JButton("LET'S ADD SOME PARTICLES! (add)");
        resetButton = new JButton("START FROM SCRATCH (reset)");
        addParticlesButton.addActionListener(this);
        resetButton.addActionListener(this);
        buttonPanel.add(addParticlesButton);
        buttonPanel.add(resetButton);
        buttonPanel.setLayout(new GridLayout(1,2));
        display.add(buttonPanel, BorderLayout.PAGE_START);

        //making the temp display panel
        tempDisplay = new JPanel();
        tempDisplay.setBackground(new Color( 0, 204, 204 ));
        leftTemp = new JLabel("Left", SwingConstants.CENTER);
        rightTemp = new JLabel("Right", SwingConstants.CENTER);
        leftTemp.setFont(new Font("Courier", Font.BOLD, 18));
        rightTemp.setFont(new Font("Courier", Font.BOLD, 18));
        tempDisplay.add(leftTemp);
        tempDisplay.add(rightTemp);
        tempDisplay.setLayout(new GridLayout(1,2));
        display.add(tempDisplay, BorderLayout.PAGE_END);

        game.closeWall();

        //this is the quick ear for my mouse
        display.addMouseListener(new MouseAdapter(){

            public void mousePressed( MouseEvent m ) {
                openCount++;
                game.openWall();
                game.repaint();
            }
            public void mouseReleased( MouseEvent m) {
                game.closeWall();
                game.repaint();
            }
        });

        //shows updated positions
        display.setVisible(true);
        
    }

    // PlayArea class to build the actual game environment and display
    public class PlayArea extends JComponent 
    {
        // to track wall presence
        private boolean hayPared = false;

        public void openWall() 
        {
            hayPared = true;
        }

        public void closeWall() 
        {
            hayPared = false;
        }

        public boolean isGateOpen() 
        {
            return hayPared;
        }

        public void paintComponent(Graphics g) 
        {
        	
            super.paintComponent(g); 
         
            //first visual of playArea created
            g.setColor(new Color( 0, 0, 0 ));
            g.fillRect( 0, 0, 1000, 520);

            int pixLoc = 497;

            //draws daemon gate
            if (!isGateOpen()) 
            {
            	g.setColor(new Color( 240,128,128 ));

                for(int i = 0; i < 5; i++){
                    pixLoc++;
                    g.drawLine(pixLoc, 0, pixLoc, 600);
                }
            }

            pixLoc = 497;

            //removes daemon gate
            if (isGateOpen()) 
            {
            	g.setColor(new Color( 230, 230, 250 ));

                for(int i = 0; i < 5; i++){
                    pixLoc++;
                    g.drawLine(pixLoc, 0, pixLoc, 600);
                }
            }


            //draws particles
            for ( int i=0; i<slowCount; i++) 
            {
                slow[i].drawMe(g);
                fast[i].drawMe(g);
            }
        }
    }

    // Particle class that models a game particle
    public class Particle
    {
        double x, y;
        double vx, vy;
        double oldx, oldy;

        public Particle( int x1, int y1 )
        {
            x = x1; y = y1;
            vx = Math.random() * 100 - 50;
            vy = Math.random() * 100 - 50;
        }

        public Particle()
        {
            x = Math.random() * 400 + 100;
            y = Math.random() * 400 + 100;
            vx = Math.random() * 100 - 50;
            vy = Math.random() * 100 - 50;
        }

        public double getX() 
        {
            return x;
        }

        public void move( double changeT )
        {
            oldx = x; oldy = y;
            x += vx * changeT;
            y += vy * changeT;
            stayOnScreen();
        }

        public void stayOnScreen()
        {    
        	if (game.isGateOpen()) 
        	{
                if (x < 0 || x > 1000) 
                { 
                	vx *= -1; 
                }
                if (y < 0 || y > 515) 
                { 
                	vy *= -1; 
                }    
            }
            if (!game.isGateOpen()) 
            {
            	if (x < 0 || x > 1000) 
            	{ 
            		vx *= -1; 
            	}
                if (y < 0 || y > 500) 
                { 
                	vy *= -1; 
                }

                if (x >= 496 && x <= 503) 
                {
                	vx *= -1; 
                }            
            }
        }
        
        //we're gonna let the children take care of this one
        public void drawMe( Graphics g ) { }
        
    }

    //HawtParticle is a Particle
    public class HawtParticle extends Particle 
    {
        HawtParticle() 
        {
            super();
        }

        HawtParticle(int x, int y) 
        {
            super(x, y);
        }

        public void drawMe ( Graphics g )
        {
            g.setColor( Color.RED );
            g.fillOval( (int)(x-2), (int)(y-2), 7, 7 );
        }
    }

    //ChillParticle is a Particle
    public class ChillParticle extends Particle 
    {
        ChillParticle() 
        {
            super();
        }

        ChillParticle(int x, int y) 
        {
            super(x, y);
        }

        public void drawMe (Graphics g)
        {
            g.setColor( Color.BLUE );
            g.fillOval( (int)(x-2), (int)(y-2), 7, 7 );
        }
    }

    //moves particles
    public void moveIt()
    {
        for ( int i=0; i < slowCount; i++ ) 
        {
            slow[i].move( 2.5 * changeT );
            fast[i].move( 5 * changeT );
        }
    }

    // adds one fast and one slow particle with randomized vectors into each space
    public void addParticles()
    {
        fast[fastCount++] = new HawtParticle(250, 300);
        fast[fastCount++] = new HawtParticle(750, 300);
        
        slow[slowCount++] = new ChillParticle(250, 300);
        slow[slowCount++] = new ChillParticle(750, 300);
    }

    //calculates the temperature in each space
    public double[] getTemps() 
    {
        double leftTemp;
        double rightTemp;
        double[] temps = new double[2];

        int slowLeft = 0;
        int fastLeft = 0;
        int slowRight = 0;
        int fastRight = 0;

        for ( int i = 0; i < fastCount; i++ ) 
        {
            if (fast[i].getX() < 500 ) 
            { 
            	fastLeft++; 
            }
            else 
            { 
            	fastRight++; 
            }
        }

        for ( int i = 0; i < slowCount; i++ ) 
        {
            if (slow[i].getX() < 500) 
            { 
            	slowLeft++; 
            }
            else 
            { 
            	slowRight++; 
            }
        }

        leftTemp = 3 * fastLeft + 1 * slowLeft;
        rightTemp = 3 * fastRight + 1 * slowRight;

        temps[0] = leftTemp;
        temps[1] = rightTemp;

        return temps;
        
    }

    //sets temp display value
    public void setTemps() 
    {
        double[] temps = getTemps();
        leftTemp.setText(String.valueOf(temps[0]) + " °");
        rightTemp.setText(String.valueOf(temps[1]) + " °");
    }

    //translates the mouse click action into procedure
    @Override
    public void actionPerformed( ActionEvent e )
    {
        //calculates temp every 10 clicks
        if ( e.getSource() == clickTimer ) 
        {
            moveIt();

            if (clicks == 0) 
            { 
            	setTemps(); 
            }
            else if (clicks > 10) 
            { 
            	clicks = 0; 
            }
            else 
            { 
            	clicks++; 
            }
        }
        else if ( e.getSource() == resetButton ) 
        {
            double[] temps = getTemps();
            double tempDifference = Math.abs(temps[0] - temps[1]);

            JOptionPane.showMessageDialog(null, "MAXWELL'S DAEMON GATE OPENED  " + openCount + " TIMES.\n" + "FINAL TEMP DIFFERENCE: " + tempDifference, "RESET RECAP", JOptionPane.INFORMATION_MESSAGE);

            HawtParticle[] reset_fast = new HawtParticle[1000];
            ChillParticle[] reset_slow = new ChillParticle[1000];

            openCount = 0;

            fast = reset_fast;
            fastCount = 0;
            slow = reset_slow;
            slowCount = 0;

            addParticles();
        }
        else if ( e.getSource() == addParticlesButton ) 
        {
            addParticles();
        }
        
        game.repaint();
        
    }

    //main funk
    public static void main(String[] args) 
    {
    	
        Maxwell gaemon = new Maxwell();
        
    }
}

