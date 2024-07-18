var UserView = (function(){

    var dao;
    var self;
    var listId = 'user-list';
    var listQuery = '#' + listId;
    var formSearchSubmit = '#show_user_search';
    var formSearch = '#user-search-form';
    var cleaner = '#clear_user_form';



    function UserView(userDAO, listContainerId){

        dao = userDAO;
        self = this;

        this.init = function() {
            insertUserList($('#' + listContainerId));
            $(formSearchSubmit).submit(function(event){
                $('#' + listId + ' > tbody').empty();

                var user = self.getUserInForm();
                if(user==null){
                    alert("Revise los campos.");
                }

                // get the user by username
                dao.listUserByUsername(user.username, function(users) {
                    $.each(users, function(key, user) {
                        appendToTable(user);
                    });
                },

                function() {
                    alert('No ha sido posible acceder al listado de usuarios.');
                });


                $(formSearch)[0].reset();


                $(cleaner).click(function(){
                   $(formSearch)[0].reset();
                });
            });
        };

             this.getUserInForm = function(){
                     var form = $(formSearch);
                     var userName = form.find('input[name="username"]').val();
                     if (userName.length > 50) {
                         alert('El nombre del user no puede exceder los 50 caracteres.');
                         return null;
                     }
                     return {
                         'username': form.find('input[name="username"]').val(),
                     };
            };
    };

    var insertUserList = function(parent) {
            parent.append(
                '<table id="' + listId + '" class="table">\
    				<thead>\
    					<tr class="row">\
    						<th class="col-sm-4">Username</th>\
    					</tr>\
    				</thead>\
    				<tbody>\
    				</tbody>\
    			</table>'
            );
        };

        // function that creates a row for the user table
    var createUserRow = function(user){
            return '<tr id="group-'+ user.userID +'" class="row">\
                <td class="username col-sm-4">' + user.username + '</td>\
                <td class="col-sm-2"><button class="btn btn-primary" id="user-button-' + user.userID +'">Profile</button></td>\
            </tr>';
        };

            // function that appends a row to the user table
    var appendToTable = function(user){
        $('#' + listId + ' > tbody:last').append(createUserRow(user));
        $("#user-button-" + user.userID ).click(function(){
            showUserView(user)
        });
    };

    var showUserView = function(user){
        let about_input = $("#about_me")
        let public_contact_input = $("#public_contact")
        let profile_picture_form = $("#profile_picture_input")
        let profile_picture_view = $("#profilePictureView")
        let submit_button = $("#edit_profile_button")
        let nickname_input = $("#nickname")

        if(user.profileAbout !== null){
            about_input.val(user.profileAbout)
        }

        if(user.publicContact !== null){
            public_contact_input.val(user.publicContact)
        }

        if(user.userID !== null){
            profile_picture_view.html('<img alt="Image not found" class="img-fluid" src="' +"/LETTA/rest/files/picture/" + user.userID+ '.png">')
        }
        else{
            profile_picture_view.html('<img alt="Image not found" class="img-fluid" src="/LETTA/rest/files/picture/default.png">')
        }

        if(user.nickname !== null){
            nickname_input.val(user.nickname)
        }




        profile_picture_view.show()
        submit_button.hide()
        profile_picture_form.hide()


        new bootstrap.Modal(document.getElementById('profile_modal'), {
            keyboard: false
        }).show()
    };

     return UserView;
})();





