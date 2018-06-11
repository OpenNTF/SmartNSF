println ("building routing...")
import java.util.Map;
import org.openntf.smartnsf.CategoryStatistics
router.useFacesContext(true);
router.trace(true);

router.GET('infos', {
	strategy(CUSTOM,{
		javaClass 'org.openntf.smartnsf.Info'
	})
})
router.GET('topics', {
     	strategy(VIEWENTRIES_PAGED, {
         	viewName('(ByDate)')
     	})
	mapJson '\$120', json:'topic', type:'STRING'	
	mapJson '\$124', json:'author', type:'STRING'
	mapJson 'date', json:'date',type:'DATETIME'
	mapJson '\$126', json:'id', type:'STRING'
})

router.GET('topics/{id}') {
	strategy(DOCUMENT_BY_UNID) {
			keyVariableName("id")
	}
	mapJson "date", json:'date',type:'STRING',isformula:true, formula:'@Text(@Created)'
	mapJson "Subject", json:'topic', type:'STRING'
	mapJson "author", json:'author', type:'STRING',isformula:true, formula:'@Name([CN]; From)'
	mapJson "body", json:'content', type:'MIME'
	mapJson "categories", json:'categories', type:'ARRAY_OF_STRING'
	events PRE_SUBMIT: {
		context, document ->
		def now = new Date()
		def timestamp = now.toTimestamp().toString();
		def payload = context.getResultPayload();
		def fc = context.getFacesContext();
		def helper = context.getNSFHelper();
		payload.put('timestamp',timestamp)
	}
	
}
router.GET('topics/{id}/attachment/{attachmentName}') {
	strategy(ATTACHMENT) {
		documentStrategy(DOCUMENT_BY_UNID) {
			keyVariableName("id")
		}
		fieldName "Body"
		selectionType BY_NAME //Could be BY_NAME, FIRST
		attachmentNameVariableName "{attachmentName}"
	}
}
router.GET('topics/bycategory/{catName}') {
	strategy(DOCUMENTS_BY_FORMULA) {
		selectQuery('SELECT  @Contains(Categories;\"{catName}\")')
	}
	mapJson "date", json:'date',type:'STRING',isformula:true, formula:'@Text(@Created)'
	mapJson "Subject", json:'topic', type:'STRING'
	mapJson "author", json:'author', type:'STRING',isformula:true, formula:'@Name([CN]; From)'
	mapJson "categories", json:'categories', type:'ARRAY_OF_STRING'
	
}
router.POST('topics/{id}') {
	strategy(DOCUMENT_BY_UNID) {
		keyVariableName("id")
	}
	mapJson "Subject", json:'topic', type:'STRING'
	mapJson "body", json:'content', type:'MIME'
	mapJson 'categories', json:'categories', type:'ARRAY_OF_STRING', readonly:true
	mapJson 'NewCats', json:'categories', type:'ARRAY_OF_STRING', writeonly:true
	mapJson "date", json:'date',type:'STRING',isformula:true, formula:'@Text(@Created)', readonly:true
	mapJson "author", json:'author', type:'STRING',isformula:true, formula:'@Name([CN]; From)', readonly:true
	mapJson 'id', json:'id', type:'STRING', isformula:true, formula:'@DocumentUniqueID', readonly:true
	
	events PRE_SAVE_DOCUMENT: {
		context, document ->
		nsfHelp = context.getNSFHelper()
		nsfHelp.computeWithForm(document)
	}
}

router.POST('topics/{id}/attachment') {
	strategy(ATTACHMENT){
		documentStrategy(DOCUMENT_BY_UNID) {
			keyVariableName("id")
		}
		fieldName "Body"
		updateType REPLACE_BY_NAME //Could be REPLACE_ALL, REPLACE_BY_NAME
	}
}

router.GET('topics/{parent_id}/comments') {
	strategy(DOCUMENTS_FROM_VIEW_BY_KEY) {
			keyVariableName("parent_id")
			viewName("commentsByParentId")
	}
	mapJson "date", json:'date',type:'STRING',isformula:true, formula:'@Created'
	mapJson "Subject", json:'topic', type:'STRING'
	mapJson "author", json:'author', type:'STRING',isformula:true,formula:'@Name([CN]; From)'
	mapJson "body", json:'content', type:'MIME'
	mapJson "categories", json:'categories', type:'ARRAY_OF_STRING'
}

router.GET('topics/{parent_id}/comments/{id}') {
	strategy(DOCUMENT_BY_UNID) {
			keyVariableName("id")
	}
	mapJson "date", json:'date',type:'STRING',isformula:true, formula:'@Created'
	mapJson "Subject", json:'topic', type:'STRING'
	mapJson "author", json:'author', type:'STRING',isformula:true,formula:'@Name([CN]; From)'
	mapJson "body", json:'content', type:'MIME'
	mapJson "categories", json:'categories', type:'ARRAY_OF_STRING'
}

router.POST('topics/{parent_id}/comments/{id}') {
	strategy(DOCUMENT_BY_UNID) {
		keyVariableName("id")
		form 'Response'
	}
	mapJson "Subject", json:'topic', type:'STRING'
	mapJson "body", json:'content', type:'MIME'
	mapJson "categories", json:'categories', type:'ARRAY_OF_STRING'
	mapJson "date", json:'date',type:'STRING',isformula:true, formula:'@Text(@Created)', readonly:true
	mapJson "author", json:'author', type:'STRING',isformula:true, formula:'@Name([CN]; From)', readonly:true
	
	events PRE_SAVE_DOCUMENT:{
		context, document ->
		parentId = context.getRouterVariables().get('parent_id')
		nsfHelp = context.getNSFHelper()
		nsfHelp.makeDocumentAsChild(parentId, document)
		nsfHelp.computeWithForm(document)
	}
}

router.DELETE('document/{id}') {
	strategy(DOCUMENT_BY_UNID) {
		keyVariableName("{id}")
	}
}
router.GET('topicsbydate/{from}/{to}'){
	strategy(DOCUMENTS_BY_FORMULA) {
		selectQuery('SELECT Form=\"MainTopic\" & @Created > [{from}] & @Created =< [{to}]')
	}
 	mapJson "date", json:'date',type:'STRING',isformula:true, formula:'@Text(@Created)'
	mapJson "Subject", json:'topic', type:'STRING'
	mapJson "author", json:'author', type:'STRING',isformula:true, formula:'@Name([CN]; From)'
	mapJson "categories", json:'categories', type:'ARRAY_OF_STRING'
	events PRE_SUBMIT: {
		context, document ->
		def payload = context.getResultPayload();
		def javaArrayObject = context.getNSFHelper().createJsonObject();
		javaArrayObject.put('docclass',document.getClass().getName());
		javaArrayObject.put('data', payload);
		CategoryStatistics catAnalyser = new CategoryStatistics(payload);
		javaArrayObject.put("stats", catAnalyser.count());
		context.setResultPayload(javaArrayObject);
	}
}
router.GET('mytopics' ) {
	strategy(VIEWENTRIES_BY_CATEGORY_PAGED) {
		viewName('byAuthorCanonical')
		calculateKey { context ->
		   context.getUserName(); 
		}
	}
	mapJson '\$34', json:'topic', type:'STRING'	
	mapJson '\$124', json:'author', type:'STRING'
	mapJson 'date', json:'date',type:'DATETIME'
	mapJson '\$126', json:'id', type:'STRING'
}