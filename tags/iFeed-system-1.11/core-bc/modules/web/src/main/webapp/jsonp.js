var currentSortCol="title";
var currentSort="asc";
var limit=0;
var currentTime = new Date()

var numdate=parseInt(currentTime.getFullYear()+pad2(currentTime.getMonth()+1)+pad2(currentTime.getDate()));

var feeds=new Array();
var docs=new Array();
$.feed=new Array();
$.callback=undefined;

function pad2(number) {
     return (number < 10 ? '0' : '') + number
}

function getDocumentId(str)
{
	var a = str.substring(str.lastIndexOf("/",str)+1);
	return a;
}

function sortByCol(a, b)
{
	if(a[currentSortCol]==undefined) a[currentSortCol]=" ";
	if(b[currentSortCol]==undefined) b[currentSortCol]=" ";

   	var x = a[currentSortCol].toString().toLowerCase();
   	var y = b[currentSortCol].toString().toLowerCase();

	if(currentSort=="asc")
	{
    		return ((x < y) ? -1 : ((x > y) ? 1 : 0));
	}
	else
	{
		return ((x < y) ? 1 : ((x > y) ? -1 : 0));
	}
}

function resortDoclist(listNumber,col)
{
	if(col==currentSortCol && currentSort=="asc")
	{
		currentSort="desc";
	}
	else if(col==currentSortCol && currentSort=="desc")
	{
		currentSort="asc";
	}
	else
	{
		currentSort="asc";
	}

	currentSortCol=col;

	printIfeed((listNumber),feeds[(listNumber)]);

	$('td').css('padding','5px');

	initTooltip();
}


function makeMetaDataUrl(docId) {
	return "http://portalen.vgregion.se/iFeed-web/documents/" + docId + "/metadata";
}

function printIfeed(i,jsonp)
{

	index=i+1;

	ob=$(".ifeedDocList").eq(i);

	$(ob).empty();

	jsonp.sort(sortByCol);

	$(ob).html("<table border='0' class='doc-list' width='100%' style='padding: 10px; font-size:"+currentFontSize+";'>");

	var cols = $(ob).attr('columnes').split( ',' );

	if ("no" == currentShowTableHeader)
		$('.doc-list', ob).append("<tr style='display:none;'>");
	else
		$('.doc-list', ob).append("<tr>");

	$(".doc-list tr:last",ob).append("<th class='info' style='width: 15px'></th>\n");

	$.each(cols,function(key,col)
	{
		subcols = col.split( '|' );

		if(subcols[1] != undefined && subcols[1] != "undefined")
			colName=subcols[1];
		else
			colName=subcols[0];

		if(subcols[2]==undefined)
			subcols[2]="left";

		if(currentSortCol==subcols[0])
		{
			$(".doc-list  tr",ob).append( "<th colname='"+subcols[0]+"' style='text-align: "+subcols[2]+"'><a title='Sortera efter "+colName+"' href=\"javascript:resortDoclist("+i+",'"+subcols[0]+"')\"><b>"+colName+"</b></a><img src='http://webframe.vgregion.se/documentListing/images/Sort"+currentSort+".gif' /></th>\n");
		}
		else
		{
			$(".doc-list tr",ob).append( "<th colname='"+subcols[0]+"' style='text-align: "+subcols[2]+"'><a title='Sortera efter "+colName+"' href=\"javascript:resortDoclist("+i+",'"+subcols[0]+"')\"><b>"+colName+"</b></a><img src='http://webframe.vgregion.se/documentListing/images/Sort.gif' /></th>\n");
		}
	});

	$.each(jsonp, function(key, val)
	{

		if(val["docid"]==undefined)
			val["docid"]=getDocumentId(val['dc.identifier.documentid']);

		docs[val["docid"]]=val;

		$(".doc-list",ob).append("<tr>");
		$(".doc-list",ob).append("<td style='width: 15px'></td>");

		var info;

		if(val['dc.date.validto']!=undefined && checkDateValidTo(val['dc.date.validto']))
		{
			//$(".doc-list tr:last",ob).append("<td style='width: 16px'><nobr><a target=_blank href='http://portalen.vgregion.se/iFeed-web/documents/"+val["docid"]+"/metadata'><img class='tooltip' feedid='"+val["docid"]+"' style='margin-right: 0.5em;' src='http://webframe.vgregion.se/information.png' /></a><img title='Dokumentet har gÃ¥tt ut: "+timestampTodate(val['dc.date.validto'])+"' src='http://webframe.vgregion.se/documentListing/images/utropstecken_rod.gif'></nobr></td>");
			info = ("<a target=_blank href='"+makeMetaDataUrl(val['docid'])+
			"'><img class='tooltip' feedid='"+val["docid"]+"' style='margin-right: 0.5em;white-space: nowrap;' src='http://webframe.vgregion.se/information.png' /><img title='Dokumentet har gÃ¥tt ut: "
			+timestampTodate(val['dc.date.validto'])+"' src='http://webframe.vgregion.se/documentListing/images/utropstecken_rod.gif'></a>");
		}
		else if(val['dc.date.validfrom']!=undefined && checkDateValidFrom(val['dc.date.validfrom']))
		{
			info = ("<a target=_blank href='"+makeMetaDataUrl(val['docid'])+"'><img class='tooltip' feedid='"
			+val["docid"]+"' style='margin-right: 0.5em;white-space: nowrap;' src='http://webframe.vgregion.se/information.png' /><img title='Dokumentet bÃ¶rjar gÃ¤lla: "
			+timestampTodate(val['dc.date.validfrom'])+"' src='http://webframe.vgregion.se/documentListing/images/utropstecken_rod.gif'></a>");
		}
		else
		{
			info = ("<a target=_blank href='"+makeMetaDataUrl(val['docid'])+
			"'><img class='tooltip' feedid='"+val["docid"]+"' style='margin-right: 0.5em;' src='http://webframe.vgregion.se/information.png' /></a>");
		}

		info = "<td style='width:15px'>" + info + "</td>";
		$(".doc-list tr:last",ob).append(info);
		info = '';

		$.each(cols,function(key2,col2)
		{
			subcols = col2.split( '|' );

			if(subcols[2]==undefined)
                        	subcols[2]="left";

			if(subcols[3]==undefined)
                        	subcols[3]="inherit";
			else
				subcols[3]=subcols[3] + '%';

			if(subcols[0]=="dc.date.issued" || subcols[0]=="dc.date.validto" || subcols[0]=="dc.date.validfrom")
			{
				$(".doc-list tr:last",ob).append( "<td style='text-align:"+subcols[2]+";width:"+subcols[3]+"'>"+timestampTodate(val[subcols[0]])+"</td>\n");
			}
			else
			{
				if(val[subcols[0]]==undefined)
					val[subcols[0]]=" ";

				var nativeLink = val['dc.identifier.native'];
				if (currentLinkOriginalDoc=='yes' && nativeLink) {
					$(".doc-list tr:last",ob).append("<td style='text-align:"+subcols[2]+";width:"+subcols[3]+"'>" + info +
						"<a target=_blank href='"+nativeLink+"'>"+val[subcols[0]]+"</a></td>\n");
				} else {
					$(".doc-list tr:last",ob).append("<td style='text-align:"+subcols[2]+";width:"+subcols[3]+"'>" + info +
						"<a target=_blank href='"+val['url']+"'>"+val[subcols[0]]+"</a></td>\n");
				//$(".doc-list tr:last",ob).append( "<td style='text-align:"+subcols[2]+";width:"+subcols[3]+"'>"+val[subcols[0]]+"</td>\n");
				}
			}
		});
	});
}

function limitList(jsonp)
{
	jsonp.sort(sortByCol);

	var d=new Array();

	if(limit>0)
	{
		d=jsonp.slice(0,limit);
		limit=0;
		$.feed=d;
		return d;
	}
	else
	{
		$.feed=jsonp;
		return jsonp;
	}
}

function printDocList(i)
{
	eval("window.f_"+$(".ifeedDocList").eq(i).attr('feedid')+"=function(j)"+
	"{"+
	"       var f=limitList(j);"+
	"       $.feed=f;"+
	"	feeds["+i+"]=f;"+
	"	printIfeed("+i+",f);"+
	"	printDocLists("+(i+1)+");"+
	"};");

	$.getScript("http://ifeed.vgregion.se/iFeed-web/documentlists/"+$(".ifeedDocList").eq(i).attr('feedid')+"/metadata.json?by=processingtime&dir=desc&callback=f_"+$(".ifeedDocList").eq(i).attr('feedid'));
}

	var currentFontSize="inherit";
	var currentShowTableHeader="yes";
	var currentLinkOriginalDoc = "no";

function printDocLists(i,f)
{

	$(".ifeedDocList").eq(i).append("<br /><b>Laddar dokumentlista:</b><br /><img src='http://webframe.vgregion.se/documentListing/images/loading.gif'>");

	currentSortCol="title";
	currentSort="asc";
	limit=0;



	if(f!=undefined)
		$.callback=f;

	if($(".ifeedDocList").eq(i).attr('feedid'))
	{

		if($(".ifeedDocList").eq(i).attr('defaultsortcolumn')!=undefined)
		{
			currentSortCol=$(".ifeedDocList").eq(i).attr('defaultsortcolumn');
		}

		if($(".ifeedDocList").eq(i).attr('defaultsortorder')!=undefined)
                {
                        currentSort=$(".ifeedDocList").eq(i).attr('defaultsortorder');
                }

		if($(".ifeedDocList").eq(i).attr('limit')!=undefined)
                {
                        limit=$(".ifeedDocList").eq(i).attr('limit');
                }

		if($(".ifeedDocList").eq(i).attr('limit')!=undefined)
                {
                        currentShowTableHeader=$(".ifeedDocList").eq(i).attr('showTableHeader');
                } else {
			currentShowTableHeader='yes';
		}

		if($(".ifeedDocList").eq(i).attr('fontSize')!=undefined)
                {
                        currentFontSize=$(".ifeedDocList").eq(i).attr('fontSize');
			$(".doc-list").css('fontSize', currentFontSize);
                } else {
			currentFontSize="inherit";
		}

		if($(".ifeedDocList").eq(i).attr('linkOriginalDoc')!=undefined)
                {
                        currentLinkOriginalDoc=$(".ifeedDocList").eq(i).attr('linkOriginalDoc');
			$(".doc-list").css('fontSize', currentFontSize);
                } else {
			currentLinkOriginalDoc="no";
		}




		if($(".ifeedDocList").eq(i).attr('hideRightColumn')=="true")
		{
			$("#rightcol").css('display','none');
	        	$("#centercolinner").css('width','660px');
                	$("#rightcol").css("display","none");
			$(".doc-list").css('width','100%');
		}

		printDocList(i);
	}
	else
	{
		initTooltip();

		if($.callback!=undefined)
		{
			eval($.callback+"();");
		}

		$('td').css('padding','5px');
	}

}
/*

function initTooltip()
{
	$('.tooltip').cluetip({
    		splitTitle: '|',
	    	showTitle: true,
	    	trigger: '.jtip',
		cluetipClass: 'jtip',
		mouseOutClose: true,
		sticky: false,
		width: '650',
		dropShadow: false,
		cluezIndex: 10000,
		activation: 'hover',
		closePosition:    'title',
    		closeText:        'Stäng',
		onShow: function(ct, c)
		{
			$('.cluetip-title').append("Titel: "+docs[$(this).attr('feedid')]['dc.title']);
			$('.cluetip-inner').append(createToolTipBody(docs[$(this).attr('feedid')]));
		}
	});
}
*/

try {
var headings_first=[];
/*headings_first['dc.title']="Titel";
headings_first['dc.description']="Beskrivning";
headings_first['dc.publisher.forunit']="Publicerat för enhet";
headings_first['dc.creator.function']="Innehållsansvarig (funktion)";
headings_first['dc.contributor.acceptedby.role']="Innehållsansvarig (funktion)";
headings_first['dc.type.document.structure']="Dokumentstruktur VGR";*/

headings_first.push({v:'dc.title', t:"Titel"});
headings_first.push({v:'dc.description', t:"Beskrivning"});
headings_first.push({v:'dc.publisher.forunit', t:"Publicerat f&ouml;r enhet"});
headings_first.push({v:'dc.creator.function', t:"Inneh&aring;llsansvarig (funktion)"});
headings_first.push({v:'dc.contributor.acceptedby.role', t:"Godk&auml;nd av (funktion)"});
headings_first.push({v:'dc.type.document.structure', t:"Dokumentstruktur VGR"});

/*

headings_first['dc.title']="Titel";
headings_first['dc.publisher']="Publicerat av";
headings_first['dc.contributer.acceptedby']="GodkÃ¤nd av";
headings_first['dc.date.issued']="Publiceringsdatum";
headings_first['dc.type.document']="Dokumenttyp";
headings_first['dc.creator.project-assignment']="Projekt";
headings_first['dc.creator.function']="Skapad av";
headings_first['dc.type.record']="Handlingstyp";
headings_first['dc.type.document.serie']="Dokumentserie";
headings_first['dc.description']="Beskrivning";
headings_first['dc.date.validfrom']="Giltig fr o m";
headings_first['dc.date.validto']="Giltig t o m";


*/
} catch(e) {
	if(console) console.log(e);
}

var headings_all=headings_first;
headings_all['dc.title']="Dokument titel";

function getFirstHeading(key)
{
	return headings_first[0].t;
/*
	if(headings_first[key]!=undefined)
		return headings_first[key];
	else
		return key;
*/
}

function getAllFirst(key)
{
	return headings_first[0].v;
/*
	if(headings_all[key]!=undefined)
		return headings_all[key];
	else
		return key;
*/
}

function checkDateValidTo(to)
{
	if(to!=undefined)
	{
		numto = parseInt(to.replace(/-/g, ""));

		if(numto < numdate)
			return true;
		else
			return false;

	}
	return false;
}

function checkDateValidFrom(from)
{
	if(from!=undefined)
	{
		numfrom = parseInt(from.replace(/-/g, ""));

		if(numfrom > numdate)
			return true;
		else
			return false;

	}
	return false;
}

function timestampTodate(d)
{
	if(d!=undefined)
		return d.substring(0, d.indexOf("T"));
	else
		return "";
}

function timeConverterDate(UNIX_timestamp){
 var a = new Date(UNIX_timestamp);
     var months = ['Jan','Feb','Mar','Apr','Maj','Jun','Jul','Aug','Sep','Okt','Nov','Dec'];
     var year = a.getFullYear();
     var month = a.getMonth();
     var date = a.getDate();

     if(month<10)
	month="0"+month;

     if(date < 10)
	date="0"+date;

     var time = year+'-'+month+'-'+date;

	if(UNIX_timestamp>0)
	     return time;
	else
		return "";
}

function createToolTipBody_old(doc)
{
	var headings=new Array('');
	headings['dc.title']="";

	var t="";


	t+="<table border=0 >";

 	$.each(doc, function(key, val)
	{
		if(key!=getFirstHeading(key))
		{
			if(val!="")
			{
				if(key=="dc.date.issued" || key=="dc.date.validfrom" || key=="dc.date.validto")
				{
					t+="<tr><td align='left' style='padding-right: 5px'><b>"+getFirstHeading(key)+"</b></td><td>"+timestampTodate(val)+"</td></tr>";
				}
				else
				{
					t+="<tr><td align='left' style='padding-right: 5px'><b>"+getFirstHeading(key)+"</b></td><td>"+val+"</td></tr>";
				}
			}
		}
	});

	t+="</table>";

	return t;

}

function createToolTipBody(doc)
{
	var headings=new Array('');
	headings['dc.title']="";

	var t=[];

	for (var i = 1; i < headings_first.length; i++) {
		var conf = headings_first[i];
		var key = conf.v;
		var val = doc[conf.v];
		if (!val) val = '';
		var label = conf.t;
		if(key=="dc.date.issued" || key=="dc.date.validfrom" || key=="dc.date.validto")	{
			t.push("<tr><td align='left' style='padding-right: 5px'><b>");
			t.push(label);
			t.push("</b></td><td>");
			t.push(timestampTodate(val));
			t.push("</td></tr>");
		} else {
			t.push("<tr><td align='left' style='padding-right: 5px'><b>");
			t.push(label);
			t.push("</b></td><td>");
			t.push(val);
			t.push("</td></tr>");
		}
	}

/*
	t.push("<tr><td align='left' style='padding-right: 5px'><b><a href='");
	t.push("</b></td><td>");
	t.push("<a target=_blank href='"+makeMetaDataUrl(val['docid'])+"'><img class='tooltip' feedid='"+doc["docid"]+"' style='margin-right: 0.5em;' src='http://webframe.vgregion.se/information.png' /></a>");
	t.push("</td></tr>");

	t.push("</table>");
*/


	return t.join("");

}

