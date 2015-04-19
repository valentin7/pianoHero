
$(document).ready(function() {
console.log("PIANO HERO");
});


function getHighScores() {
 
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


}