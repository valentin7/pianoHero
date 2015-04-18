
$(document).ready(function(){

	/////////////////////////////////////////////////////
	// GETTING SONG INFORMATION FROM SERVER
	/////////////////////////////////////////////////////
	
	/*
	var currInd = 0;
	var curr;
	var list = [];

	$.get("/getsongs", function(responseJSON) {
		var songs = JSON.parse(responseJSON);
		for (i = 0; i < songs.length; i++) {
			var song = {
				songID: songs[i]._id,
				songTitle: songs[i]._title,
				songImage: songs[i]._imagePath
			}
			list.push(song);
		}
		curr = list[currInd];
		setPageElements();
	}) */


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
		window.location.href = '/playsong#' + curr.songID;
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
		console.log(curr.songImage);
		$("#bckGndImg").attr("src", curr.songImage);
		$("#songTitle").text(curr.songTitle);
	}

	/////////////////////////////////////
	//GAME DEFAULTS
	/////////////////////////////////////

});
