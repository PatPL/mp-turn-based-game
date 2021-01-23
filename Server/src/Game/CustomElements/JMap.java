package Game.CustomElements;

import Game.Game;
import Game.Units.Unit;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JMap extends JPanel {
    
    private final Image redBaseImage;
    private final Image blueBaseImage;
    private JPanel topPanel; // Used to add more unused space
    private Game game;
    private static final int padding = 8;
    private static final int unitMargin = 8;
    private static final double baseWidth = 0.15;
    private static final boolean DRAW_DEBUG_SQUARES = false;
    private static final Color redBaseFieldColor = Color.decode ("#FF4444");
    private static final Color blueBaseFieldColor = Color.decode ("#6666FF");
    private static final Color regularFieldColor = Color.decode ("#668866");
    private static final int fieldBorderWidth = 2;
    private static final Color fieldBorderColor = Color.decode ("#444444");
    private static final int unitFieldPadding = 4;
    private static final boolean healthbarVertical = true;
    private static final double healthbarHeight = 0.1;
    private static final int healthbarBorderWidth = 2;
    private static final Color healthbarBorderColor = Color.decode ("#444444");
    private static final Color healthbarFullColor = Color.green;
    private static final Color healthbarEmptyColor = Color.red;
    
    private Color getHealthbarColor (Unit unit) {
        double fullMultiplier = ((double) unit.getHealth ()) / ((double) unit.getMaxHealth ());
        double emptyMultiplier = ((double) (unit.getMaxHealth () - unit.getHealth ())) / ((double) unit.getMaxHealth ());
        return new Color (
            (int) (healthbarFullColor.getRed () * fullMultiplier + healthbarEmptyColor.getRed () * emptyMultiplier),
            (int) (healthbarFullColor.getGreen () * fullMultiplier + healthbarEmptyColor.getGreen () * emptyMultiplier),
            (int) (healthbarFullColor.getBlue () * fullMultiplier + healthbarEmptyColor.getBlue () * emptyMultiplier)
        );
    }
    
    public JMap () throws IOException {
        this.setOpaque (false);
        
        redBaseImage = ImageIO.read (getClass ().getClassLoader ().getResource ("redCastle.png"));
        blueBaseImage = ImageIO.read (getClass ().getClassLoader ().getResource ("blueCastle.png"));
    }
    
    public void setGame (Game game) {
        if (this.game == null) {
            this.game = game;
        }
    }
    
    public void setTopPanel (JPanel topPanel) {
        if (this.topPanel == null) {
            this.topPanel = topPanel;
        }
    }
    
    private int topPanelHeight () {
        if (topPanel == null) {
            return 0;
        }
        
        return topPanel.getHeight ();
    }
    
    private int mapWidth () {
        return this.getWidth () - padding * 2;
    }
    
    private int mapHeight () {
        return (this.getHeight () / 2) - padding * 2 + topPanelHeight () / 2;
    }
    
    private int pxToMapX (int px) {
        return px + padding;
    }
    
    private int pxToMapY (int px) {
        return px + padding + (this.getHeight () / 2) - topPanelHeight () / 2;
    }
    
    private interface PXConverter {
        public int convert (int px);
    }
    
    @Override
    protected void paintComponent (Graphics g) {
        super.paintComponent (g);
        
        if (this.game == null) {
            return;
        }
        
        int baseImageSize = (int) Math.round (mapWidth () * baseWidth);
        
        // Debug
        if (DRAW_DEBUG_SQUARES) {
            g.setColor (Color.red);
            g.drawRect (0, 0, this.getWidth (), this.getHeight ());
            g.setColor (Color.magenta);
            g.drawRect (0, this.getHeight () / 2, this.getWidth (), this.getHeight () / 2);
            g.setColor (Color.blue);
            g.drawRect (pxToMapX (0), pxToMapY (0), mapWidth (), mapHeight ());
            g.setColor (Color.red);
            g.drawRect (pxToMapX (baseImageSize), pxToMapY (0), mapWidth () - baseImageSize * 2, mapHeight ());
        }
        
        // Bases
        g.drawImage (
            redBaseImage,
            pxToMapX (0),
            pxToMapY ((mapHeight () - baseImageSize) / 2),
            baseImageSize,
            baseImageSize,
            this
        );
        g.drawImage (
            blueBaseImage,
            pxToMapX (mapWidth () - baseImageSize),
            pxToMapY ((mapHeight () - baseImageSize) / 2),
            baseImageSize,
            baseImageSize,
            this
        );
        
        // Units
        int unitFieldWidth = mapWidth () - baseImageSize * 2;
        // Horizontal field count
        int fieldLength = game.getColumns ();
        // Vertical field count
        int fieldHeight = game.getRows ();
        
        if (fieldHeight * fieldLength == 0) {
            // No field on map
            return;
        }
        
        // Width-space available for a unit
        int unitAvailableWidth = (unitFieldWidth - ((fieldLength + 1) * (unitMargin))) / fieldLength;
        int unitAvailableHeight = (mapHeight () - ((fieldHeight + 1) * (unitMargin))) / fieldHeight;
        
        int unitFieldSize = Math.min (unitAvailableWidth, unitAvailableHeight);
        
        // Free space added due to scaling into a square
        int unitWidthFreefloat = (unitAvailableWidth - unitFieldSize) / 2;
        int unitHeightFreefloat = (unitAvailableHeight - unitFieldSize) / 2;
        
        PXConverter rawUnitStartPosX = x -> (
            (1 + x) * unitMargin +                // Required margins between units
                x * unitFieldSize +               // Previous units themselves
                (1 + 2 * x) * unitWidthFreefloat  // Free space added due to bad aspect ratio of the map
        );
        PXConverter unitStartPosX = x -> pxToMapX (
            baseImageSize + rawUnitStartPosX.convert (x) + (
                unitFieldWidth - rawUnitStartPosX.convert (fieldLength - 1) - unitFieldSize - unitMargin - unitWidthFreefloat
            ) / 2
        );
        
        PXConverter rawUnitStartPosY = y -> (
            (1 + y) * unitMargin +                // Required margins between units
                y * unitFieldSize +               // Previous units themselves
                (1 + 2 * y) * unitHeightFreefloat // Free space added due to bad aspect ratio of the map
        );
        PXConverter unitStartPosY = y -> pxToMapY (
            rawUnitStartPosY.convert (y) + (
                mapHeight () - rawUnitStartPosY.convert (fieldHeight - 1) - unitFieldSize - unitMargin - unitHeightFreefloat
            ) / 2
        );
        
        for (int i = 0; i < fieldLength; ++i) {
            for (int j = 0; j < fieldHeight; ++j) {
                // Debug
                if (DRAW_DEBUG_SQUARES) {
                    g.setColor (Color.black);
                    g.drawRect (
                        unitStartPosX.convert (i) - unitMargin - unitWidthFreefloat,
                        unitStartPosY.convert (j) - unitMargin - unitHeightFreefloat,
                        unitFieldSize + unitMargin * 2 + unitWidthFreefloat * 2,
                        unitFieldSize + unitMargin * 2 + unitHeightFreefloat * 2
                    );
                    g.setColor (Color.orange);
                    g.drawRect (
                        unitStartPosX.convert (i) - unitMargin,
                        unitStartPosY.convert (j) - unitMargin,
                        unitFieldSize + unitMargin * 2,
                        unitFieldSize + unitMargin * 2
                    );
                }
                
                // Unit fields
                // Border
                g.setColor (fieldBorderColor);
                g.fillRect (
                    unitStartPosX.convert (i),
                    unitStartPosY.convert (j),
                    unitFieldSize,
                    unitFieldSize
                );
                
                // Field background
                if (i == 0) {
                    g.setColor (redBaseFieldColor);
                } else if (i == fieldLength - 1) {
                    g.setColor (blueBaseFieldColor);
                } else {
                    g.setColor (regularFieldColor);
                }
                g.fillRect (
                    unitStartPosX.convert (i) + fieldBorderWidth,
                    unitStartPosY.convert (j) + fieldBorderWidth,
                    unitFieldSize - fieldBorderWidth * 2,
                    unitFieldSize - fieldBorderWidth * 2
                );
                
                // Units
                Unit unit = game.getUnit (i, j);
                if (unit != null) {
                    g.drawImage (
                        unit.getImage (),
                        unitStartPosX.convert (i) + fieldBorderWidth,
                        unitStartPosY.convert (j) + fieldBorderWidth,
                        unitFieldSize - fieldBorderWidth * 2,
                        unitFieldSize - fieldBorderWidth * 2,
                        this
                    );
                    
                    if (unit.getTeam () != 0) {
                        int healthbarSize = Math.max ((int) (unitFieldSize * healthbarHeight), healthbarBorderWidth * 3);
                        g.setColor (healthbarBorderColor);
                        if (healthbarVertical) {
                            g.fillRect (
                                unitStartPosX.convert (i) + fieldBorderWidth + unitFieldPadding,
                                unitStartPosY.convert (j) + fieldBorderWidth + unitFieldPadding,
                                healthbarSize,
                                unitFieldSize - (fieldBorderWidth + unitFieldPadding) * 2
                            );
                            g.setColor (getHealthbarColor (unit));
                            int fullHeight = unitFieldSize - (fieldBorderWidth + unitFieldPadding + healthbarBorderWidth) * 2;
                            int height = fullHeight * unit.getHealth () / unit.getMaxHealth ();
                            g.fillRect (
                                unitStartPosX.convert (i) + fieldBorderWidth + unitFieldPadding + healthbarBorderWidth,
                                unitStartPosY.convert (j) + fieldBorderWidth + unitFieldPadding + healthbarBorderWidth + fullHeight - height,
                                healthbarSize - healthbarBorderWidth * 2,
                                height
                            );
                        } else {
                            g.fillRect (
                                unitStartPosX.convert (i) + fieldBorderWidth + unitFieldPadding,
                                unitStartPosY.convert (j) + unitFieldSize - fieldBorderWidth - unitFieldPadding - healthbarSize,
                                unitFieldSize - (fieldBorderWidth + unitFieldPadding) * 2,
                                healthbarSize
                            );
                            g.setColor (getHealthbarColor (unit));
                            int fullWidth = unitFieldSize - (fieldBorderWidth + unitFieldPadding + healthbarBorderWidth) * 2;
                            int width = fullWidth * unit.getHealth () / unit.getMaxHealth ();
                            g.fillRect (
                                unitStartPosX.convert (i) + fieldBorderWidth + unitFieldPadding + healthbarBorderWidth,
                                unitStartPosY.convert (j) + unitFieldSize - fieldBorderWidth - unitFieldPadding - healthbarSize + healthbarBorderWidth,
                                width,
                                healthbarSize - healthbarBorderWidth * 2
                            );
                        }
                    }
                }
            }
        }
    }
}
