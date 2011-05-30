	var canvasElement, context;
    var activeTool;
    var drawingTools = {};
    var activeColor;
	
  var ws;
	function conn() {
		try {
			ws = new WebSocket("ws://"+document.domain+"/FreehandDrawing");
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
    	    canvasElement = document.getElementById('freehandCanvas');
    	    context = canvasElement.getContext('2d');
    	    activeTool = new drawingTools['pencilTool']();
            canvasElement.addEventListener('mousedown', canvas_event, false);
    	    canvasElement.addEventListener('mousemove', canvas_event, false);
    	    canvasElement.addEventListener('mouseup',   canvas_event, false);
    	  };
    	  


    	  function freehandTool() {
    	    var tool = this;
    	    this.started = false;
			this.inkColor = "#000000"; //Default color
			this.lineWidth = 1;
    	 	this.setInkColor = function(color){
				tool.inkColor = color;
			};

			this.mousedown = function (ev) {
    	        context.beginPath();
    	        context.moveTo(ev._x, ev._y);
    	        tool.started = true;
    	        context.strokeStyle = tool.inkColor;
    	        context.lineWidth = tool.lineWidth;
  	          
	            drawMessage = tool.inkColor + ";" + tool.lineWidth ;
    	    };

    	    this.mousemove = function (ev) {
    	      if (tool.started) {
    	        context.lineTo(ev._x, ev._y);
    	        drawMessage += ";" + ev._x + "," + ev._y;
    	        context.stroke();
    	        
    	      }
    	    };

    	    this.mouseup = function (ev) {
    	      if (tool.started) {
    	        tool.mousemove(ev);
    	        tool.started = false;
    	        ws.send(drawMessage)
    	      }
    	    };
    	    this.drawPoints =function(pathdata) {
			    context.beginPath();
    	         var allxy = pathdata.split(';');
    	         if(allxy.length > 1)
    	         {
    	             var remoteColor = allxy[0];
    	             context.strokeStyle = remoteColor;
    	             var remoteLineWidth = allxy[1];
    	             context.lineWidth = remoteLineWidth;
    	             
    	             for (i = 2; i < allxy.length; i++) {
    	                 xy = allxy[i];
    	                 if (xy != "") {
    	                	 xyArr = xy.split(",");
    	                	    x = xyArr[0];
	    	         	        y = xyArr[1];
	    	         		 if(i == 1){
    	                     	context.moveTo(x, y) ;
    	                     }
	    	         		 else{
    	                 	    context.lineTo((x), (y));
    	    	         	    context.stroke();
    	    	             }
    	                 }
    	             }
    	         }
    	     };
    	  };
    	  
    	 //Pencil tool
    	 drawingTools.pencilTool = function() {
    	 };
    	 drawingTools.pencilTool.prototype = new freehandTool();
         	
    	 //Brush tool 
    	 drawingTools.brushTool = function() {};
       	 drawingTools.brushTool.prototype = new freehandTool();
       	 drawingTools.brushTool.prototype.lineWidth = 10;
       	
       	
    	 //Eraser tool 
       	 drawingTools.eraserTool = function() {};
     	 drawingTools.eraserTool.prototype =  new freehandTool();
     	 drawingTools.eraserTool.prototype.inkColor = "#ffffff"; 
     	 drawingTools.eraserTool.prototype.lineWidth = 20;
     	 //override the setInkColor method to do nothing
   	 	 drawingTools.eraserTool.prototype.setInkColor = function(color){};
     	
     	 function canvas_event (ev) {
    	    if (ev.layerX || ev.layerX == 0) { // Firefox
    	      ev._x = ev.layerX;
    	      ev._y = ev.layerY;
    	    } else if (ev.offsetX || ev.offsetX == 0) { // Opera
    	      ev._x = ev.offsetX;
    	      ev._y = ev.offsetY;
    	    }
    	    // Call the event handler of the tool.
    	    var func = activeTool[ev.type];
    	    if (func) {
    	      func(ev);
    	    }
    	  };
    	  
    	  function changeTool(selectorBox) {
     		 activeTool = new drawingTools[selectorBox.value]();
    		 
    	  };
    	  function updateActiveColor(color){
    		  activeColor = color;
    		  activeTool.setInkColor(color);
    		  
    	  }
    	  function handleServerMessage(received_msg){
    		  activeTool.drawPoints(received_msg);
    		}

    	  