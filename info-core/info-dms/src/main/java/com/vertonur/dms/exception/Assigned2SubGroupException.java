package com.vertonur.dms.exception;

import com.vertonur.pojo.security.Group;

public class Assigned2SubGroupException extends Exception {

	private static final long serialVersionUID = -2305309245986580825L;

	public Assigned2SubGroupException(Group group, Group parentGroup) {
		super("Group [" + group.getId() + "," + group.getName()
				+ "] is been assigning to its sub group ["
				+ parentGroup.getId() + "," + parentGroup.getName()
				+ "] or one of its descension");
	}
}
