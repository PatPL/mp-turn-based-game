package Game.CustomElements;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JImage extends JPanel {
	
	private Image image;
	private Color background;
	private final boolean center;
	
	public void setImage(String path) {
		try {
			setImage(ImageIO.read(getClass().getClassLoader().getResource(path)));
		}
		catch(IOException e) {
			System.out.println("Couldn't read image ");
			e.printStackTrace();
		}
	}
	
	public void setImage(Image img) {
		image = img;
		if(!center) {
			this.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		}
		this.repaint();
	}
	
	public void setBackground(Color newBackground) {
		background = newBackground;
		this.repaint();
	}
	
	public JImage(String path) throws IOException {
		this(path, false);
	}
	
	public JImage(String path, boolean center) throws IOException {
		this(path, null, center, -1, -1);
	}
	
	public JImage(String path, int width, int height) throws IOException {
		this(path, null, false, width, height);
	}
	
	public JImage(String path, Color background, int width, int height) throws IOException {
		this(path, background, false, width, height);
	}
	
	public JImage(String path, Color background, boolean center, int width, int height) throws IOException {
		image = ImageIO.read(getClass().getClassLoader().getResource(path));
		this.center = center;
		this.background = background;
		if(!center) {
			int finalWidth = width < 0 ? image.getWidth(this) : width;
			int finalHeight = height < 0 ? image.getHeight(this) : height;
			this.setPreferredSize(new Dimension(finalWidth, finalHeight));
		}
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(center) {
			if(background != null) {
				g.setColor(background);
				g.fillRect(
					(this.getWidth() - image.getWidth(this)) / 2,
					(this.getHeight() - image.getHeight(this)) / 2,
					image.getWidth(this),
					image.getHeight(this)
				);
			}
			g.drawImage(
				image,
				(this.getWidth() - image.getWidth(this)) / 2,
				(this.getHeight() - image.getHeight(this)) / 2,
				image.getWidth(this),
				image.getHeight(this),
				this
			);
		}
		else {
			if(background != null) {
				g.setColor(background);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
			
		}
	}
}
