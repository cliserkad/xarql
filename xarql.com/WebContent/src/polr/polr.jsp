<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html id="html">
<head>
  <!-- Global site tag (gtag.js) - Google Analytics -->
  <script async src="https://www.googletagmanager.com/gtag/js?id=${google_analytics_id}"></script>
  <script>
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());
    gtag('config', '${google_analytics_id}');
  </script>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>${posts.get(0).getTitleText()} ~ xarql</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta id="domain" value="${domain}">
  <link rel="stylesheet" type="text/css" id="theme-styles" href="${domain}/src/common/${theme}-common.min.css">
  <script src="https://code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous" defer=""></script>
  <script src="${domain}/src/common/jscookie.js" defer=""></script>
  <script src="${domain}/src/polr/polr.min.js" defer=""></script>
  <link rel="shortcut icon" href="${domain}/logo.png" type="image/x-icon">
  <link rel="apple-touch-icon" href="${domain}/logo-circle.png">
  <style id="font-size">
    html, body {
      font-size: ${font_size}
    }
  </style>
  <style id="font-weight">
    p {
      font-weight: ${font_weight}
    }
    h6, .bold {
      font-weight: ${font_weight + 200}
    }
  </style>
  <!-- rich embed tags using OpenGraph -->
  <meta property="og:title" content="${posts.get(0).getTitleText()}">
  <meta property="og:description" content="${posts.get(0).getContentPreview()}">
  <meta property="og:url" content="${domain}/polr/${posts.get(0).getId()}">
  <meta property="og:site_name" content="xarql">
  <meta name="twitter:site" content="@xarql">
  <meta name="theme-color" content="#f2f2f2">
  <link type="application/json+oembed" href="${domain}/src/common/embed-options.json">
</head>
<body>
  <div id="wrapper">
    <div id="column">
      <div id="main-post">
      <c:forEach begin="0" var="post" items="${posts}" end="0">
		  <div class="large-card">
		    <p class="overline">ID : <span id="main-post-id">${post.getId()}</span> ~ <a href="${domain}/polr/${post.getAnswers()}" class="view-link" post-id="${post.getAnswers()}">Replied To : ${post.getAnswers()}</a> ~ Date : ${post.getDisplayDate()}</p>
		    <p class="overline">Replies : <span id="reply-count">${post.getResponses()}</span> ~ SubReplies : ${post.getSubresponses()} ~ Bump : ${post.timeSinceBump()} ~ SubBump : ${post.timeSinceSubbump()}</p>
		    <h6>${post.getTitle()}</h6>
		    <p>${post.getContent()}</p>
        <c:if test="${post.isExpired()}">
          <p class="warn">This post has expired. You can not reply to it.</p>
        </c:if>
        <p>
          <a class="report-link" href="${domain}/flag?id=${post.getId()}">Report</a>
          <c:if test="${post.getAuthor() != 'Unknown'}">
            ⤷<a href="${domain}/user/view?name=${post.getAuthor()}">${post.getAuthor()}</a>
          </c:if>
        </p>
		  </div>
		</c:forEach>
		</div>
      <div class="large-card" style="x-overflow:hidden;">
       <h4>Create Post</h4>
       <c:if test="${account_name != 'Unknown'}">
         <p>Logged in as <a href="${domain}/user">@${account_name}</a></p>
       </c:if>
       <form action="${domain}/polr/post" method="POST" id="post-form" accept-charset="UTF-8">
         <input autocomplete="off" spellcheck="true" type="text" name="title" placeholder="Title (optional)" maxlength="64" style="width:100%;">
         <br/>
         <textarea autocomplete="off" spellcheck="true" name="content" cols="64" rows="8" tabindex="0" placeholder="Content (required)" wrap="soft" maxlength="4096" required style="width:100%;height:12rem;resize:vertical;"></textarea>
         <br/>
         <label>Replying To : </label><input type="number" id="replying-to-input" name="answers" value="${id}" min="0" size="4" required="" style="width:4rem;"/>
         <input id="submit" class="button" type="submit" value="Post"/>
         <p style="display:inline;margin-left:1rem;"><a href="${domain}/jott" target="_blank">Test</a></p>
       </form>
       <p id="advisory" style="display:none;"></p>
       <c:if test="${not auth}">
         <form action="${domain}/auth/recaptcha" method="POST" id="recaptcha-form">
           <div style="position:relative;">
             <div data-theme="${theme}" class="g-recaptcha" data-callback="recaptchaCallback" data-sitekey="${recaptcha_key}"></div>
             <input id='recaptcha_check_empty' required="" tabindex='-1' style='width:50px; height:0; opacity:0; pointer-events:none; position:absolute; bottom:0;'>
           </div>
         </form>
         <script src="${domain}/src/auth/auth.min.js" defer=""></script>
         <script src="https://www.google.com/recaptcha/api.js" async="" defer=""></script>
       </c:if>
       <div class="link-div">
         <span class="link-span">
           <p class="link"><a href="${domain}/help">Help</a></p>
           <span class="ajax-bar" style="display:none">
             <p class="link"><a id="option-pane-open-button">Options</a></p>
             <p class="link"><a class="update-button">Update</a> <span class="status"></span></p>
           </span>
         </span>
       </div>
      </div>
      <div class="large-card" id="option-pane" style="display:none;position:relative;">
        <table>
          <tr><td><p>Size</td><td><span style="letter-spacing:1em">:</span></td><td><a class="spacey-link" id="text-up">↑</a><a class="spacey-link" id="text-dn">↓</a></p></td></tr>
          <tr><td><p>Font</td><td>:</td><td><a class="spacey-link" id="font-light-button">Light</a><a class="spacey-link" id="font-normal-button">Normal</a></p></td></tr>
          <tr><td><p>Theme</td><td>:</td>
          <td>
            <a class="theme-button" id="light-theme-button" data="light">Light</a>
            <a class="theme-button" id="dark-theme-button" data="dark">Dark</a>
            <a class="theme-button" id="rainbow-theme-button" data="rainbow">Rainbow</a>
          </td></tr>
        </table>
        <p><span style="position:absolute;bottom:0.8rem;right:2rem;"><a id="option-pane-close-button">Close</a></span></p>
      </div>
      <div id="replies">
		<c:forEach begin="1" var="post" items="${posts}">
		  <div class="large-card">
		    <p class="overline">ID : ${post.getId()} ~ Date : ${post.getDisplayDate()}</p>
		    <p class="overline">Replies : ${post.getResponses()} ~ SubReplies : ${post.getSubresponses()} ~ Bump : ${post.timeSinceBump()} ~ SubBump : ${post.timeSinceSubbump()}</p>
        <c:if test="${post.isExpired()}"><p class="overline"><span class="warn">Expired</span></p></c:if>
        <div id="post-inner-${post.getId()}"<c:if test="${post.getWarning() != 'None'}">style="display:none;"</c:if>>
          <h6>${post.getTitle()}</h6>
          <p>${post.getContent()}</p>
        </div>
        <c:if test="${post.getWarning() != 'None'}">
          <h6 class="warn" id="post-warning-${post.getId()}">${post.getWarning()} Content</h6>
        </c:if>
		    <p>
          <a href="${domain}/polr/${post.getId()}" class="view-link" post-id="${post.getId()}">View</a>
          <c:if test="${post.getAuthor() != 'Unknown'}">
            ⤷<a href="${domain}/user/view?name=${post.getAuthor()}">${post.getAuthor()}</a>
          </c:if>
          <c:if test="${post.getWarning() != 'None'}"><a class="reveal-link" data="${post.getId()}">Reveal</a></c:if>
        </p>
		  </div>
		</c:forEach>
	  </div>
	  <div class="large-card">
	    <h4>Navigation</h4>
	    <form id="nav-form" action="${domain}/polr/${id}" method="GET" accept-charset="utf-8" style="display:inline;">
	      <table style="display:inline">
	        <tr><td>Page</td><td>Sort</td><td>Flow</td></tr>
	        <tr><td><select name="page" id="page-dropdown">
	                <c:forEach begin="0" end="4" var="i">
                    <option value="${i}" <c:if test="${i == page}">selected="selected"</c:if> >${i}</option>
                    </c:forEach>
                  </select></td>
                <td><select name="sort" id="sort-dropdown">
                    <option value="date" <c:if test="${sort.equals('date')}">selected="selected"</c:if> >Date</option>
                    <option value="responses" <c:if test="${sort.equals('responses')}">selected="selected"</c:if> >Responses</option>
                    <option value="subresponses" <c:if test="${sort.equals('subresponses')}">selected="selected"</c:if> >SubResponses</option>
                    <option value="bump" <c:if test="${sort.equals('bump')}">selected="selected"</c:if> >Bump</option>
                    <option value="subbump" <c:if test="${sort.equals('subbump')}">selected="selected"</c:if> >SubBump</option>
                  </select></td>
                <td><select name="flow" id="flow-dropdown">
                    <option value="asc"  <c:if test="${flow.equals('asc')}">selected="selected"</c:if> >Ascending</option>
                    <option value="desc" <c:if test="${flow.equals('desc')}">selected="selected"</c:if> >Descending</option>
                  </select></td>
            </tr>
	      </table>
	      <input id="submit" class="button" type="submit" value="Custom"/>
	    </form>
	    <form method="GET" <c:if test="${page <= 0}">style="display:none;"</c:if> id="prev-form" action="${domain}/polr/${id}?page=${page - 1}&sort=${sort}&flow=${flow}" style="display:inline;"><input id="submit" class="button" type="submit" value="Prev"/></form>
	    <form method="GET" <c:if test="${page >= 4}">style="display:none;"</c:if> id="next-form" action="${domain}/polr/${id}?page=${page + 1}&sort=${sort}&flow=${flow}" style="display:inline;"><input id="submit" class="button" type="submit" value="Next"/></form>
      <div class="link-div">
        <span class="link-span">
          <p class="link"><a href="${domain}/polr/find">Search</a></p>
          <p class="link"><a href="${domain}/polr/flat">Browse</a></p>
          <span class="ajax-bar" style="display:none">
            <p class="link"><a class="update-button">Update</a> <span class="status"></span></p>
          </span>
        </span>
      </div>
	  </div>
    <div class="small-card">
      <p class="centered">
        <c:if test="${account_name == 'Unknown'}"><a class="spacey-link"href="${domain}/user/log_in">Log In</a></c:if>
        <a class="spacey-link" href="https://xarql.net/-/upload">Upload An Image</a>
        <a class="spacey-link" href="https://twitter.com/xarql">@xarql Twitter</a>
      </p>
    </div>
	  <div id="data" class="card" style="visibility:hidden;display:none;">
		  <p id="page">${page}</p>
		  <p id="sort">${sort}</p>
		  <p id="flow">${flow}</p>
	  </div>
    </div>
  </div>
</body>
</html>
