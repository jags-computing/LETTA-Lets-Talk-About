var GroupsDAO = (function(){

    var resourcePath = "rest/groups/";
    var resourcePathTopic = "rest/topics/";
    var resourcePathUser = "rest/users/";
    var requestByAjax = function(data, done, fail, always) {
		done = typeof done !== 'undefined' ? done : function() {};
		fail = typeof fail !== 'undefined' ? fail : function() {};
		always = typeof always !== 'undefined' ? always : function() {};

		let authToken = localStorage.getItem('authorization-token');
		if (authToken !== null) {
			data.beforeSend = function(xhr) {
			xhr.setRequestHeader('Authorization', 'Basic ' + authToken);
			};
		}

		$.ajax(data).done(done).fail(fail).always(always);
    };


    function GroupsDAO(){
        this.listGroupByName = function(groupname, done, fail, always){
            requestByAjax({
            url : resourcePath + "byname/" + groupname,
            type : 'GET'
            }, done, fail, always);
        };

        this.listGroupByTopic = function(grouptopic, done, fail, always){
            requestByAjax({
            url : resourcePathTopic + grouptopic,
            type : 'GET'
            }, done, fail, always);
        };

        this.addGroup = function(group, done, fail, always) {
            requestByAjax({
                url : resourcePath,
                type : 'POST',
                data : group
            }, done, fail, always);
        };

        this.joinGroup = function(groupId, done, fail, always){
            requestByAjax({
                url : resourcePath + "join",
                type : 'POST',
                data : {'group_id': groupId}
            }, done, fail, always);
        };

        this.getEventReports = function(groupID, done, fail, always) {
            requestByAjax({
                url : resourcePath + "eventReports/" + groupID,
                type : 'GET'
            }, done, fail, always);
        };
        
        this.getEvents = function(groupID, done, fail, always) {
            requestByAjax({
                url : resourcePath + "eventIds/" + groupID,
                type : 'GET'
            }, done, fail, always);
        };

        this.isModerator = function(groupId, done, fail, always){
            requestByAjax({
                url : resourcePathUser + "isModerator/" + groupId,
                type : 'GET'
            }, done, fail, always);
        };

    }

    return GroupsDAO;

})();