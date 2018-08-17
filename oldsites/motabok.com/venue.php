<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Venue</title>
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
    	<div id="motab_banner_sub"><img src="images/motab_banner_sub.png" alt="home_banner" width="777" height="120" border="0" usemap="#Map" />
<map name="Map" id="Map"><area shape="rect" coords="588,29,686,80" href="index.php" />
</map></div>
<div id="main_menu">
       	  <?php include('includes/main_menu-banner_sub.php'); ?>
        </div>
        <div id="content_heading_venue"></div>
        <div id="content_area_venue">
        	<div id="content_areaL_sub"></div>
            <div id="content_areaM_sub">
            	<div id="content_areaM_L_sub">
                    	<div id="mainpage_text">
                    	  <p><strong>Address:</strong><br />
                    	    2900 Jenkins Ave.<br />
                    	    Norman, OK. 73019<br />
                    	    <br />
                   	     <strong> Directions:</strong><br />
                   	      From I35 take HWY 9 East 2.5 miles to Jenkins Ave. At Jenkins Ave turn North (left) 1/2 mile. The Lloyd Noble Center is located to the West (left). The main entrance, Administrative Office and ticket office are located on the East side of the building.<br />
                    	  <br />
                    	  To get directions from your location click <a href="http://maps.ask.com/maps?a1=2900 Jenkins Center&amp;a2=73019&amp;btnGetMap=Get+Maps&amp;qsrc=256&amp;o=10285&amp;l=iac" target="_blank">here</a>. Enter your address in the &quot;Add  location&quot; field, then hit the &quot;Go&quot; button.</p>
               	  </div>
                  <div id="venue_layout"></div>
                  <div class="mainpage_text_privacy">Seating charts reflect the general layout for the venue at this time. For some events, the layout and specific seat locations may vary without notice.                  </div>
              </div>
            	<div id="content_areaM_R_sub"><?php include('sponsors_links.php'); ?>                </div>
          </div>
            <div id="content_areaR_sub"></div>
        </div>
        <div class="clearer"></div>
        <div id="footer_sub"></div>
    </div>
</div>

</body>
</html>
