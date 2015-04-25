<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>Create Level</title>
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,100,700,900' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="css/main.css">
    <script src="js/jquery-2.1.1.js"></script>
    <script src="js/create.js"></script>
  </head>
  <body>
    <img id="bckGndImg" class="backLayer" src="">
    <div class="frontLayer">
      <div class="rightCol">
        <div class="scoreDiv">
          <h4 class="smallMarginBott light">Progress</h4>
          <div class="barOutline">
            <span id="progressBar" class="barInner"></span>
            <label class="barLabel leftBarLabel light">0:00</label>
            <label id="songLength" class="barLabel rightBarLabel light"></label>
          </div>
        </div>

        <div class="scoreDiv">
          <button id="restartButt" class="centeredButt medButt hidden">restart</button>
        </div>

        <div class="scoreDiv">
          <button id="recordButt" class="centeredButt medButt">start</button>
        </div>
      </div>

      <div class="mainDiv">
        <button id="homeButt" class="topLeftButt medButt">home</button>
        <h3 id="songTitleGame">Song Title</h3>
        <h4 id="artistNameGame" class="light"> Artist Name</h4>

        <div id="gameDiv" class="">
          <canvas id="canvas" width="920px" height="650px"></canvas>
          <div id="gameOverLay" class="">
            <h2 id="overLayMessage" class="white">Please Fill Out Below</h2>
            <p id="overLaySubMess" class="light subMessage"></p>
            <div id="overLayForm">
              <label>mp3 file</label>
              <input type="file" accept="audio/*" id="songFileInput" class="overLayInput"></input>
              <label>song title</label>
              <input type="text" id="songTitleInput" class="overLayInput"></input>
              <label>artist name</label>
              <input type="text" id="artistNameInput" class="overLayInput"></input>
              <label>image for the song</label>
              <input type="file" accept="image/*" id="songImageInput" class="overLayInput"></input>
            </div>
            <button id="recordButt2" class="centeredButt medButt gameDivButt">start</button>
            <button id="restartButt2" class="centeredButt medButt gameDivButt gone">restart</button>
            <button id="saveButt" class="centeredButt medButt gameDivButt gone">save level</button>
            <button id="playButt" class="centeredButt medButt gameDivButt gone">play it!</button>
            <button id="createButt" class="centeredButt medButt gameDivButt gone">create another</button>
          </div>
          <div id="zeroSlot" class="slot createSlot redSlot"><p>A</p></div>
          <div id="oneSlot" class="slot createSlot pinkSlot"><p>S</p></div>
          <div id="twoSlot" class="slot createSlot purpSlot"><p>W</p></div>
          <div id="threeSlot" class="slot createSlot blueSlot"><p>D</p></div>
          <div id="fourSlot" class="slot createSlot greenSlot"><p>E</p></div>
          <div id="fiveSlot" class="slot createSlot yellSlot"><p>F</p></div>
          <div id="sixSlot" class="slot createSlot orangeSlot"><p>SP</p></div>
          <div id="sevenSlot" class="slot createSlot yellSlot"><p>J</p></div>
          <div id="eightSlot" class="slot createSlot greenSlot"><p>I</p></div>
          <div id="nineSlot" class="slot createSlot blueSlot"><p>K</p></div>
          <div id="tenSlot" class="slot createSlot purpSlot"><p>O</p></div>
          <div id="elevenSlot" class="slot createSlot pinkSlot"><p>L</p></div>
          <div id="twelveSlot" class="slot createSlot redSlot"><p>;</p></div>
        </div>
      </div>
    </div>

    </div>
  </body>
</html>
