$(document).ready(function(){

	//////////////////////////////////
	// LOCAL DEFAULT INFO
	//////////////////////////////////
	var macMiller = {
		songTitle: "Here We Go",
		songImage: "../img/macmiller.jpg",
		highScore: 888,
		songLength: "4:20"
	}

	//////////////////////////////////
	// SCREEN ELEMENTS
	//////////////////////////////////
	var curr = macMiller;
	var life = 100;
	var progress = 0;
	var score = 0;
	var lifeInc = 10;
	var lifeDec = 20;
	var playing = false;
	var started = false;
	initiatePageElements();
	// WILL HAVE TO CONVERT TIME INTO SOMETHING TO CHANGE PROGRESS

	// FUNCTION TO SET ELEMENTS
	function initiatePageElements() {
		$("#bckGndImg").attr("src", curr.songImage);
		$("#songTitleGame").text(curr.songTitle);
		$("#highScore").text(curr.highScore);
		$("#score").text(score);
		$("#songLength").text(curr.songLength);
	}

	///////////////////////////////////////
	// GAME ENGINE
	///////////////////////////////////////

	// ATTACHES START FUNCTION TO PLAY BUTTON
	$("#playButt").bind("click", function() {
		if (!started) getReady();
		else {
			if (playing) pauseGame();
			else resumeGame();
		}
	})

	// STARTS GAME WHEN BUTTON HIT
	function getReady() {
		$("#gameOverLay").css("background-color", "rgba(255, 153, 0, .5)");
		$("#overLayMessage").text("READY");
		setTimeout(function() {
			$("#gameOverLay").css("background-color", "rgba(204, 204, 0, .5)");
			$("#overLayMessage").text("SET");
			setTimeout(function() {
			$("#gameOverLay").css("visibility", "hidden");
			$("#restartButt").css("visibility", "visible");
			$("#playButt").text("pause");
			started = true;
			playing = true;
			// START GAME HERE
			}, 2000);
		}, 2000);
	}

	// PAUSES THE GAME
	function pauseGame() {
		playing = false;
		// PAUSE THE GAME HERE
		$("#playButt").text("resume");
	}

	// RESUMES THE GAME
	function resumeGame() {
		playing = true;
		// RESUME THE GAME HERE
		$("#playButt").text("pause");
	}

	// RESTARTS THE GAME
	function restart() {
		// RESTART THE GAME HERE? JUST REFRESH?
	}

	// CHANGES THE LIFE BAR
	function updateLifeBar() {
		$("#lifeBar").css("width", life + "%");
		if (life < 25) {
			$("#lifeBar").css("background-color", "red");
		} else if (life >= 25 && life < 75) {
			$("#lifeBar").css("background-color", "orange");
		} else {
			$("#lifeBar").css("background-color", "green");
		}
	}

	// UPDATES LIFE VARIABLE
	function updateLife(type) {
		if (type == "inc") {
			life = life + lifeInc;
			if (life > 100) life = 100;
		} else {
			life = life = lifeDec;
			if (life < 0) life = 0;
		}
	}

	// UPDATES PROGRESS BAR VISUALLY
	function updateProgress() {
		$("#progressBar").css("width", progress + "%");
	}

	// INCREASES SCORE AND UPDATES IT VISUALLY
	function updateScore() {
		score++;
		$("#score").text(score);
	}

	// FOR HIT
	function hit() {
		updateLife("inc");
		updateLifeBar();
		updateScore();
	}

	// FOR MISS
	function miss() {
		updateLife("dec");
		updateLifeBar();
		checkIfGameOver();
	}

	// CHECKS IF GAME OVER, AND IF SO, ENDS IT
	function checkIfGameOver() {
		if (life == 0) {
			$("#gameOverLay").css("visibility", "visible");
			$("#gameOverLay").css("background-color", "red");
			$("#overLayMessage").text("GAME OVER");
			// STOP TIMER HERE
		}
	}
});
