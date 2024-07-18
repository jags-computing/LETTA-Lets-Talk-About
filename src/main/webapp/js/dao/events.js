var EventsDAO = (function(){

    var resourcePath = "rest/events";
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

    function EventsDAO(){

        this.addEvent = function(formData, done, fail, always){
        requestByAjax({
            url : resourcePath,
            type : 'POST',
            data : formData
            }, done, fail, always);
        };

        this.addEventReport = function(formData, done, fail, always){
            requestByAjax({
                url : resourcePath + "/report",
                type : 'POST',
                data : formData
            },done, fail, always);
        };

        this.addCommentReport = function(formData, done, fail, always){
            requestByAjax({
                url : resourcePath + "/report/comment",
                type : 'POST',
                data : formData,
            },done, fail, always);
        };

        this.addComment= function(formData, eventId, done, fail, always){
            requestByAjax({
                url : resourcePath + "/" + eventId + "/comments",
                type : 'POST',
                data : formData,
            },done, fail, always);
        };

        this.getEventsForGroup= function(group_id, done, fail, always){
            requestByAjax({
                url : resourcePath + "/by_group/" + group_id,
                type : 'GET',
            }, done, fail, always);
        };
        this.getEventId_ByName = function(eventName, done, fail, always){
            requestByAjax({
                url : resourcePath + "/byname/" +  eventName,
                type : 'GET',   
            }, 
            function(data) {
                var eventId = JSON.parse(data);
                done(eventId);
            }, 
            fail, 
            always);
        };
        this.getEventName_ById = function(eventId, done, fail, always){
            requestByAjax({
                url : resourcePath + "/byid/" +  eventId,
                type : 'GET',
            }, 
            function(data) {
                try {
                    var eventName = JSON.parse(data);
                    done(eventName);
                } catch (error) {
                    console.error("Error parsing JSON:", error);
                    alert("me cago en mis muertos")
                    fail();
                }
            }, 
            fail, 
            always);
        };

        this.getCommentsByEventId = function(eventId, done, fail, always) {
            requestByAjax({
                url : resourcePath + "/" + eventId + "/comments",
                type : 'GET',
            }, done, fail, always);
        };

        this.getReportComments = function(commentId, done, fail, always){
            requestByAjax({
                url : resourcePath + "/report/comment/" + commentId,
                type : 'GET',
            }, done, fail, always);
        };

    }

    return EventsDAO;
})();