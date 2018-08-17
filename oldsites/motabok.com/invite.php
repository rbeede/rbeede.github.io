<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Invite a Friend</title>
<style type="text/css">
<!--

-->
</style>
<link href="styles/motab.css" rel="stylesheet" type="text/css">

<script type="text/javascript">
<!--
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
//-->
</script>
</head>

<body onload="MM_preloadImages('images/main_menu_buy_ticketsB.png','images/main_menu_inviteB.png','images/main_menu_FAQB.png','images/main_menu_choir_factsB.png','images/main_menu_2009_tourB.png','images/main_menu_venueB.png','images/main_menu_sponsorshipB.png','images/main_menu_contactB.png')">

<div id="container">
	<div id="main">
    	<div id="motab_banner_sub"><img src="images/motab_banner_sub.png" alt="home banner" width="777" height="120" border="0" usemap="#Map" />
<map name="Map" id="Map"><area shape="rect" coords="589,29,686,79" href="index.php" />
</map></div>
<div id="main_menu">
       	  <?php include('includes/main_menu-banner_sub.php'); ?>
        </div>
        <div id="content_heading_invite"></div>
        <div id="content_area_sub">
        	<div id="content_areaL_sub"></div>
            <div id="content_areaM_sub">
            	<div id="content_areaM_L_sub">
                    	<div id="mainpage_text">
                    	  <p>
You can send a friend an invitation e-card for the choir performance by using the form below.  If you'd like to see what it looks like before sending
it to your friend then simply send yourself one first.</p>

                          <form action="/cgi-bin/e-card_preview.pl" method="post">

<p>
<label for="from_address" class="lineup">From:</label> <input type="text" name="from address" id="from_address" size="35" maxlength="320"><i><br />
(Must be email address of sender. Thanks)</i>
</p>

<p>
<label for="to_addresses" class="lineup">To:</label> <textarea name="to addresses" id="to_addresses" rows="10" cols="35"></textarea> <i><br />
(Multiple addresses each on a separate line. Do not use a comma.)</i></p>

<p>
<label for="personal_message">Personal Message: <i>(1000 character limit)</i></label><br >
<textarea name="personal message" id="personal_message" rows="15" cols="50">Just wanted to let you know about a performance by the renowned Mormon Tabernacle Choir and Orchestra at Temple Square coming here to Norman, Oklahoma!</textarea>
</p>

<p>
By clicking on this button you certify that you have provided accurate and legitment information and that you will not use this service for the transmission of spam or other abuse.
</p>

<p>
<input type="submit" value="I Agree, Preview E-card" >
</p>

</form>
               	  </div>
                  <div class="mainpage_text_privacy">
                    <b>Privacy Notice:</b>
<br />
 Any e-mail addresses submitted via the e-card mechanism will be kept confidential and not shared with third parties.  No e-mail addresses submitted via this mechanism will be added to any mailing lists or used for any purpose other than transmitting the e-card.  Please see other sections of this site if you do wish to be added to a mailing list.  Certain information, such as but not limited to your IP address, may be retained on the server for security purposes.  Any communication you transmit is not encrypted.  No warranty or guarantee is provided that your submitted e-card invitation will be received.  Any communication transmitted may contain your e-mail address, the recipient's e-mail address, your personal message, and your IP address in the communication.
                  </div>
              </div>
            	<div id="content_areaM_R_sub"><?php include('sponsors_links.php'); ?></div>
          </div>
            <div id="content_areaR_sub"></div>
        </div>
        <div class="clearer"></div>
        <div id="footer_sub"></div>
    </div>
</div>

</body>
</html>
