package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.core.internal.resources.TestingSupport;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.JFaceTextUtil;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.viewers.ISelection;

public class GroovyContentAssistProcessor implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer arg0, int arg1) {
		String code = arg0.getDocument().get();
		try {
			int line = arg0.getDocument().getLineOfOffset(arg1);
			int lineStart = arg0.getDocument().getLineOffset(line);
			int column = arg1 - lineStart;
			line++;
			System.out.println("DA sind wir ---> " + arg1);
			ISelection sel = arg0.getSelectionProvider().getSelection();
			System.out.println("Line: " + line + " / Column: " + column);
			ASTAnalyser analyzer = new ASTAnalyser(code, line, column);
			if (analyzer.parse()) {
				ASTNode node = analyzer.getNode();
				List<ASTNode> hir = analyzer.getHierarchie();
				System.out.println(node.getText());
			}
			System.out.println("computeCompletionProposals");
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
		System.out.println("computeContextInformation");
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.', '{' };
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return "nix proposal";
	}

}
