
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
			println "id is blank"
			context.throwException("ID should not be null or empty")
		}
	}
}

