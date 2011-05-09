var canvas, context, mainCanvasElement, mainCanvasContext;

   var activeTool;
   var umlTools = {};
   var activeShapeIndex;
   var shapesArray= new Array();
   var ws;
	
function conn() {
		try {
			ws = new WebSocket("ws://"+document.domain+"/UMLDrawing");
			ws.onopen = function() {
				var message = document.createTextNode('Web Socket is connected.');
				var messageDiv = document.getElementById('message')
				messageDiv.appendChild(message);
			};
			ws.onmessage = function (evt) { 
				var received_msg = evt.data;
				handleServerMessage(received_msg);
			    //DrawPoints(received_msg);
			};
			ws.onclose = function() {
				// websocket is closed.
				alert('conn closed');
			};
			ws.onerror = function(e) {
				//alert("on error:" + e);
			};
		} catch(e) {
			alert("General catch" + e);
		}
	};
   

function init () {
   
    mainCanvasElement = document.getElementById('mainCanvas');
    mainCanvasContext = mainCanvasElement.getContext('2d');
    
	var canvasPanel = mainCanvasElement.parentNode;
    canvas = document.createElement('canvas');
    canvas.id = 'tempCanvas';
    canvas.width = mainCanvasElement.width;
    canvas.height = mainCanvasElement.height;
    canvasPanel.appendChild(canvas);
    context = canvas.getContext('2d');

    var tool_selector = document.getElementById('toolSelector');
  	tool_selector.addEventListener('change', ev_tool_change, false);
    // Activate the default tool.
    activeTool = new  umlTools['classTool']();
    // Attach mouse event listeners.
    canvas.addEventListener('mousedown', ev_canvas, false);
    canvas.addEventListener('mousemove', ev_canvas, false);
    canvas.addEventListener('mouseup',   ev_canvas, false);
  }

function ev_canvas (ev) {
    if (ev.layerX || ev.layerX == 0) { // Firefox
      ev._x = ev.layerX;
      ev._y = ev.layerY;
    } else if (ev.offsetX || ev.offsetX == 0) { // Opera
      ev._x = ev.offsetX;
      ev._y = ev.offsetY;
    }
    // Call the event handler of the active tool.
    var func = activeTool[ev.type];
    if (func) {
      func(ev);
    }
  }

function ev_tool_change (ev) {
    if (umlTools[this.value]) {
      activeTool = new umlTools[this.value]();
    }
  }

function shapeObject (x0,y0,x1,y1,type, contentData){
  	this.shape_x0 = parseInt(x0);
	this.shape_y0 = parseInt(y0);
	this.shape_x1 = parseInt(x1);
	this.shape_y1 = parseInt(y1);
	this.shape_type = type;
	this.shape_contentData = contentData ;
	this.shape_active = false;
  }
  
function findActiveShape(ev){
  	activeShapeIndex = -1;
    for(var i=shapesArray.length-1;i>=0;i--){
    tempShape = shapesArray[i];
	var temp_x0 = Math.min(tempShape.shape_x0,  tempShape.shape_x1),
       	 temp_y0 = Math.min(tempShape.shape_y0, tempShape.shape_y1),
       	 temp_x1 = Math.max(tempShape.shape_x0, tempShape.shape_x1),
       	 temp_y1= Math.max(tempShape.shape_y0, tempShape.shape_y1);
	if(ev._x >= temp_x0 && ev._y>= temp_y0 &&ev._x <= temp_x1 && ev._y <= temp_y1 && activeShapeIndex== -1){
		shapesArray[i].shape_active = true;
		activeShapeIndex = i;
	}else{
		shapesArray[i].shape_active = false;
	}
  }
  return activeShapeIndex;
}
  
function addObject(shape,sendmessage){
	  shapesArray.push(shape);
	  if(sendmessage){
		  ws.send(createServerMessage(shape,'add'));
	  }
  };
 
function createServerMessage(shape,operation){
	 
	 var serializedShape = 
		 operation
		 +'#'+shape.shape_x0 
		 +'#'+shape.shape_y0
		 +'#'+ shape.shape_x1
		 +'#'+shape.shape_y1
		 +'#'+shape.shape_type
		 +'#'+shape.shape_contentData
		 +'#'+shape.shape_active
		 +'#'+activeShapeIndex;
	 return serializedShape;	 
 };
  
function handleServerMessage(received_msg){

	var contentData = received_msg.split('#');
	 var operation = contentData[0];
	 if(operation == 'add'){
			  addObject(new shapeObject (contentData[1],contentData[2], contentData[3],contentData[4],contentData[5],contentData[6]),false);
	 }else
	 if(operation == 'move'){
		 activeShapeIndex = parseInt(contentData[8]);
		 if(activeShapeIndex > -1){
			 shapesArray[activeShapeIndex].shape_x0 = parseInt(contentData[1]);
			 shapesArray[activeShapeIndex].shape_y0 = parseInt(contentData[2]);
			 shapesArray[activeShapeIndex].shape_x1 = parseInt(contentData[3]);
			 shapesArray[activeShapeIndex].shape_y1 = parseInt(contentData[4]);
		 }
	  }else
	 if(operation == 'updateContent'){
		 activeShapeIndex = parseInt(contentData[8]);
		 if(activeShapeIndex > -1){
			 shapesArray[activeShapeIndex].shape_contentData = contentData[6];
		 }
	  }
	 drawShapes();
 };
			 
function drawShapes(){
	mainCanvasContext.clearRect(0, 0, mainCanvasElement.width, mainCanvasElement.height);
	for(var i=0;i< shapesArray.length;i++){
	    tempShape = shapesArray[i];
		if(tempShape.shape_type == "class"){
			tempTool = new umlTools.classTool();
		}else
		if(tempShape.shape_type == "line"){
			tempTool = new umlTools.lineTool();
		}
		tempTool.drawShape(tempShape);
	}
	context.clearRect(0, 0, canvas.width, canvas.height);
  };
  
function umlTool(){
  
  	var tool = this;
    this.started = false;
	this.dragStarted = false;
	
    this.mousedown = function (ev) {
      tool.started = true;
      tool.x0 = ev._x;
      tool.y0 = ev._y;
	  deActivateTextEntryPanel();
	  findActiveShape(ev);
	  if(activeShapeIndex > -1){
		tool.started = false;
		tool.dragStarted = true;
		loadActiveShapeText();
		drawShapes();
	  }
    };
	
	this.mouseup = function (ev) {
      if (tool.started || tool.dragStarted) {
        tool.mousemove(ev);
		if(tool.started){
			  w = Math.abs(ev._x - tool.x0),
       	      h = Math.abs(ev._y - tool.y0);
			  if (!w || !h) {
				return;
			  }
			  addObject(new shapeObject (tool.x0,tool.y0, ev._x,ev._y,tool.toolType, ''),true);
			  tool.started = false;
		}
		 	
		if(tool.dragStarted){ 
			ws.send(createServerMessage(shapesArray[activeShapeIndex],'move'));
		}
		tool.dragStarted = false;
	    drawShapes();
      }
    };
  };
  
  // The classTool.
  umlTools.classTool = function () {
    
	this.inheritFrom = umlTool; 
    this.inheritFrom();
    var tool = this;
	tool.toolType = "class";

	this.mousemove = function (ev) {
      if (!tool.started && !tool.dragStarted) {
        return;
      }
		if(tool.started){
     	 var x = Math.min(ev._x,  tool.x0),
       	   y = Math.min(ev._y,  tool.y0),
       	   w = Math.abs(ev._x - tool.x0),
       	   h = Math.abs(ev._y - tool.y0);
	      context.clearRect(0, 0, canvas.width, canvas.height);
	      if (!w || !h) {
	    	  return;
      	  }
		  context.strokeRect(x, y, w, h);
		}else
		if(tool.dragStarted){
     	   var move_x = ev._x - tool.x0,
       	   move_y = ev._y - tool.y0;
     	   shapesArray[activeShapeIndex].shape_x0 += move_x ;
		   tool.x0 =  ev._x;
		   shapesArray[activeShapeIndex].shape_y0 += move_y;
 		   tool.y0 = ev._y;
		   shapesArray[activeShapeIndex].shape_x1 += + move_x;
		   shapesArray[activeShapeIndex].shape_y1 += move_y;
		   drawShapes();
		}
	};
   this.drawShape = function(shape){
		var x = Math.min(shape.shape_x1, shape.shape_x0),
        y = Math.min(shape.shape_y1,  shape.shape_y0),
        w = Math.abs(shape.shape_x1 - shape.shape_x0),
        h = Math.abs(shape.shape_y1 - shape.shape_y0);
 		if(tempShape.shape_active){
		 	mainCanvasContext.fillStyle = 'grey'; 
		  	mainCanvasContext.fillRect(x, y, w, h);
			activateTextEntryPanel(x,y);
			
		}else{
	   		mainCanvasContext.strokeRect(x, y, w, h);
		}
		mainCanvasContext.strokeRect(x, y, w, 15);
		mainCanvasContext.strokeRect(x, y, w, 60);
		mainCanvasContext.font = "italic bold 10px sans-serif"; 
	    mainCanvasContext.textBaseline = "top";
	    mainCanvasContext.fillStyle = 'blue'; 
	   	mainCanvasContext.fillText('Attributes', x+(w/4), y + 15); 
		mainCanvasContext.fillText('Operations', x+(w/4), y + 60); 
		
		if(shape.shape_contentData){
			mainCanvasContext.font = " bold 12px sans-serif"; 
	    	mainCanvasContext.fillStyle = '#000000'; 
	   		var contentData = shape.shape_contentData.split(';');
   			mainCanvasContext.fillText(contentData[0], x+(w/4), y); 
			mainCanvasContext.fillText(contentData[1], x+10, y + 27); 
			mainCanvasContext.fillText(contentData[2], x+10, y + 72); 
		}else{
			mainCanvasContext.fillText('Class', x+(w/4), y); 
		}
	};
  };
  
umlTools.lineTool = function () {
  
   	var tool = this;
  	this.inheritFrom = umlTool; 
    this.inheritFrom();
    var tool = this;
	tool.toolType = "line";
  
	this.mousemove = function (ev) {
    if (!tool.started && !tool.dragStarted) {
        return;
      }
		if(tool.started){
     		context.clearRect(0, 0, canvas.width, canvas.height);
		    context.beginPath();
    		context.moveTo(tool.x0, tool.y0);
    		context.lineTo(ev._x,   ev._y);
    		context.stroke();
    		context.closePath();
		}else
		if(tool.dragStarted){
	 	   var move_x = ev._x - tool.x0,
       	   move_y = ev._y - tool.y0;
 		   shapesArray[activeShapeIndex].shape_x0 += move_x ;
		   tool.x0 =  ev._x;
		   shapesArray[activeShapeIndex].shape_y0 += move_y;
		   tool.y0 = ev._y;
			shapesArray[activeShapeIndex].shape_x1 += move_x;
			shapesArray[activeShapeIndex].shape_y1 += move_y;
			drawShapes();
		}
  };

  this.drawShape = function(shape){
  		mainCanvasContext.beginPath();
    	mainCanvasContext.moveTo(shape.shape_x0,shape.shape_y0);
    	mainCanvasContext.lineTo(shape.shape_x1,shape.shape_y1);
    	mainCanvasContext.stroke();
    	mainCanvasContext.closePath();
	};	
};


function loadActiveShapeText(){
	
	classNameElement = document.getElementById('classNameInput');
	classAttributesElement = document.getElementById('classAttributesInput');
  	classOperationsElement = document.getElementById('classOperationsInput');
  	classNameElement.value = '';
	classAttributesElement.value = '';
	classOperationsElement.value = '';
	if(shapesArray[activeShapeIndex].shape_contentData){
		var contentData = shapesArray[activeShapeIndex].shape_contentData.split(';');
   		classNameElement.value = contentData[0];
		classAttributesElement.value = contentData[1];
		classOperationsElement.value = contentData[2];
	}
};

function updateActiveShapeText(){
	classNameElement = document.getElementById('classNameInput');
	classAttributesElement = document.getElementById('classAttributesInput');
  	classOperationsElement = document.getElementById('classOperationsInput');
  	shapesArray[activeShapeIndex].shape_contentData = classNameElement.value+';'+classAttributesElement.value+';'+ classOperationsElement.value;
	ws.send(createServerMessage(shapesArray[activeShapeIndex],'updateContent'));
  	drawShapes();
};
function activateTextEntryPanel(x,y){
	textEntryPanelElement = document.getElementById('textEntryPanel');
  	//textEntryPanelElement.style.top = y +'px' ;
	//textEntryPanelElement.style.left = x + 'px';
	textEntryPanelElement.style.display = '';
};

function deActivateTextEntryPanel(){
	textEntryPanelElement = document.getElementById('textEntryPanel');
  	textEntryPanelElement.style.display = 'none';
};	