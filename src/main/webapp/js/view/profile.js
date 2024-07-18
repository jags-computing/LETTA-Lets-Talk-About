var ProfileView = (function(){

    var dao;
    var self;
    var formSubmit = '#edit_profile_button';
    var form = '#updateProfileForm';



    function ProfileView(userDAO, listContainerId){

        dao = userDAO;
        self = this;

        this.init = function() {
            $(formSubmit).submit(function(event){

                var file = $('#profilePicture')[0].files[0];
                var reader = new FileReader();
                var form = $("#updateProfileForm");

                reader.onloadend = function() {
                    newProfile = {
                        "nickname": form.find('input[name="nickname"]').val(),
                        "about_me": form.find('input[name="about_me"]').val(),
                        "public_contact": form.find('input[name="public_contact"]').val(),
                        "profilePicture": reader.result
                    }

                    dao.updateProfile(newProfile,
                        function(){
                            alert("Perfil actualizado");
                        }
                    );

                    new bootstrap.Modal(document.getElementById('profile_modal'), {
                        keyboard: false
                    }).show()
                }

                if($('#profilePicture')[0].files.length !== 0){
                    reader.readAsDataURL(file);
                }
                else{
                    newProfile = {
                        "nickname": form.find('input[name="nickname"]').val(),
                        "about_me": form.find('input[name="about_me"]').val(),
                        "public_contact": form.find('input[name="public_contact"]').val(),
                    }

                    dao.updateProfile(newProfile,
                        function(){
                            alert("Perfil actualizado");
                        }
                    );

                    new bootstrap.Modal(document.getElementById('profile_modal'), {
                        keyboard: false
                    }).show()
                }



            });
        };

    }


     return ProfileView;
})();





