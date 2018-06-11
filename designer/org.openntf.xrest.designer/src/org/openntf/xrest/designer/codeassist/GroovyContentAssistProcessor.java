package org.openntf.xrest.designer.codeassist;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.openntf.xrest.designer.codeassist.analytics.ASTAnalyser;
import org.openntf.xrest.designer.utils.DesignerProjectClassLoaderFactory;

import com.ibm.designer.domino.ide.resources.NsfException;
import com.ibm.designer.runtime.ApplicationException;

public class GroovyContentAssistProcessor implements IContentAssistProcessor {
	private static final String TROUBLECHARS = ".{(";

	private ProposalFactory proposalFactory = new ProposalFactory();

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		String code = viewer.getDocument().get();
		IProject project = findActiveProject();
		try {
			ClassLoader cl = DesignerProjectClassLoaderFactory.buildDesignerClassLoader(project);
			int line = viewer.getDocument().getLineOfOffset(offset);
			int lineStart = viewer.getDocument().getLineOffset(line);
			int column = offset - lineStart;
			line++;
			String triggerChar = Character.toString(code.charAt(offset - 1));
			code = sanatizeCode(code, triggerChar, offset - 1);
			ASTAnalyser analyzer = new ASTAnalyser(code, line, column, cl);
			if (analyzer.parse()) {
				ASTNode node = analyzer.getNode();
				CodeProposal cp = proposalFactory.getCodeProposal(analyzer);
				if (cp != null) {
					return cp.suggestions(offset).toArray(new ICompletionProposal[0]);
				} else {
					System.out.println("NO Proposal for: " + node.getText() + " // " + node.getClass());
				}
			} else {
				analyzer.getException().printStackTrace();
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		} catch (NsfException e) {
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
		return "No proposal found.";
	}

	private IProject findActiveProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage activePage = window.getActivePage();
			IEditorPart activeEditor = activePage.getActiveEditor();
			if (activeEditor != null) {
				IEditorInput input = activeEditor.getEditorInput();

				IProject project = (IProject) input.getAdapter(IProject.class);
				if (project == null) {
					IResource resource = (IResource) input.getAdapter(IResource.class);
					if (resource != null) {
						project = resource.getProject();
					}
				}
				return project;
			}
		}
		return null;
	}
}
