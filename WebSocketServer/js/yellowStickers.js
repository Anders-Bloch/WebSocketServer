/**
 * Yellow stickers javascript file
 */
var ws;
function conn() {
	try {
		ws = new WebSocket("ws://"+document.domain+"/YellowStickers");
		ws.onopen = function() {
			var message = document.createTextNode('Web Socket is connected.');
			var messageDiv = document.getElementById('message')
			messageDiv.appendChild(message);
		};
		ws.onmessage = function (evt) { 
			var received_msg = evt.data;
			var msgArray = received_msg.split(':');
			if(msgArray[0] == 'new') {
				newSticker(msgArray[1],'','','new');
			} else if(msgArray[0] == 'move') {
				move(msgArray[1],document.getElementById(msgArray[2]));
			} else if(msgArray[0] == 'text') {
				document.getElementById(msgArray[1]).value = msgArray[2];
			} else if(msgArray[0] == 'header') {
				document.getElementById(msgArray[1]).value = msgArray[2];
			} else if(msgArray[0] == 'delete') {
				var dropElement = document.getElementById(msgArray[1]);
				dropElement.parentNode.removeChild(dropElement);
			} else if(msgArray[0] == 'id') {
				var splitTasks = received_msg.split(';');
				for(var i = 0; i < splitTasks.length-1; i++) {
					var task = splitTasks[i].split(':');
					newSticker(task[1], task[5], task[7], task[3]);
				}
			}
		};
		ws.onclose = function() {
			alert('conn closed');
		};
		ws.onerror = function(e) {
			alert('conn closed');
		};
	} catch(e) {
		alert("General catch" + e);
	}
}

//Sticker state
var tasks = new Array();

//Sticker CRUD functions
function newSticker(id, headerText, bodyText, state) {
	var div = document.createElement('div');
	div.draggable = 'true'; 
	div.id = id;
	div.className = 'sticker';

	var label = document.createElement('p');
	label.innerHTML = id;
	label.className = 'stickerId';
	div.appendChild(label);

	var header = document.createElement('input');
	header.type = 'text';
	header.value = headerText;
	header.id = 'header'+id;
	header.className = 'header';
	header.onkeyup = function() {
		ws.send("header:"+this.id+":"+this.value);
	}
	div.appendChild(header);
	
	var textarea = document.createElement('textarea');
	textarea.className = 'text';
	textarea.innerHTML = bodyText;
	textarea.id = 'text'+id;
	textarea.onkeyup = function() {
		ws.send("text:"+this.id+":"+this.value);
	}
	div.appendChild(textarea);

	div.ondragstart = function(event) {
		event.dataTransfer.setData('Text', id);
	} 
	document.getElementById(state).appendChild(div);
	tasks[id] = div;
}

function addSticker() {
	if(ws) {
		var nextId = tasks.length;
		newSticker(nextId,'','','new');
		ws.send("new:" + nextId);
	} else {
		alert('You need to connect to the server before you can create new tasks!');
	}
}

function deleteSticker(e) {
	var dropId = e.dataTransfer.getData('Text');
	var dropElement = document.getElementById(dropId);
	dropElement.parentNode.removeChild(dropElement);
	ws.send("delete:"+dropId);
	e.preventDefault();
}

//Drag and drop
function drop(target, e) {
	var dropId = e.dataTransfer.getData('Text');
	move(dropId, target);
	ws.send('move:'+dropId+':'+target.id);
	e.preventDefault();
}

function move(dropId, target) {
	var dropElement = document.getElementById(dropId);
	var children = target.getElementsByTagName('div');
	if(children.length == 0) {
		target.appendChild(dropElement);
	} else {
		var droped = false;
		for(var i = 0; i < children.length; i++) {
			var elementId = children[i].id;
			if(elementId > dropId) {
				target.insertBefore(dropElement,children[i]);
				droped = true;
				break;
			}
		}
		if(!droped) {
			target.appendChild(dropElement);
		}
	}
}