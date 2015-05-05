$(document).ready(function(){

	//////////////////////////////////
	// GET SONG INFO FROM SERVER
	//////////////////////////////////
	var _curr;
	var songID = window.location.hash.substring(1);
	var postParameters = {
		songID: songID
	};
	$.post("/getsongtoplay", postParameters, function(responseJSON){
		response = JSON.parse(responseJSON);
		_curr = {
			songFile: response.song._mp3Path,
			songImage: response.song._imagePath,
			songTitle: response.song._title,
			highScore: response.highScore,
			length: response.song._length,
			array: response.song._keyStrokes
		}
		setUpAudio();
		initiatePageElements();
	})

	//////////////////////////////////
	// LOCAL DEFAULT INFO
	//////////////////////////////////

	var macMiller = {
		songTitle: "Ave Maria",
		songImage: "../img/macmiller.jpg",
		highScore: 10,
		songLength: "0:10",
		songFile: "../songFiles/Ave_Maria.mp3",
		gameArray: null
	}

	function genFakeArray() {
		var array = [];
		var numOfRows = figureNumOfRowsForArray();

		for (var i = 0; i < numOfRows + 1; i++) { //
			var ind = getArrayInd(i);
			for (var j = ind; j < _numOfSlots + ind; j++) { // sets everything to false from left key dep on row
				array[j] = false;
			}

			var chance = getRandomInt(0, 21);
			if (chance < 2) {
				var tile = getRandomInt(0, 13);
				var tileInd = Math.round(tile + ind);
				array[tileInd] = true; // sets one tile slot to true so it has a tile
			}
		}

		return array
	}

	// FIGURES OUT HOW MANY ROWS ARRAY SHOULD HAVE, NOT EXTENDIBLE!
	function figureNumOfRowsForArray() {
		return Math.round(_totalSecs / _secPerRow);
	}

	//////////////////////////////////
	// SCREEN ELEMENTS
	//////////////////////////////////
	// var _curr = macMiller;
	var _gameArray;
	var _totalSecs;
	var _life = 100;
	var _progress = 0;
	var _score = 0;
	var _lifeInc = 10;
	var _lifeDec = 15;
	var _playing = false;
	var _started = false;
	var _restarted = false;
	var _currSec = -2;
	var _currRow = -20;
	var _numOfSlots = 13;
	var _secPerRow = .1;
	var _milliInterval = Math.round(_secPerRow * 1000);
	var _readyTimer = 1000;
	var _sizeOfTiles = 50;
	var _heightOfRows = _sizeOfTiles;
	var _canvas = $("#canvas");
	var _rowsOnCanvas = figureRowsOnCanvas();
	var _ctx = _canvas.get(0).getContext("2d");

	//////////////////
	// SOUND STUFF
	//////////////////

	var _soundFile;
	var _src;

	// SONG STUFF
	function setUpAudio() {
		_soundFile = document.createElement("audio");
		_soundFile.preload = "auto";
		_soundFile.id = "audio";
		_src = document.createElement("source");
		_src.src = _curr.songFile;
		_soundFile.appendChild(_src);
		_soundFile.load();
		_soundFile.volume = 0.500000;
		_totalSecs = _curr.length;
	}

	// STOCK SOUNDS
	var _tap = new Audio("../sounds/tap.mp3");
	var _bump = new Audio("../sounds/door_bump.mp3");
	var _snap = new Audio("../sounds/snap.mp3");

	//////////////////
	// GET IT STARTED
	//////////////////

	// SETS ELEMENTS
	function initiatePageElements() {
		if (_curr.array.length === 0) {
			console.log("made fake array");
			_gameArray = genFakeArray();
		} else {
			_gameArray = _curr.array.slice(0);
		}

		$("#bckGndImg").attr("src", _curr.songImage);
		$("#songTitleGame").text(_curr.songTitle);
		$("#highScore").text(_curr.highScore._score);
		$("#score").text(_score);
		$("#songLength").text(convertSongLength());
	}

	// FOR WHEN USERS CLICK ON KEYS
	$(document.body).on("keydown", function(e){
		if (e.which == 13) { // if hit enter
			if (!_started) getReady();
			else {
				if (_playing) pauseGame();
				else resumeGame();
			}
		} else checkForHit(e.which);
  });

	$("#homeButt").on("click", function(e) {
		window.location = "/";
	});

	/////////////////////////////////////////
	// CONVERT FUNCTIONS
	/////////////////////////////////////////

	// CONVERTS SONG LENGTH TO SECS
	function convertSongLength() {
		var minutes = Math.floor(_totalSecs / 60);
		var seconds = _totalSecs - (minutes * 60);
		return minutes + ":" + seconds;
	}

	// CONVERTS CURR SEC TO PROGRESS
	function getProgress() {
		if (_currSec < 0) {
			return 0;
		} else {
			return Math.floor((_currSec / _totalSecs) * 100);
		}
	}

	// GETS THE ARRAY INDEX DEPENDING ON WHAT ROW
	function getArrayInd(row) {
		return Math.round(row * _numOfSlots);
	}

	// MAKES RANDOM INTEGERS
	function getRandomInt(min, max) {
  	return Math.floor(Math.random() * (max - min)) + min;
	}

	// ROUNDS AT TWO DECIMAL PLACES BECAUSE JAVASCRIPT SUCKS
	function truncAtTwo(num) {
		return Math.round(num * 100) / 100;
	}

	// FIGURES OUT HOW MANY ROWS WE CAN HAVE ON CANVAS
	function figureRowsOnCanvas() {
		return Math.floor(_canvas.height() / _heightOfRows);
	}

	// GETS TOP ROW OF THE CANVAS
	function getTopRow() {
		var bottomRow = getBottomRow();
		return bottomRow + Math.round(_rowsOnCanvas * _numOfSlots);
	}

	// GETS THE BOTTOM ROW OF THE CANVAS, TWO BELOW THE SLOTS
	function getBottomRow() { // set to one below the slots
		return Math.round((_currRow - 2) * _numOfSlots);
	}

	// GETS THE ROW OF THE SLOTS
	function getCurrRow() { // set to one below the slots
		return Math.round((_currRow) * _numOfSlots);
	}

	// GETS THE X OF A TILE
	function getX(horizontalSlot) {
		if (horizontalSlot < 6) {
			return Math.round(20 + (horizontalSlot * 60));
		} else if (horizontalSlot == 6) {
			return Math.round(435);
		} else {
			return Math.round(550 + ((horizontalSlot - 7) * 60));
		}
	}

	// GETS THE Y OF A TILE
	function getY(verticalSlot) {
		return Math.round((_canvas.height() - _heightOfRows) - (verticalSlot * _heightOfRows));
	}

	///////////////////////////////////////
	// GAME ENGINE
	///////////////////////////////////////

	// UPDATES THE GAME AFTER TICK, MAJOR FUNCTION
	function updateGame() {
		_currSec = truncAtTwo(_currSec + _secPerRow);
		_currRow = _currRow + 1;
		_progress = getProgress();
		updateProgressBar();
		drawCanvas();
		checkForMissedTiles();
		checkIfCompleted();
	}

	// ATTACHES START FUNCTION TO PLAY BUTTON
	$("#playButt").bind("click", function() {
		if (!_started) getReady();
		else {
			if (_playing) pauseGame();
			else resumeGame();
		}
		$("#playButt").blur();
	})

	$("#playButt2").bind("click", function() {
		if (!_started) getReady();
		else {
			if (_playing) pauseGame();
			else resumeGame();
		}
	})

	// STARTS GAME WHEN BUTTON HIT
	function getReady() {
		$("#overLayMessage").text("READY");
		$("#playButt2").css("display", "none");
		$("#overLaySubMess").text("");
		setTimeout(function() {
			$("#overLayMessage").text("SET");
			_started = true;
			_playing = true;
			if (!_restarted) play();
			setTimeout(function() {
				_soundFile.play();
				$("#overLayMessage").text("");
				$("#restartButt").css("visibility", "visible");
				$("#playButt").text("pause");
			}, _readyTimer);
		}, _readyTimer);
	}

	// MOVES GAME FORWARD
	function play() {
		setInterval(function() {
			if (_playing) {
				updateGame();
			}
		}, _milliInterval);
	}

	// PAUSES THE GAME
	function pauseGame() {
		_playing = false;
		_soundFile.pause();
		$("#playButt").text("resume");
	}

	// RESUMES THE GAME
	function resumeGame() {
		_playing = true;
		_soundFile.play();
		$("#playButt").text("pause");
	}

	// ATTACH TO RESTART BUTTON
	$("#restartButt").on("click", function(e) {
		restart();
		$("#restartButt").blur();
	})
	$("#restartButt2").on("click", function(e) {
		restart();
	})

	// RESTARTS THE GAME
	function restart() {
		clearCanvas();
		$("#restartButt2").css("display", "none");
		_soundFile.pause();
		_soundFile.currentTime = 0;
		if (_curr.array.length !== 0) {
			_gameArray = _curr.array.slice(0);
		} else {
			_gameArray = genFakeArray();
		}
		checkHighScore();
		_life = 100;
		_progress = 0;
		_score = 0;
		_playing = false;
		_started = false;
		_restarted = true;
		_currSec = -2;
		_currRow = -20;
		$("#score").text(_score);
		$("#score").css("color", "white");
		$("#highScore").text(_curr.highScore._score);
		$("#highScore").css("color", "white");
		$("#playButt").css("visibility", "visible");
		updateProgressBar();
		updateLifeBar();
		getReady();
	}

	// INCREASES SCORE AND UPDATES IT VISUALLY
	function updateScore() {
		_score++;
		if (_score == _curr.highScore._score) {
			$("#score").css("color", "gold");
			$("#highScore").css("color", "red");
		}

		$("#score").text(_score);
	}

	// CHECKS IF THE HIGH SCORE WAS BEATEN TO RESET IT
	function checkHighScore() {
		if (_curr.highScore._score <= _score) {
			_curr.highScore._score = _score;
			// MORE HERE TO SAVE IT IN THE BACK END
			var postParameters = {
				songID: songID,
				score: _score,
				user: "Default"
			};
			$.post("/savescore", postParameters);
		}
	}

	// UPDATES PROGRESS BAR VISUALLY
	function updateProgressBar() {
		$("#progressBar").css("width", _progress + "%");
	}

	// CHECKS IF GAME COMPLETED
	function checkIfCompleted() {
		if (_progress > 101) {
			_playing = false;
			checkHighScore();
			_progress = 100;
			updateProgressBar();
			$("#overLayMessage").text("YOU SURVIVED");
			$("#playButt").css("visibility", "hidden");
			$("#restartButt2").css("display", "block");
		}
	}

	// UPDATES LIFE VARIABLE
	function updateLife(type) {
		if (type == "inc") {
			_life = _life + _lifeInc;
			if (_life > 100) _life = 100;
		} else {
			_life = _life - _lifeDec;
			if (_life < 0) _life = 0;
		}
	}

	// CHANGES THE LIFE BAR
	function updateLifeBar() {
		$("#lifeBar").css("width", _life + "%");
		if (_life < 25) {
			$("#lifeBar").css("background-color", "red");
		} else if (_life >= 25 && _life < 75) {
			$("#lifeBar").css("background-color", "orange");
		} else {
			$("#lifeBar").css("background-color", "green");
		}
	}

	// CHECKS IF GAME OVER, AND IF SO, ENDS IT
	function checkIfDead() {
		if (_life == 0) {
			_playing = false;
			updateLifeBar();
			checkHighScore();
			$("#overLayMessage").text("YOU DIED");
			$("#playButt").css("visibility", "hidden");
			$("#restartButt2").css("display", "block");
		}
	}

	// CHECKS FOR MISSED TILES
	function checkForMissedTiles() {
		var bottomRowInd = getBottomRow();
		for (var j = bottomRowInd; j < bottomRowInd + _numOfSlots; j++) {
			if (j >= 0) {
				if (_gameArray[j]) {
					miss();
				}
			}
		}
	}

	// CHECKS IF CLICK WAS A HIT
	function checkForHit(keycode) {
		var ind;
		var key;
		if(keycode == 65) {
			ind = 0;
			key = $("#zeroSlot");
		} else if (keycode == 83) {
			ind = 1;
			key = $("#oneSlot");
		} else if (keycode == 87) {
			ind = 2;
			key = $("#twoSlot");
		} else if (keycode == 68) {
			ind = 3;
			key = $("#threeSlot");
		} else if (keycode == 69) {
			ind = 4;
			key = $("#fourSlot");
		} else if (keycode == 70) {
			ind = 5;
			key = $("#fiveSlot");
		} else if (keycode == 32) {
			ind = 6;
			key = $("#sixSlot");
		} else if (keycode == 74) {
			ind = 7;
			key = $("#sevenSlot");
		} else if (keycode == 73) {
			ind = 8;
			key = $("#eightSlot");
		} else if (keycode == 75) {
			ind = 9;
			key = $("#nineSlot");
		} else if (keycode == 79) {
			ind = 10;
			key = $("#tenSlot");
		} else if (keycode == 76) {
			ind = 11;
			key = $("#elevenSlot");
		} else if (keycode == 186) {
			ind = 12;
			key = $("#twelveSlot");
		} else return;

		var currRowInd = getCurrRow();
		var oneAboveInd = currRowInd + 13;
		var oneBelowInd = currRowInd - 13;
		if (currRowInd > 0) {
			if (_gameArray[currRowInd + ind]) {
				_gameArray[currRowInd + ind] = false; // deletes it for missed function
				hit(key);
			} else if (_gameArray[oneAboveInd + ind]) { // checks one row above for buffer
				_gameArray[oneAboveInd + ind] = false;
				hit(key);
			} else if (_gameArray[oneBelowInd + ind]) { // checks one row below for buffer
				_gameArray[oneBelowInd + ind] = false;
				hit(key);
			} else miss(key);
		} else {
			shakeSlot(key);
			_tap.play();
		}
	}

	// FOR HIT
	function hit(key) {
		if (key != null) indicateHit(key);
		_snap.play();
		updateLife("inc");
		updateLifeBar();
		updateScore();
	}

	// CCHANGES CSS TO INDICATE A HIT FOR THE SLOT
	function indicateHit(slot) {
		slot.css("border", "2px solid white");
		slot.css("color", "white");
		setTimeout(function() {
			slot.css("border", "1px solid black");
			slot.css("color", "black");
		}, 250);
	}

	// FOR MISS
	function miss(key) {
		if (key != null) shakeSlot(key);
		_bump.play();
		updateLife("dec");
		updateLifeBar();
		checkIfDead();
	}

	// SHAKES AN OBJECT
	function shakeSlot(slot) {
   var l = 3;
   for (var i = 0; i < 10; i++ ) {
     slot.animate({"bottom": "+=" + ( l = -l ) + 'px' }, 10);
   }
 }

	// GETS ALL TILES THAT BELONG ON CANVAS
	function getTiles(callback) {
		var topRowInd = getTopRow();
		var bottomRowInd = getBottomRow();
		var vSlot = 0;
		for (var i = bottomRowInd; i < topRowInd + 1; i += _numOfSlots) { // goes throw each row
			for (var j = i; j < i + _numOfSlots; j++) {
				if (j >= 0 && _gameArray[j]) { // we start out negative for buffer space
					var hSlot = Math.round(j - i);
					callback(vSlot, hSlot);
				}
			}
			vSlot++;
		}
	}

	////////////////////////////////////////////////////////////
	// DRAWING
	////////////////////////////////////////////////////////////

	// CLEARS THE CANVAS
	function clearCanvas() {
		_ctx.clearRect(0, 0, _canvas.width(), _canvas.height());
	}

	// DRAWS THE ENTIRE CANVAS
	function drawCanvas() {
		clearCanvas();
		getTiles(function(vSlot, hSlot) {
			drawTile(vSlot, hSlot);
		});
	}

	// DRAWS A TILE
	function drawTile(vSlot, hSlot) {
		_ctx.strokeStyle = "black";
		if (hSlot == 0 || hSlot == 12) {
			_ctx.fillStyle = "red";
		} else if (hSlot == 1 || hSlot == 11) {
			_ctx.fillStyle = "pink";
		} else if (hSlot == 2 || hSlot == 10) {
			_ctx.fillStyle = "purple";
		} else if (hSlot == 3 || hSlot == 9) {
			_ctx.fillStyle = "blue";
		} else if (hSlot == 4 || hSlot == 8) {
			_ctx.fillStyle = "green";
		} else if (hSlot == 5 || hSlot == 7) {
			_ctx.fillStyle = "yellow";
		} else _ctx.fillStyle = "orange";

		var X = getX(hSlot);
		var Y = getY(vSlot);
		_ctx.strokeRect(X, Y, _sizeOfTiles, _sizeOfTiles);
		_ctx.fillRect(X, Y, _sizeOfTiles, _sizeOfTiles);
	}
});
