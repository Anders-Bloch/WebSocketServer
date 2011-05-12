/**
 * UML Elements available to the UML sketch pad
 */
var umlElement = {
	mousedown : function(event) {
		this.startX = event.offsetX;
		this.startY = event.offsetY;
		this.endX = event.offsetX;
		this.endY = event.offsetY;
	},
	mousemove : function(event) {
		this.endX = event.offsetX;
		this.endY = event.offsetY;
	},
	mouseup : function(event) {
		this.endX = event.offsetX;
		this.endY = event.offsetY;
	},
	draw : {
		//Empty on purpose
	},
	move : function(moveX,moveY){
		this.startX = this.startX+moveX;
		this.startY = this.startY+moveY;
		this.endX = this.endX+moveX;
		this.endY = this.endY+moveY;
	},
	select : function(event) {
		var x = event.offsetX,
			y = event.offsetY;
		return (x > this.startX && x < this.endX && y > this.startY && y < this.endY);
	},
	type : 'element'
}

var lineElement = Object.create(umlElement, {
	draw: {value : function(context) {
		context.strokeStyle = this.color;
		context.beginPath(); 
		context.moveTo(this.startX, this.startY); 
		context.lineTo(this.endX, this.endY);
		context.stroke();
	}}
});

var classElement = Object.create(umlElement, {
	draw: {value : function(context) {
		context.strokeStyle = this.color;
        var w = this.endX - this.startX,
        	h = this.endY - this.startY;
		context.strokeRect(this.startX, this.startY, w, h);
	}}
});