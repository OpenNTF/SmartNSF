package org.openntf.xrest.designer.codeassist;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public interface CodeProposal {

	List<ICompletionProposal> suggestions(int offset);

}