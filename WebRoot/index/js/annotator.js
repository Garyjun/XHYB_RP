var annotatorSetting =  {
	prefix : '${path}/api',
	urls : {
		create :  '/annotations/create.action',
		update :  '/annotations/update.action',
		destroy : '/annotations/delete.action',
		search :  '/annotations/search.action'
	},
	annotationData : {
		'uri' : '${path}/index/index.jsp'
	},
	loadFromSearch : {
		'limit' : 20,
		'uri' : '${path}/index/index.jsp'
	}
};