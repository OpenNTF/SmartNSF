package org.openntf.xrest.xsp.exec.datacontainer;

import java.util.List;

import com.ibm.commons.util.NotImplementedException;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

public class DocumentListDataContainer extends AbstractDataContainer<List<Document>> {


	public DocumentListDataContainer( View view, Database db) {
		super(view,db);
	}

	@Override
	public List<Document> getData() {
		throw new NotImplementedException("Function is not Implemented");
	}

	@Override
	public boolean isList() {
		return true;
	}

	@Override
	public boolean isBinary() {
		return false;
	}

	@Override
	protected void executeCleanUp() {
	}

}
