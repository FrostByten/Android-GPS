/******************************************************************* 
* 
* File    : JSFX_Layer.js © JavaScript-FX.com
* 
* Created : 2000/06/08 
* 
* Author  : Roy Whittle  www.Roy.Whittle.com 
* 
* Purpose : To create a cross browser dynamic layers. This 
*		library is based on the library defined in the 
*		excellent book. "JavasScript - The Definitive guide" 
*		by David Flanagan. Published by O'Reilly. 
*		ISBN 1-56592-392-8 
* 
* History 
* Date         Version        Description 
* 
* 2001-03-17	2.0		Converted for use by javascript-fx
***********************************************************************/ 
if(!window.JSFX)
	JSFX=new Object();

JSFX.layerNo=0; 
JSFX.Layer = function(newLayer, x, y) 
{
	if(!newLayer)
		return;

	if(x==null)x=0; 
	if(y==null)y=0; 
 	if(document.layers) 
	{
		if(typeof newLayer == "string")
		{
			this.layer=new Layer(2000); 
			this.layer.document.open(); 
			this.layer.document.write(newLayer); 
			this.layer.document.close(); 
		}
		else
			this.layer=newLayer;

		this.layer.moveTo(x,y); 
		this.images=this.layer.document.images; 
 	} 
	else 
	if(document.all) 
	{
		var xName;
		if(typeof newLayer == "string")
		{
			xName="xLayer" + JSFX.layerNo++; 
 			var txt =   "<DIV ID='" + xName 
				+ "' STYLE=\"position:absolute;" 
				+ "left:"  + x + ";" 
				+ "top:"   + y + ";" 
				+ "visibility:hidden\">" 
				+ newLayer 
 				+ "</DIV>"; 

			document.body.insertAdjacentHTML("BeforeEnd",txt); 
		}
		else
			xName=newLayer.id;

		this.content = document.all[xName]; 
		this.layer   = document.all[xName].style; 
		this.images  = document.images; 
	} 
	else 
	if (document.getElementById)
	{
		/*** Add Netscape 6.0 support (NOTE: This may work in I.E. 5+. Will have to test***/

		var newDiv;

		if(typeof newLayer == "string")
		{
			var xName="xLayer" + JSFX.layerNo++;

			var txt = ""
				+ "position:absolute;"
				+ "left:"  + x + "px;"
				+ "top:"   + y + "px;"
				+ "visibility:hidden";

			var newRange   = document.createRange();

			newDiv = document.createElement("DIV");
			newDiv.setAttribute("style",txt);
			newDiv.setAttribute("id", xName);

			document.body.appendChild(newDiv);

			newRange.setStartBefore(newDiv);
			strFrag = newRange.createContextualFragment(newLayer);	
			newDiv.appendChild(strFrag);
		}
		else
			newDiv = newLayer;

		this.content = newDiv;	
		this.layer   = newDiv.style;
		this.images  = document.images;
	}

	return(this); 
} 

JSFX.findLayer = function(theDiv)
{
	if(document.layers)
		return(document.layers[theDiv]);
	else 
	if(document.all)
		return(document.all[theDiv]);
	else 
	if(document.getElementById)
		return(document.getElementById(theDiv));
	else
		return("Undefined.....");
}

if(navigator.appName.indexOf("Netscape") != -1 && !document.getElementById)
{
var eventmasks = {
      onabort:Event.ABORT, onblur:Event.BLUR, onchange:Event.CHANGE,
      onclick:Event.CLICK, ondblclick:Event.DBLCLICK, 
      ondragdrop:Event.DRAGDROP, onerror:Event.ERROR, 
      onfocus:Event.FOCUS, onkeydown:Event.KEYDOWN,
      onkeypress:Event.KEYPRESS, onkeyup:Event.KEYUP, onload:Event.LOAD,
      onmousedown:Event.MOUSEDOWN, onmousemove:Event.MOUSEMOVE, 
      onmouseout:Event.MOUSEOUT, onmouseover:Event.MOUSEOVER, 
      onmouseup:Event.MOUSEUP, onmove:Event.MOVE, onreset:Event.RESET,
      onresize:Event.RESIZE, onselect:Event.SELECT, onsubmit:Event.SUBMIT,
      onunload:Event.UNLOAD
};

/**** START prototypes for NS ***/ 
JSFX.Layer.prototype.moveTo 	= function(x,y) 	{ this.layer.moveTo(x,y); }
JSFX.Layer.prototype.moveBy 	= function(x,y) 	{ this.layer.moveBy(x,y); }
JSFX.Layer.prototype.show		= function() 	{ this.layer.visibility = "show"; }
JSFX.Layer.prototype.hide 		= function() 	{ this.layer.visibility = "hide"; }
JSFX.Layer.prototype.setzIndex	= function(z)	{ this.layer.zIndex = z; }
JSFX.Layer.prototype.setBgColor 	= function(color) { this.layer.bgColor = color; if(color==null)this.layer.background.src=null;}
JSFX.Layer.prototype.setBgImage 	= function(image) { this.layer.background.src = image; }
JSFX.Layer.prototype.getX 		= function() 	{ return this.layer.left; }
JSFX.Layer.prototype.getY 		= function() 	{ return this.layer.top; }
JSFX.Layer.prototype.getWidth 	= function() 	{ return this.layer.clip.right; }
//JSFX.Layer.prototype.getWidth 	= function() 	{ return this.layer.width; }
JSFX.Layer.prototype.getHeight 	= function() 	{ return this.layer.clip.bottom; }
//JSFX.Layer.prototype.getHeight 	= function() 	{ return this.layer.height; }
JSFX.Layer.prototype.getzIndex	= function()	{ return this.layer.zIndex; }
JSFX.Layer.prototype.isVisible 	= function() 	{ return this.layer.visibility == "show"; }
JSFX.Layer.prototype.setContent   = function(xHtml)
{
	this.layer.document.open();
	this.layer.document.write(xHtml);
	this.layer.document.close();
}

JSFX.Layer.prototype.clip = function(x1,y1, x2,y2)
{
	this.layer.clip.top	=y1;
	this.layer.clip.left	=x1;
	this.layer.clip.bottom	=y2;
	this.layer.clip.right	=x2;
}
JSFX.Layer.prototype.addEventHandler = function(eventname, handler) 
{
        this.layer.captureEvents(eventmasks[eventname]);
        var xl = this;
        this.layer[eventname] = function(event) { 
		event.clientX	= event.pageX;
		event.clientY	= event.pageY;
		event.button	= event.which;
		event.keyCode	= event.which;
		event.altKey	=((event.modifiers & Event.ALT_MASK) != 0);
		event.ctrlKey	=((event.modifiers & Event.CONTROL_MASK) != 0);
		event.shiftKey	=((event.modifiers & Event.SHIFT_MASK) != 0);
            return handler(xl, event);
        }
}
JSFX.Layer.prototype.removeEventHandler = function(eventName) 
{
	this.layer.releaseEvents(eventmasks[eventName]);
	delete this.layer[eventName];
}

/*** END NS ***/ 
} 
else if(document.all) 
{ 
/*** START prototypes for IE ***/ 
JSFX.Layer.prototype.moveTo = function(x,y) 
{ 
	this.layer.pixelLeft = x; 
	this.layer.pixelTop = y; 
} 
JSFX.Layer.prototype.moveBy = function(x,y) 
{ 
	this.layer.pixelLeft += x; 
	this.layer.pixelTop += y; 
} 
JSFX.Layer.prototype.show		= function() 	{ this.layer.visibility = "visible"; } 
JSFX.Layer.prototype.hide		= function() 	{ this.layer.visibility = "hidden"; } 
JSFX.Layer.prototype.setzIndex	= function(z)	{ this.layer.zIndex = z; } 
JSFX.Layer.prototype.setBgColor	= function(color) { this.layer.backgroundColor = color==null?'transparent':color; } 
JSFX.Layer.prototype.setBgImage	= function(image) { this.layer.backgroundImage = "url("+image+")"; } 
JSFX.Layer.prototype.setContent   = function(xHtml)	{ this.content.innerHTML=xHtml; } 
JSFX.Layer.prototype.getX		= function() 	{ return this.layer.pixelLeft; } 
JSFX.Layer.prototype.getY		= function() 	{ return this.layer.pixelTop; } 
JSFX.Layer.prototype.getWidth	= function()
{ 
	if(document.getElementById)
		return this.content.offsetWidth; 
	else
		return this.content.scrollWidth;
} 
JSFX.Layer.prototype.getHeight	= function() 	{ return this.content.offsetHeight; } 
JSFX.Layer.prototype.getzIndex	= function()	{ return this.layer.zIndex; } 
JSFX.Layer.prototype.isVisible	= function()	{ return this.layer.visibility == "visible"; } 
JSFX.Layer.prototype.clip		= function(x1,y1, x2,y2) 
{ 
	this.layer.clip="rect("+y1+" "+x2+" "+y2+" "+x1+")"; 
	this.layer.pixelWidth=x2; 
	this.layer.pixelHeight=y2; 
	this.layer.overflow="hidden"; 
}
JSFX.Layer.prototype.addEventHandler = function(eventName, handler) 
{
	var xl = this;
	this.content[eventName] = function() 
	{ 
            var e = window.event;
            e.cancelBubble = true;
		if(document.getElementById)
		{
			e.layerX = e.offsetX;
			e.layerY = e.offsetY;
		}
		else
		{
			/*** Work around for IE 4 : clone window.event ***/
			ev = new Object();
			for(i in e)
				ev[i] = e[i];
			ev.layerX	= e.offsetX;
			ev.layerY	= e.offsetY;
			e = ev;
		}

            return handler(xl, e); 
	}
}
JSFX.Layer.prototype.removeEventHandler = function(eventName) 
{
	this.content[eventName] = null;
}
 /*** END IE ***/ 
} 
else if (document.getElementById) 
{
/*** W3C (NS 6) ***/ 
JSFX.Layer.prototype.moveTo = function(x,y)
{
	this.layer.left = x+"px";
	this.layer.top = y+"px";
}
JSFX.Layer.prototype.moveBy 	= function(x,y) 	{ this.moveTo(this.getX()+x, this.getY()+y); } 
JSFX.Layer.prototype.show		= function() 	{ this.layer.visibility = "visible"; }
JSFX.Layer.prototype.hide		= function() 	{ this.layer.visibility = "hidden"; }
JSFX.Layer.prototype.setzIndex	= function(z)	{ this.layer.zIndex = z; }
JSFX.Layer.prototype.setBgColor	= function(color) { this.layer.backgroundColor = color==null?'transparent':color; }
JSFX.Layer.prototype.setBgImage	= function(image) { this.layer.backgroundImage = "url("+image+")"; }
JSFX.Layer.prototype.getX		= function() 	{ return parseInt(this.layer.left); }
JSFX.Layer.prototype.getY		= function() 	{ return parseInt(this.layer.top); }
JSFX.Layer.prototype.getWidth		= function() 	{ return this.content.offsetWidth; }
JSFX.Layer.prototype.getHeight	= function() 	{ return this.content.offsetHeight; }
JSFX.Layer.prototype.getzIndex	= function()	{ return this.layer.zIndex; }
JSFX.Layer.prototype.isVisible	= function()	{ return this.layer.visibility == "visible"; }
JSFX.Layer.prototype.clip		= function(x1,y1, x2,y2)
{
	this.layer.clip="rect("+y1+" "+x2+" "+y2+" "+x1+")";
	this.layer.width=x2 + "px";
	this.layer.height=y2+ "px";
	this.layer.overflow="hidden";
}
JSFX.Layer.prototype.addEventHandler = function(eventName, handler) 
{
	var xl = this;
	this.content[eventName] = function(e) 
	{ 
            e.cancelBubble = true;
            return handler(xl, e);
	}
}
JSFX.Layer.prototype.removeEventHandler = function(eventName) 
{
	delete this.content[eventName];
}
JSFX.Layer.prototype.setContent   = function(xHtml)
{
	var newRange   = document.createRange();
	newRange.setStartBefore(this.content);

	while (this.content.hasChildNodes())
		this.content.removeChild(this.content.lastChild);

	var strFrag    = newRange.createContextualFragment(xHtml);	
	this.content.appendChild(strFrag);
}

} else
{
JSFX.Layer.prototype.moveTo 	= function(x,y) 	{  }
JSFX.Layer.prototype.moveBy 	= function(x,y) 	{  }
JSFX.Layer.prototype.show 		= function() 	{  }
JSFX.Layer.prototype.hide 		= function() 	{  }
JSFX.Layer.prototype.setzIndex	= function(z) {  }
JSFX.Layer.prototype.setBgColor 	= function(color) {  }
JSFX.Layer.prototype.setBgImage 	= function(image) {  }
JSFX.Layer.prototype.getX 		= function() 	{ return 0; }
JSFX.Layer.prototype.getY 		= function() 	{ return 0; }
JSFX.Layer.prototype.getWidth 	= function() 	{ return 0; }
JSFX.Layer.prototype.getHeight 	= function() 	{ return 0; }
JSFX.Layer.prototype.getzIndex	= function()	{ return 0; }
JSFX.Layer.prototype.isVisible 	= function() 	{ return false; }
JSFX.Layer.prototype.setContent   = function(xHtml) { }
}

/*** End  - xLayer - a cross browser layer object by www.Roy.Whittle.com ***/ 