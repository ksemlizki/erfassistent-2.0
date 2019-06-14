
package org.assist.components.report;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

import org.assist.constants.ImageConstants;
import org.assist.domain.ReportMonth;

/**
 * @author k.semlizki
 * created on: 30.08.2013
 */
public class MonthReportSelectDialog extends JDialog {

	private TreeMap<ReportMonth, JasperPrint> prints;
	protected ArrayList<MonthCheckBox> boxes;
	protected JButton okButton;
	protected JButton selectAllButton;
	protected JButton deselectAllButton;

	public MonthReportSelectDialog(JDialog parentDialog, TreeMap<ReportMonth, JasperPrint> prints) {
		super(parentDialog, "Monatsauswahl", true);
		this.prints = prints;

		init();
	}

	private void init() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		boxes = new ArrayList<MonthCheckBox>();
		ItemListener itemListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				changeBtnEnabledState();
			}
		};

		for (ReportMonth reportMonth : prints.keySet()) {
			MonthCheckBox checkBox = new MonthCheckBox(reportMonth);
			checkBox.addItemListener(itemListener);
			panel.add(checkBox);
			boxes.add(checkBox);
		}
		JScrollPane pane = new JScrollPane(panel);


		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("Ausdrucken");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					printSelectedReports();
				}
				catch (Exception exception) {
					exception.printStackTrace();
				}

			}
		});

		JButton cancelButton = new JButton("Abbrechen");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
		pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(createToolbara(), BorderLayout.NORTH);
		add(pane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setSize(320, 340);
		setResizable(false);
		setLocationRelativeTo(getOwner());
		changeBtnEnabledState();
	}

	/**
	 * @author k.semlizki
	 * created on: 02.09.2013
	 * @return
	 */
	private JToolBar createToolbara() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		selectAllButton = new JButton("", ImageConstants.SELECT_ALL);
		deselectAllButton = new JButton("", ImageConstants.DESELECT_ALL);
		selectAllButton.setFocusable(false);
		selectAllButton.setEnabled(false);
		selectAllButton.setToolTipText("Alle auswählen");

		deselectAllButton.setToolTipText("Alle abwählen");
		deselectAllButton.setFocusable(false);

		toolBar.add(selectAllButton);
		toolBar.add(deselectAllButton);


		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (MonthCheckBox checkBox : boxes) {
					checkBox.setSelected(true);
				}

				deselectAllButton.setEnabled(true);
				selectAllButton.setEnabled(false);
			}
		});

		deselectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (MonthCheckBox checkBox : boxes) {
					checkBox.setSelected(false);
				}

				selectAllButton.setEnabled(true);
				deselectAllButton.setEnabled(false);
			}
		});

		return toolBar;
	}

	/**
	 * @author k.semlizki
	 * created on: 30.08.2013
	 * @throws JRException
	 */
	protected void printSelectedReports() throws Exception {
		JasperPrint mainReport = null;
		for (MonthCheckBox checkBox : boxes) {
			if (checkBox.isSelected()) {
				if (mainReport == null) {
					mainReport = prints.get(checkBox.getMonth());
				}
				else {
					addPagesToReport(mainReport, prints.get(checkBox.getMonth()));
				}
			}
		}

		JasperPrintManager.printReport(mainReport, true);
		setVisible(false);
	}

	private void addPagesToReport(JasperPrint mainPrint, JasperPrint otherPrint) {
		List<?> pages = otherPrint.getPages();
		for (int j = 0; j < pages.size(); j++) {
			JRPrintPage object = (JRPrintPage) pages.get(j);
			mainPrint.addPage(object);
		}
	}

	/**
	 * @author k.semlizki
	 * created on: 02.09.2013
	 */
	protected void changeBtnEnabledState() {
		int selectedCount = 0;
		for (MonthCheckBox checkBox : boxes) {
			if (checkBox.isSelected()) {
				selectedCount++;
			}
		}
		okButton.setEnabled(selectedCount > 0);
		selectAllButton.setEnabled(selectedCount != boxes.size());
		deselectAllButton.setEnabled(selectedCount != 0);
	}



}
