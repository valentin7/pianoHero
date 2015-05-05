<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>Game Play</title>
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,100,700,900' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="css/main.css">
    <script src="js/jquery-2.1.1.js"></script>
    <script src="js/game.js"></script>
  </head>
  <body>
    <img id="bckGndImg" class="backLayer" src="">
    <div class="frontLayer darkerBackground">
      <div class="rightCol">
        <div class="scoreDiv">
          <label class="smallLabel light">High Score: <span id="highScore" class="white"></span></label>
        </div>

        <div class="scoreDiv">
          <h3 class="light">Score:</h3>
          <h1 id="score"></h1>
        </div>

        <div class="scoreDiv">
          <h4 class="smallMarginBott light">Progress</h4>
          <div class="barOutline">
            <span id="progressBar" class="barInner"></span>
            <label class="barLabel leftBarLabel light">0:00</label>
            <label id="songLength" class="barLabel rightBarLabel light"></label>
          </div>
        </div>

        <div class="scoreDiv">
          <h4 class="smallMarginBott light">Life</h4>
          <div class="barOutline">
            <span id="lifeBar" class="barInner lifeGood"></span>
            <label class="barLabel leftBarLabel light">Dead</label>
            <label class="barLabel rightBarLabel light">Perfect</label>
          </div>
        </div>

        <div class="scoreDiv">
          <button id="restartButt" class="centeredButt medButt hidden">restart</button>
        </div>

        <div class="scoreDiv">
          <button id="playButt" class="centeredButt medButt">start</button>
        </div>
      </div>

      <div class="mainDiv">
        <button id="homeButt" class="topLeftButt medButt">home</button>
        <h3 id="songTitleGame"></h3>

        <div id="gameDiv" class="">
          <canvas id="canvas" width="920px" height="650px"></canvas>
          <div id="gameOverLay" class="">
            <h2 id="overLayMessage" class="white">Welcome</h2>
            <p id="overLaySubMess" class="light subMessage">Before starting, click the keys below
              to experience the layout. For the "black keys" (W, E and I, O), use your middle
              and ring fingers.</p>
            <button id="playButt2" class="centeredButt medButt gameDivButt">start</button>
            <button id="restartButt2" class="centeredButt medButt gameDivButt gone">restart</button>
          </div>
          <div id="zeroSlot" class="slot gameSlot redSlot"><p>A</p></div>
          <div id="oneSlot" class="slot gameSlot pinkSlot"><p>S</p></div>
          <div id="twoSlot" class="slot gameSlot purpSlot"><p>W</p></div>
          <div id="threeSlot" class="slot gameSlot blueSlot"><p>D</p></div>
          <div id="fourSlot" class="slot gameSlot greenSlot"><p>E</p></div>
          <div id="fiveSlot" class="slot gameSlot yellSlot"><p>F</p></div>
          <div id="sixSlot" class="slot gameSlot orangeSlot"><p>SP</p></div>
          <div id="sevenSlot" class="slot gameSlot yellSlot"><p>J</p></div>
          <div id="eightSlot" class="slot gameSlot greenSlot"><p>I</p></div>
          <div id="nineSlot" class="slot gameSlot blueSlot"><p>K</p></div>
          <div id="tenSlot" class="slot gameSlot purpSlot"><p>O</p></div>
          <div id="elevenSlot" class="slot gameSlot pinkSlot"><p>L</p></div>
          <div id="twelveSlot" class="slot gameSlot redSlot"><p>;</p></div>
        </div>
      </div>
    </div>
  </body>
</html>
