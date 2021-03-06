/* global $, Cookies */
/* eslint-env browser*/
$(document).ready(function() {
  var ERROR = "error";
  var POST_MESSAGE_TIMEOUT = 500; // milliseconds
  var MAX_PAGE_NUM = 4;

  var domain = document.getElementById("domain").getAttribute("value");

  // update page contents
  function update() {
    $(".status").each(function() {
      $(this).text("trying");
    });
    var updt = $("<div></div>").load(domain + "/polr/updt?id=" + $("#main-post-id").text() + "&page=" + $("#page").text() + "&sort=" + $("#sort").text() + "&flow=" + $("#flow").text() + "#full", function(response, status, xhr) {
      if (status === "error") {
        $(".status").each(function() {
          $(this).text(xhr.statusText);
        });
      }
      else {
        $("#main-post").replaceWith(updt.find("#main-post-container").html());
        $("#replies").replaceWith(updt.find("#replies-container").html());
        $("title").text(updt.find("#main-post-title").text() + " ~ xarql");
        viewLinks();
        revealLinks();
        $(".status").each(function() {
          $(this).text(xhr.statusText);
        });
      }
    });
  }

  $(".update-button").each(function() {
    var $this = $(this);
    $this.on("click", function() {
      update();
    });
  });

  // ajax posting
  $( "#post-form" ).submit(function( event ) {
    // stop form from submitting normally
    event.preventDefault();
    $(".status").each(function() {
      $(this).text("trying");
    });

    // get values from form, reset form
    var $form = $( this ),
      title = $form.find("input[name='title']").val(),
      content = $form.find("textarea[name='content']").val(),
      answers = $form.find("input[name='answers']").val(),
      url = $form.attr("action");
    $form.trigger("reset");
    $("#replying-to-input").val(parseInt($("#main-post-id").text()));

    // send the data using AJAX POST
    $.ajax({
      type: "POST",
      url: url,
      data : {
        title: title,
        content: content,
        answers: answers,
      }
    }).done(function(){
      $(".status").each(function() {
        $(this).text("success");
        $("#advisory").text("Wait 20 seconds between posts");
        $("#advisory").show();
      });
    })
      .fail(function(){
        $(".status").each(function() {
          $form.find("input[name='title']").val(title.trim());
          $form.find("textarea[name='content']").val(content.trim());
          $form.find("input[name='answers']").val(answers);
          $(this).text("error");
          $("#advisory").text("Try reloading if posting fails. Remember to solve the Recaptcha.");
          $("#advisory").show();
        });
      })
      .always(function(){
        window.setTimeout(update, POST_MESSAGE_TIMEOUT); // wait .5 seconds
      });
  });

  function nav() {
    $(".status").each(function() {
      $(this).text("trying");
    });
    var
      page = $("#page-dropdown").val(),
      sort = $("#sort-dropdown").val(),
      flow = $("#flow-dropdown").val();
    var updt = $("<div></div>").load(domain + "/polr/updt?id=" + $("#main-post-id").text() + "&page=" + page + "&sort=" + sort + "&flow=" + flow, function(response, status, xhr) {
      if (status === "error") {
        $(".status").each(function() {
          $(this).text(xhr.statusText);
        });
      }
      else {
        $("#main-post").replaceWith(updt.find("#main-post-container").html());
        $("#replies").replaceWith(updt.find("#replies-container").html());
        $("title").text(updt.find("#main-post-title").text() + " ~ xarql");
        history.pushState("xarql", "xarql", window.location.pathname + "?page=" + page + "&sort=" + sort + "&flow=" + flow);
        $("#page").text(page);
        $("#sort").text(sort);
        $("#flow").text(flow);
        viewLinks();
        revealLinks();
        if (page > 0) {
          $("#prev-form").show(); $("#prev-form").css("display", "inline");
        }
        else {
          $("#prev-form").hide();
        }
        if (page < MAX_PAGE_NUM) {
          $("#next-form").show(); $("#next-form").css("display", "inline");
        }
        else {
          $("#next-form").hide();
        }
        $(".status").each(function() {
          $(this).text(xhr.statusText);
        });
        $("html, body").animate({scrollTop: 0}, "fast");
      }
    });
  }

  $("#nav-form").submit(function(event) {
    event.preventDefault();
    nav();
  });
  $("#next-form").submit(function(event) {
    event.preventDefault();
    var pageNum = parseInt($("#page-dropdown").val());
    if (pageNum < MAX_PAGE_NUM) {
      $("#page-dropdown").val(pageNum + 1);
    }
    nav();
  });
  $("#prev-form").submit(function(event) {
    event.preventDefault();
    var pageNum = parseInt($("#page-dropdown").val());
    if (pageNum > 0) {
      $("#page-dropdown").val(pageNum - 1);
    }
    nav();
  });

  // change font size
  $("html").css("font-size", Cookies.get("font-size"));
  $("#text-up").on("click", function() {
    var computedFontSize = parseFloat(window.getComputedStyle(document.getElementById("html")).fontSize);
    $("#font-size").remove();
    $("html").css("font-size", computedFontSize + 1 + "px");
    Cookies.set("font-size", computedFontSize + 1 + "px");
  });
  $("#text-dn").on("click", function() {
    var computedFontSize = parseFloat(window.getComputedStyle(document.getElementById("html")).fontSize); // get font size of <html></html>
    $("#font-size").remove();
    $("html").css("font-size", computedFontSize - 1 + "px"); // change font size by -1
    Cookies.set("font-size", computedFontSize - 1 + "px");
  });

  function view(id, popstate) {
    $(".status").each(function() {
      $(this).text("trying");
    });
    var updt = $("<div></div>").load(domain + "/polr/updt?id=" + id + "&page=0", function(response, status, xhr) {
      if (status === ERROR) {
        $(".status").each(function() {
          $(this).text(xhr.statusText);
        });
      }
      else {
        $("#main-post").replaceWith(updt.find("#main-post-container").html());
        $("#replies").replaceWith(updt.find("#replies-container").html());
        $("title").text(updt.find("#main-post-title").text() + " ~ xarql");
        if (popstate) {
          // do nothing
        }
        else {
          history.pushState("xarql", "xarql", domain + "/polr/" + id);
        }
        $(".status").each(function() {
          $(this).text(xhr.statusText);
        });
        $("html, body").animate({scrollTop: 0}, "fast");
        $("#replying-to-input").val(parseInt($("#main-post-id").text()));
        $("#page-dropdown").val(0);
        $("#prev-form").hide();
        $("#next-form").show();
        $("#next-form").css("display", "inline");
        viewLinks();
        revealLinks();
      }
    });
  }

  function viewLinks() {
    $(".view-link").each(function() {
      $(this).unbind("click");
      $(this).on("click", function() {
        var id = $(this).attr("post-id");
        view(id, false);
        return false;
      });
    });
  }
  viewLinks();

  window.addEventListener("popstate", function(event) {
    var pieces = window.location.href.split("/");
    if (pieces[pieces.length - 2] === "polr") {
      var currentID = pieces[pieces.length - 1];
      if (currentID.includes("?")) {
        window.location.href = window.location.href;
      }
      else {
        view(currentID, true);
      }
    }
    else {
      window.location.href = window.location.href;
    }
  }, false);

  function changeTheme(theme) {
    if (theme !== "light" && theme !== "dark" && theme !== "rainbow") {
      theme = "light";
    }
    $("#theme-styles").attr("href", domain + "/src/common/" + theme + "-common.min.css");
    Cookies.set("theme", theme);
    $(".theme-button").show();
    $("#" + theme + "-theme-button").hide();
  }
  changeTheme(Cookies.get("theme"));
  $(".theme-button").each(function() {
    var $this = $(this);
    $this.on("click", function() {
      changeTheme($this.attr("data"));
    });
  });

  // option Pane
  function fontWeight(weight) {
    if (weight === "light") {
      $("p").css("font-weight", "200");
      $(".bold").css("font-weight", "400");
      $("h6").css("font-weight", "400");
      $("#font-light-button").hide();
      $("#font-normal-button").show();
      Cookies.set("font-weight", "light");
    }
    else {
      $("p").css("font-weight", "400");
      $(".bold").css("font-weight", "600");
      $("h6").css("font-weight", "600");
      $("#font-normal-button").hide();
      $("#font-light-button").show();
      Cookies.set("font-weight", "normal");
    }
  }
  fontWeight(Cookies.get("font-weight"));
  $("#font-light-button").on("click", function() {
    fontWeight("light");
  });
  $("#font-normal-button").on("click", function() {
    fontWeight("normal");
  });

  // enable JS buttons + Option Pane
  $(".ajax-bar").each(function() {
    $(this).show();
  });
  $("#option-pane-open-button").on("click", function() {
    $("#option-pane").show();
    $("#option-pane-close-button").show();
    $(this).hide();
  });
  $("#option-pane-close-button").on("click", function() {
    $("#option-pane").hide();
    $("#option-pane-open-button").show();
    $(this).hide();
  });

  // enables links for viewing censored content
  function reveal(id) {
    $("#post-inner-" + id).show();
    $("#post-warning-" + id).hide();
  } // reveal()

  function revealLinks() {
    $(".reveal-link").each(function() {
      var $this = $(this);
      $this.on("click", function() {
        reveal($this.attr("data"));
        $this.hide();
      });
    });
  }
  revealLinks();

  /*
   * auto update page to reflect options set on another page
   * function autoOption() {
   * if($('#styles').length)
   * defaultStylesInjected = true;
   * if(defaultStylesInjected == false)
   * window.setTimeout(autoCrunch, 100);  wait 100 milliseconds before checking again
   * else {
   * if(Cookies.get('theme') === 'dark')
   * setTheme('dark');
   * }
   * }
   * autoOption();
   */
});
