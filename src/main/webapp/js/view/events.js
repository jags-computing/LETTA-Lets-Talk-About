var EventsView = (function(){

    var dao;

    var self;
    var formNewEventSubmit = "#new_event_submit";
    var eventContainer = "#events-container";
    var eventoForm = "#eventoForm";
    var reportCommentFormContainer = "#reportCommentFormContainer";
    var formNewCommentReport = "#add_comment_report_button";

    function EventsView(eventsDAO){
        dao = eventsDAO;
        self = this;

        this.init = function(){

            $(eventContainer).hide();
            
            $(formNewEventSubmit).click(function(event){
                event.preventDefault(); 
                
                var newEvent = getFormData();
                
                dao.addEvent(newEvent, 
                    function(){
                        alert("Se ha insertado el evento!");
                    }
                );

                $("#events-container").hide();
                $(eventoForm)[0].reset();
                
            });

            
            $(document).on("click", ".show-comments-button", function() {
                var eventId = $(this).attr("data-event-id");
                loadComments(eventId);
                var button = $(".show-comments-button");

                if (button.text() === "Cerrar comentarios") {
                    $("#event-comments").hide();
                    button.removeClass("btn-danger").addClass("btn-primary").text("Comentarios");
                }
                else if (button.text() === "Comentarios"){
                    $("#event-comments").show();
                    button.removeClass("btn-primary").addClass("btn-danger").text("Cerrar comentarios");
                }

            });

            $(document).on("click", ".addComment", function(){

            });

            $(document).on("click", ".report-comment-button", function() {
                $("#reportCommentForm")[0].reset();
                $("#report-form").hide();
                $("#newEventTitle").hide();
                $("#eventoForm").hide();

                var commentId = $(this).data("comment-id");
                $("#reportCommentForm #comment_report_id").val(commentId);


                $(reportCommentFormContainer).show();
            });

            $(formNewCommentReport).click(function(event){
                event.preventDefault();

                $("#reports-container").hide();

                var newCommentReport = {
                    comment_id: $("#reportCommentForm #comment_report_id").val(),
                    reportReason: $("#reportReason").val()
                };
                
                dao.addCommentReport(newCommentReport, 
                    function(){
                        alert("Se ha insertado el reporte al comentario!");
                    },
                    function(xhr, status, error) {
                        console.log(xhr.responseText);
                    }
                );

                $(reportCommentFormContainer).hide();
            });

        

        };

        
        function loadComments(eventId) {
            $("#report-form").hide();
            $("#reports-container").hide();
            $(reportCommentFormContainer).hide();
            
            dao.getCommentsByEventId(eventId,
                function(comments) {
                    var commentsList = $("#comments-list");
                    commentsList.empty();
                    commentsList.css({
                        "border": "1px solid #ccc",
                        "padding": "30px",
                        "border-radius": "5px"
                    });
                    
                    comments.forEach(function(comment) {
                        var listItem = $("<li></li>").text(comment.commentText);
                        var reportButton = $("<button></button>")
                            .text("Reportar comentario")
                            .addClass("report-comment-button")
                            .attr("data-comment-id", comment.commentId)
                            .css({
                                "color": "red",
                                "border": "none",
                                "background-color": "white",
                                "text-decoration": "underline"
                            });
                        
                        if (isLoggedIn()) {
                            listItem.append(reportButton);
                        }
                        
                        commentsList.append(listItem);
                    });

                    if(localStorage.getItem('authorization-token') !== null)      {
                        var addCommentButton = $("<button></button>").addClass("btn btn-primary addComment").text("+");
                        commentsList.append(addCommentButton);


                        var commentForm = $('<form id="comment-form" style="display:none;"></form>').css({
                            "margin-top": "20px"
                        });
                        var commentTextInput = $('<textarea id="comment-text" class="form-control" rows="3" placeholder="Escribe tu comentario"></textarea>').css({
                            "margin-bottom": "10px"
                        });
                        var submitButton = $('<button type="submit" class="btn btn-success">Enviar</button>');
                        var cancelButton = $('<button type="button" class="btn btn-secondary">Cancelar</button>').css({
                            "margin-left": "10px"
                        });

                        commentForm.append(commentTextInput, submitButton, cancelButton);
                        commentsList.append(commentForm);

                        addCommentButton.on("click", function() {
                            commentForm.show();
                            addCommentButton.hide();
                        });

                        cancelButton.on("click", function() {
                            commentForm.hide();
                            addCommentButton.show();
                        });

                        commentForm.on("submit", function(event) {
                            event.preventDefault();
                            var commentText = $("#comment-text").val().trim();

                            if (commentText !== "") {
                                var formData = { commentText: commentText };

                                dao.addComment(formData, eventId,
                                    function(response){
                                        $("#comment-text").val("");
                                        commentForm.hide();
                                        addCommentButton.show();
                                        loadComments(eventId);
                                    },
                                    function(xhr, status, error) {
                                        console.error("Error al enviar el comentario:", error);
                                    },
                                    function() {
                                    }
                                )
                            } else {
                                alert("El comentario no puede estar vacío.");
                            }
                        });
                    }

        
                },
                function(xhr, status, error) {
                    console.error("Error loading comments:", error);
                }
            );
        }
        function getFormData() {
            var formData = $("#eventoForm").serialize();
            return formData;
        }

        function isLoggedIn(){
            return localStorage.getItem('authorization-token') !== null
        }
        
    }

    return EventsView;
})();
