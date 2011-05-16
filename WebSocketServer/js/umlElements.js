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
	type : 'element',
	header: ''
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
		context.font = '14px Arial';
		var w = context.measureText(this.header).width + 4;
		if(w < 100)
			w = 100;
		context.strokeStyle = this.color;
		this.endX = this.startX + w;
		this.endY = this.startY + 50;
		context.save();
		context.shadowOffsetX = 2;
		context.shadowOffsetY = 2;
		context.shadowBlur    = 2;
		context.shadowColor   = 'rgba(0, 0, 0, 0.5)';
		context.fillStyle = 'white';
		context.fillRect(this.startX, this.startY, w, 50);
		context.restore();
		context.strokeRect(this.startX, this.startY, w, 50);
		context.beginPath(); 
		context.moveTo(this.startX, this.startY+20); 
		context.lineTo(this.endX, this.endY-30);
		context.stroke();
		context.moveTo(this.startX, this.startY+20);
		context.fillText(this.header, this.startX+2, this.startY+16);
	}}
});