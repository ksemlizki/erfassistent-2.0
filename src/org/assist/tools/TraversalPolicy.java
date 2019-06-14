package org.assist.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Vector;

public class TraversalPolicy extends FocusTraversalPolicy {

	private Vector<Component> order;

	public TraversalPolicy(Vector<Component> order) {
		this.order = new Vector<Component>(order);
	}

	@Override
	public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
		int index = (order.indexOf(aComponent) + 1) % order.size();
		Component component = order.get(index);
		return component.isFocusable() && component.isEnabled() ? component : getComponentAfter(focusCycleRoot, component);
	}

	@Override
	public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
		int index = order.indexOf(aComponent) - 1;
		if (index < 0) {
			index = order.size() - 1;
		}
		Component component = order.get(index);
		return component.isFocusable() && component.isEnabled() ? component : getComponentBefore(focusCycleRoot, component);
	}

	@Override
	public Component getDefaultComponent(Container focusCycleRoot) {
		return order.get(0);
	}

	@Override
	public Component getLastComponent(Container focusCycleRoot) {
		return order.lastElement();
	}

	@Override
	public Component getFirstComponent(Container focusCycleRoot) {
		return order.firstElement();
	}
}