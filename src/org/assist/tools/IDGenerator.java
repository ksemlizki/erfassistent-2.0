package org.assist.tools;

import java.util.TreeSet;

public class IDGenerator {

	private TreeSet<Integer> usedIDs;

	public IDGenerator() {
		usedIDs = new TreeSet<Integer>();
	}

	public Integer getNextId() {
		Integer newId = null;
		if (usedIDs.isEmpty()) {
			newId = Integer.valueOf(0);
		}
		else {
			Integer lastId = usedIDs.last();
			if (lastId.intValue() < Integer.MAX_VALUE - 2) {
				newId = Integer.valueOf(lastId.intValue() + 1);

			}
			else {
				for (int i = 0; i < lastId.intValue(); i++) {
					if (!usedIDs.contains(Integer.valueOf(i))) {
						newId = Integer.valueOf(i);
						break;
					}
				}
			}
		}

		usedIDs.add(newId);

		return newId;
	}

	public void removeId(Integer id) {
		usedIDs.remove(id);
	}

}
