<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Piano Hero</title>
    <link rel="stylesheet" href="css/jquery-ui.css">
    <link rel="stylesheet" href="css/main.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <script src="js/jquery-1.10.2.js"></script>
    <script src="js/jquery-ui.js"></script>
    <script src="js/main.js"></script>
  </head>


  <body id="body">
    <h1>PIANO HERO</h1>
    <form action="/storesong"
      enctype="multipart/form-data" method="post">
      <p>
      Name of the song:<br>
      <input type="text" name="songTitle" size="30">
      </p>
      <p>
      Upload song image:<br>
      <input type="file" name="songImage" size="40">
      </p>
      <p>
      Upload song mp3:<br>
      <input type="file" name="songMp3" size="40">
      </p>
      <div>
      <input type="submit" value="Send">
      </div>
    </form>

    <button onclick="createSong()">Create a song</button>
  </body>
</html>