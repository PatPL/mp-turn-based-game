package Client;

import javax.swing.*;
import java.awt.*;

public class HostGameFormGUI {
    private final JFrame parent;
    private final JDialog dialog;
    private JPanel panel1;
    private JButton cancelButton;
    private JButton hostGameButton;
    private JSlider lengthInputSlider;
    private JSlider heightInputSlider;
    private JLabel lengthInputLabel;
    private JLabel heightInputLabel;
    private JLabel boardPreviewLabel;
    private JTextField serverNameInput;
    private JCheckBox aiOponentCheckbox;
    private JCheckBox publicGameCheckbox;
    
    public interface HostGameFormSubmitHandler {
        public void onSubmit (
            int length,
            int height,
            String name,
            boolean ai,
            boolean isPublic
        );
    }
    
    public void refresh () {
        lengthInputLabel.setText (String.valueOf (lengthInputSlider.getValue ()));
        heightInputLabel.setText (String.valueOf (heightInputSlider.getValue ()));
        
        boardPreviewLabel.setText (String.format (
            "<html>%s</html>",
            "※".repeat (lengthInputSlider.getValue ()).concat ("<br />").repeat (heightInputSlider.getValue ())
        ));
        
        dialog.pack ();
    }
    
    public void setupListeners () {
        lengthInputSlider.addChangeListener (l -> refresh ());
        heightInputSlider.addChangeListener (l -> refresh ());
    }
    
    public HostGameFormGUI (HostGameFormSubmitHandler handler, JFrame parent) {
        JDialog dialog = new JDialog (parent);
        this.parent = parent;
        this.dialog = dialog;
        dialog.setContentPane (panel1);
        dialog.setUndecorated (true);
        dialog.setAlwaysOnTop (true);
        dialog.setModalityType (Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable (false);
        dialog.pack ();
        dialog.setLocation (-dialog.getWidth () / 2, -dialog.getHeight () / 2);
        dialog.setLocationRelativeTo (parent);
        
        setupListeners ();
        refresh ();
        
        hostGameButton.addActionListener (l -> {
            handler.onSubmit (
                lengthInputSlider.getValue (),
                heightInputSlider.getValue (),
                serverNameInput.getText ().replace (";", ","),
                aiOponentCheckbox.isSelected (),
                publicGameCheckbox.isSelected ()
            );
            dialog.dispose ();
        });
        
        cancelButton.addActionListener (l -> {
            dialog.dispose ();
        });
        
        dialog.setVisible (true);
    }
    
}
