import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Course extends JPanel{
	static final int HEIGHT = 600;
	static final int WIDTH = 900;
	static final float ACCELERATION_RATE = 16; //lower value = higher acceleration
	static ArrayList<Rectangle> obstacles;
	static Rectangle target;
	private JButton[][] boxes = new JButton[20][20];
	private Ball fittest;
	private Ball[] balls;
	private float mutationRate;
	private int maxMoves;
	private int gen = 1;
	
	public Course(int numBalls, int maxMoves, float mutationRate, Rectangle target, ArrayList<Rectangle> obstacles)
	{
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		this.target = new Rectangle(Course.WIDTH - 21, Course.HEIGHT / 2 - 10, 21, 21);
		this.maxMoves = maxMoves;

		setLayout(new GridLayout(boxes.length, boxes[0].length));
		for(int r = 0; r < boxes.length; r++)
		{
			for(int c = 0; c < boxes[0].length; c++)
			{
				boxes[r][c] = new JButton();
				boxes[r][c].setBorderPainted(false);
				boxes[r][c].setContentAreaFilled(false);
				boxes[r][c].addActionListener(e -> {
					JButton button = ((JButton)e.getSource());
					Rectangle obstacle = button.getBounds();
					int index = this.obstacles.indexOf(obstacle);
					if(index == -1)
						this.obstacles.add(obstacle);
					else
						this.obstacles.remove(index);
					for(int i = 0; i < numBalls; i++)
						balls[i] = new Ball(maxMoves);
					gen = 0;
				});
				add(boxes[r][c]);
			}
		}
		
		if(obstacles != null)
			this.obstacles = obstacles;
		else {
			this.obstacles = new ArrayList<Rectangle>();
//			int count = 25;
//			while(count > 0)
//			{
//				int r = (int)(Math.random() * boxes.length);
//				int c = (int)(Math.random() * boxes[0].length);
//				System.out.println(boxes[r][c].getBounds());
//				if(this.obstacles.indexOf(boxes[r][c].getBounds()) != -1)
//				{
//					this.obstacles.add(boxes[r][c].getBounds());
//					count--;
//				}
//			}
		}
		
		balls = new Ball[numBalls];
		for(int i = 0; i < numBalls; i++)
			balls[i] = new Ball(maxMoves);
		
		this.mutationRate = mutationRate;
		
		
		calcFittest();
		
		
		Timer singleGenAnimator = new Timer(25, e -> {
			{
				for(int i = 0; i < numBalls; i++)
					balls[i].move();
				
				calcFittest();
				repaint();
			}
		});
		
		Timer timer = new Timer(singleGenAnimator.getDelay() * (maxMoves + 1), e -> {
			singleGenAnimator.stop();
			System.out.println("Generation: " + gen + ", fitness: " + fittest.getFitness());

			//find max velocity vector
//			float[] fastest = new float[2];
//			for(int i = 0; i < numBalls; i++) {
//				if(balls[i].getXVelocity() > fastest[0])
//					fastest[0] = balls[i].getXVelocity();
//				if(balls[i].getYVelocity() > fastest[1])
//					fastest[1] = balls[i].getYVelocity();
//			}
//			System.out.println("Maximum velocity vector: " + fastest[0] + ", " + fastest[1]);
			
			evolve();
			
			singleGenAnimator.start();
		});
		
		singleGenAnimator.start();
		timer.start();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLUE);
		for(int i = 0; i < obstacles.size(); i++)
		{
			if(obstacles.get(i) == null)
				break;
			g2.fill(obstacles.get(i));
		}
		g2.setColor(Color.GREEN);
		g2.fill(target);
		g2.drawLine((int)fittest.getXPosition(), (int)fittest.getYPosition(), (int)target.getCenterX(), (int)target.getCenterY());
		g2.setColor(new Color(255, 0, 0, 100));
		for(int i = 0; i < balls.length; i++) {
			//represent ball as an arrow
			g2.drawArc((int)balls[i].getXPosition() - 2, (int)balls[i].getYPosition() - 2, 5, 5, (int)(Math.atan2(-balls[i].getXVelocity(), -balls[i].getYVelocity()) * 57.2958), 180);
			//represent ball as an oval
			//g2.fillOval((int)balls[i].getXPosition() - 2, (int)balls[i].getYPosition() - 2, 5, 5);
			//streaks
			g2.drawLine((int)balls[i].getXPosition(), (int)balls[i].getYPosition(), (int)(balls[i].getXPosition() - balls[i].getXVelocity()), (int)(balls[i].getYPosition() - balls[i].getYVelocity()));
		}
		g2.setColor(Color.WHITE);
		g2.drawString("Generation: " + gen, WIDTH / 2 - 20, 15);
		g2.drawString("Fitness: " + fittest.getFitness(), WIDTH / 2 - 40, 30);
	}

	private void calcFittest()
	{
		Ball fittest = balls[0];
		for(int i = 1; i < balls.length; i++)
			if(balls[i].getFitness() > fittest.getFitness())
				fittest = balls[i];
		this.fittest = fittest;
	}
	
	public void evolve() {
			
		double[] proportions = new double[balls.length];
		double sum = 0;
		for(int i = 0; i < proportions.length; i++)
			sum += balls[i].getFitness();
		for(int i = 0; i < proportions.length; i++)
			proportions[i] = balls[i].getFitness() / sum;
		
		gen++;
		Ball[] newBalls = new Ball[balls.length];
		newBalls[0] = new Ball(fittest.getInstructions());
		for(int i = 1; i < balls.length; i++)
		{
			Ball mother = chooseParent(proportions), father = chooseParent(proportions);
			float[][] instructions = new float[maxMoves][2];
			for(int k = 0; k < maxMoves; k++)
			{
//				if(Math.random() * 5 * (father.getStepsTaken() + mother.getStepsTaken()) / k < mutationRate)
				if(Math.random() * 2.5 * (father.getStepsTaken() + mother.getStepsTaken()) / k / k < mutationRate)
					instructions[k] = new float[] {(float)Math.random() / ACCELERATION_RATE - (.5f / ACCELERATION_RATE), (float)Math.random() / ACCELERATION_RATE - (.5f / ACCELERATION_RATE)};
				else
					if(k < maxMoves / 2)//Math.random() < .5
						instructions[k] = mother.getInstructions()[k];
					else
						instructions[k] = father.getInstructions()[k];
				
			}
			newBalls[i] = new Ball(instructions);
		}
		balls = newBalls;
	}
	
	public Ball chooseParent(double[] props)
	{
		int index = 0;
		double x = Math.random();
		while(x > 0)
			x -= props[index++];
		return balls[--index];
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Path Finder");
			frame.add(new Course(10000, 200, 0.00005f, null, null));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocation(200, 50);
			frame.setResizable(false);
			frame.pack();
			frame.setVisible(true);
		});
	}
}
