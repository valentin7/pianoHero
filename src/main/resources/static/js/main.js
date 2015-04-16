
var tilesCache = {};
var trafficCache = {};
var requestingCache = {};

var nearestNeighborsQueue = [];

const TILE_SIZE = 0.001;

//var NW = {lat: 41.826772 + TILE_SIZE*2, lng: -71.402548 - TILE_SIZE*2};
//var SE = {lat: 41.826772 - TILE_SIZE*2, lng: -71.402548 + TILE_SIZE*2};
//var NW = {lat: 41.823, lng: -71.396};
//var SE = {lat: 41.819, lng: -71.283};

//var NW = {lat: 41.831 + TILE_SIZE, lng: -71.406 - TILE_SIZE};
//var SE = {lat: 41.824 - TILE_SIZE, lng: -71.400 + TILE_SIZE};

var NW = latLng(41.831 - TILE_SIZE*3, -71.406 + TILE_SIZE*3);
var SE = latLng(41.824, -71.400);
var userLatLng = undefined;


//for smallMaps testing
//NW = {lat: 41.8207, lng: -71.411};
//SE = {lat: 41.819, lng: -71.39};

var mouseLocation = xyPos(0, 0);
var zoomLevel = 1;
var maxZoomLevel = 1.016;
var tilesWithNoEdges = {};

const CANVAS_SCALE = 1;
const STREET_COLOR = "rgb(23, 185 ,254)";//"white";
var mapsCanvas;
var canvasWidth;
var canvasHeight;
var ctx;
var updatingTiles = false;

var ratioFactor;
var trafficInterval;
var updatingTraffic = true;

var currentPath;
var showedPathNotFoundAlert = false;

var drawingText = true;

var paintingWithTraffic = false;

function xyPos(x, y) {
  return {
    x: x,
    y: y
  }
}

function latLng(lat, lng) {
  return {
    lat: lat,
    lng: lng
  }
}

function convert(node) {  
  latlng = latLng(getLat(node), getLng(node));
 
  return convertToXYPos(latlng);
}

function convertToLatLng(xyPos) {
  var lat  = xyPos.y * ((SE.lat - NW.lat)/canvasHeight) + NW.lat;
  var lng  = xyPos.x * ((SE.lng - NW.lng)/canvasWidth) + NW.lng;

  return latLng(lat, lng);
}

function convertToXYPos(latLng) {
  var y = (latLng.lat - NW.lat) / ((SE.lat - NW.lat)/canvasHeight);
  var x =  (latLng.lng - NW.lng) / ((SE.lng - NW.lng)/canvasWidth);
  return xyPos(x, y);
}


function getOrDefault(key) {
  trafficValue = trafficCache[key];
  if (trafficValue == undefined) {
    return 1;
  } else {
    return trafficValue;
  }
}

$(document).ready(function() {
  mapsCanvas = document.getElementById("mapsCanvas");
  //document.getElementById("body").style.backgroundColor  = "rgb(191, 219, 151)";
  mapsCanvas.style.backgroundColor = "rgb(0, 0, 10)";///"rgb(228, 223, 211)"; //"rgb(191, 219, 151)";//

  scaledWidth = window.innerWidth*CANVAS_SCALE;
  scaledHeight = window.innerHeight*CANVAS_SCALE; 
  mapsCanvas.width  = scaledWidth;
  mapsCanvas.height = scaledHeight;

  ratioFactor = Math.floor(scaledWidth/scaledHeight + 1);
  SE = latLng(NW.lat - TILE_SIZE*6/ratioFactor, NW.lng + TILE_SIZE*6);

  var conversion = 1*scaledWidth/scaledHeight;
  //SE = latLng(NW.lat - TILE_SIZE*10, NW.lng + TILE_SIZE*10*conversion);


  canvasWidth = mapsCanvas.width;
  canvasHeight = mapsCanvas.height;
  ctx = mapsCanvas.getContext("2d");
  ctx.strokeStyle = STREET_COLOR;
  
  loadAllTraffic();

  getLocation();

  updateTiles();


  trafficInterval = setInterval(updateTraffic, 1000);

  setInterval(function() {
    paintingWithTraffic = true;
    updateTiles();
    }, 250);
});

$(window).resize(function(){
  scaledWidth = window.innerWidth*CANVAS_SCALE;
  scaledHeight = window.innerHeight*CANVAS_SCALE; 
    console.log("resizing window to:" + scaledWidth);
  mapsCanvas.width  = scaledWidth;
  mapsCanvas.height = scaledHeight;
  ratioFactor = Math.floor(scaledWidth/scaledHeight + 1);

});


function toggleTrafficUpdates() {
  updatingTraffic = !updatingTraffic;

  if (!updatingTraffic) {
    clearInterval(trafficInterval);
  } else {
      trafficInterval = setInterval(updateTraffic, 1000);
  }
}

function toggleStreetNames() {
  drawingText = !drawingText;
  updateTiles();
}

function addToTrafficCache(newData) {
  for(var id in newData) {
    trafficCache[id] = newData[id];
  }
}

function updateTraffic() {
  console.log("updating traffic");
  $.post("/traffic", {getAll: "false"}, 
    function(responseJSON) {
        responseObject = JSON.parse(responseJSON);
        addToTrafficCache(responseObject);
    });
}

function loadAllTraffic() {
  console.log("updating traffic");
  $.post("/traffic", {getAll: "true"}, 
    function(responseJSON) {
        responseObject = JSON.parse(responseJSON);
        addToTrafficCache(responseObject);
    });
}

var change = 0;

function updateTiles() {
  updatingTiles = true;
  // Clear the entire canvas
  ctx.clearRect(0, 0, canvasWidth, canvasHeight);
  ctx.lineWidth = 1;
  ctx.beginPath();

  for (var i = NW.lat; i > SE.lat; i -= TILE_SIZE) {
    for (var j = NW.lng; j < SE.lng; j += TILE_SIZE) {

      newLatLng = snapToUpLeft(latLng(i, j));
      seLatLng = snapToUpLeft(latLng(i - TILE_SIZE*2, j + TILE_SIZE*2));

      tileId = newLatLng.lat + "_" + newLatLng.lng;
      var tile = tilesCache[tileId];

      middleOfTileLatLng = latLng(newLatLng.lat + TILE_SIZE/2, newLatLng.lng + TILE_SIZE/2);
      if (tile == undefined) {
        tilePostRequest(tileId);
        } else {
          //console.log("FOUND ON CACHE!!");
          if (tile[0] == undefined) {
            tilesWithNoEdges[tileId] = {nw: newLatLng, se: seLatLng};
            console.log("FROM CACHE: edge in tile is undefined");
          }
          paintTile(tile);
        }
    }
  }
  paintPath(currentPath);
  paintNearestNeighbors();
  drawUserLocation();
  updatingTiles = false;
  paintingWithTraffic = false;
}

function tilePostRequest(tileId) {
  $.post("/tiles", {northLat: newLatLng.lat,
                    westLng: newLatLng.lng,
                    eastLng: seLatLng.lng,
                    southLat: seLatLng.lat}, 
    function(responseJSON) {
      newTile = JSON.parse(responseJSON);
      tilesCache[tileId] = newTile;
      if (newTile[0] == undefined) {
        tilesWithNoEdges[tileId] = {nw: newLatLng, se: seLatLng};
        console.log("edge in tile is undefined");
      } 
      paintTile(newTile);
    });
}

function paintTile(tile) {
  if (paintingWithTraffic && updatingTraffic && zoomLevel <= 1.003) {
    paintTileWithTraffic(tile);
  } else {
    ctx.beginPath();
    for (var j = 0; j < tile.length; j++) {
      p1 = convert(tile[j].from);     
      p2 = convert(tile[j].to);

      ctx.moveTo(p1.x, p1.y);
      ctx.lineTo(p2.x, p2.y);
      
      if (drawingText && tile[j].type.length > 1){
        drawText(tile[j].name, p1, p2);
      }
    }
    ctx.stroke();
    resetCtxToDefault();
  }

}

function paintTileWithTraffic(tile) {
  //console.log(trafficCache);
  for (var j = 0; j < tile.length; j++) {
    trafficValue = getOrDefault(tile[j].id);
    var color = STREET_COLOR;
    if (trafficValue > 2) {
      color = "#ffb2b2";
    }
    if (trafficValue > 4) {
      color = "#ff7f7f";
    }
    if (trafficValue > 6) {
      color = "#ff4c4c";
    }
    if (trafficValue > 8) {
      color = "#ff1919";
    }
    ctx.beginPath();
    ctx.strokeStyle = color;
    //console.log(color);
    //console.log(ctx.strokeStyle);

    p1 = convert(tile[j].from);     
    p2 = convert(tile[j].to);

    ctx.moveTo(p1.x, p1.y);
    ctx.lineTo(p2.x, p2.y);

    ctx.stroke();
    
    if (drawingText && tile[j].type.length > 1){
      drawText(tile[j].name, p1, p2);
    }
  }
}


function resetCtxToDefault() {
  ctx.font = "9pt Calibri";
  ctx.strokeStyle = STREET_COLOR;
  ctx.lineWidth = 1;
}

function snapToUpLeft(latLng) {
  var newLat = Math.floor((latLng.lat + TILE_SIZE) / TILE_SIZE) * TILE_SIZE;
  var newLng = Math.floor(latLng.lng / TILE_SIZE) * TILE_SIZE;
 return {lat: newLat, lng: newLng};
}

function addNeighborToQueue(neighbor) {
  nearestNeighborsQueue.unshift(neighbor);
  while (nearestNeighborsQueue.length > 2) {
    nearestNeighborsQueue.pop();
  }
}

function nearestNeighbor(point) {
  $.post("/NearestNeighbors", point, function(responseJSON) {
    responseObject = JSON.parse(responseJSON);
    addNeighborToQueue(responseObject);

    updateTiles();
  });
}

function paintNearestNeighbors() {
  if (nearestNeighborsQueue[0] != undefined) {
    paintCircle(convert(nearestNeighborsQueue[0]), 10, "red");
  }
  if (nearestNeighborsQueue[1] != undefined) {
    paintCircle(convert(nearestNeighborsQueue[1]), 10, "green");
  }
}

function shortestPathNearestNeighbors() {
  if (nearestNeighborsQueue.length == 2) {
    startNodeJson = JSON.stringify(nearestNeighborsQueue[0]);
    endNodeJson = JSON.stringify(nearestNeighborsQueue[1]);
    clearPath();

    $.post("/path", {type: "nodes", startNode: startNodeJson, endNode: endNodeJson}, function(responseJSON){
      responseObject = JSON.parse(responseJSON);
      currentPath = null;
      currentPath = responseObject;
      paintPath(responseObject);
    });
  } else {
    alert("Need target!");
  }
}

function shortestPathIntersections(startStreet, startCrossStreet, endStreet, endCrossStreet) {
  clearPath();
  $.post("/path", {type: "intersections", startStreet: startStreet, startCrossStreet: startCrossStreet, endStreet: endStreet, endCrossStreet: endCrossStreet}, function(responseJSON) {
    responseObject = JSON.parse(responseJSON);
    currentPath = null;
    currentPath = responseObject;
    paintPath(responseObject);
  });
}

function drawText(text, xyFrom, xyTo) {
  if (zoomLevel >= 1.006) {
    return;
  }
  ctx.save();
  ctx.fillStyle = "white";//"#696969";
  ctx.translate(xyFrom.x, xyFrom.y);
  ctx.rotate(Math.atan((xyTo.y - xyFrom.y)/(xyTo.x - xyFrom.x)));
  ctx.textAlign = "center";
  ctx.fillText(text, xyFrom.x, 0);
  ctx.restore();
}


function paintCircle(xyPos, r, color) {
  ctx.beginPath();
  ctx.arc(xyPos.x,xyPos.y,r,0,2*Math.PI);
  ctx.fillStyle = color;
  ctx.fill();
  ctx.lineWidth = 2;
  ctx.stroke();
}

function paintCircleNoStroke(xyPos, r, color) {
  ctx.beginPath();
  ctx.arc(xyPos.x,xyPos.y,r,0,2*Math.PI);
  ctx.fillStyle = color;
  ctx.lineWidth = 2;
  ctx.fill();
  resetCtxToDefault();
}

function clickedSubmit() {
  startStreet = $("#startStreet").val();
  startCrossStreet = $("#startCrossStreet").val();
  endStreet = $("#endStreet").val();
  endCrossStreet = $("#endCrossStreet").val();
  showedPathNotFoundAlert = false;
  shortestPathIntersections(startStreet, startCrossStreet, endStreet, endCrossStreet);
}



function paintPath(path) {
  if (path == null || path == undefined) {
    return;
  }
  if (path.length > 0) {
    ctx.beginPath();
    ctx.strokeStyle="white";
    ctx.lineJoin="round";
    ctx.lineWidth = 7;

    var xyPos = convert(path[0]);

    ctx.moveTo(xyPos.x, xyPos.y);


    for (var i = 1; i < path.length; i++) {
      xyPos = convert(path[i]);
      ctx.lineTo(xyPos.x, xyPos.y);
    }

    ctx.stroke();
    paintCircle(xyPos, 7, "green");
    paintCircle(convert(path[0]), 7, "red");

  } else {
    console.log("PATH ALERT SHOWN: " +showedPathNotFoundAlert);
    if (!showedPathNotFoundAlert) {
      alert("Path not found! Have you tried switching St with Street or viceversa?");
    }
    showedPathNotFoundAlert = true;
  }
  ctx.strokeStyle = STREET_COLOR;
  ctx.lineWidth = 1;
}

function clearPath() {
  currentPath = null;
  updateTiles();
}

function getLat(node) {
  return node.location.location[0];
}

function getLng(node) {
  return node.location.location[1];
}





/**         **
 ** PANNING **
 **         **/
var isDown = false;
var startCoords = {x: 0, y: 0};
var last = {x: 0, y: 0};
var startLatLng = {lat:0, lng:0};
var lastLatLng = {lat:0, lng:0};
var countsAsClick = false;

$(document).ready(function() {
  mapsCanvas.addEventListener('mousemove', mouseMoved);
  mapsCanvas.addEventListener('mousedown', mouseDown);
  mapsCanvas.addEventListener('mouseup', mouseUp);
});

function xyPosOfMouse(e) {
  return xyPos(e.x - mapsCanvas.offsetLeft, e.y - mapsCanvas.offsetTop);
}


var deltaPanCounter = 0;

function mouseMoved(e) {

  if (!isDown) {
    return;
  } 
  countsAsClick = false;
  normalizer = 1;
  xyMouse = xyPosOfMouse(e);
  latLngMouse = convertToLatLng(xyMouse);

  xDelta = (latLngMouse.lng - startLatLng.lng)*zoomLevel;
  yDelta = -(latLngMouse.lat - startLatLng.lat)*zoomLevel;

  deltaPanCounter += xDelta*xDelta + yDelta*yDelta;

  NW.lat += yDelta; 
  NW.lng -= xDelta;

  SE.lat += yDelta;
  SE.lng -= xDelta;

  if (xDelta/TILE_SIZE > 5 || xDelta/TILE_SIZE < 5 || yDelta/TILE_SIZE > 5 || yDelta/TILE_SIZE < 5) {
    updateTiles();
  }
}

function mouseDown(e) {
  isDown = true;

  offSetLatLng = convertToLatLng(xyPosOfMouse(e));
  startLatLng.lng = offSetLatLng.lng - lastLatLng.lng;
  startLatLng.lat = offSetLatLng.lat - lastLatLng.lat;
  countsAsClick = true;
}

function mouseUp(e) {
  isDown = false;

  xyMouse = xyPosOfMouse(e);

  offSetLatLng = convertToLatLng(xyMouse);

  if (countsAsClick) {
    nearestNeighbor(offSetLatLng);
  }
}


/**         **
 ** ZOOMING **
 **         **/          

$(document).ready(function() {

  var map = {};
  
  if (mapsCanvas.addEventListener) {
    //mousewheel is for chrome and others
    mapsCanvas.addEventListener("mousewheel", MouseWheelHandler, false);
    //DOMMOUSE is for Firefox
    mapsCanvas.addEventListener("DOMMouseScroll", MouseWheelHandler, false);
  }
  else {
    // onmousewheel is for IE.. 
    mapsCanvas.attachEvent("onmousewheel", MouseWheelHandler);
  }

var deltaCounter = 0;
var zoomOnAlert;
var showedZoomAlert = false;

  function MouseWheelHandler(e) {
    // cross-browser wheel delta
    var e = window.event || e;
    var delta = -Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail)))/1000;
    deltaCounter +=delta;
    if (zoomLevel >= maxZoomLevel && !showedZoomAlert) {
      showedZoomAlert = true;
      alert("Hey, you've reached the maximum zoom level. We want to maintain a pleasant experience, so this is the max zoom out. Try zooming in.");
      zoomOnAlert = zoomLevel;
    }
    if (deltaCounter >= 1/500 || deltaCounter <= -1/500) {

      if (delta<=0 || zoomLevel<maxZoomLevel) {
        NW.lat += delta/ratioFactor; 
        NW.lng -= delta;
        SE.lat -= delta/ratioFactor;
        SE.lng += delta;
        updateTiles();
        zoomLevel += delta;///TILE_SIZE;
        deltaCounter = 0;
        showedZoomAlert = false;
      }
    }
    console.log("CURRENT ZOOM LEVEL: " + zoomLevel); 
   
    return false;
  }
});

/** GEOLOCATION **/
var x = $("#textHolder");
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else {
      //alert("Geolocation is not supported by this browser.");
      x.innerHTML = "Geolocation is not supported by this browser.";
    }
}
function showPosition(position) {
  positionString =  "Latitude: " + position.coords.latitude + 
    "<br>Longitude: " + position.coords.longitude; 
  userLatLng = latLng(position.coords.latitude, position.coords.longitude);
  x.innerHTML = positionString;
  updateTiles();
}

function drawUserLocation() {
  var userPosition = xyPos(canvasWidth/2, canvasHeight/2); 
  var text = "Searching for your location...";
  if (userLatLng != undefined) {
    userPosition = convertToXYPos(userLatLng);
    text = "You're here.";
  }

  ctx.fillStyle = "orange";
  ctx.font = 15*zoomLevel+"pt Calibri";
  ctx.textAlign = "center";
  ctx.fillText(text, userPosition.x, userPosition.y);

  resetCtxToDefault();
}

function goToMyLocation() {
  if (userLatLng != undefined) {
    newZoom = TILE_SIZE * 5;
    NW = latLng(userLatLng.lat + newZoom/ratioFactor, userLatLng.lng - newZoom);
    SE = latLng(userLatLng.lat - newZoom/ratioFactor, userLatLng.lng + newZoom);
    updateTiles();
  }
}


/*******************************
 ** AUTOCORRECT STUFF **********
 *******************************/

 $("#startStreet").bind('keyup', function(e) {
  console.log("startStreet");
});

var testArray = ["street", "street2", "stretch"];

$(document).on('keyup', '#startStreet', function (e){
  $("#startStreet").autocomplete({
    source: function( request, response ) {
      $.post("/autocorrect", {location: request.term}, function(responseJSON){
        responseObject = JSON.parse(responseJSON);  
        response(responseObject);
      });
    }
  });
});

$(document).on('keyup', '#startCrossStreet', function (e){
  $("#startCrossStreet").autocomplete({
    source: function( request, response ) {
      $.post("/autocorrect", {location: request.term}, function(responseJSON){
        responseObject = JSON.parse(responseJSON);  
        response(responseObject);
      });
    }
  });
});

$(document).on('keyup', '#endStreet', function (e){
  $("#endStreet").autocomplete({
    source: function( request, response ) {
      $.post("/autocorrect", {location: request.term}, function(responseJSON){
        responseObject = JSON.parse(responseJSON);  
        response(responseObject);
      });
    }
  });
});

$(document).on('keyup', '#endCrossStreet', function (e){
  $("#endCrossStreet").autocomplete({
    source: function( request, response ) {
      $.post("/autocorrect", {location: request.term}, function(responseJSON){
        responseObject = JSON.parse(responseJSON);  
        response(responseObject);
      });
    }
  });
});

