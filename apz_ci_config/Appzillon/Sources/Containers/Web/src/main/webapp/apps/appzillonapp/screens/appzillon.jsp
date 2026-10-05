<%@ page import="javax.servlet.http.*,javax.servlet.*"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>APPZILLONPAGETITLE</title>
<style type="text/css" media="screen"> 
.sno{display:none !important}
.ssp{visibility:hidden !important} 
</style>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1,shrink-to-fit=no">
<meta name="description" content="APPZILLONDESCRIPTION">
<meta http-equiv="Cache-Control" content="no-cache, no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
APPZILLONCSRFSCRIPT
APPZILLONPRELOADS
APPZILLONCSSFILES
CUSTOMIZERCSSFILE
</head>
<body APPZILLONANGULARTAGS>
APPZILLONSVGCONTENT
APPZILLONMSGCONTENT
APPZILLONLOVCONTENT
	<div id="apzloader" class="sno"
		style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: #fff; z-index: 99999; opacity: 0.2">
		<div id="status" class="fa fa-spin-sp"
			style="width: 200px; height: 200px; position: absolute; left: 50%; top: 50%; background-image: url(APPZILLONLOADERIMG); background-repeat: no-repeat; background-position: center; margin: -100px 0 0 -100px;">
			&nbsp;</div>
	</div>
	FIREBASENOTIFICATIONSCRIPTS
	APPZILLONJSFILES
	CUSTOMIZERJSFILES
	APPZILLONGOOGLELOGIN 
	APPZILLONLINKEDINCLIENTID
	APPZILLONFACEBOOKSCRIPT
	<script type="text/javascript" charset="utf-8">
		APPZILLONASYNCHRONOUSCSSARRAY
		isTestMode = APPZILLONTESTMODEFLAG
		isCodeCoverageRun = APPZILLONCODECOVERAGEFLAG;
		APPZILLONCODECOVERAGECONFIG
			apz = new Apz();
		$(document).ready(function() {
			apzargs = {};
			APZWEBDEFAULTSCRIPTCONTENT
			apz.appId = "APPZILLONAPPID";
			apz.CSRF = APPZILLONCSRFPREVENTION;
			apz.deviceOs = "APPZILLONDEVICEOS";
			var sNonce = "<c:out value="${serverNonce}"></c:out>";
			if(payloadEncryptionReq =="Y") {
				apz.ns.apzGetKey();
			}
			APZAPPSECTOKEN
			APPZILLONBOOTSTRAP
			APZREGISTERWEBNOTIFICATIONTOKEN
			CUSTOMIZERDEVICEID
		});
		//ADPPT-1771 Invoking the function for preventing third party scripts
		apz.preventTPSInclude();
		APPZILLONREQUIREJSFILES
	</script>
	<div id="pt-main">
		APPZILLONCODECOVERAGEBUTTON
		<div id="page_1">APPZILLONTAGANGULAR</div>
		<div id="page_2"></div>
		CUSTOMIZERDIVCONTENTS
		APPZILLONQUNITTAGS
	</div>
	APPZILLONJSANGULAR
	APZWEBDEFAULTSCRIPTCONTENTDATA
</body>
</html>
