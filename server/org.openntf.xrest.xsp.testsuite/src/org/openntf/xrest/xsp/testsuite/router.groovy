
def calcGroups = {
	def groups = ["c1","c10","c100","c1000"]
	
	return groups
}

router.GET('customers/{id}') {
	strategy(GET_FROM_VIEW_BY_KEY) {
		key("{id}")
		view("customerById")
	}
	accessPermission "SalesManager","[CustomerService]"
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}

router.GET('customers') {
	strategy(GET_FROM_VIEW_BY_KEY) {
		key("{id}")
		view("customerById")
	}
	accessPermission calcGroups;
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}
router.PUT('customers/{id}') {
	strategy(GET_FROM_VIEW_BY_KEY) {
		key("{id}")
		view("customerById")
	}
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}
router.POST('comment/{id}') {
	strategy(GET_FROM_VIEW_BY_KEY) {
		key("{id}")
		view("customerById")
	}
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}
router.DELETE('quote/{id}') {
	strategy(GET_FROM_VIEW_BY_KEY) {
		key("{id}")
		view("customerById")
	}
	mapJson "company", json:'company',type:'String'
	mapJson "fdFirstName", json:'firstname', type:'String'
}