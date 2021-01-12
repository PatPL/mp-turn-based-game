package Game.CustomElements;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JImage extends JPanel {
	
	private final Image image;
	private final boolean center;
	
	public JImage(String path) throws IOException {
		this(path, false);
	}
	
	public JImage(String path, boolean center) throws IOException {
		image = ImageIO.read(getClass().getClassLoader().getResource(path));
		this.center = center;
		if(!center) {
			this.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		}
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(center) {
			g.drawImage(
				image,
				(this.getWidth() - image.getWidth(this)) / 2,
				(this.getHeight() - image.getHeight(this)) / 2,
				this
			);
		}
		else {
			g.drawImage(image, 0, 0, this);
		}
	}
}
