var $;

$(document).ready(function () {
    "use strict";
    $.ajax({
        url: "navbar.html",
        isLocal: true
    }).done(function (data) {
        $("nav").load("navbar.html");
        $("nav").addClass("navbar");
        $("nav").css("border-radius", "0px");
    });
});
