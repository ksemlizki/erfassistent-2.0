package org.assist.components.tabpane;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.assist.components.main.EntryTab;
import org.assist.components.main.MainFrame;
import org.assist.constants.ImageConstants;

public class TabHeader extends JPanel implements ActionListener {

	private JLabel titleLabel;
	protected TabCloseButton closeButton;
	private final ArrayList<CloseListener> listeners;

	public TabHeader(String title) {
		this.listeners = new ArrayList<CloseListener>();
		init(title);
	}

	private void init(String title) {
		setOpaque(false);
		setFocusable(false);
		double[][] sizes = { { TableLayoutConstants.PREFERRED, 17 }, { TableLayoutConstants.PREFERRED } };
		TableLayout layout = new TableLayout(sizes);
		layout.setVGap(0);
		setLayout(layout);

		titleLabel = new JLabel(title);
		titleLabel.setIcon(ImageConstants.SAVED);
		titleLabel.setIconTextGap(2);
		titleLabel.setBorder(new LineBorder(Color.red));

		closeButton = new TabCloseButton();
		closeButton.setVisible(false);
		closeButton.addActionListener(this);

		add(titleLabel, "0,0");
		add(closeButton, "1,0");
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent event) {
				closeButton.setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent event) {
				closeButton.setVisible(false);
			}

			@Override
			public void mouseClicked(MouseEvent event) {
				changeTab();
			}
		});
	}

	protected void changeTab() {
		CloseTabbedPane tabbedPane = MainFrame.getInstance().getTabbedPane();
		int index = tabbedPane.indexOfTabComponent(this);
		tabbedPane.setSelectedIndex(index);
	}

	public void addCloseListener(CloseListener listener) {
		listeners.add(listener);
	}

	public void addCloseListeners(ArrayList<CloseListener> closeListeners) {
		listeners.addAll(closeListeners);
	}

	public void removeCloseListener(CloseListener listener) {
		listeners.remove(listener);
	}

	public void setIcon(Icon icon) {
		titleLabel.setIcon(icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		CloseTabbedPane tabbedPane = (CloseTabbedPane)SwingUtilities.getAncestorOfClass(CloseTabbedPane.class, this);
		int index = tabbedPane.indexOfTabComponent(this);
		EntryTab EntryTab = (EntryTab)tabbedPane.getComponentAt(index);
		for (CloseListener listener : listeners) {
			listener.close(EntryTab);
		}
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	public String getTitle() {
		return titleLabel.getText();
	}
}
