package coolosity.tad.client.display;

import java.awt.image.BufferedImage;

public abstract class Overlay
{
	
	private double xloc, yloc, width, height;
	
	public Overlay(double xloc, double yloc, double width, double height)
	{
		this.xloc = xloc;
		this.yloc = yloc;
		this.width = width;
		this.height = height;
	}
	
	public abstract void draw(BufferedImage img);
	
	public double getXLoc()
	{
		return xloc;
	}
	
	public double getYLoc()
	{
		return yloc;
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public double getHeight()
	{
		return height;
	}
}
