package org.openntf.xrest.designer.editors;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class DSLScanner extends RuleBasedScanner {

	public DSLScanner(ColorManager manager) {
		IToken procInstr =
			new Token(
				new TextAttribute(
					manager.getColor(IXMLColorConstants.PROC_INSTR)));

		IToken string = new Token(new TextAttribute(manager.getColor(IXMLColorConstants.STRING)));
		IRule[] rules = new IRule[4];
		//Add rule for processing instructions
		rules[0] = new SingleLineRule("<?", "?>", procInstr);
		// Add generic whitespace rule.

		// Add rule for double quotes
		rules[1] = new SingleLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[2] = new SingleLineRule("'", "'", string, '\\');
		rules[3] = new WhitespaceRule(new XMLWhitespaceDetector());

		setRules(rules);
	}
}
