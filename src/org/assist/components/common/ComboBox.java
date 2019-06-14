package org.assist.components.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;
import javax.swing.event.PopupMenuListener;

import org.assist.tools.ExceptionHandler;
import org.assist.tools.IReleaseable;

public class ComboBox<T> extends JComboBox<T> implements IReleaseable {

	private boolean layingOut = false;

	public ComboBox() {
		super();
		init();
	}

	public ComboBox(T[] items) {
		super(items);
		init();
	}

	protected void init() {
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent event) {
				changeSelectedIndex(event.getWheelRotation());
			}
		});
	}

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);

		if (editable) {
			getEditor().getEditorComponent().setBackground(Color.WHITE);
		}
	}

	public String getText() {
		return ((JTextField) this.getEditor().getEditorComponent()).getText();
	}

	protected void changeSelectedIndex(int wheelRotation) {
		if (isEnabled()) {
			int selectedIndex = getSelectedIndex();
			if (wheelRotation == -1) {
				if (selectedIndex > 0) {
					setSelectedIndex(selectedIndex - 1);
				}
			}
			else {
				if (selectedIndex < (getItemCount() - 1)) {
					setSelectedIndex(selectedIndex + 1);
				}
			}
		}
	}

	public boolean contains(Object value) {
		for (int i = 0; i < getItemCount(); i++) {
			if (getItemAt(i).equals(value)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public void setSelectedItem(Object object) {
		final Object valueToSet = contains(object) ? object : null;
		Runnable runnable = new Runnable() {
			@Override
			@SuppressWarnings("synthetic-access")
			public void run() {
				ComboBox.super.setSelectedItem(valueToSet);
			}
		};
		if (EventQueue.isDispatchThread()) {
			runnable.run();
		}
		else {
			try {
				EventQueue.invokeAndWait(runnable);
			}
			catch (Exception exception) {
				ExceptionHandler.handle(exception);
			}
		}
	}

	@Override
	public void setSelectedIndex(final int index) {
		Runnable runnable = new Runnable() {

			@Override
			@SuppressWarnings("synthetic-access")
			public void run() {
				ComboBox.super.setSelectedIndex(index);
			}
		};
		if (EventQueue.isDispatchThread()) {
			runnable.run();
		}
		else {
			try {
				EventQueue.invokeAndWait(runnable);
			}
			catch (Exception exception) {
				ExceptionHandler.handle(exception);
			}
		}
	}

	@Override
	public void release() {
		setRenderer(null);
		ComboBoxModel<T> model = getModel();
		if (model instanceof DefaultComboBoxModel) {
			DefaultComboBoxModel<T> defaultComboBoxModel = (DefaultComboBoxModel<T>) model;
			ListDataListener[] listDataListeners = defaultComboBoxModel.getListDataListeners();
			for (ListDataListener listDataListener : listDataListeners) {
				defaultComboBoxModel.removeListDataListener(listDataListener);
			}
		}

		for (ActionListener listener : getListeners(ActionListener.class)) {
			removeActionListener(listener);
		}
		for (PopupMenuListener listener : getPopupMenuListeners()) {
			removePopupMenuListener(listener);
		}
		if (getItemCount() > 0) {
			removeAllItems();
		}
		setModel(new DefaultComboBoxModel<T>());

	}

	@Override
	public void doLayout() {
		try {
			layingOut = true;
			super.doLayout();
		}
		finally {
			layingOut = false;
		}
	}

	@Override
	public Dimension getSize() {
		Dimension size = super.getSize();
		if (!layingOut) {
			size.width = Math.max(size.width, getPreferredSize().width);
		}

		return size;
	}

}
