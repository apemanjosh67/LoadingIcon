//Josh Muszka
//This program displays an animation-loop that resembles a loading icon
//November 19, 2021

package loadingicon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MyLoadingIcon implements ActionListener {

	final static int N = 5; //change number of dots



	JFrame window;
	DrawingPanel panel;
	int panW = 600; //width of screen
	int panH = panW; //height of screen

	ArrayList<Dot> dotList = new ArrayList<>(); //dots
	ArrayList<Laser> laserList = new ArrayList<>(); //lasers

	int diameter = 18; //diameter of each dot
	double startingRadius = (diameter/2)*5.5; //affects how far apart the dots start from each other
	int laserLength = 40;; //length of the lasers
	double laserMovement = 0; //as time goes on, this value increases, thus making the lasers move
	Color colours[] = new Color []{ //colours are stored in an array to make dot colours random
			Color.decode("#ff6161"), //new color
			Color.decode("#ffb359"), //new color
			Color.decode("#fbff6a"), //new color
			Color.decode("#8bff73"), //new color
			Color.decode("#4fefff"), //new color
			Color.decode("#5494ff"), //new color
			Color.decode("#7b66ff"), //new color
			Color.decode("#ff93ff"), //new color
	};

	Color dotColour; //current colour of dots is stored here (keeping track of the current colour allows for no repeats in a row to occur)
	Color backgroundColour = new Color(35,35,35,85); //colour of the background
	Timer timer;


	public static void main (String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MyLoadingIcon();
			}
		});
	}



	public void setup() {

		//initalize dots
		for (int i = 0; i < N; i++) {
			Dot d = new Dot(0,diameter,0,0);
			dotList.add(d);
		}


		//initalize lasers
		for (int i = 0; i < N*N; i++) {
			Laser a = new Laser(laserLength,0,0,0,0);
			//starting position values don't matter since lasers won't be drawn initially
			laserList.add(a);
		}


		//set random starting colour of dots
		dotColour = colours[(int)(Math.random()*colours.length)];

		
		
		//calculate starting angles of dots
		int i = 0;
		for (Dot d : dotList) {
			d.angle = 360 - (360/N *i);
			i++;
		}


		//calculate xx and yy
		for (Dot d : dotList) {
			d.xx = panW/2 + startingRadius*Math.sin(Math.toRadians(d.angle)) - (diameter/2);
			d.yy = panH/2 - startingRadius*Math.cos(Math.toRadians(d.angle)) - (diameter/2);
			//subtracting half the diameter (diameter/2) centres the system of dots on the screen
		}

	}


	MyLoadingIcon() {

		window = new JFrame("Loading Icon");
		panel = new DrawingPanel();
		window.add(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setup();

		//timer
		timer = new Timer (14, this);
		timer.start();


		window.pack();
		window.setVisible(true);

	}

	class DrawingPanel extends JPanel {

		DrawingPanel() {
			this.setPreferredSize(new Dimension(panW,panH));
			this.setBackground(backgroundColour);
		}


		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON); //antialiasing


			//draw lines

			g2.setColor(Color.white);
			for (Dot d : dotList) {
				for (Dot e : dotList) {

					g2.drawLine((int)d.xx +(d.diameter/2), (int)d.yy+(d.diameter/2), (int)e.xx+(d.diameter/2), (int)e.yy+(d.diameter/2));

				}
			}


			//draw dot outlines

			for (Dot d : dotList) {
				g2.fill(new Ellipse2D.Double(d.xx-2, d.yy-2, d.diameter+4, d.diameter+4));
			}



			//draw lasers

			g2.setColor(dotColour);
			g2.setStroke(new BasicStroke(6));

			if (dotList.get(0).angle <= 167) {
				for (Laser a: laserList) {

					g2.drawLine((int) a.laserXX1, 
							(int) a.laserYY1, 
							(int) a.laserXX2, 
							(int) a.laserYY2);
				}
			}


			//draw dots

			g2.setColor(dotColour);
			for (Dot d : dotList) {
				g2.fill(new Ellipse2D.Double(d.xx, d.yy, d.diameter, d.diameter));
			}


			//add numbers on top of each dot
			//for debugging purposes
			//			g.setColor(Color.black);
			//			int i = 0;
			//			for (Dot d : dotList) {
			//				g2.drawString(""+(i+1), (int)(d.xx + 7), (int)(d.yy + 13));
			//				i++;
			//			}

		}

	}

	public void dotMovement() {

		//decrease angle to get dots to rotate

		for (Dot d : dotList) {
			d.angle--;
		}


		//update dot positions

		int i = 0;	
		for (Dot d : dotList) {

			//alternate function transformations so that some are stretched horizonally and some vertically
			//cosine and sine functions are used for horizonal and vertical motion respectively to make dots move in a circle (or an ellipse, technically)
			
			if (i != N-1) {
				if (i%2 == 0) {
					d.xx -= 2*Math.cos(Math.toRadians(d.angle));
					d.yy -= Math.sin(Math.toRadians(d.angle));
				}
				else {
					d.xx -= Math.cos(Math.toRadians(d.angle));
					d.yy -= 2*Math.sin(Math.toRadians(d.angle));
				}
			}
			i++;
		}

		//make last dot move in a normal circle (for cooler effect)
		dotList.get(N-1).xx -= 2*Math.cos(Math.toRadians(dotList.get(N-1).angle));
		dotList.get(N-1).yy -= 2*Math.sin(Math.toRadians(dotList.get(N-1).angle));	



		//reset angles after a full rotation has taken place 
		//(makes math for other things easier)

		i = 0;
		for (Dot d : dotList) {
			if (dotList.get(N-1).angle <= 360 - (360/N *(N-1)) - 360 ) {
				d.angle = 360 - (360/N *i);
			}
			i++;
		}




	}

	public void shootLasers() {

		//move lasers
		laserMovement += 5;

		//reset laserMovement to 0 every time it hits 167 degrees
		//this is so they can fire again every rotation
		if (dotList.get(0).angle%167 == 0) {
			laserMovement = 0;
		}


		int i = 0;
		int j = 0;
		//i represents the laser's starting dot
		//j represents the dot that the laser is traveling to
		if (dotList.get(0).angle <= 167) { //so lasers dont move at beginning of loop

			for (Laser a : laserList) {

				
				
				
				
				//quadrants don't correlate to position on the screen, but to position on a geometric cartesian plane

				//ie, if the first dot is located at (350, 400) and the second dot at (200, 200), then the distance between them mathematically is (-150, -200)
				//and thus this correlates to the bottom left quadrant of the plane (negative x, negative y)
				//distance is calculated by subtracting dot1 from dot2 since lasers are travelling from dot1 to dot2

				//since the mathematical quadrant affects whether you're adding or subtracting, there must be four separate if-statements to account for each scenario
				//the calculations are the exact same for each scenario with the exception of the positive/negative signs

				//the quadrant changes since the dots are constantly rotating around each other




				//angle between two dots is calculated by taking the tan inverse of the y and x distances between them
				//(opposite/adjacent) is calculated by (y2-y1)/(x2-x1), which is represented by the 'slope' variable
				//this angle is then used to determine the x and y position of the lasers so that they are always positioned directly along the path between the two dots

				//first point of laser position is calculated by multiplying the a.length (aka hypotenuse, which is constant) by the cosine or sine of the angle (whether it's for the x or y position)
				//then the position of the first dot is added/subtracted
				//after this calculation, the laser moves by adding/subtracting the laserMovement (which is calculated via the same method, but with laserMovement instead of a.length) to make this point move toward the dot its traveling to
				//this first point of the laser is the one that's closer to the destination-dot

				//second point of dot is calculated by adding/subtracting the laserMovement calculation to/from the first dot position
				//since the second point of the laser is moving at the same rate as the first point due to the laserMovement calculation, the length of the laser remains constant
				
				
				
				
				
				
				double slope;
				if (i == j)	 {
					slope = 0; //if i == j, that means that the program will try to calculate the slope between a dot and itself
					//in this case, the slope calculation will try to divide by zero, which produces NaN and messes up the math
					//if we don't account for this by manually setting it to zero, the math down below will spit out 0 for both the x and y positions of the laser, resulting in it popping up at the top left corner of the screen
				}
				else {
					slope =  (dotList.get(j).yy - dotList.get(i).yy) / (dotList.get(j).xx - dotList.get(i).xx);

				}

				
				
				
				/**bottom left quadrant**/
				if (dotList.get(j).yy - dotList.get(i).yy <= 0 && dotList.get(j).xx - dotList.get(i).xx >= 0) {

					a.laserXX1 = dotList.get(i).xx + Math.abs((a.length)*Math.cos(Math.atan(slope))) + (diameter/2) + Math.abs(laserMovement*Math.cos(Math.atan(slope)));
					a.laserXX2 = dotList.get(i).xx + Math.abs(laserMovement*Math.cos(Math.atan(slope))) + (diameter/2);


					a.laserYY1 = dotList.get(i).yy - Math.abs((a.length)*Math.sin(Math.atan(slope))) + (diameter/2) - Math.abs(laserMovement*Math.sin(Math.atan(slope)));
					a.laserYY2 = dotList.get(i).yy - Math.abs(laserMovement*Math.sin(Math.atan(slope))) + (diameter/2);


				}

				/**bottom right quadrant**/
				if (dotList.get(j).yy - dotList.get(i).yy <= 0 && dotList.get(j).xx - dotList.get(i).xx <= 0) {

					a.laserXX1 = dotList.get(i).xx - Math.abs((a.length)*Math.cos(Math.atan(slope))) + (diameter/2) - Math.abs(laserMovement*Math.cos(Math.atan(slope)));
					a.laserXX2 = dotList.get(i).xx - Math.abs(laserMovement*Math.cos(Math.atan(slope))) + (diameter/2);


					a.laserYY1 = dotList.get(i).yy - Math.abs((a.length)*Math.sin(Math.atan(slope))) + (diameter/2) - laserMovement*Math.sin(Math.atan(slope));
					a.laserYY2 = dotList.get(i).yy - Math.abs(laserMovement*Math.sin(Math.atan(slope))) + (diameter/2);

				}

				/**top left quadrant**/
				if (dotList.get(j).yy - dotList.get(i).yy >= 0 && dotList.get(j).xx - dotList.get(i).xx >= 0) {

					a.laserXX1 = dotList.get(i).xx + Math.abs((a.length)*Math.cos(Math.atan(slope))) + (diameter/2) + Math.abs(laserMovement*Math.cos(Math.atan(slope)));
					a.laserXX2 = dotList.get(i).xx + Math.abs(laserMovement*Math.cos(Math.atan(slope))) + (diameter/2);


					a.laserYY1 = dotList.get(i).yy + Math.abs((a.length)*Math.sin(Math.atan(slope))) + (diameter/2) + Math.abs(laserMovement*Math.sin(Math.atan(slope)));
					a.laserYY2 = dotList.get(i).yy + Math.abs(laserMovement*Math.sin(Math.atan(slope))) + (diameter/2);

				}

				/**top right quadrant**/
				if (dotList.get(j).yy - dotList.get(i).yy >= 0 && dotList.get(j).xx - dotList.get(i).xx <= 0) {


					a.laserXX1 = dotList.get(i).xx - Math.abs((a.length)*Math.cos(Math.atan(slope))) + (diameter/2) - Math.abs(laserMovement*Math.cos(Math.atan(slope)));
					a.laserXX2 = dotList.get(i).xx - Math.abs(laserMovement*Math.cos(Math.atan(slope))) + (diameter/2);


					a.laserYY1 = dotList.get(i).yy + Math.abs((a.length)*Math.sin(Math.atan(slope))) + (diameter/2) + Math.abs(laserMovement*Math.sin(Math.atan(slope)));
					a.laserYY2 = dotList.get(i).yy + Math.abs(laserMovement*Math.sin(Math.atan(slope))) + (diameter/2);

				}

				//when slope between two dots is 0, that means it really just represents the slope between a line and itself
				//to avoid trig quirks (since cos0 = 1), just manually set the laser position to under its respective dot
				if (slope == 0) {
					a.laserXX1 = dotList.get(i).xx +(diameter/2);
					a.laserXX2 = dotList.get(i).xx + (diameter/2);
					a.laserYY1 = dotList.get(i).yy + (diameter/2);
					a.laserYY2 = dotList.get(i).yy + (diameter/2);
				}

				j++;
				if (j == N) {
					i++;
					j = 0;
				}
			}


		}


	}

	public void changeColour() {

		//change colour every rotation
		Color temp;
		if (dotList.get(0).angle%360 == 0) {

			temp = dotColour;

			while (temp.equals(dotColour)) {
				dotColour = colours[(int)(Math.random()*colours.length)];
			}

		}


	}


	@Override
	public void actionPerformed(ActionEvent e) {

		dotMovement();
		shootLasers();
		changeColour();

		panel.repaint();

	}
}

