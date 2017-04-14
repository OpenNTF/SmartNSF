package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class GroovyContentAssistProcessor implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer arg0, int arg1) {
		String code = arg0.getDocument().get();
		AstBuilder sb = new AstBuilder();

		List<ASTNode> nodes = sb.buildFromString(code);
		System.out.println(nodes.size());

		for (ASTNode node: nodes) {
			System.out.println(node.getClass().getCanonicalName());
			if (node instanceof BlockStatement) {
				BlockStatement bs = (BlockStatement)node;
				for (Statement st : bs.getStatements()) {
					System.out.println("AST: "+st.getText() +" / "+ st.getColumnNumber() +" / "+ st.getLineNumber() +" - "+ st.getLastLineNumber());
					
				}
			}
		}
		System.out.println("DA sind wir ---> "+ arg1);
		try {
			System.out.println(arg0.getDocument().get(arg1-10, arg1) );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("computeCompletionProposals");
		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
		System.out.println("computeContextInformation");
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.' ,'{'};
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
		return null;
	}

}
