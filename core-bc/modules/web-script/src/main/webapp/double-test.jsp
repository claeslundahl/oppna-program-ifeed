<%@ page isELIgnored="false" %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the -->
<!-- browser's rendering engine into -->
<!-- "Standards Mode". Replacing this declaration -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout. -->

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Web Application Starter Project</title>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>

</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to toTableDef a completely dynamic UI.        -->
<!--                                           -->
<body style="overflow: hidden;">

<table style="width: 100%;">
    <tr>
        <td style="width: 50%;">ifeed.vgregion.se</td>
        <td style="width: 50%;">ifeed-stage.vgregion.se</td>
    </tr>
    <tr>
        <td style="width: 50%;">
            <iframe style="width: 100%; height: 800px;"
                    src="http://ifeed.vgregion.se/iFeed-web-script/test.jsp?ifeedId=${param.ifeedId}">

            </iframe>
        </td>
        <td style="width: 50%;">
            <iframe style="width: 100%; height: 800px;"
                    src="http://ifeed-stage.vgregion.se/iFeed-web-script/test.jsp?ifeedId=${param.ifeedId}">

            </iframe>
        </td>
    </tr>
</table>


</body>
</html>
