function confirmDelete(username) {
    var result = confirm("Are you sure you want to delete your account? This action cannot be undone.");
    if (result) {
        fetch('/users/delete/' + username, {
            method: 'DELETE'
        })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function(text) {
                console.log('Account deleted:', text);
                window.location.href = '/login';
            })
            .catch(function(error) {
                console.error('Error deleting account:', error);
            });
    }
}
