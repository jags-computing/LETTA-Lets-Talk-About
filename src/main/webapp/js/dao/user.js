var UserDAO = (function(){

    var resourcePath = "rest/users/";
    var requestByAjax = function(data, done, fail, always) {
		done = typeof done !== 'undefined' ? done : function() {};
		fail = typeof fail !== 'undefined' ? fail : function() {};
		always = typeof always !== 'undefined' ? always : function() {};

		let authToken = localStorage.getItem('authorization-token');
		if (authToken !== null) {
			data.beforeSend = function(xhr) {
			xhr.setRequestHeader('Authorization', 'Basic ' + authToken);
            xhr.setRequestHeader('content-type', 'application/x-www-form-urlencoded; charset=UTF-8');
			};
		}

		$.ajax(data).done(done).fail(fail).always(always);
    };


    function UserDAO(){

        this.listUserByUsername = function(username, done, fail, always){
            requestByAjax({
                url : resourcePath + "byname/" + username,
                type : 'GET'
            }, done, fail, always);
        };

        this.getJoinedGroups = function(done, fail, always){
            requestByAjax({
            url : resourcePath + "groups/",
            type : 'GET'
            }, done, fail, always);
        };

        this.updateProfile = function(profile, done, fail, always){
            requestByAjax({
                url : resourcePath + "profile",
                type : 'POST',
                data : profile
            }, done, fail, always);
        }



    }

      return UserDAO;



})();