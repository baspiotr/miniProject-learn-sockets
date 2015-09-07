package Tpo3;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Piotr on 2015-04-16.
 */
class ComboBoxRenderer extends JPanel implements ListCellRenderer {

    private static final long serialVersionUID = -1L;
    private Color[] colors;
    private String[] strings;

    JPanel textPanel;
    JLabel text;

    public ComboBoxRenderer(JComboBox combo) {

        textPanel = new JPanel();
        textPanel.add(this);
        text = new JLabel();
        text.setOpaque(true);
        text.setFont(combo.getFont());
        textPanel.add(text);
    }

    public void setColors(Color[] col) {
        colors = col;
    }

    public void setStrings(String[] str) {
        strings = str;
    }

    public void refresh() {

        colors = null;
        strings = null;
    }

    public Color[] getColors() {
        return colors;
    }

    public String[] getStrings() {
        return strings;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        try {

            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }

            if (colors.length != strings.length) {
                System.out.println("colors.length does not equal strings.length");
                return this;
            } else if (colors == null) {
                System.out.println("use setColors first.");
                return this;
            } else if (strings == null) {
                System.out.println("use setStrings first.");
                return this;
            }

            text.setBackground(getBackground());

            text.setText(value.toString());
            if (index > -1) {
                text.setForeground(colors[index]);
            }

        } catch (Exception er) {
        er.printStackTrace();
        }

        return text;
    }
}