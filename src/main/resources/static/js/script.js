$(document).ready(function () {
    $("#editUserButton, #deleteUserButton").on("click", function (e) {
        e.preventDefault();
        $("#editDeleteUserModal").modal("show").find(".modal-content").load($(this).attr("href"));
    });
});
