$(document).ready(function(){

  //////////////////////////////////
  // LOCAL DEFAULT INFO
  //////////////////////////////////

  // var chance = {
  //   songTitle: "Juice",
  //   songImage: "../img/chance.png",
  //   songLength: "0:30",
  //   songFile: "../songFiles/Juice.mp3",
  //   gameArray: null
  // }

  //////////////////////////////////
  // SCREEN ELEMENTS
  //////////////////////////////////
  var _curr = {};
  var _totalSecs = null;
  var _progress = 0;
  var _score = 0;
  var _recording = false;
  var _started = false;
  var _restarted = false;
  var _currSec = 0;
  var _currRow = 0;
  var _secPerRow = .1;
  var _keyStrokes = []; // WILL KEEP THE KEYSTROKES DONE BY THE USER
  var _onCanvas = []; // HOLDS TILES ON THE CANVAS
  var _milliInterval = Math.round(_secPerRow * 1000);
  var _readyTimer = 1000;
  var _numOfSlots = 13;
  var _sizeOfTiles = 50;
  var _heightOfRows = _sizeOfTiles;
  var _canvas = $("#canvas");
  var _ctx = _canvas.get(0).getContext("2d");
  var _song = null;
  // initiatePageElements();

  // ATTACH TO FORM ELEMENTS
  $("#songTitleInput").on("blur", function(e) {
    _curr.songTitle = $("#songTitleInput").val();
    $("#songTitleGame").text(_curr.songTitle);
  })

  $("#artistNameInput").on("blur", function(e) {
    _curr.artistName = $("#artistNameInput").val();
    $("#artistNameGame").text(_curr.artistName);
  })

  $("#songImageInput").on("blur", function(e) {
    var file = e.currentTarget.files[0];
    var path = URL.createObjectURL(file);
    _curr.songImage = file;
    $("#bckGndImg").attr("src", path);
  })

  $("#songFileInput").on("blur", function(e) {
    var file = e.currentTarget.files[0];
    var path = URL.createObjectURL(file);
    _curr.songFile = file;
    if (_song != null) _song.pause();
    _song = new Audio(path);

    _song.addEventListener('loadedmetadata', function() {
      _curr.songLength = _song.duration;
      $("#songLength").text(convertSongSecsToNormal());
      _totalSecs = _song.duration;
      //_song.play();
    });
  })

  // SETS ELEMENTS
  function initiatePageElements() {
    $("#bckGndImg").attr("src", _curr.songImage);
    $("#songTitleGame").text(_curr.songTitle);
    $("#songLength").text(_curr.songLength);
  }

  // FOR WHEN USERS CLICK ON KEYS
  $(document.body).on("keydown", function(e){
    if (_recording) recordKeystroke(e.which);
  });

  $("#homeButt").on("click", function(e) {
    window.location = "/";
  });

  // CREATES A KEYSTROKE
  function newKeyStroke(t, s) {
    return {type: t, secs: s};
  }

  /////////////////////////////////////////
  // CONVERT FUNCTIONS
  /////////////////////////////////////////

  // CONVERTS SONG LENGTH TO SECS
  function convertSongLengthToSecs() {
    var parts = _curr.songLength.split(":");
    return Math.round(parts[0] * 60) + (parts[1] * 1);
  }

  // CONVERTS SONG LENGTH IN SECS TO NORMAL FORMAT
  function convertSongSecsToNormal() {
    var min = Math.floor(_curr.songLength / 60);
    var secs = Math.round(_curr.songLength % 60);
    return min + ":" + secs;
  }

  // CONVERTS CURR SEC TO PROGRESS
  function getProgress() {
    if (_currSec < 0) {
      return 0;
    } else {
      return Math.floor((_currSec / _totalSecs) * 100);
    }
  }

  // FIGURES OUT HOW MANY ROWS ARRAY SHOULD HAVE, NOT EXTENDIBLE!
  function figureNumOfRowsForArray() {
    return Math.round(_totalSecs / _secPerRow);
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
  function getY(time) {
    var diff = truncAtTwo(_currSec - time);
    var numOfRowsPassed = truncAtTwo(diff / _secPerRow);
    return Math.round(50 + (numOfRowsPassed * _heightOfRows));
  }

  ///////////////////////////////////////
  // GAME ENGINE
  ///////////////////////////////////////

  // UPDATES THE GAME AFTER TICK, MAJOR FUNCTION
  function updateScreen() {
    _currSec = truncAtTwo(_currSec + _secPerRow);
    _progress = getProgress();
    updateProgress();
    drawCanvas();
    checkIfCompleted();
  }

  // CHECKS IF OKAY TO START
  function canStart() {
    if (_curr.songTitle == null || _curr.songTitle == "") return false;
    if (_curr.artistName == null || _curr.artistName == "") return false;
    if (_curr.songImage == null) return false;
    if (_curr.songFile == null) return false;
    if (_curr.songLength == null) return false;
    return true;
  }

  // ACTUALLY BEGINS GAME
  function startRecording() {
    if (!canStart()) $("#overLaySubMess").text("You didn't fill something out");
    else getReady();
  }

  // ATTACHES START FUNCTION TO PLAY BUTTON
  $("#recordButt").on("click", function() {
    if (!_started) startRecording();
    else {
      if (_recording) pauseRecording();
      else resumeRecording();
    }
    $("#recordButt").blur();
  })

  $("#recordButt2").on("click", function() {
    startRecording();
  })

  // STARTS GAME WHEN BUTTON HIT
  function getReady() {
    $("#overLayForm").css("display", "none");
    _song.pause();
    _song.currentTime = 0;
    $("#overLayMessage").text("READY");
    $("#overLaySubMess").text("");
    $("#recordButt2").css("display", "none");
    setTimeout(function() {
      $("#overLayMessage").text("SET");
      setTimeout(function() {
        _song.play();
        _started = true;
        _recording = true;
        if (!_restarted) record();
        $("#overLayMessage").text("");
        $("#restartButt").css("visibility", "visible");
        $("#recordButt").text("pause");
      }, _readyTimer);
    }, _readyTimer);
  }

  // MOVES GAME FORWARD
  function record() {
    setInterval(function() {
      if (_recording) {
        updateScreen();
      }
    }, _milliInterval)
  }

  // PAUSES THE GAME
  function pauseRecording() {
    _recording = false;
    _song.pause();
    $("#recordButt").text("resume");
  }

  // RESUMES THE GAME
  function resumeRecording() {
    _recording = true;
    _song.play();
    $("#recordButt").text("pause");
  }

  // ATTACH RESTART FUNCTIONS
  $("#restartButt").on("click", function() {
    restart();
    $("#restartButt").blur();
  })
  $("#restartButt2").on("click", function() {
    restart();
  })

  // RESTARTS THE GAME
  function restart() {
    clearCanvas();
    _song.pause();
    _song.currentTime = 0;
    _keyStrokes = [];
    _onCanvas = [];
    _progress = 0;
    _recording = false;
    _started = false;
    _restarted = true;
    _currSec = 0;
    _currRow = 0;
    $("#recordButt").css("visibility", "visible");
    $("#restartButt2").css("display", "none");
    $("#saveButt").css("display", "none");
    updateProgress();
    getReady();
  }

  // UPDATES PROGRESS BAR VISUALLY
  function updateProgress() {
    $("#progressBar").css("width", _progress + "%");
  }

  // CHECKS IF GAME COMPLETED
  function checkIfCompleted() {
    if (_progress > 102) {
      _recording = false;
      $("#overLayMessage").text("YOU CREATED A LEVEL");
      $("#recordButt").css("visibility", "hidden");
      $("#restartButt2").css("display", "block");
      $("#saveButt").css("display", "block");
      console.log(_keyStrokes.length);
    }
  }

  // ATTACH SAVE FUNCTION TO BUTTON
  $("#saveButt").on("click", function(e) {
    saveGame();
  })

  // SAVES THE ARRAY AND SENDS TO THE BACK END
  function saveGame() {
    _curr.gameArray = createGameArray();
    console.log("current object");
    console.log(_curr.songFile);

    var postParameters = {
        title: _curr.songTitle,
        artist: _curr.artistName,
        length: _curr.songLength,
        mp3File: _curr.songFile.name,
        imgFile: _curr.songImage.name,
        keyStrokes: JSON.stringify(_curr.gameArray)
      };

      $.post("/storesong", postParameters, function(responseJSON){
        $("#overLayMessage").text("SAVED");
        $("#recordButt").css ("visibility", "hidden");
        $("#restartButt").css("visibility", "hidden");
        $("#restartButt2").css("display", "none");
        $("#saveButt").css("display", "none");
        $("#playButt").css("display", "block");
        $("#createButt").css("display", "block");
      })
  }

  // TURNS THE KEYSTROKE ARRAY INTO A GAME ARRAY
  function createGameArray() {
    var array = [];
    var numOfRows = figureNumOfRowsForArray();
    console.log("number of rows:" + numOfRows);

    // create an array filled with falses
    for (var i = 0; i < numOfRows + 1; i++) {
      var ind = getArrayInd(i);
      for (var j = ind; j < _numOfSlots + ind; j++) { // sets everything to false from left key dep on row
        array[j] = false;
      }
    }

    for (var i = 0; i < _keyStrokes.length; i++) {
      var ind = getArrayInd(_keyStrokes[i].secs); // will have a sec of .1, so this finds its row
      console.log("piece in row:" + ind);
      var col = _keyStrokes[i].type; // either 0 - 12
      console.log("and col" + col);
      array[ind + col] = true;
    }

    var counter = 0;
    for (var i = 0; i < array.length; i++) {
      if (array[i]) counter++;
    }

    console.log("have this many trues:" + counter);

    return array;
  }

  // ATTACH FUNCTIONS AFTER SAVING GAME
  $("#createButt").on("click", function() {
    window.location = "/songfactory";
  })

  $("#playButt").on("click", function() {
    window.location = "game.html";
  })

  // CHECKS IF CLICK WAS A HIT
  function recordKeystroke(keycode) {
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

    var obj = newKeyStroke(ind, _currSec);
    _keyStrokes.push(obj);
    _onCanvas.push(obj)
  }

  // GETS TILES ON CANVAS
  function getTiles(callback) {
    for (var i = 0; i < _onCanvas.length; i++) {
      var tile = _onCanvas[i];
      if (getY(tile.secs) > (_canvas.height() - _heightOfRows)) { // removes tile if no longer on screen
        _onCanvas.splice(i, 1);
      } else {
        callback(tile);
      }
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
  function drawCanvas(type) {
    clearCanvas();
    getTiles(function(tile) {
      drawTile(tile);
    })
  }

  // DRAWS A TILE
  function drawTile(tile) {
    var hSlot = tile.type;
    var secs = tile.secs;
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
    var Y = getY(secs);
    _ctx.strokeRect(X, Y, _sizeOfTiles, _sizeOfTiles);
    _ctx.fillRect(X, Y, _sizeOfTiles, _sizeOfTiles);
  }
});
