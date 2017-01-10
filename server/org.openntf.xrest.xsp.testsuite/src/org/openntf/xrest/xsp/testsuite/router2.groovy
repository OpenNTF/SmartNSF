
router.POST('customers/{id}') {
	strategy(GET_FROM_VIEW_BY_KEY) {
		keyVariableName("{id}")
		viewName("customerById")
	}
	accessPermission "SalesManager","[CustomerService]"
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
	events VALIDATE: {
		context -> 
		//Accessing the payload
		json = context.getJsonPayload()
		//Accessing the JsonObject
		id = json.getJsonProperty('id')
		if (id == '') {
			context.throwException("ID should not be null or empty")
		}
	}
}
router.POST('customers/{customerid}/phonecall/{id}') {
	strategy(GET_FROM_VIEW_BY_KEY) {
		keyVariableName("{id}")
		viewName("customerById")
	}
	accessPermission "SalesManager","[CustomerService]"
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
	events PRE_SAVE_DOCUMENT:{
		context, document ->
		parentId = context.getRouterVariables().get('customerid')
		nsfHelp = context.getNSFHelper()
		nsfHelp.makeDocumentAsChild(parentId, document)
	}, POST_SAVE_DOCUMENT: {
		context, document ->
		nsfHelp = context.getNSFHelper()
		nsfHelp.executeAgent("processHistory", document)
	}
}

