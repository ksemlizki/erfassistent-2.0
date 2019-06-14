package org.assist.components.common;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class WaitWindow extends JDialog {

	public WaitWindow(Window window) {
		super(window);
		setUndecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		JPanel contentPane = (JPanel)getContentPane();
		double[][] size = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL, TableLayoutConstants.PREFERRED, TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));
		contentPane.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(10, 10, 10, 10)));
		JLabel waitLabel = new JLabel("Bitte warten");
		waitLabel.setFont(new Font(null, Font.BOLD, 13));
		waitLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBorder(new LineBorder(Color.DARK_GRAY, 1));
		progressBar.setStringPainted(true);
		progressBar.setString("Bitte warten");
		progressBar.setFont(new Font(null, Font.BOLD, 13));
		contentPane.add(progressBar, "0, 1");

		setSize(220, 55);
		setLocationRelativeTo(null);

	}

}
