var geocoder;
var map;
var parseXml;

if (typeof window.DOMParser != "undefined") 
{
    parseXml = function(xmlStr) 
    {
        return ( new window.DOMParser() ).parseFromString(xmlStr, "text/xml");
    };
}
else if(typeof window.ActiveXObject != "undefined" && new window.ActiveXObject("Microsoft.XMLDOM")) 
{
	parseXml = function(xmlStr)
	{
		var xmlDoc = new window.ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async = "false";
		xmlDoc.loadXML(xmlStr);
		return xmlDoc;
    };
}
else
{
    throw new Error("No XML parser found");
}

function initialize()
{
	var mapProp = {center:new google.maps.LatLng(49.266667, -122.966667), zoom:12, mapTypeId:google.maps.MapTypeId.ROADMAP};
	geocoder = new google.maps.Geocoder();
	map=new google.maps.Map(document.getElementById("googleMap"),mapProp);
	
	loadMarkers();
	alert("Seizure warning: Flashing lights!");
}

function loadMarkers()
{
	map.markers = map.markers || []
	downloadUrl("data.xml", function(data)
	{
		var xml = data.responseXML.getElementsByTagName("ENTRY");
		var at = " @ ";
		
		for(var i = 0; i < xml.length; i++)
		{
			var name = xml[i].getElementsByTagName("HOSTNAME")[0].innerHTML;
			name = name.concat(at);
			name = name.concat(xml[i].getElementsByTagName("TIME")[0].innerHTML);
			
			var icon = xml[i].getElementsByTagName("ICON")[0].innerHTML;
			
			var point = new google.maps.LatLng(parseFloat(xml[i].getElementsByTagName("LATITUDE")[0].innerHTML), parseFloat(xml[i].getElementsByTagName("LONGITUDE")[0].innerHTML));
			var marker = new google.maps.Marker({map: map, position: point, title: name});
			
			if(typeof icon != 'undefined')
				marker.setIcon(icon);
			
			map.markers.push(marker);
			if(i == xml.length-1) //If this is the latest location
			{
				marker.setAnimation(google.maps.Animation.BOUNCE);
				setTimeout(function()
				{
					marker.setAnimation(null);
				}, 2750);
			}
			else
			{
				setTimeout(function()
				{
					marker.setAnimation(google.maps.Animation.DROP);
				}, 200 * (i+1));
			}
		}
	});
}

function downloadUrl(url, callback)
{
	var request = new XMLHttpRequest;
	request.onreadystatechange = function()
	{
		if(request.readyState == 4) //doNothing
		{
			callback(request, request.status);
		}
	};
	
	request.open('GET', url, true);
	request.send(null);
}
