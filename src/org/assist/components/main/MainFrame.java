package org.assist.components.main;

import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.assist.components.tabpane.CloseListener;
import org.assist.components.tabpane.CloseTabbedPane;
import org.assist.components.tabpane.TabHeader;
import org.assist.constants.ImageConstants;

public class MainFrame extends JFrame {

	private static MainFrame instance;

	protected boolean showSaveMessage;
	protected CloseTabbedPane tabbedPane;
	protected MainMenu mainMenu;
	private TreeSet<String> savedFiles;


	private MainFrame() {
		savedFiles = new TreeSet<String>();
	}

	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
			instance.init();
		}

		return instance;
	}

	private void init() {
		setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
		setTitle("Erfassungsassistent");
		//setSize(1152, 864);
		setLocationRelativeTo(null);
		setIconImage(ImageConstants.FRAME.getImage());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainMenu = new MainMenu(this);
		setJMenuBar(mainMenu);
		setState(MAXIMIZED_BOTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				exit();
			}

			@Override
			public void windowActivated(WindowEvent e) {
				showSaveMessage = true;
				showSavedFiles();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				showSaveMessage = false;
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				showSaveMessage = true;
				showSavedFiles();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				showSaveMessage = true;
				showSavedFiles();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				showSaveMessage = false;
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				showSaveMessage = false;
			}

			@Override
			public void windowOpened(WindowEvent e) {
				showSaveMessage = true;
				showSavedFiles();
			}

		});

		tabbedPane = new CloseTabbedPane();
		tabbedPane.addCloseListener(new CloseListener() {
			@Override
			public void close(EntryTab panel) {
				if (panel.checkSaved()) {
					panel.close();
					tabbedPane.remove(panel);
				}
			}
		});

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = tabbedPane.getSelectedIndex();
				if (selectedIndex != -1) {
					TabHeader tabComponentAt = (TabHeader)tabbedPane.getTabComponentAt(selectedIndex);
					if (tabComponentAt != null) {
						String title = tabComponentAt.getTitle().replace('*', ' ').trim();
						MainFrame.getInstance().setTitle(title + " - Erfassungsassistent");
						mainMenu.changeReportMenuActiveState(!getSelectedTab().isEmpty());
					}
				}
			}
		});

		tabbedPane.addContainerListener(new ContainerListener() {

			@Override
			public void componentAdded(ContainerEvent e) {
				changeMenuActiveState();
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
				changeMenuActiveState();
			}

		});
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		changeMenuActiveState();
	}


	protected void changeMenuActiveState() {
		mainMenu.changeMenuActiveState(tabbedPane.getTabCount() > 0);
	}

	protected void showSavedFiles() {
		if (!savedFiles.isEmpty()) {
			Iterator<String> iterator = savedFiles.iterator();
			StringBuilder files = new StringBuilder();
			for (int i = 0; iterator.hasNext(); i++) {
				if (i > 0) {
					files.append(", ");
				}
				files.append("<b>").append(iterator.next()).append("</b>");
			}

			int count = savedFiles.size();
			StringBuilder text = new StringBuilder("<html>Datei");
			text.append(count > 1 ? "en " : " ");
			text.append(files);
			text.append(" wurde");
			text.append(count > 1 ? "n" : "");
			text.append(" erfolgreich gespeichert.</html>");

			JOptionPane.showMessageDialog(this, text);
			savedFiles.clear();
		}
	}

	public void exit() {
		closeTabs();
		if (tabbedPane.getTabCount() == 0) {
			System.exit(0);
		}
	}

	public void closeTabs() {
		EntryTab panel;
		while (tabbedPane.getTabCount() > 0) {
			panel = (EntryTab) tabbedPane.getComponentAt(0);
			if (panel.checkSaved()) {
				tabbedPane.remove(0);
			}
			else {
				break;
			}
		}
	}

	public CloseTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public EntryTab getSelectedTab() {
		return (EntryTab) tabbedPane.getSelectedComponent();
	}

	public boolean isShowSaveMessage() {
		return showSaveMessage;
	}

	public TreeSet<String> getSavedFiles() {
		return savedFiles;
	}

}
