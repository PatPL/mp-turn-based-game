package Game.CustomElements;

import Game.Base;
import Game.Units.Unit;
import Game.Units.UnitType;
import common.PlaySound;
import common.enums.Sounds;
import common.interfaces.IConsumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class JUnit {
    
    private JPanel mainPanel;
    private JImage unitImage;
    private JLabel healthLabel;
    private JLabel damageLabel;
    private JLabel rangeLabel;
    private JLabel speedLabel;
    private JLabel costLabel;
    private JPanel labelPanel;
    private JLabel nameLabel;
    private boolean selected = false;
    private boolean enabled;
    private UnitType unitType;
    
    public UnitType getUnitType () {
        return unitType;
    }
    
    public void hover () {
        if (this.enabled) {
            mainPanel.setBackground (Color.decode ("#EEEEEE"));
        }
    }
    
    public void reset () {
        if (this.enabled) {
            selected = false;
            mainPanel.setBackground (Color.decode ("#DDDDDD"));
        }
    }
    
    public void disable () {
        selected = false;
        mainPanel.setBackground (Color.decode ("#888888"));
    }
    
    public void select () {
        if (this.enabled) {
            selected = true;
            mainPanel.setBackground (Color.decode ("#BBBBBB"));
        }
    }
    
    public JPanel getMainPanel () {
        return mainPanel;
    }
    
    private void createUIComponents () throws IOException {
        unitImage = new JImage ("null64.png", 128, 128);
    }
    
    public JUnit (UnitType unitType, Base base, boolean enabled, IConsumer<Boolean> onClick) {
        Unit unit = new Unit (unitType, base);
        this.unitType = unitType;
        
        labelPanel.setOpaque (false);
        unitImage.setImage (unit.getImage ());
        
        nameLabel.setText (unitType.name);
        healthLabel.setText (String.valueOf (unit.getMaxHealth ()));
        damageLabel.setText (String.valueOf (unit.getDamage ()));
        rangeLabel.setText (String.valueOf (unit.getRange ()));
        speedLabel.setText (String.valueOf (unit.getSpeed ()));
        costLabel.setText (String.valueOf (unit.getCost ()));
        
        this.enabled = enabled;
        if (!enabled) {
            disable ();
            return;
        }
        
        mainPanel.addMouseListener (new MouseAdapter () {
            @Override
            public void mouseClicked (MouseEvent e) {
                if (!selected) {
                    PlaySound.once (Sounds.buttonPress);
                    select ();
                    if (!onClick.invoke ()) {
                        reset ();
                    }
                }
            }
            
            @Override
            public void mouseEntered (MouseEvent e) {
                if (!selected) {
                    hover ();
                }
            }
            
            @Override
            public void mouseExited (MouseEvent e) {
                if (!selected) {
                    reset ();
                }
            }
        });
        
        reset ();
    }
    
}
