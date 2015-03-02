/*******************************************************************
*
* File    : pinwheel.js © JavaScript-FX.com
*
* Created : 2000/06/17
*
* Author  : Roy Whittle www.Roy.Whittle.com
*
* Purpose : To create a pinwheel firework that follows the cursor.
*
* Requires	: JSFX_Layer.js - for layer creation, movement
*		: JSFX_Mouse.js - to track the mouse x,y coordinates
*
* History
* Date         Version        Description
*
* 2000-06-17	1.0		Converted for javascript-fx
***********************************************************************/
var oneDeg=(2*Math.PI)/360;

/*** Pinwheel type 1 ***/
var Radius = 5;
var NumStars=50;
var NumSteps=16;
var StepAngle=(25)*oneDeg;

var StarObject=new Array();

var hexDigit=new Array("0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F");
function dec2hex(dec)
{
	return(hexDigit[dec>>4]+hexDigit[dec&15]);
}
function hex2dec(hex)
{
	return(parseInt(hex,16))
}

/*** Class PinWheelSpark extends Layer ***/
JSFX.PinWheelSpark = function()
{
	//Call the superclass constructor
	this.superC	= JSFX.Layer;
	this.superC("X", 200, 200);

	this.currAngle 	= 0;
	this.step		= 0;
	this.x		= 200;
	this.y		= 200;
}
JSFX.PinWheelSpark.prototype = new JSFX.Layer;
/*** END Class PinWeelSpark ***/

/*** Choose a random pinwheel configuration ***/
function restart()
{
	var num=Math.floor(Math.random()*4);
	
	for(i=0;i<NumStars;i++)
		StarObject[i].hide();

	if(num==0){
		Radius = 10;
		NumStars=32;
		NumSteps=16;
		StepAngle=(25)*oneDeg;
	}
	else
	if(num==1){
		Radius = 20;
		NumStars=16;
		NumSteps=8;
		StepAngle=(22.5)*oneDeg;
	}
	else
	if(num==2){
		Radius = 10;
		NumStars=16;
		NumSteps=16;
		StepAngle=(22.5)*oneDeg;
	}
	else{
		Radius = 20;
		NumStars=32;
		NumSteps=16;
		StepAngle=(11.25)*oneDeg;
	}


	for(i=0 ; i<NumStars; i++)
	{
		var s=StarObject[i];
		s.currAngle = (StepAngle*i);
		s.step = (i%NumSteps);
		s.x=JSFX.Browser.mouseX;
		s.y=JSFX.Browser.mouseX;
	}

	for(i=0;i<NumStars;i++)
		StarObject[i].show();

	setTimeout("restart()", 10000);
}
JSFX.Pinwheel = function()
{
	for(i=0 ; i<NumStars; i++)
	{
		StarObject[i]=new JSFX.PinWheelSpark();
		StarObject[i].currAngle = (StepAngle*i);
		StarObject[i].step = (i%NumSteps);
		StarObject[i].clip(0,0,4,4);
		StarObject[i].setBgColor("red");
	}

	for(i=0 ; i<NumStars ; i++)
		StarObject[i].show();

	/*** Remove this if you only 1 type of pinwheel ***/
	setTimeout("restart()", 10000);

	Rotate();
}

function changeColour(s)
{
	var colour="";

	r2= Math.floor(Math.random()*2)*255;
	g2= Math.floor(Math.random()*2)*255;
	b2= Math.floor(Math.random()*2)*255;

	if(r2==0 && g2==0 && b2==0)
		r2=255;

	colour = "#" + dec2hex(r2) + dec2hex(g2) + dec2hex(b2);
	s.setBgColor(colour);

	s.x=JSFX.Browser.mouseX;
	s.y=JSFX.Browser.mouseY;
}
function Rotate() 
{
	var x;
	var y;
	for (i = 0 ; i < NumStars ; i++ ) {
		var s=StarObject[i];
		
		if(s.step==0)
			changeColour(s);

		var angle = s.currAngle;
		var rad   = s.step*Radius + 10;
		x = s.x + rad*Math.cos(angle);
		y = s.y + rad*Math.sin(angle);

		s.moveTo(x,y);

		s.step = (s.step+1)%NumSteps;
	}

	setTimeout("Rotate()", 30);
}

/*** If no other script has added it yet, add the ns resize fix ***/
if(navigator.appName.indexOf("Netscape") != -1 && !document.getElementById)
{
	if(!JSFX.ns_resize)
	{
		JSFX.ow = outerWidth;
		JSFX.oh = outerHeight;
		JSFX.ns_resize = function()
		{
			if(outerWidth != JSFX.ow || outerHeight != JSFX.oh )
				location.reload();
		}
	}
	window.onresize=JSFX.ns_resize;
}


