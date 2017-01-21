router.GET('topics}') {
	strategy(ALL_FROM_VIEW) {
		viewName {
			
		}
	}
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}


router.GET('topics/{id}') {
	strategy(SELECT_DOCUMENT_FROM_VIEW_BY_KEY) {
		keyVariableName("{id}")
		viewName("customerById")
	}
	accessPermission "SalesManager","[CustomerService]"
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
	events VALIDATE: {
		context -> return true}
}

router.GET('customers/{id}/contract/{attachmentName}') {
	strategy(SELECT_ATTACHMENT) {
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
	strategy(SELECT_ATTACHMENT){
		documentStrategy(SELECT_DOCUMENT_FROM_VIEW_BY_KEY) {
			keyVariableName("{id}")
			viewName("customerById")
		}
		fieldName "Body"
		updateType REPLACE_ALL //Could be REPLACE_ALL, REPLACE_BY_NAME
	}
}

router.POST('comment/{id}') {
	strategy(SELECT_DOCUMENT_BY_UNID) {
		keyVariableName("{id}")
	}
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}
router.DELETE('quote/{id}') {
	strategy(SELECT_DOCUMENT_FROM_VIEW_BY_KEY) {
		keyVariableName("{id}")
		viewName("customerById")
	}
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}