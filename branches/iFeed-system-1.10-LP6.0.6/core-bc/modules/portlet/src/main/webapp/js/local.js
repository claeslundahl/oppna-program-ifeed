var pageframe="<div id='exampleListing'>"+
"        <div id='colPart'>"+
"                <table>"+
"                <tr>"+
"                        <td>"+
"                                Välj ifeed lista:</td><td><a id='ifeedButton' href='javascript:doAddIfeeId()'>1234567</a>"+
"                        </td>"+
"                </tr>"+
"                <tr>"+
"                        <td>"+
"                                Begränsa listan:</td><td><a id='limitButton' href='javascript:doLimit()'>0</a>"+
"                        </td>"+
"                </tr>"+
"                <tr>"+
"                        <td>"+
"                                Dölj epi högerkolumn:</td><td><a id='hideRightButton' href='javascript:doHideRight()'>Ja</a>"+
"                        </td>"+
"                </tr>"+
"                <tr>"+
"                        <td>"+
"                                Välj sortering:</td><td><a id='defaultOrderButton' href='javascript:doSetOrder()'>Titel, Stigande</a>"+
"                        </td>"+
"                </tr>"+
"                <tr>"+
"                        <td>"+
"                                Lägg till ny kolumn:</td><td><a href='javascript:doAddColumn()'>Ny</a>"+
"                        </td>"+
"                </tr>"+
"                </table>"+
"        </div>"+
"        <div id='codeFrameBox'><div class='ifeedDocList' columnes='title|Titel|left,dc.date.issued|Publiceringsdatum|right' defaultsortcolumn='title' defaultsortorder='desc' limit='0' hiderightcolumn='true' feedid='0'></div></div>"+
"        <div id='exampleList'></div>"+
"        <br />"+
"        <b>Kod att kopiera in</b>"+
"        <pre id='codeFrame' style='margin-top: 0px; height: 260px; background-color: #cccccc;overflow: auto; border: 1px solid; padding: 3px'></pre>"+
"</div>";




var feedId=123;

var meta = new Array();
meta['id']="Dokument id";
meta['url']="Länk";
//meta['title']="";
meta['creationdate']="";
meta['format']="Format";
meta['author']="Författare";
meta['published']="Publicerad";
meta['version']="Version";
meta['language']="Språk";
meta['dc.creator.project-assignment']="";
meta['dc.publisher']="Publicerare";
meta['dc.source.documentid']="";
meta['dc.identifier.checksum']="";
meta['dc.type.record']="";
meta['dc.type.process.name']="";
meta['dc.identifier.native']="";
meta['dc.contributor.savedby']="";
meta['dc.creator.document.id']="";
meta['vgregion.status.document']="";
meta['dc.format.extension']="";
meta['dc.relation.isversionof']="";
meta['dc.contributor.controlledby.unit.freetext']="";
meta['dc.publisher.id']="";
meta['dc.description']="";
meta['dc.subject.keywords']="";
meta['dc.creator']="";
meta['dc.creator.forunit']="";
meta['dc.identifier']="";
meta['dc.publisher.forunit']="";
meta['dc.contributor.savedby.id']="";
meta['dc.creator.recordscreator']="";
meta['dc.contributor.acceptedby.unit.freetext']="";
meta['dc.date.validfrom']="";
meta['dc.identifier.documentid']="";
meta['dc.type.document.serie']="";
meta['dc.subject.authorkeywords']="";
meta['dc.relation.replaces']="";
meta['dc.contributor.acceptedby']="";
meta['dc.date.issued']="";
meta['dc.contributor.acceptedby.id']="";
meta['dc.contributor.controlledby.role']="";
meta['dc.language']="";
meta['dc.creator.id']="";
meta['dc.contributor.controlledby']="";
meta['dc.publisher.project-assignment']="";
meta['dc.type.file']="";
meta['dc.date.validto']="";
meta['dc.source.origin']="";
meta['dc.coverage.hsacode']="";
meta['dc.creator.function']="";
meta['dc.type.file.process']="";
meta['dc.title']="";
meta['dc.identifier.version']="";
meta['dc.creator.freetext']="";
meta['dc.format.extension.native']="";
meta['dc.contributor.acceptedby.role']="";
meta['dc.title.filename.native']="";
meta['dc.title.filename']="";
meta['dc.source']="";
meta['dc.title.alternative']="";
meta['dc.type.templatename']="";
meta['dc.format.extent.mimetype.native']="";
meta['dc.creator.document']="";
meta['dc.date.availablefrom']="";
meta['dc.type.document.id']="";
meta['dc.rights.accessrights']="";
meta['dc.identifier.checksum.native']="";
meta['dc.type.document']="";
meta['dc.format.extent.mimetype']="";
meta['dc.date.saved']="";
meta['dc.identifier.diarie.id']="";
meta['dc.format.extent']="";
meta['dc.contributor.controlledby.freetext']="";
meta['dc.contributor.controlledby.id']="";
meta['dc.identifier.location']="";
meta['dc.contributor.acceptedby.freetext']="";
meta['source']="";
meta['infotype']="";
meta['public']="";
meta['processingtime']="";
meta['archived']="";

function doSetOrder()
{
	var txt = "<p style='font-size: 14px'>Välj sorteringskolumn och stigande eller avtagande:</p>"+
	"<select class='jqiinput' id='defaultSort' name='defaultSort' style='width: 200px'>"+
	"</select>"+
	"<select class='jqiinput' id='defaultOrder' name='defaultOrder' style='width: 200px'>"+
		"<option value='desc'>Stigande</option>"+
		"<option value='asc'>Avtagande</option>"+
	"</select>";

	$.prompt(txt,{
                buttons: { Avbryt: 'cancel', 'Spara': 'add' },
                zIndex: 1000101,
                callback: function(e,v,m,f) {
                        if(v=='add')
                        {
				$("#defaultOrderButton").html($('#defaultSort option:selected').text()+', '+$('#defaultOrder option:selected').text());
				$(".ifeedDocList").eq(0).attr('defaultsortcolumn',f.defaultSort);
				$(".ifeedDocList").eq(0).attr('defaultsortorder',f.defaultOrder);
				updateCodeFrame();
                        }
                }
        });

	var cols = $(".ifeedDocList").eq(0).attr('columnes').split( ',' );

        $.each(cols,function(key,col)
        {
                subcols = col.split( '|' );
		$("#defaultSort").append($('<option>', { value : subcols[0], id : subcols[0] }).text(subcols[1]));
        });

}

function doLimit()
{
	var txt = "<p style='font-size: 14px'>Välj antal rader du vill begränsa listan till, 0 är ingen begränsning.</p>"+
	"<select id='limit' class='jqiinput' name='limit' style='width: 200px'>"+
                "<option value='0'>Ingen begränsning</option>"+
                "<option value='5'>5</option>"+
                "<option value='10'>10</option>"+
                "<option value='20'>20</option>"+
                "<option value='30'>30</option>"+
                "<option value='50'>50</option>"+
                "<option value='70'>70</option>"+
                "<option value='100'>100</option>"+
                "<option value='150'>150</option>"+
                "<option value='200'>200</option>"+
        "</select>";
	
	$.prompt(txt,{
                buttons: { Avbryt: 'cancel', 'Spara': 'add' },
                zIndex: 1000101,
                callback: function(e,v,m,f) {
                        if(v=='add')
                        {
				if(f.limit=="")
					f.limit=0;

				$("#limitButton").html(f.limit);
				$(".ifeedDocList").eq(0).attr('limit',f.limit);
				updateCodeFrame();
                        }
                }
        });
}

function doHideRight()
{
	var txt = "<p style='font-size: 14px'>Vill du dölja högerkolumnen i episerver för att få mer plats?</p>"+
	"<select id='hideright' class='jqiinput' name='hideright' style='width: 200px'>"+
                "<option value='true'>Ja</option>"+
                "<option value='false'>Nej</option>"+
        "</select>";
	
	$.prompt(txt,{
                buttons: { Avbryt: 'cancel', 'Spara': 'add' },
                zIndex: 1000101,
                callback: function(e,v,m,f) {
                        if(v=='add')
                        {
				$("#hideRightButton").html($('#hideright option:selected').text());
				$(".ifeedDocList").eq(0).attr('hiderightcolumn',f.hideright);
				updateCodeFrame();
                        }
                }
        });
}

function doAddIfeeId()
{
	var txt = "<p style='font-size: 14px'>Fyll i ifeed id för listan:</p>"+
	"<input type='text' class='jqiinput' id='ifeeId' name='ifeeId' />";
	
	$.prompt(txt,{
                buttons: { Avbryt: 'cancel', 'Spara': 'add' },
                zIndex: 1000101,
                callback: function(e,v,m,f) {
                        if(v=='add')
                        {
				$("#ifeedButton").html(f.ifeeId);
				feedId=f.ifeeId;
				$(".ifeedDocList").eq(0).attr('feedid',f.ifeeId);
				updateCodeFrame();
                        }
                }
        });
}

function doAddColumn()
{
	var txt = "<p style='font-size: 14px'>Fyll i columnegenskaperna i fälten:</p>"+
		"<table><tr><td>Välj meta:</td><td>"+
		"<select id='colMeta' class='jqiinput' name='colMeta' onchange=\"$('#colAlias').val($(this).val())\" style='width: 200px'>"+
                        "<option value='0'>Lägg till kolumn</option>"+
                "</select>"+
		"</td></tr><tr><td>Välj Kolumnalias:</td><td>"+
		"<input type='text' class='jqiinput' id='colAlias' style='width: 200px' name='colAlias' />"+
		"</td></tr><tr><td>Välj orgentering:</td><td>"+
		"<select id='colAlign' class='jqiinput' name='colAlign' style='width: 200px'>"+
			"<option value='right'>Högerställd kolumn</option>"+
             		"<option value='center'>Centrerad kolumn</option>"+
              		"<option value='left'>Vänsterställd kolumn</option>"+
             	"</select>"+
		"</td></tr></table>";


        $.prompt(txt,{
                buttons: { Avbryt: 'cancel', 'Lägg till': 'add' },
                zIndex: 1000101,
		callback: function(e,v,m,f) {
			if(v=='add' && f.colMeta !=0)
			{
				addColumn($('#colMeta option:selected').text(),f.colAlias,f.colAlign)
			}
		}
        });

	for(var i in meta) 
	{
		if(meta[i]!="")
			value=meta[i];
		else
			value=i;

		$("#colMeta").append($('<option>', { value : value, id : i }).text(i));
	}
}

function addColumn(colMeta,colAlias,colAlign)
{
	colAlias=colAlias.replace(/\,/gi, "");

	$(".ifeedDocList").attr('columnes',$(".ifeedDocList").attr('columnes')+","+colMeta+"|"+colAlias+"|"+colAlign);

	updateCodeFrame();
	updateExampleList();
}

function trim(str) {
    str.replace(/^\s*/, '').replace(/\s*$/, '');

   return str;
}

function updateExampleList()
{
	$("#exampleList").html("<br /><b>Exempel lista</b><br /><table border='0' id='exampleTable' width='100%' style='border: 1px dotted gray;'>");

        var cols = $(".ifeedDocList").eq(0).attr('columnes').split( ',' );

        $('#exampleTable').append("<tr>");

        $.each(cols,function(key,col)
        {
                subcols = col.split( '|' );

		if(subcols[0]!="title")
          		$("#exampleTable tr").append( "<th colname='"+subcols[0]+"' style='white-space: nowrap;text-align: "+subcols[2]+";background-color: gray; color: white'>"+subcols[1]+"<img src='http://webframe.vgregion.se/documentListing/images/remove.png' style='cursor: pointer; margin-left: 5px;' title='Ta bort kolumn: "+subcols[0]+"' onclick=\"removeColumn('"+subcols[0]+"')\"></th>");
        	else
          		$("#exampleTable tr").append( "<th colname='"+subcols[0]+"' style='white-space: nowrap;text-align: "+subcols[2]+";background-color: gray; color: white'>"+subcols[1]+"</th>");
	});

	$('#exampleTable').append("<tr />");

	$.each(cols,function(key,col)
        {
		subcols = col.split( '|' );
		$('#exampleTable tr:last').append("<td style='text-align: "+subcols[2]+"'>dokumentdata</td>");

        });


}

function updateCodeFrame()
{
	var text = trim($("#codeFrameBox").html()+"<noscript><iframe src='http://ifeed.vgregion.se/iFeed-web/documentlists/"+feedId+"/?by=dc.title&dir=desc' id='iframenoscript' name='iframenoscript' style='width: 100%; height: 400px' frameborder='0'></iframe></noscript>");

	if ($.browser.msie && $.browser.version.substr(0,1)<8) 
	{
		delimiter="\r\n";
	}
	else
	{
		delimiter="\n";
	}

	var text2 = text.replace(/(\<\/)/g, delimiter+'$1');
	var text3 = text2.replace(/(\w*=\"[\w|,\ \.åäö:\;%\/\-?\&=]*\")/g, delimiter+'\t$1');



	$("#codeFrame").text(text3);
}

function removeColumn(str)
{
	var cols = $(".ifeedDocList").eq(0).attr('columnes').split( ',' );

	$.each(cols,function(key,col)
        {
		subcols = col.split( '|' );

		if(subcols[0]==str)
		{
			//cols.splice(cols.indexOf(col), 1);
			cols.splice($.inArray(col, cols),1);
			$(".ifeedDocList").attr('columnes',cols.join(","));
			
			updateCodeFrame();
        		updateExampleList();
			return false;
		}				
	});
}


function loadCodeFrame(fid)
{
	feedId=fid;
	$(document).ready(function()
	{	
		if($("#documentFrame").attr("id")==undefined)
			$("body").append("<div id='documentFrame'></div>");
	
	        $(function() 
		{
	                $("#documentFrame").attr('title','Generera en dokumentlista till episerver');
	                
			$("#documentFrame").dialog({
	                        modal: true,
	                        width: 850,
	                        height: 600
	                });
	        });

		
		$("#documentFrame").html(pageframe);

		$(".ifeedDocList").eq(0).attr('feedid',fid);

		$("#ifeedButton").html(fid);

		for(var i in meta) {
			
			if(meta[i]!="")
				value=meta[i];
			else
				value=i;
		
			$("#colPartSelect").append($('<option>', { value : i, id : i }).text(value));
		}

		updateExampleList();
		updateCodeFrame();
	});
}
