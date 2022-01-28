router.authorizationEndpoint {
	additionalDirectories "test/crm.nsf","test/xrm.nsf"
	publicKey "-----BEGIN PUBLIC KEY-----"+
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3A5MO1GdDcSD8svKhxvw"+
				"R6yd00EQ2qSrERL8vizkciDEAYh9GP7Ebx0gq77tngFyOqJCO0i7jEob8SuT9/F1"+
				"wwQv9F/mtB5cpMv5e7kXpMjIaQuJI4mdxqt4T8dGLZ6icoTW/q7WMaqLUPzMdJif"+
				"lpMzYRozM87eQWrmuK0hEcWS1/0SWs5fTfCUetsywPd+AVvF1QyPpcy51G9iYiZY"+
				"n4wm9RLBdyBGawoKSkH3T72qIyJudxQLoXlqfXF6BcEQRS0NZjvZsRReykE7hvDo"+
				"dJr3g17RlhW/Xd+XQK2qilrJNmUx7SbHTIZwqMTaR7j1XxFtQEym4g2nwxsuIDFw"+
				"8wIDAQAB"+
				"-----END PUBLIC KEY-----"
	onUserNotFound {
		context,userObject->
		println("Hello World")
	}
	header "smartnsf-auth"
	ssoDomain ".smart.nsf"
}