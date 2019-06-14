package org.assist.components.main;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.TextAction;

import org.assist.components.common.IUndoable;
import org.assist.components.report.AccountSheetReportDialog;
import org.assist.components.report.CashBookReportDialog;
import org.assist.components.tabpane.CloseTabbedPane;
import org.assist.tools.Tools;

public class MainMenu extends JMenuBar implements ActionListener {

	public enum Action {
		NEW_FILE, INSERT_FILE, OPEN_TEXT, OPEN_EXCEL, SAVE_FILE_AS, SAVE_FILE, ACCOUNT_SHEET, CASH_BOOK, CLOSE_ALL, EXIT, OPEN_LEX_EXCEL, UNDO, REDO, COPY, CUT, PASTE, SELECT_ALL;
	}

	private static final Font FONT = new Font(null, Font.PLAIN, 12);
	private JMenuItem saveMI;
	private JMenuItem saveAsMI;
	private JMenuItem insertMI;
	private JMenuItem closeAll;
	private JMenuItem accountSheetMI;
	private JMenuItem cashBookMI;
	private JMenu reportMenu;
	private JMenu editMenu;
	private final MainFrame mainFrame;

	public MainMenu(MainFrame mainFrame) {
		this.mainFrame = mainFrame;

		JMenu fileMenu = createMenu("Datei");
		editMenu = createMenu("Bearbeiten");
		reportMenu = createMenu("Bericht");

		JMenuItem newMI = createMenuItem("Neu", Action.NEW_FILE);
		newMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));

		JMenu openMenu = createMenu("Öffnen");
		JMenuItem openTextMI = createMenuItem("Text Datei", Action.OPEN_TEXT);
		openTextMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));

		JMenuItem openExcelMI = createMenuItem("MS Excell Mappe", Action.OPEN_EXCEL);
		openExcelMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));

		JMenuItem openLexExcelMI = createMenuItem("Lexware Excel Mappe", Action.OPEN_LEX_EXCEL);

		saveMI = createMenuItem("Speichern", Action.SAVE_FILE);
		saveMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

		saveAsMI = createMenuItem("Speichern unter...", Action.SAVE_FILE_AS);

		insertMI = createMenuItem("Hizufügen", Action.INSERT_FILE);
		insertMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));

		closeAll = createMenuItem("Alle schließen", Action.CLOSE_ALL);

		JMenuItem exitMI = createMenuItem("Beenden", Action.EXIT);

		accountSheetMI = createMenuItem("Kontoblatt", Action.ACCOUNT_SHEET);
		cashBookMI = createMenuItem("Kassenbericht", Action.CASH_BOOK);

		JMenuItem undoMI = createMenuItem(new TextAction("Rückgängig") {
			@Override
			public void actionPerformed(ActionEvent e) {
				((IUndoable)getFocusedComponent()).undo();
			}
		});
		undoMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		editMenu.add(undoMI);

		JMenuItem redoMI = createMenuItem(new TextAction("Wiederherstellen") {
			@Override
			public void actionPerformed(ActionEvent e) {
				((IUndoable)getFocusedComponent()).redo();
			}
		});
		redoMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		editMenu.add(redoMI);
		editMenu.addSeparator();
		JMenuItem cutMI = createMenuItem(new TextAction("Ausschneiden") {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFocusedComponent().cut();
			}
		});
		cutMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		editMenu.add(cutMI);
		JMenuItem copyMI = createMenuItem(new TextAction("Kopieren") {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFocusedComponent().copy();
			}
		});
		copyMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		editMenu.add(copyMI);

		JMenuItem pasteMI = createMenuItem(new TextAction("Einfügen") {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFocusedComponent().paste();
			}
		});
		pasteMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		editMenu.add(pasteMI);
		editMenu.addSeparator();

		JMenuItem selectAllMI = createMenuItem(new TextAction("Alles markieren") {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFocusedComponent().selectAll();
			}
		});
		selectAllMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		editMenu.add(selectAllMI);

		add(fileMenu);
		add(editMenu);
		add(reportMenu);

		reportMenu.add(accountSheetMI);
		reportMenu.add(cashBookMI);

		fileMenu.add(newMI);
		fileMenu.add(openMenu);
		fileMenu.add(insertMI);

		fileMenu.addSeparator();
		fileMenu.add(saveMI);
		fileMenu.add(saveAsMI);
		fileMenu.addSeparator();
		fileMenu.add(closeAll);
		fileMenu.addSeparator();
		fileMenu.add(exitMI);

		openMenu.add(openTextMI);
		openMenu.add(openExcelMI);
		openMenu.add(openLexExcelMI);


	}

	public void changeMenuActiveState(boolean enabled) {
		reportMenu.setVisible(enabled);
		editMenu.setVisible(enabled);
		closeAll.setEnabled(enabled);
		insertMI.setEnabled(enabled);
		saveAsMI.setEnabled(enabled);
		saveMI.setEnabled(enabled);
	}

	public void changeReportMenuActiveState(boolean enabled) {
		accountSheetMI.setEnabled(enabled);
		cashBookMI.setEnabled(enabled);

	}

	private JMenu createMenu(String name) {
		JMenu menu = new JMenu(name);
		menu.setFont(FONT);

		return menu;
	}

	private JMenuItem createMenuItem(TextAction action) {
		JMenuItem item = new JMenuItem((String)action.getValue(javax.swing.Action.NAME));
		item.setFont(FONT);
		item.setAction(action);

		return item;
	}

	private JMenuItem createMenuItem(String name, Action action) {
		JMenuItem item = new JMenuItem(name);
		item.setFont(FONT);
		if (action != null) {
			item.addActionListener(this);
			item.setActionCommand(action.name());
		}

		return item;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Action action = Action.valueOf(event.getActionCommand());
		EntryTab entryTab;
		CloseTabbedPane tabbedPane = mainFrame.getTabbedPane();
		EntryTab selectedTab = mainFrame.getSelectedTab();
		switch (action) {
			case EXIT:
				mainFrame.exit();
				break;
			case CLOSE_ALL:
				mainFrame.closeTabs();
				break;
			case NEW_FILE:
				entryTab = new EntryTab();
				entryTab.setFile(null);
				tabbedPane.addTab("Unbenannt", entryTab);
				tabbedPane.setSelectedComponent(entryTab);
				sendChange(tabbedPane);
				break;
			case OPEN_TEXT:
				entryTab = new EntryTab();
				String filename = entryTab.openTextFile();
				if (!Tools.isStringEmpty(filename)) {
					tabbedPane.addTab(filename, entryTab);
					tabbedPane.setSelectedComponent(entryTab);
					sendChange(tabbedPane);
				}
				break;
			case OPEN_EXCEL:
				entryTab = new EntryTab();
				filename = entryTab.openExcel();
				if (!Tools.isStringEmpty(filename)) {
					tabbedPane.addTab(filename, entryTab);
					tabbedPane.setSelectedComponent(entryTab);
					sendChange(tabbedPane);
				}
				break;
			case INSERT_FILE:
				entryTab = selectedTab;
				entryTab.insertTextFile();
				sendChange(tabbedPane);
				break;
			case OPEN_LEX_EXCEL:
				entryTab = new EntryTab();
				filename = entryTab.openLexwareExcel();
				if (!Tools.isStringEmpty(filename)) {
					tabbedPane.addTab(filename, entryTab);
					tabbedPane.setSelectedComponent(entryTab);
					sendChange(tabbedPane);
				}
			 break;
			case SAVE_FILE:
				selectedTab.saveFile();
				sendChange(tabbedPane);
				break;
			case SAVE_FILE_AS:
				selectedTab.setNewFile(true);
				selectedTab.saveFile();
				sendChange(tabbedPane);
//				String fileName = new File(selectedTab.getFileName()).getName();
//				fileName = fileName.substring(0, fileName.lastIndexOf('.'));
//				int index = tabbedPane.indexOfComponent(selectedTab);
//				tabbedPane.setTitleAt(index, fileName);
				break;
			case ACCOUNT_SHEET:
				final AccountSheetReportDialog accountSheetReportDialog = new AccountSheetReportDialog(selectedTab);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						accountSheetReportDialog.setVisible(true);
					}
				});
				break;
			case CASH_BOOK:
				final CashBookReportDialog cashBookReportDialog = new CashBookReportDialog(selectedTab);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						cashBookReportDialog.setVisible(true);
					}
				});
				break;
			default:
				break;
		}
	}

	private void sendChange(CloseTabbedPane tabbedPane) {
		ChangeListener[] changeListeners = tabbedPane.getChangeListeners();
		for (ChangeListener changeListener : changeListeners) {
			changeListener.stateChanged(new ChangeEvent(tabbedPane));
		}
	}

}
