<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>Game Play</title>
    <link rel="stylesheet" href="../css/main.css">
    <script src="../js/jquery-2.1.1.js"></script>
    <script src="../js/game.js"></script>
  </head>
  <body>
    <img id="bckGndImg" class="backLayer" src="">
    <div class="frontLayer">
    	<div class="rightCol">
        <div class="scoreDiv">
          <label class="smallLabel">High Score: <span id="highScore" class="white"></span></label>
        </div>

        <div class="scoreDiv">
          <h3 class="light">Score:</h3>
          <h1 id="score"></h1>
        </div>

        <div class="scoreDiv">
          <h4 class="smallMarginBott light">Progress</h4>
          <div class="barOutline">
            <span id="progressBar" class="barInner"></span>
            <label class="barLabel leftBarLabel">0:00</label>
            <label id="songLength" class="barLabel rightBarLabel"></label>
          </div>
        </div>

        <div class="scoreDiv">
          <h4 class="smallMarginBott light">Life</h4>
          <div class="barOutline">
            <span id="lifeBar" class="barInner lifeGood"></span>
            <label class="barLabel leftBarLabel">Dead</label>
            <label class="barLabel rightBarLabel">Perfect</label>
          </div>
        </div>

        <div class="scoreDiv">
          <button id="restartButt" class="centeredButt medButt">restart</button>
        </div>

        <div class="scoreDiv">
          <button id="playButt" class="centeredButt medButt">start</button>
        </div>
      </div>

      <div class="mainDiv">
        <button id="homeButt" class="topLeftButt medButt">home</button>
        <h3 id="songTitleGame"></h3>

        <div id="gameDiv" class="">
          <canvas width="920px" height="620px"></canvas>
          <div id="gameOverLay" class="">
            <h3 id="overLayMessage" class="white">Hit Start</h3>
          </div>
          <div id="qSlot" class="slot redSlot"><p>Q</p></div>
          <div id="wSlot" class="slot pinkSlot"><p>W</p></div>
          <div id="threeSlot" class="slot purpSlot"><p>3</p></div>
          <div id="eSlot" class="slot blueSlot"><p>E</p></div>
          <div id="fourSlot" class="slot greenSlot"><p>4</p></div>
          <div id="rSlot" class="slot yellSlot"><p>R</p></div>
          <div id="vSlot" class="slot orangeSlot"><p>V</p></div>
          <div id="spSlot" class="slot orangeSlot"><p>SP</p></div>
          <div id="jSlot" class="slot yellSlot"><p>J</p></div>
          <div id="iSlot" class="slot greenSlot"><p>I</p></div>
          <div id="kSlot" class="slot blueSlot"><p>K</p></div>
          <div id="oSlot" class="slot purpSlot"><p>O</p></div>
          <div id="lSlot" class="slot pinkSlot"><p>L</p></div>
          <div id="semiSlot" class="slot redSlot"><p>;</p></div>
        </div>
      </div>
    </div>
  </body>
</html>

