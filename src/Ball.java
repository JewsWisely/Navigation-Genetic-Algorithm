import java.util.Random;

public class Ball {
	
	static Random rng = new Random();
	static float maxVelocity = 100;
	private float[][] instructions;
	private float[][] dna = new float[3][2];
	private boolean isAlive = true;
	private boolean hitTarget = false;
	private int stepsTaken = 0;
	private double distance;
	
	public Ball(int numSteps) {
		dna[0][0] = numSteps == 0 ? Course.WIDTH - 20 : 100;
		dna[0][1] = Course.HEIGHT / 2;
		
		instructions = new float[numSteps][2];
		for(int i = 0; i < numSteps; i++)
			for(int k = 0; k < 2; k++)
				instructions[i][k] = rng.nextFloat() / Course.ACCELERATION_RATE - (.5f / Course.ACCELERATION_RATE);
	}

	public Ball(float[][] instructions) {
		dna[0][0] = 100;
		dna[0][1] = Course.HEIGHT / 2;
		this.instructions = instructions;
	}
	
	public void move()
	{
		if(isAlive) {
			for(int i = 0; i < 2; i++)
			{
				dna[2][i] += instructions[stepsTaken][i];
				dna[1][i] += dna[2][i];
				if(dna[1][i] > maxVelocity)
					dna[1][i] = maxVelocity;
				else if(dna[1][i] < -maxVelocity)
					dna[1][i] = -maxVelocity;
				dna[0][i] += dna[1][i];
			}
			distance += Math.sqrt(Math.pow(dna[1][0], 2) + Math.pow(dna[1][1], 2));
			stepsTaken++;
		}
		if(dna[0][0] > Course.WIDTH) {
			dna[0][0] = Course.WIDTH;
			isAlive = false;
		}
		else if(dna[0][0] < 0) {
			dna[0][0] = 0;
			isAlive = false;
		}
		if(dna[0][1] > Course.HEIGHT) {
			dna[0][1] = Course.HEIGHT;
			isAlive = false;
		}
		else if(dna[0][1] < 0) {
			dna[0][1] = 0;
			isAlive = false;
		}
		
		else if(Course.target.contains(dna[0][0], dna[0][1])) {
			isAlive = false;
			hitTarget = true;
		}
		for(int i = 0; i < Course.obstacles.size(); i++){
			if(Course.obstacles.get(i) != null && Course.obstacles.get(i).contains(dna[0][0], dna[0][1]))
			{
				isAlive = false;
			}
		
		}
	}
	
	public int getStepsTaken()
	{
		return stepsTaken;
	}
	
	public float getFitness()
	{
		//return (float)(1 / (1 + Math.pow(dna[0][0] - Course.target.getCenterX(), 2) + Math.pow(dna[0][1] - Course.target.getCenterY(), 2)) * distance * (hitTarget ? 1 : distance));
		//return (float)(1000000 / (1 + Math.sqrt(Math.pow(dna[0][0] - Course.target.getCenterX(), 2) + Math.pow(dna[0][1] - Course.target.getCenterY(), 2)) * (hitTarget ? distance / stepsTaken : 1000000)));
		//return (1f) / (float)(1 + stepsTaken * (hitTarget ? 1 : Math.sqrt(Math.pow(dna[0][0] - Course.target.getCenterX(), 2) + Math.pow(dna[0][1] - Course.target.getCenterY(), 2))));
		return (float)(1 / ((hitTarget ? stepsTaken : 10 * Math.sqrt(Math.pow(dna[0][0] - Course.target.getCenterX(), 2) + Math.pow(dna[0][1] - Course.target.getCenterY(), 2)))));
	}

	public float[][] getInstructions() {
		return instructions;
	}

	public void setInstructions(float[][] instructions) {
		this.instructions = instructions;
	}

	public float[][] getDna() {
		return dna;
	}

	public void setDna(float[][] dna) {
		this.dna = dna;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public float getXPosition()
	{
		return dna[0][0];
	}
	
	public float getYPosition()
	{
		return dna[0][1];
	}
	
	public float getXVelocity()
	{
		return dna[1][0];
	}
	
	public float getYVelocity()
	{
		return dna[1][1];
	}
	
	public float getXAcceleration()
	{
		return dna[2][0];
	}
	
	public float getYAcceleration()
	{
		return dna[2][1];
	}

	public float getMaxVelocity() {
		return maxVelocity;
	}

	public void setXPosition(float n)
	{
		dna[0][0] = n;
	}
	
	public void setYPosition(float n)
	{
		dna[0][1] = n;
	}
	
	public void setXVelocity(float n)
	{
		dna[1][0] = n;
	}
	
	public void setYVelocity(float n)
	{
		dna[1][1] = n;
	}
	
	public void setXAcceleration(float n)
	{
		dna[2][0] = n;
	}
	
	public void setYAcceleration(float n)
	{
		dna[2][1] = n;
	}

	public void setMaxVelocity(float maxVelocity) {
		this.maxVelocity = maxVelocity;
	}
}
