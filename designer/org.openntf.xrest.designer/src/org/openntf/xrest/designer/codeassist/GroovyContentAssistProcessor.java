package org.openntf.xrest.designer.codeassist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.openntf.xrest.xsp.model.Router;

public class GroovyContentAssistProcessor implements IContentAssistProcessor {
	private static final String TROUBLECHARS = ".{(";

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
				List<ASTNode> hir = analyzer.getHierarchie();
				System.out.println(node.getText());
				System.out.println(node.getNodeMetaData());
				System.out.println(node.getClass());
				System.out.println(hir.size());
				if (node instanceof VariableExpression) {
					VariableExpression ve = (VariableExpression) node;
					Map<String, Class<?>> predefindeObject = new HashMap<String, Class<?>>();
					predefindeObject.put("router", Router.class);
					VEProposal veproposal = new VEProposal(ve, hir, predefindeObject);
					List<ICompletionProposal> proposals = veproposal.suggestions(arg1);
					return proposals.toArray(new ICompletionProposal[0]);
				}
			} else {

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
