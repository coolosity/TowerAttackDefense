package coolosity.tad.client;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import coolosity.tad.client.display.Overlay;
import coolosity.tad.util.TADLogger;

public class Display implements Runnable
{

	private JFrame frame;
	private JLabel label;
	private DisplaySettings settings;
	
	private ArrayList<Overlay> overlays, toadd;
	private ArrayList<Class<?>> toremove;
	
	public Display(String title, DisplaySettings settings)
	{
		this.settings = settings;
		overlays = new ArrayList<Overlay>();
		toadd = new ArrayList<Overlay>();
		toremove = new ArrayList<Class<?>>();
		frame = new JFrame(title);
		int width = 852;
		int height = 480;
		try
		{
			for(Field field : settings.getClass().getFields())
			{
				if(field.getName().startsWith("RES_"))
				{
					if(field.getInt(null)==settings.getResMode())
					{
						String[] spl = field.getName().split("_");
						width = Integer.parseInt(spl[1]);
						height = Integer.parseInt(spl[2]);
					}
				}
			}
		}
		catch(Exception e)
		{
			TADLogger.err("Could not load resolution settings from DisplaySettings. Defaulting to "+width+"x"+height);
			e.printStackTrace();
		}
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		label = new JLabel();
		frame.add(label);
		frame.setVisible(true);
		(new Thread(this)).start();
	}
	
	public void addOverlay(Overlay overlay)
	{
		toadd.add(overlay);
	}
	
	public void removeOverlay(Class<?> cls)
	{
		toremove.add(cls);
	}
	
	private BufferedImage draw()
	{
		BufferedImage img = new BufferedImage(label.getWidth(),label.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(Overlay overlay : overlays)
		{
			int xloc = (int)(img.getWidth()*overlay.getXLoc());
			int yloc = (int)(img.getHeight()*overlay.getYLoc());
			int wid = (int)(img.getWidth()*overlay.getWidth());
			int hei = (int)(img.getHeight()*overlay.getHeight());
			overlay.draw(img.getSubimage(xloc, yloc, wid, hei));
		}
		return img;
	}

	@Override
	public void run()
	{
		long lastDraw = System.currentTimeMillis();
		while(true)
		{
			while(toadd.size()>0)
			{
				overlays.add(toadd.get(0));
				toadd.remove(0);
			}
			while(toremove.size()>0)
			{
				for(int i=overlays.size()-1;i>=0;i--)
				{
					if(overlays.get(i).getClass() == toremove.get(0))
					{
						overlays.remove(i);
					}
				}
				toremove.remove(0);
			}
			BufferedImage img = draw();
			while(System.currentTimeMillis()-lastDraw<1000/settings.getFPSMax());
			label.setIcon(new ImageIcon(img));
		}
	}
}
