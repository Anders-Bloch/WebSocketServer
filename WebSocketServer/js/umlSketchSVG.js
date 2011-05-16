/**
 * 
 */
var selectedElement = null;

function selected(element) {
	selectedElement = element;
}

function deselected(element) {
	selectedElement = null;
}

function move(event) {
	if(selectedElement) {
		selectedElement.setAttribute("transform", "translate(" + event.offsetX + " " + event.offsetY + ")");
	}
}
		
function newClass() {
	var newClass = document.createElement('svg');
	newClass.setAttribute('height','500px');
	newClass.setAttribute('width','1000px');
	//newClass.setAttribute('onmousedown','selected(this)');
	//newClass.setAttribute('onmouseup','deselected(this)');
	//newClass.id = id;
	var rect = document.createElement('rect');
	rect.setAttribute('x',300);
	rect.setAttribute('y',100);
	rect.setAttribute('height','50');
	rect.setAttribute('width','100');
	rect.setAttribute('style','stroke:#000000; fill: #fff;');
	newClass.appendChild(rect);
	console.log(document.getElementById('svg1'));
	document.getElementsByTagName('body')[0].appendChild(newClass);
}

window.addEventListener('mousemove', move, true);