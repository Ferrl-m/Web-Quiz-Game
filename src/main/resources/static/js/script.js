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
                window.location.href = '/';
            })
            .catch(function(error) {
                console.error('Error deleting account:', error);
            });
    }
}

function confirmQuizDelete(quiz) {
    var result = confirm("Are you sure you want to delete your quiz? This action cannot be undone.");
    if (result) {
        fetch('/quizzes/' + quiz + '/delete', {
            method: 'DELETE'
        })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function(text) {
                console.log('Quiz deleted:', text);
                window.location.href = '/quizzes/0';
            })
            .catch(function(error) {
                console.error('Error deleting quiz:', error);
            });
    }
}

let sortDirections = {};

function sortTable(columnIndex) {
    var table, rows, switching, i, x, y, shouldSwitch;
    table = document.getElementById("myTable");
    switching = true;
    while (switching) {
        switching = false;
        rows = table.rows;
        for (i = 1; i < (rows.length - 1); i++) {
            shouldSwitch = false;
            x = rows[i].getElementsByTagName("td")[columnIndex];
            y = rows[i + 1].getElementsByTagName("td")[columnIndex];
            let sortDirection = sortDirections[columnIndex] || "asc";
            if (sortDirection === "asc") {
                if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            } else {
                if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
        }
    }
    // Toggle the sort direction for the current column
    let currentSortDirection = sortDirections[columnIndex] || "asc";
    sortDirections[columnIndex] = currentSortDirection === "asc" ? "desc" : "asc";
}

function search() {
    // Get the input value
    let input = document.getElementById("searchInput");
    let filter = input.value.toUpperCase();

    // Get the table rows
    let table = document.getElementById("myTable");
    let rows = table.getElementsByTagName("tr");

    // Loop through all rows and hide those that don't match the search query
    for (let i = 0; i < rows.length; i++) {
        let title = rows[i].getElementsByTagName("td")[1];
        if (title) {
            let text = title.textContent || title.innerText;
            if (text.toUpperCase().indexOf(filter) > -1) {
                rows[i].style.display = "";
            } else {
                rows[i].style.display = "none";
            }
        }
    }
}

// Call the search function when the input value changes
document.getElementById("searchInput").addEventListener("input", search);