var $, $scrollTransition;

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
setClassPresent = function($el, cls, present) {
    if (present && !$el.hasClass(cls)) $el.addClass(cls);
    if (!present && $el.hasClass(cls)) $el.removeClass(cls);
};
checkNav = function() {
    $nav = $("nav");
    if ($(window).scrollTop() < $("#vision").offset().top - 100) {
        if ($nav.hasClass("not-top")) $nav.removeClass("not-top");
    } else {
        if (!$nav.hasClass("not-top")) $nav.addClass("not-top");
    }
    $scrollTransition.each(function(index, el) {
        var scrollBottom = $(window).scrollTop() + $(window).height();
        var elPos = $(el).offset().top + parseInt($(el).attr("data-scroll-transition"));
        setClassPresent($(el), "in-view", scrollBottom > elPos);
    });
};

$(document).ready(function () {
    $("nav").load("navbar.html", addListeners);
    $("nav").addClass("navbar");
    $("nav").addClass("navbar-default");
    $("nav").css("border-radius", "0px");
    $("nav").css("margin-bottom", "0px");
    $("nav").css("position", "fixed");
    $scrollTransition = $("[data-scroll-transition]");
    checkNav();
});

$(document).scroll(checkNav);

