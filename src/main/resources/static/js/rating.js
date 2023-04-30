document.addEventListener('DOMContentLoaded', function() {
    var quizId = document.querySelector('[name="quizId"]').value;
    var ratingInputs = document.querySelectorAll('input[name="rate"]');
    for (var i = 0; i < ratingInputs.length; i++) {
        ratingInputs[i].addEventListener('change', function() {
            var rating = this.value;
            var xhr = new XMLHttpRequest();
            xhr.open('POST', '/quizzes/rate/' + quizId, true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onreadystatechange = function() {
                if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
                    console.log('Rating submitted');
                } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status !== 200) {
                    console.log('Error submitting rating: ' + xhr.status);
                }
            };
            xhr.send('rating=' + encodeURIComponent(rating));
        });
    }
});
