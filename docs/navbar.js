var $;

$(document).ready(function () {
    "use strict";
    $.ajax({
        url: "navbar.html",
        isLocal: true
    }).done(function (data) {
        document.getElementById("navbar").innerHTML = data;
    });
});
