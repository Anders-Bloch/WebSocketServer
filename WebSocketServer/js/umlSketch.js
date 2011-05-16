/**
 * 
 */

var mouseButtonDown = false;
var umlElements = [];
var currentObject = uml;

var selectedElement = null;

var canvas = document.getElementById('canvas');
var context = canvas.getContext('2d');
canvas.addEventListener('mousedown',mouseDownEvent,true);
canvas.addEventListener('mouseup',mouseUpEvent,true);
canvas.addEventListener('mousemove',mouseMove,true);

var uml = {
	mousedown : function(event) {},
	mousemove : function(event) {},
	mouseup : function(event) {},
	draw : function(event) {}
}


function mouseDownEvent(event) {
	mouseButtonDown = true;
	currentObject.mousedown(event);
	drawElements();
	currentObject.draw(context);
}

function mouseMove(event) {
	if(mouseButtonDown) {
		currentObject.mousemove(event);
		drawElements();
		currentObject.draw(context);
	}
}

function mouseUpEvent(event) {
	mouseButtonDown = false;
	currentObject.mouseup(event);
	if(currentObject.type == 'element')
		umlElements[umlElements.length] = currentObject;
	currentObject = uml;
	drawElements();
	enableButtons();
}

function drawElements() {
	console.log('redraw' + umlElements);
	canvas.width = canvas.width; // Clears the canvas
	for(var element in umlElements) {
		var e = umlElements[element];
		if(e && e.type == 'element') {
			e.draw(context);
		}
	}
}

function enableButtons() {
	var buttons = document.getElementsByTagName('input');
	for(var b in buttons) {
		buttons[b].disabled = '';
	}
}

function newElement(button,element) {
	enableButtons();
	currentObject = Object.create(element, {'color' : {value : 'black', writable:true}});
	button.disabled = 'disabled';
}

function newTool(button,tool) {
	enableButtons();
	currentObject = Object.create(tool, {});
	button.disabled = 'disabled';
}
function editHeader(textElement) {
	if(selectedElement) {
		selectedElement.header = textElement.value;
		drawElements();
	}
}
function editHeaderBlur(textElement) {
	selectedElement.color = 'black';
	selectedElement = null;
	textElement.value = '';
}

