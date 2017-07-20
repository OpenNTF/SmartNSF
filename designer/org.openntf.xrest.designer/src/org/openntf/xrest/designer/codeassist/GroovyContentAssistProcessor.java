package org.openntf.xrest.designer.codeassist;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class GroovyContentAssistProcessor implements IContentAssistProcessor {
	private static final String TROUBLECHARS = ".{(";

	private ProposalFactory proposalFactory = new ProposalFactory();
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer arg0, int arg1) {
		String code = arg0.getDocument().get();
		try {
			int line = arg0.getDocument().getLineOfOffset(arg1);
			int lineStart = arg0.getDocument().getLineOffset(line);
			int column = arg1 - lineStart;
			line++;
			String triggerChar = "" + code.charAt(arg1 - 1);
			code = sanatizeCode(code, triggerChar, arg1 - 1);
			ASTAnalyser analyzer = new ASTAnalyser(code, line, column);
			if (analyzer.parse()) {
				ASTNode node = analyzer.getNode();
				System.out.println(node.getText() + "-->"+ node.getClass() );
				CodeProposal cp = proposalFactory.getCodeProposal(analyzer);
				if (cp != null) {
					return cp.suggestions(arg1).toArray(new ICompletionProposal[0]);
				} else {
					System.out.println("NO Proposal for: " +node.getText() +" // "+node.getClass());
				}
			} else {
				analyzer.getException().printStackTrace();
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String sanatizeCode(String code, String triggerChar, int pos) {
		if (TROUBLECHARS.contains(triggerChar)) {
			StringBuilder sanCode = new StringBuilder(code);
			sanCode.setCharAt(pos, ' ');
			return sanCode.toString();
		}
		return code;
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
