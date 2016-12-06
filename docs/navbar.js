var $;

$(document).ready(function () {
    "use strict";
    $.ajax({
        url: "navbar.html",
        isLocal: true
    }).done(function (data) {
        document.getElementsByTagName("nav")[0].innerHTML = data;
    });
});
