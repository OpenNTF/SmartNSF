package org.openntf.xrest.designer.editors;

import org.eclipse.jdt.internal.ui.text.CombinedWordRule;
import org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher;
import org.eclipse.jdt.internal.ui.text.JavaWordDetector;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;
import org.openntf.xrest.xsp.model.AttachmentSelectionType;
import org.openntf.xrest.xsp.model.AttachmentUpdateType;
import org.openntf.xrest.xsp.model.EventType;
import org.openntf.xrest.xsp.model.Strategy;

@SuppressWarnings("restriction")
public class DSLScanner extends RuleBasedScanner {
	private static final String[] types = { "boolean", "byte", "char", "class", "double", "float", "int", "interface", "long", "short", "void" };
	private static final String[] keywords = { "abstract", "assert", "break", "case", "catch", "const", "continue", "default", "do", "else", "enum", "extends", "false", "final", "finally", "for",
			"goto", "if", "implements", "import", "instanceof", "interface", "native", "new", "null", "package", "private", "protected", "public",
			// "return", use the special return keyword now so returns can be
			// highlighted differently
			"static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while", };
	private static final String[] groovyKeywords = { "as", "in", "def", "trait","println", };
	private static final String[] dslRouterKeywords = { "router","GET", "PUT", "POST", "DELETE", };
	private static final String[] dslKeywords = { "strategy", "mapJson", "events", "accessPermission", };
	private static final String returnKeyword = "return";
	private final ColorManager manager;

	public DSLScanner(ColorManager manager) {
		this.manager = manager;
		IToken procInstr = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.PROC_INSTR)));

		IToken string = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.STRING)));
		IToken commentToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.COMMENT)));
		IRule[] rules = new IRule[7];
		// Add rule for processing instructions
		rules[0] = new SingleLineRule("<?", "?>", procInstr);
		// Add generic whitespace rule.

		// Add rule for double quotes
		rules[1] = new SingleLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[2] = new SingleLineRule("'", "'", string, '\\');
		rules[3] = new WhitespaceRule(new DSLWhitespaceDetector());
		rules[4] = new EndOfLineRule("//", commentToken);
		rules[5] = new MultiLineRule("/*", "*/", commentToken);
		rules[6] = buildWordRules();
		setRules(rules);
	}

	private IRule buildWordRules() {
		IToken defaultToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.DEFAULT)));
		IToken javaToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.JAVA), null, SWT.BOLD));
		IToken javaTypeToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.JAVA), null, SWT.BOLD));
		IToken groovyToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.GOOVY), null, SWT.BOLD));
		IToken dslRouterToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.DSLROUTER), null, SWT.BOLD));
		IToken dslToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.DSL), null, SWT.BOLD));
		IToken dslEnumToken = new Token(new TextAttribute(manager.getColor(IDSLColorConstants.DSL_ENUM), null, SWT.BOLD));
		

		JavaWordDetector wordDetector = new JavaWordDetector();
		CombinedWordRule combinedWordRule = new CombinedWordRule(wordDetector, defaultToken);

		// Java keywords
		WordMatcher javaKeywordsMatcher = new WordMatcher();
		for (int i = 0; i < keywords.length; i++) {
			javaKeywordsMatcher.addWord(keywords[i], javaToken);
		}
		combinedWordRule.addWordMatcher(javaKeywordsMatcher);

		// Java types
		WordMatcher javaTypesMatcher = new WordMatcher();
		for (int i = 0; i < types.length; i++) {
			javaTypesMatcher.addWord(types[i], javaTypeToken);
		}
		combinedWordRule.addWordMatcher(javaTypesMatcher);

		// Groovy Keywords, including additional keywords
		WordMatcher groovyKeywordsMatcher = new WordMatcher();
		for (int i = 0; i < groovyKeywords.length; i++) {
			groovyKeywordsMatcher.addWord(groovyKeywords[i], groovyToken);
		}
		combinedWordRule.addWordMatcher(groovyKeywordsMatcher);
		
		WordMatcher dslRouterKeywordsMatcher = new WordMatcher();
		for (int i = 0; i < dslRouterKeywords.length; i++) {
			dslRouterKeywordsMatcher.addWord(dslRouterKeywords[i], dslRouterToken);
		}
		combinedWordRule.addWordMatcher(dslRouterKeywordsMatcher);


		WordMatcher dslKeywordsMatcher = new WordMatcher();
		for (int i = 0; i < dslKeywords.length; i++) {
			dslKeywordsMatcher.addWord(dslKeywords[i], dslToken);
		}
		combinedWordRule.addWordMatcher(dslKeywordsMatcher);

		WordMatcher dslEnum = new WordMatcher();
		for (EventType et : EventType.values()) {
			dslEnum.addWord(et.name(), dslEnumToken);
		}
		for (Strategy strategy : Strategy.values()) {
			dslEnum.addWord(strategy.name(), dslEnumToken);
		}
		for (AttachmentSelectionType ast : AttachmentSelectionType.values()) {
			dslEnum.addWord(ast.name(), dslEnumToken);
		}
		for (AttachmentUpdateType aut : AttachmentUpdateType.values()) {
			dslEnum.addWord(aut.name(), dslEnumToken);
		}
		combinedWordRule.addWordMatcher(dslEnum);

		return combinedWordRule;
	}
}
