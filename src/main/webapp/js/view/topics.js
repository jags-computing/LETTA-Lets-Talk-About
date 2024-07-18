var TopicsView = (function(){
    var dao;
    var self;
    var selectedTopic = '';
    
    function TopicsView(topicsDAO, listContainerId){
        dao = topicsDAO;
        self = this;

        this.init = function() {
            $('#select-list-topic').click(function(event){
                
                dao.getTopics(
                    function(topics){
                        appendToList(topics);
                    }
                );
            });
        };
    }

    var appendToList = function(topics){
        selectedTopic = $('#select-list-topic').val();
        $('#select-list-topic').empty();
        $('#select-list-topic').append('<option value=""></option>');
        $.each(topics, function(index, topic){
            $('#select-list-topic').append('<option value="' + topic + '">' + topic + '</option>');
        });
        $('#select-list-topic').val(selectedTopic);
    };

    return TopicsView;
})();
