package org.assist.components.common;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import org.assist.tools.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpecialReplaceDialog extends JDialog {
    private IndicatorTF searchTextTF;
    private IndicatorTF countTF;
    private JButton okButton;
    private JButton cancelButton;

    private boolean result = false;

    public SpecialReplaceDialog(Window parent) {
        super(parent, "Ersetzen");
        init();
    }

    private void init() {
        double preferred = TableLayoutConstants.PREFERRED;
        double[][] layoutSizes = {{10,preferred, preferred,10}, {10, preferred, preferred, 20, preferred}};

        TableLayout layout = new TableLayout(layoutSizes);
        layout.setHGap(5);
        layout.setVGap(2);

        setLayout(layout);
        searchTextTF = new IndicatorTF(".{0,255}", true, null);
        add(Tools.createLabel("Suchbegriff"), "1,1");
        add(searchTextTF, "2,1");
        countTF = new IndicatorTF(".{0,255}", true, null);
        add(Tools.createLabel("Zeichenanzahl"), "1,2");
        add(countTF, "2,2");
        add(createButtonPanel(), "1,4,2,4");
        pack();
        setLocationRelativeTo(getOwner());
    }


    protected JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("Suchen");
        cancelButton = new JButton("Abbrechen");

        panel.add(okButton);
        panel.add(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SpecialReplaceDialog.this.dispose();
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = true;
                SpecialReplaceDialog.this.dispose();
            }
        });


        return panel;
    }

    public IndicatorTF getSearchTextTF() {
        return searchTextTF;
    }

    public IndicatorTF getCountTF() {
        return countTF;
    }

    public boolean isResult() {
        return result;
    }
}
