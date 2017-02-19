package org.openntf.xrest.designer.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class DSLWhitespaceDetector implements IWhitespaceDetector {

	@Override
	public boolean isWhitespace(char c) {
		return  Character.isWhitespace(c);
	}
}
