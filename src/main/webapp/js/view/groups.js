var GroupsView = (function(){

    var dao;
    
    var self;

    var listId = 'group-list';
    var listQuery = '#' + listId;
    var formSearchSubmit = '#show_search';
    var formSearch = '#search-form';
    var cleaner = '#clear_form';
    var formEventReport = '#report-form';

    
    function GroupsView(groupsDAO, listContainerId){

        dao = groupsDAO;
        self = this;
        
        
        
        this.init = function() {



            insertGroupList($('#' + listContainerId));
                $(formSearchSubmit).submit(function(event){

                $('#' + listId + ' > tbody').empty();

                var group = self.getGroupInForm();
                if(group==null){
                    alert("Revise los campos.");
                }

                if(group.selectedTopic!=''){
                    dao.listGroupByTopic(group.selectedTopic, function(groups) {
                        
                        $.each(groups, function(key, group) {
                            appendToTable(group);
                        });
                    });
                }else if (group.groupname!=''){
                    dao.listGroupByName(group.groupname, function(group) {
                        appendToTable(group);
                    });
                }else{
                    alert("No ha insertado ningún campo!");
                }
                $("#reports-container").empty();
                
                $(formSearch)[0].reset();

            });

            $(cleaner).click(function(){
                $(formSearch)[0].reset();
            });

            $('#group-detail-container').on('submit', '#report-form', function(event){
                let event_dao = new EventsDAO();
                event.preventDefault();
                var reportReason = $('#report_reason').val();
                var event_id = $(this).find('#event_id').val(); 
            
                var formData = {
                    event_id: event_id,
                    reportReason: reportReason
                };
            
                event_dao.addEventReport(formData,
                    function(){
                        alert("Se ha reportado el evento");
                        $('#report-form-container').hide(); 
                    },
                    function(error){
                        alert("Ha ocurrido un error al reportar el evento: " + error);
                    }
                );
            
                $(formEventReport)[0].reset();
            });
            
            $('#events-container').on('click', '#new_event_cancel', function(event){
                $("#eventoForm").hide();
            });

            $('#group-detail-container').append('<div id="report-form-container"></div>');
    
        };

        this.getGroupInForm = function(){
            var form = $(formSearch);
            var groupName = form.find('input[name="groupname"]').val();
            if (groupName.length > 50) {
                alert('El nombre del grupo no puede exceder los 50 caracteres.');
                return null; 
            }
            return {
                'groupname': form.find('input[name="groupname"]').val(),
                'selectedTopic': form.find('select[name="select-list"]').val()
            };
        };

    };

    var insertGroupList = function(parent) {
        parent.append(
            '<table id="' + listId + '" class="table">\
				<thead>\
					<tr class="row">\
						<th class="col-sm-4">Nombre de grupo</th>\
						<th class="col-sm-5">Descripción</th>\
						<th class="col-sm-3">Detalles</th>\
					</tr>\
				</thead>\
				<tbody>\
				</tbody>\
			</table>'
        );
    };

    var appendToTable = function(group){
        $('#' + listId + ' > tbody:last').append(createGroupRow(group));
        $("#group-button-" + group.groupID ).click(function(){
            showGroupView(group, dao)
        });
    };

    var createGroupRow = function(group){
        var toret = "";
        
        toret = '<tr id="group-'+ group.groupID +'" class="row">\
            <td class="groupname col-sm-4">' + group.groupname + '</td>\
            <td class="description col-sm-5">' + group.description + '</td>\
            <td class="col-sm-2"><button class="btn btn-primary" id="group-button-' + group.groupID +'">Detalles</button></td>\
        </tr>';

        if(localStorage.getItem('authorization-token') !== null) {
            dao.isModerator(group.groupID, function (isModerator) {
                if (isModerator) {

                    var rowElement = document.getElementById("group-" + group.groupID);
                    if (rowElement) {
                        rowElement.style.border = "2px solid orange";
                    }
                }
            });
        }
    
        
        return toret;
    };

    

    return GroupsView;
})();

function showGroupView(group, dao){
    
    let container = $('#group-detail-container')
    let content = $('#group-content')
    let header = $('#group-title')
    let event_list = $('#group-event-list')

    let event_dao = new EventsDAO();

    dao = new GroupsDAO();
    user_dao = new UserDAO();

    content.empty()
    header.empty()
    event_list.empty()
    header.append("<h2>" + group.groupname + "</h2>")
    content.append("<p>" + group.description + "<p>")
    if(localStorage.getItem('authorization-token') != null){
        content.append("<button class='btn btn-primary newEventButton-" + group.groupID + "'>Añadir nuevo evento</button>");
        dao.isModerator(group.groupID,
            function(isModerator) {
                if (isModerator) {
                    content.append("<button class='btn btn-warning showEventReportsButton-" + group.groupID + "'>Ver los reportes de este grupo</button>");
                }
            }
        );
    }


    event_dao.getEventsForGroup(group.groupID,
        function(event) {
            $.each(event, function(key, event) {
                addEvent(event, event_list)

            });
        },
        function() {
            alert('No ha sido posible acceder al listado de eventos para el grupo ' + group.groupname);
        }
    );



    $('#groupId').val(group.groupID);

    if(localStorage.getItem('authorization-token') !== null){
        $(".newEventButton-" + group.groupID).click(function(){
            $('#events-container').show();
            $("#newEventTitle").show();
            $("#eventoForm").show();
            if ($("#eventoForm").is(":visible")) {
                document.getElementById("eventoForm").scrollIntoView({ behavior: 'smooth' });
            }
        });

        $("#joinButton").click(function(){
            joinGroup(group.groupID)
        });

        //remove onclick from button if user has already joined
        user_dao.getJoinedGroups(function(groups) {

            $.each(groups, function(key, g) {
                if(g === group.groupID){
                    button = $("#joinButton")
                    button.empty();
                    button.append("Unido");
                    button.prop("onclick", null).off("click");
                }
            });
        });
    }
    else{
        $(".newEventButton-" + group.groupID).hide()
        $("#joinButton").hide()
    }



    $(document).on('click', ".showEventReportsButton-" + group.groupID, function(){
        var button = $(".showEventReportsButton-" + group.groupID);

        if (button.text() === "Ver los reportes de este grupo") {
            dao.getEventReports(group.groupID, function(eventReports) {
                appendToEventReportList(eventReports);
            });
    
            dao.getEvents(group.groupID, function(eventId) {
                event_dao.getCommentsByEventId(eventId, function(comments) {
                    comments.forEach(function(comment) {
                        event_dao.getReportComments(comment.commentId, function(commentReports) {
                            appendToCommentReportList(commentReports);
                        }, function(error) {
                            console.error("Error obteniendo reportes de comentarios:", error);
                        });
                    });
                }, function(error) {
                    console.error("Error obteniendo comentarios por ID de evento:", error);
                });
            }, function(error) {
                console.error("Error obteniendo eventos por ID de grupo:", error);
            });
    
            button.removeClass("btn-warning").addClass("btn-danger").text("Cerrar reportes de grupo");
            $("#reports-container").show();
        } else if (button.text() === "Cerrar reportes de grupo") {
            
            button.removeClass("btn-danger").addClass("btn-warning").text("Ver los reportes de este grupo");
            $("#reports-container").hide();
        }
    });

    cleanView();

    container.show();
}

function joinGroup(groupID){
    let dao = new GroupsDAO()
    dao.joinGroup(groupID,
        function(){
        button = $("#joinButton")
        button.empty();
        button.append("Unido");
        button.prop("onclick", null).off("click");
    });
}

function appendToCommentReportList(commentReports) {
    var container = $("#reports-container");

    var ul = $("<ul>");

    commentReports.forEach(function(report) {
        var li = $("<li>");
        li.append($("<span>").text("Se ha reportado el comentario {" + report.commentId + "} con razón: ").css("color", "black"));
        li.append($("<span>").text("'" + report.reportReason + ".'").css("color", "red"));
        li.append($("<span>").text(" Por el usuario: " + report.userId + ".").css("color", "black"));
        ul.append(li);

        
    });

    container.append(ul);

};
function appendToEventReportList(eventReports) {
    var container = $("#reports-container");
    container.empty();

    var ul = $("<ul>");

    eventReports.forEach(function(report) {
        var li = $("<li>");

        li.append($("<span>").text("Se ha reportado el evento {" + report.eventId + "} con razón: ").css("color", "black"));
        li.append($("<span>").text("'" + report.reportReason + ".'").css("color", "red"));
        li.append($("<span>").text(" Por el usuario: " + report.ownerId + ".").css("color", "black"));
        ul.append(li);


    });

    container.append(ul);
};



function addEvent(event, parent) {
    let event_dao = new EventsDAO();
    var newDiv = $("<div></div>").addClass("card");

    var cardBody = $("<div></div>").addClass("card-body");

    var eventName = $("<h5></h5>").addClass("card-title").text(event.eventName);
    var eventDate = $("<h6></h6>").addClass("card-subtitle mb-2 text-muted").text(event.eventDate);
    var eventDescription = $("<p></p>").addClass("card-text").text(event.description);

    var reportButton = $("<button></button>").addClass("btn btn-danger").text("Reportar Evento");
    var showCommentsButton = $("<button>Comentarios</button>");

    var event_id = null;

    if (!isLoggedIn()){
        reportButton.hide();
    }

    event_dao.getEventId_ByName(event.eventName,
        function(eventId) {
            event_id = eventId;
            showCommentsButton.addClass("btn btn-primary show-comments-button").attr("data-event-id", eventId);
        },
        function() {
            alert("No ha sido posible obtener el id del evento.");
        }
    );

    reportButton.click(function() {
        showReportForm(event_id);
    });

    cardBody.append(eventName, eventDate, eventDescription, reportButton, showCommentsButton);
    newDiv.append(cardBody);

    parent.append(newDiv);
}


function showReportForm(event_id) {
    $("#newEventTitle").hide();
    $("#eventoForm").hide();
    $("#reportCommentFormContainer").hide();
    $('#report-form-container').empty();

    var reportForm = '<form id="report-form">' +
                        '<input type="hidden" id="event_id" name="event_id" value="' + event_id + '">' +
                        '<div class="mb-3">' +
                            '<label for="report_reason" class="form-label">Razón del reporte:</label>' +
                            '<textarea class="form-control" id="report_reason" name="report_reason" rows="3"required></textarea>' +
                        '</div>' +
                        '<button type="submit" class="btn btn-danger">Enviar Reporte</button>' +
                    '</form>';
 
    $('#report-form-container').append(reportForm);
    $('#report-form-container').show();
}

function hideGroupView(){
    
    let container = $('#group-detail-container');
    $("#search-container").show();
    container.hide();
    $('#events-container').hide();
}

function showEventView(event){
    let container = $('#event-detail-container')
    let content = $('#event-content')
    let header = $('#event-title')
}

function cleanView(){
    $("#search-container").hide();
}

function isLoggedIn(){
    return localStorage.getItem('authorization-token') !== null
}
