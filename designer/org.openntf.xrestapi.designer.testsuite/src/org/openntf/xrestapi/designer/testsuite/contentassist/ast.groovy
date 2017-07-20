def blubber = "blubbi"
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
	events ()
	mapJson
	strategy
}
router.POST('customer/{id}') {
	events (POST_SAVE_DOCUMENT: {
		context, document -> 
		def resp = context.getHttpResp();
	}, PRE_SAVE_DOCUMENT: {context, document ->
		def helper = context.getNSFHelper();
		helper
		context
		calcGroups
	})
}