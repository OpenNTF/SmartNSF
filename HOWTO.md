### Installation
Installation is pretty straightforward. Download latest release from [OpenNTF.org website](https://openntf.org/main.nsf/project.xsp?r=project/SmartNSF/releases/51463E9CB504A313852580CC00830EF4). Unzip the downloaded file, you should get following folder structure:

designer-updatesite  
server-updatesite  
sample

Install content of `designer-updatesite` into your Domino Designer via File - Application -Install... menu (and restart DDE afterwards, import content of `server-updatesite` into update site database on your server (and issue `restart task http` command on your server console to load it) and you are all set to use it.

### Getting started
#### Defining routes
After installing Designer plug-in you can see new design element called XRest API Routes under Application Configuration category.  
It is used for editing routes your application will be listening on for REST calls.

##### Syntax of routes.groovy
```
router.METHOD('route/path/{variable}') {
	strategy(STRATEGY_NAME){
	}
	...
}
```
where METHOD is one of:

* GET
* POST
* DELETE

and STRATEGY is one of:

* SELECT_DOCUMENT_FROM_VIEW_BY_KEY
* SELECT_DOCUMENT_BY_UNID
* SELECT_DOCUMENTS_BY_SEARCH_FT
* SELECT_DOCUMENTS_BY_SEARCH_FT_PAGED
* SELECT_DOCUMENTS_BY_FORMULA
* SELECT_DOCUMENTS_BY_FORMULA_PAGED
* SELECT_ALL_DOCUMENTS_BY_VIEW
* SELECT_ALL_DOCUMENTS_BY_VIEW_PAGED
* SELECT_ALL_DOCUMENTS_FROM_VIEW_BY_KEY
* SELECT_ALL_DOCUMENTS_FROM_VIEW_BY_KEY_PAGED
* SELECT_ATTACHMENT

I guess the names again speak for themselves.

##### Example route

Let's try to create simple `GET` route: assuming we have view named `(Topics)` in our test database containing some documents with field `Topic` in them, we can enter this to our `routes.groovy` file:

```
route.GET('docs') {
	strategy(SELECT_ALL_DOCUMENTS_BY_VIEW) {
		viewName('(Topics)')
	}
	mapJson 'Topic', json: 'topic', type: STRING
}
```

and access our new REST point at `http://server.name/path/database.nsf/xsp/.xrest/docs`
Returned data should look like this:
```
{
	[
		{ "topic": "Topic 1" },
		{ "topic": "Topic 2" }
		...
	]
}
```
You can see it is basically returning content of field Topic from all documents in (Topics) view.

When the number of documents in the application is higher than just a few, it might be better not to read all of them with one call.

If we change strategy to `SELECT_ALL_DOCUMENTS_BY_VIEW_PAGED`, so we have it defined like this:
```
route.GET('docs') {
	strategy(SELECT_ALL_DOCUMENTS_BY_VIEW_PAGED) {
		viewName('(Topics)')
	}
	mapJson 'Topic', json: 'topic', type: STRING
}
```
we can now pass parameters `start` and `count` in our URL to get just a subset of documents. 
For example calling `http://server.name/path/database.nsf/xsp/.xrest/docs?start=100&count=5` will return 5 documents starting at position 100 in the view. Returned JSON is a bit different:
```
{
	"start": 100,
	"count": 5,
	"total": 24900,
	"entries": [
		{
			"topic": "Topic 100"
		}
		{
			"topic": "Topic 101"
		}
		{
			"topic": "Topic 102"
		}
		{
			"topic": "Topic 103"
		}
		{
			"topic": "Topic 104"
		}
	]
}
```