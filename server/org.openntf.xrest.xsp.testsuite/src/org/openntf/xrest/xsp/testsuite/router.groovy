
def calcGroups = {
	def groups = ["c1","c10","c100","c1000"]
	
	return groups
}

router.GET('customers/{id}') {
	strategy(DOCUMENT_FROM_VIEW_BY_KEY) {
		keyVariableName("{id}")
		viewName("customerById")
	}
	accessPermission "SalesManager","[CustomerService]"
	mapJson "company", json:'company',type:'STRING'
	mapJson "fdFirstName", json:'firstname', type:'STRING'
	events VALIDATE: {
		context -> return true}
}

router.GET('customers/{id}/contract/{attachmentName}') {
	strategy(ATTACHMENT) {
		documentStrategy(SELECT_DOCUMENT_FROM_VIEW_BY_KEY) {
			keyVariableName("{id}")
			viewName("customerById")
		}
		fieldName "Body"
		selectionType BY_NAME //Could be BY_NAME, FIRST
		attachmentNameVariableName "{attachmentName}"
	}
}

router.POST('customers/{id}/contract') {
	strategy(ATTACHMENT){
		documentStrategy(SELECT_DOCUMENT_FROM_VIEW_BY_KEY) {
			keyVariableName("{id}")
			viewName("customerById")
		}
		fieldName "Body"
		updateType REPLACE_ALL //Could be REPLACE_ALL, REPLACE_BY_NAME
	}
}

router.GET('customers') {
	strategy(DOCUMENTS_BY_VIEW) {
		viewName("customersActive")
	}
	accessPermission calcGroups;
	mapJson "company", json:'company',type:'STRING'
	mapJson "fdFirstName", json:'firstname', type:'STRING'
}
router.PUT('customers/{id}') {
	strategy(DOCUMENT_FROM_VIEW_BY_KEY) {
		keyVariableName("{id}")
		viewName("customerById")
	}
	mapJson "company", json:'company',type:'STRING'
	mapJson "fdFirstName", json:'firstname', type:'STRING'
	events (POST_SAVE_DOCUMENT: {
		context, document ->
	}, PRE_SAVE_DOCUMENT: {context, document ->})
}
router.POST('comment/{id}') {
	strategy(DOCUMENT_BY_UNID) {
		keyVariableName("{id}")
	}
	mapJson "company", json:'company',type:'STRING'
	mapJson "fdFirstName", json:'firstname', type:'STRING'
}
router.DELETE('quote/{id}') {
	strategy(DOCUMENT_FROM_VIEW_BY_KEY) {
		keyVariableName("{id}")
		viewName("customerById")
	}
	mapJson "company", json:'company',type:'STRING'
	mapJson "fdFirstName", json:'firstname', type:'STRING'
}