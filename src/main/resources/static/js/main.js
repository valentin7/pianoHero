

$(document).ready(function() {
console.log("PIANO HERO");

});


function createSong() {

  songLength = Math.round(Math.random() * 100 + 32);
  maxKeys = 8;
  console.log(songLength);
  var randomKeyStrokes = new Array(songLength);

  for (var i = 0; i < songLength; i ++) {
    //startingIndex = Math.round(Math.random() * 8);
    skipStep = Math.floor(Math.random() * maxKeys);

    console.log("got here");
    randomKeyStrokes[i] = new Array(maxKeys);
    randomKeyStrokes[i][skipStep] = true;

    for (var j = 1; j < maxKeys; j++) {
      if (j != skipStep){
        randomKeyStrokes[i][j] = false;
      }
    }
    /*for (var strokeIndex = 0; strokeIndex < maxKeys; strokeIndex+=skipStep) {
      randomKeyStrokes[i][strokeIndex] = true;
    }*/
  }

  console.log("created random song strokes:");
  console.log(randomKeyStrokes);
}