package coolosity.tad.client.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class MainMenu extends Overlay
{

	public MainMenu()
	{
		super(0.0, 0.0, 1.0, 1.0);
	}

	@Override
	public void draw(BufferedImage img)
	{
		Graphics g = img.getGraphics();
		g.setColor(Color.CYAN);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setColor(Color.RED);
		g.fillRect(10, 10, img.getWidth()-20, img.getHeight()-20);
	}
}
