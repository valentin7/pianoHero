$(document).ready(function() {
console.log("PIANO HERO");



});


function createSong() {

  songLength = Math.round(Math.random() * 300 + 20);
  maxKeys = 8;
  console.log(songLength);
  var randomKeyStrokes = new Array(songLength);

  for (var i = 0; i < songLength; i ++) {
    startingIndex = Math.random() * 8;
    skipStep = Math.round(Math.random() * maxKeys);

    randomKeyStrokes[i] = new Array();
    for (var strokeIndex = startingIndex; strokeIndex < maxKeys; strokeIndex+=skipStep) {
      randomKeyStrokes[i][strokeIndex] = true;
    }
  }

  console.log("created random song strokes:");
  console.log(randomKeyStrokes);
}