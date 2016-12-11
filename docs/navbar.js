var $;

addListeners = function() {
  $('a').click(function() {
      var href = this.href.substring(this.href.indexOf("#"));
      console.log(href);
      $('html, body').animate({
          scrollTop: ($(href).offset().top - $("nav").height() / 2)
      }, 750);
      return false;
  })
};

$(document).ready(function () {
    $("nav").load("navbar.html", addListeners);
    $("nav").addClass("navbar");
    $("nav").css("border-radius", "0px");
    $("nav").css("margin-bottom", "0px");
    $("nav").css("position", "fixed");
});

