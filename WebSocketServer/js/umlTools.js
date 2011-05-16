/**
 * Javascript file with tools for editing UML elements.
 */
var umlTool = {
	
}

var selectTool = Object.create(uml, {
	mousedown : {value : function(event) {
		selectedElement = null;
		for(var key in umlElements) {
			var e = umlElements[key];
			if(e.select(event) === true) {
				selectedElement = e;
				e.color = 'blue';
				document.getElementById('headerName').value = e.header;
			} else {
				e.color = 'black';
			}
		}
		console.log(selectedElement);
	}}
});

var deleteTool = Object.create(uml, {
	mousedown : {value : function(event) {
		var delIndex = null;
		for(var i = 0; i < umlElements.length; i++) {
			if(umlElements[i].select(event) === true) {
				delIndex = i;
				console.log(delIndex);
			}
		}
		if(delIndex != null)
			umlElements.splice(delIndex,1);
		console.log(umlElements);
	}}
});

var moveTool = Object.create(uml, {
	mousedown : {value : function(event) {
		this.x = event.offsetX;
		this.y = event.offsetY;
		this.dx = 0;
		this.dy = 0;
		selectedElement = null;
		for(var key in umlElements) {
			var e = umlElements[key];
			if(e.select(event) === true && !selectedElement) {
				selectedElement = e;
				e.color = 'blue';
			} else {
				e.color = 'black';
			}
		}
		console.log(selectedElement);
	}},
	mousemove : {value : function(event) {
		if(selectedElement) {
			var olddx = this.dx;
			var olddy = this.dy;
			this.dx = (event.offsetX-this.x);
			this.dy = (event.offsetY-this.y);
			selectedElement.move((this.dx - olddx),(this.dy - olddy));
		}
		console.log('moving');
	}},
	mouseup : {value : function(event) {
		selectedElement = null;
		for(var key in umlElements) {
			umlElements[key].color = 'black';
		}
	}}
});