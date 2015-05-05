$(document).ready(function(){

	////////////////////////////////////////////////////
	// GETTING SONG INFORMATION FROM SERVER
	/////////////////////////////////////////////////////

	var _currInd = 0;
	var _curr;
	var _song;
	var _list = [];

	$.get("/getsongs", function(responseJSON) {
		var songs = JSON.parse(responseJSON);
		console.log(songs);
		for (i = 0; i < songs.length; i++) {
			var song = {
				songID: songs[i]._id,
				songTitle: songs[i]._title,
				songImage: songs[i]._imagePath,
				songFile: songs[i]._mp3Path,
				artistName: songs[i]._artistName
			}
			_list.push(song);
		}
		_curr = _list[_currInd];
		setPageElements();
	})

	/////////////////////////////////////////////////////
	// LOCAL DEFAULTS
	/////////////////////////////////////////////////////
	var mickJenkins = {
		artistName: "Mick Jenkins",
		songTitle: "Jazz",
		songImage: "../img/mick.png",
		songFile: "../songFiles/Jazz.mp3"
	}

	var macMiller = {
		artistName: "Mac Miller",
		songTitle: "Diablo",
		songImage: "../img/mac2.png",
		songFile: "../songFiles/Diablo.mp3"
	}

	var vinceStaples = {
		artistName: "Vince Staples",
		songTitle: "Nate",
		songImage: "../img/vince.jpg",
		songFile: "../songFiles/Nate.mp3"
	}

	// var _list = [mickJenkins, macMiller, vinceStaples];

	/////////////////////////////////////////////////////////
	// HOME ELEMENTS
	/////////////////////////////////////////////////////////

	// SET HOME ELEMENTS
	//var _currInd = 0;
	//var _curr = _list[_currInd];

	// ATTACH SCROLL FUNCTION TO BUTTONS
	$("#leftArr").on("click", function() {
		scrollThroughList("left");
	})
	$("#rightArr").on("click", function() {
		scrollThroughList("right");
	})
	$(document.body).on("keydown", function(e){
		if (e.which == 37) { // left arrow key
			hoverButton($("#leftArr"));
			scrollThroughList("left");
		}
		if (e.which == 39) { // right arrow key
			hoverButton($("#rightArr"));
			scrollThroughList("right");
		}
		if (e.which == 13) { // enter
			window.location.href = '/playsong#' + _curr.songID;
		}
  });

	//SCROLL THROUGH LIST FUNCTION
	function scrollThroughList(dir) {
		if (dir == "right") {
			if (_currInd == _list.length - 1) _currInd = 0;
			else _currInd = _currInd + 1;
		} else {
			if (_currInd == 0) _currInd = _list.length - 1;
			else _currInd = _currInd - 1;
		}
		_curr = _list[_currInd];
		setPageElements();
	}

	// SET HOME PAGE ELEMENT FUNCTION
	function setPageElements() {
		if (_song != null) _song.pause();
		_song = new Audio(_curr.songFile);
		_song.volume = 0.15;
		_song.play();
		$("#bckGndImg").attr("src", _curr.songImage);
		$("#songTitle").text(_curr.songTitle);
		$("#artistName").text(_curr.artistName);
	}

	// MAKES BUTTON APPEAR HOVERED FOR THE ARROW KEYS
	function hoverButton(button) {
		button.css("background-color", "rgba(0, 0, 0, .9)");
		button.css("border", "3px solid #fff");
		setTimeout(function() {
			button.removeAttr('style');
		}, 800);
	}

	// ATTACH FUNCTION TO PLAY BUTTON SO GET NEW PAGE W/ CORRECT INFO
	$("#playButt").on("click", function() {
		window.location.href = '/playsong#' + _curr.songID;
	})

	// ATTACH TO CREATE BUTTON
	$("#createButt").on("click", function(e) {
		window.location.href = '/songfactory';
	})
});
