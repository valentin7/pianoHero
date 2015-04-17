
$(document).ready(function(){

	/////////////////////////////////////////////////////
	// LOCAL DEFAULTS
	/////////////////////////////////////////////////////
	var whiteStripes = {
		songTitle: "Seven Nation Army",
		songImage: "../img/seven.jpg"
	}

	var macMiller = {
		songTitle: "Here We Go",
		songImage: "../img/macmiller.jpg"
	}

	var earlSweatshirt = {
		songTitle: "Chum",
		songImage: "../img/sweatshirt.jpg"
	}

	var list = [whiteStripes, macMiller, earlSweatshirt];

	/////////////////////////////////////////////////////////
	// HOME ELEMENTS
	/////////////////////////////////////////////////////////

	// SET HOME ELEMENTS
	var currInd = 0;
	var curr = list[currInd];
	setPageElements();

	// ATTACH SCROLL FUNCTION TO BUTTONS
	$("#leftArr").bind("click", function() {
		scrollThroughList("left");
	})

	$("#rightArr").bind("click", function() {
		scrollThroughList("right");
	})

	// ATTACH FUNCTION TO PLAY BUTTON SO GET NEW PAGE W/ CORRECT INFO
	$("#playButt").bind("click", function() {
		// method here to get html for game page and the correct information
	})

	//SCROLL THROUGH LIST FUNCTION
	function scrollThroughList(dir) {
		if (dir == "right") {
			if (currInd == list.length - 1) currInd = 0;
			else currInd = currInd + 1;
		} else {
			if (currInd == 0) currInd = list.length - 1;
			else currInd = currInd - 1;
		}
		curr = list[currInd];
		setPageElements();
	}

	// SET HOME PAGE ELEMENT FUNCTION
	function setPageElements() {
		$("#bckGndImg").attr("src", curr.songImage);
		$("#songTitle").text(curr.songTitle);
	}

	/////////////////////////////////////
	//GAME DEFAULTS
	/////////////////////////////////////

});
