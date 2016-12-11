var $;

addListeners = function() {
  $('a').click(function() {
      var href = this.href.substring(this.href.indexOf("#"));
      $('html, body').animate({
          scrollTop: ($(href).offset().top - $("nav").height() / 2)
      }, 750);
      return false;
  });
  $(".nav-link").mouseup(function() {
      $(this).blur();
  });
};
checkNav = function() {
    $nav = $("nav");
    if ($(window).scrollTop() < $("#vision").offset().top - 100) {
        if ($nav.hasClass("not-top")) $nav.removeClass("not-top");
    } else {
        if (!$nav.hasClass("not-top")) $nav.addClass("not-top");
    }
}

$(document).ready(function () {
    $("nav").load("navbar.html", addListeners);
    $("nav").addClass("navbar");
    $("nav").css("border-radius", "0px");
    $("nav").css("margin-bottom", "0px");
    $("nav").css("position", "fixed");
    checkNav();
});

$(document).scroll(checkNav);

