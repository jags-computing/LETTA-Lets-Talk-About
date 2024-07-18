var TopicsDAO = (function() {
    var resourcePath = "rest/topics/";
    var requestByAjax = function(data, done, fail, always){
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

    function TopicsDAO(){

        this.getTopics = function(done, fail, always){
            requestByAjax({
            url : resourcePath,
            type : 'GET'
            }, done, fail, always);
        };

    }
    return TopicsDAO;
})();