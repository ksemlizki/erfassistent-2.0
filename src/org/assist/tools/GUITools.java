package org.assist.tools;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.ItemSelectable;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class GUITools {

	public static final String CLOSE_ACTION_NAME = "CLOSEDIALOG";

	/**
	 * Liefert alle Komponenten vom Typ <code>clazz</code> die im
	 * <code>parentComponent</code> enthalten sind
	 */
	public static <T extends Component> ArrayList<T> findComponents(Component parentComponent, Class<T> clazz) {
		ArrayList<T> list = new ArrayList<T>();
		findComponents(list, parentComponent, clazz);
		return list;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Component> void findComponents(ArrayList<T> list, Component parentComponent, Class<T> clazz) {
		if (clazz.isAssignableFrom(parentComponent.getClass())) {
			list.add((T) parentComponent);
		}
		else if (parentComponent instanceof Container) {
			Component[] components = ((Container) parentComponent).getComponents();
			for (Component component : components) {
				findComponents(list, component, clazz);
			}
		}
	}

	public static void releaseMemory(Window window) {
		for (Component component : window.getComponents()) {
			releaseMemory(component);
		}

		if (window instanceof JDialog) {
			releaseMemory(((JDialog) window).getGlassPane());
		}
		if (window instanceof JFrame) {
			releaseMemory(((JFrame) window).getGlassPane());
		}
		if (window instanceof JWindow) {
			releaseMemory(((JWindow) window).getGlassPane());
		}

		for (ComponentListener listener : window.getComponentListeners()) {
			window.removeComponentListener(listener);
		}
		for (ContainerListener listener : window.getContainerListeners()) {
			window.removeContainerListener(listener);
		}

		for (WindowStateListener listener : window.getWindowStateListeners()) {
			window.removeWindowStateListener(listener);
		}
		for (WindowFocusListener listener : window.getWindowFocusListeners()) {
			window.removeWindowFocusListener(listener);
		}
		for (WindowListener listener : window.getWindowListeners()) {
			listener.windowClosed(null);
			window.removeWindowListener(listener);
		}

		System.runFinalization();
	}

	public static void releaseMemory(Component component) {
		if (component != null) {
			processReleaseMemory(component);
		}

		System.runFinalization();
	}

	public static void processReleaseMemory(Component component) {
		if (component != null) {
			if (component instanceof Container) {
				Container container = (Container) component;
				for (Component comp : container.getComponents()) {
					releaseMemory(comp);
					// container.remove(comp);
				}
			}
			if (component instanceof JRootPane) {
				releaseMemory(((JRootPane) component).getGlassPane());
			}
			if (component instanceof RootPaneContainer) {
				releaseMemory(((RootPaneContainer) component).getGlassPane());
			}
			if (component instanceof JInternalFrame) {
				releaseMemory(((JInternalFrame) component).getGlassPane());
			}

			removeListeners(component);

			if (component instanceof IReleaseable) {
				((IReleaseable) component).release();
			}
		}
	}

	public static void removeListeners(Component component) {
		if (component instanceof Container) {
			Container container = (Container) component;
			for (ContainerListener listener : container.getContainerListeners()) {
				container.removeContainerListener(listener);
			}
		}

		for (FocusListener listener : component.getFocusListeners()) {
			component.removeFocusListener(listener);
		}

		for (ComponentListener listener : component.getComponentListeners()) {
			component.removeComponentListener(listener);
		}
		for (MouseListener listener : component.getMouseListeners()) {
			component.removeMouseListener(listener);
			if (listener instanceof IReleaseable) {
				((IReleaseable) listener).release();
			}
		}
		for (MouseMotionListener listener : component.getMouseMotionListeners()) {
			component.removeMouseMotionListener(listener);
			if (listener instanceof IReleaseable) {
				((IReleaseable) listener).release();
			}
		}
		for (MouseWheelListener listener : component.getMouseWheelListeners()) {
			component.removeMouseWheelListener(listener);
			if (listener instanceof IReleaseable) {
				((IReleaseable) listener).release();
			}
		}
		for (KeyListener listener : component.getKeyListeners()) {
			component.removeKeyListener(listener);
		}
		for (PropertyChangeListener listener : component.getPropertyChangeListeners()) {
			component.removePropertyChangeListener(listener);
		}

		for (HierarchyBoundsListener listener : component.getHierarchyBoundsListeners()) {
			component.removeHierarchyBoundsListener(listener);
		}

		for (HierarchyListener listener : component.getHierarchyListeners()) {
			component.removeHierarchyListener(listener);
		}

		for (InputMethodListener listener : component.getInputMethodListeners()) {
			component.removeInputMethodListener(listener);
		}

		if (component instanceof JComponent) {
			JComponent jComponent = (JComponent) component;
			for (AncestorListener listener : jComponent.getAncestorListeners()) {
				jComponent.removeAncestorListener(listener);
			}
			for (VetoableChangeListener listener : jComponent.getVetoableChangeListeners()) {
				jComponent.removeVetoableChangeListener(listener);
			}

			jComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).clear();
			jComponent.getInputMap(JComponent.WHEN_FOCUSED).clear();
			jComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).clear();

			jComponent.getActionMap().clear();
		}

		if (component instanceof ItemSelectable) {
			ItemSelectable itemSelectable = (ItemSelectable) component;
			for (ItemListener listener : component.getListeners(ItemListener.class)) {
				itemSelectable.removeItemListener(listener);
			}
		}

		if (component instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) component;
			for (ActionListener listener : component.getListeners(ActionListener.class)) {
				button.removeActionListener(listener);
			}
			for (ChangeListener listener : button.getChangeListeners()) {
				button.removeChangeListener(listener);
			}
		}
		else if (component instanceof JTextComponent) {
			JTextComponent field = (JTextComponent) component;
			PlainDocument document = ((PlainDocument) field.getDocument());
			DocumentListener[] listeners = document.getDocumentListeners();

			for (DocumentListener listener : listeners) {
				document.removeDocumentListener(listener);
			}

			UndoableEditListener[] undoableEditListeners = document.getUndoableEditListeners();
			for (UndoableEditListener listener : undoableEditListeners) {
				document.removeUndoableEditListener(listener);
			}

			for (CaretListener listener : field.getCaretListeners()) {
				field.removeCaretListener(listener);
			}
		}
		else if (component instanceof JViewport) {
			JViewport viewport = (JViewport) component;
			ChangeListener[] changeListeners = viewport.getChangeListeners();
			for (ChangeListener changeListener : changeListeners) {
				viewport.removeChangeListener(changeListener);
			}
		}
		else if (component instanceof JPopupMenu) {
			JPopupMenu popupMenu = (JPopupMenu) component;
			for (PopupMenuListener listener : popupMenu.getPopupMenuListeners()) {
				popupMenu.removePopupMenuListener(listener);
			}
			for (MenuKeyListener listener : popupMenu.getMenuKeyListeners()) {
				popupMenu.removeMenuKeyListener(listener);
			}
			popupMenu.setInvoker(null);
			popupMenu.removeAll();
		}
	}

	public static void extendFocusTraversalKeys(JComponent component) {
		Set<AWTKeyStroke> focusTraversalKeys = component.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newTraversalKeys = new HashSet<AWTKeyStroke>(focusTraversalKeys);
		newTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_RIGHT, 0));
		newTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_DOWN, 0));
		component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newTraversalKeys);

		focusTraversalKeys = component.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		newTraversalKeys = new HashSet<AWTKeyStroke>(focusTraversalKeys);
		newTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_LEFT, 0));
		newTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_UP, 0));
		component.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newTraversalKeys);
	}

	public static void performTabKey(Component component) {
		Set<KeyStroke> strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
		component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
		strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
		component.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);
	}

	public static void initAreaTabFocus(JComponent component, ArrayList<Component> orderedComponents) {
		initAreaTabFocus(component, orderedComponents, orderedComponents);
	}

	public static void initAreaTabFocus(JComponent component, final ArrayList<Component> orderedComponents, final ArrayList<Component> areaOrderedComponents) {
		performTabKey(component);

		KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
		final String ACTION_NAME = "areaTabFocus";
		component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, ACTION_NAME);
		component.getActionMap().put(ACTION_NAME, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				boolean focusableComponentFounded = false;
				boolean focusSet = false;
				for (Component orderedComponent : orderedComponents) {
					if (orderedComponent.isEnabled()) {
						if (!focusableComponentFounded) {
							if (orderedComponent.isFocusOwner()) {
								focusableComponentFounded = true;
							}
						}
						else {
							if (areaOrderedComponents.indexOf(orderedComponent) != -1) {
								orderedComponent.requestFocus();
								focusSet = true;
								break;
							}
						}
					}
				}

				if (!focusSet) {
					areaOrderedComponents.get(0).requestFocus();
				}
			}
		});
	}


	public static void makeDialogCloseableByEscape(final JDialog dialog) {
		AbstractAction closeAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};

		JComponent component = dialog.getRootPane();
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CLOSE_ACTION_NAME);
		component.getActionMap().put(CLOSE_ACTION_NAME, closeAction);

	}
}
