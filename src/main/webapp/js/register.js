async function register(username, password, email) {
    postObject = {username: username, password: password, email: email}
    $.ajax({
        url: 'rest/users',
        type: 'POST',
        data: postObject
    })
        .done(function (response) {
            localStorage.setItem('email', email);
            localStorage.setItem('role', response.role);
            localStorage.setItem('authorization-token', btoa(username + ":" + password));
            window.location = 'main.html';
        })
        .fail(function () {
            alert('User could not be created');
        });
}