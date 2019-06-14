package org.assist.components.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

import org.assist.components.common.ComboBox;
import org.assist.components.common.IndicatorTF;
import org.assist.components.main.EntryTab;
import org.assist.components.main.MainFrame;
import org.assist.tools.GUITools;
import org.assist.tools.Tools;

public abstract class AbstractReportDialog extends JDialog {

	protected final EntryTab tab;
	protected IndicatorTF firmNameTF;
	protected ComboBox<String> startMonthCB;
	protected ComboBox<Integer> startYearCB;
	protected ComboBox<String> endMonthCB;
	protected ComboBox<Integer> endYearCB;
	protected JCheckBox useEndDateChB;
	protected JButton okButton;
	protected JButton cancelButton;

	protected abstract void createReport() throws JRException;

	public AbstractReportDialog(String title, EntryTab tab) {
		super(MainFrame.getInstance(), title, true				);
		this.tab = tab;

		firmNameTF = new IndicatorTF(".{0,255}", true, null);
		startMonthCB = Tools.createMonthComboBox();
		startYearCB = Tools.createYearComboBox();
		endMonthCB = Tools.createMonthComboBox();
		endYearCB = Tools.createYearComboBox();
		useEndDateChB = new JCheckBox("Enddatum berücksichtigen");

		((JPanel)getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		GUITools.makeDialogCloseableByEscape(this);
	}

	protected JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("Bericht erstellen");
		cancelButton = new JButton("Abbrechen");

		panel.add(okButton);
		panel.add(cancelButton);

		return panel;
	}

	protected void initListeners() {
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					createReport();
				}
				catch (JRException exception) {
					Tools.log(exception);
				}
			}
		});

		useEndDateChB.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				boolean selected = useEndDateChB.isSelected();
				endMonthCB.setEnabled(selected);
				endYearCB.setEnabled(selected);
			}
		});
	}

	protected void viewReport(String title, JasperPrint print) {
		JRViewer viewer = new JRViewer(print);
		viewer.setFitPageZoomRatio();
		setVisible(false);
		JDialog dialog = new JDialog((Frame)getOwner(), title, true);
		dialog.getContentPane().add(viewer, BorderLayout.CENTER);
		Dimension size = getOwner().getSize();
		dialog.setSize(size.width - 20, size.height - 20);
		dialog.setLocationRelativeTo(getOwner());
		GUITools.makeDialogCloseableByEscape(dialog);
		dialog.setVisible(true);
	}

}
