//Init area
const canvas = document.getElementById('drawing_area');
const ctx = canvas.getContext('2d');
const input = document.getElementById('input');
const inputCheckbox = document.getElementById('input_checkbox');
const radio1 = document.getElementById('radio-1');
const radio2 = document.getElementById('radio-2');
const radio3 = document.getElementById('radio-3');
const radio4 = document.getElementById('radio-4');
const radio5 = document.getElementById('radio-5');
const radio6 = document.getElementById('radio-6');
const apiUrl = "http://213.183.44.69:80/api?g6="

const pForSets = document.getElementById('p_for_sets');
//Size of canvas
const canvasHeight = canvas.height;
const canvasWidth  = canvas.width;
const canvasRadius = canvasHeight / 3 | 0;
const canvasScaleX = canvasWidth / canvas.getBoundingClientRect().width;
const canvasScaleY = canvasHeight / canvas.getBoundingClientRect().height;
const rect = canvas.getBoundingClientRect();

//Init styles for text to canvas

ctx.translate(0.5, 0.5);
ctx.lineWidth = 1;
ctx.width = 400;
ctx.height = 400;


//Styles for vertexes
defaultVertexStyle = '#000';
selectedVertexStyle = '#0000B2';

//Graph array
let graph = new Array(0);
//Graph array length
let graphArrayLength = null;
//Array of vertexes for drawing them
let vertexes = [];
//Vertex radius
let vertexRadius = 25;
//Current index on vertexes array
let currentVertexIndex = null;
//Draw vertex or edge
let isWeDrawingEdgesRightNow = false;
//Delte edges indexes
let firstVertexIndexToActEdge = null;
let secondVertexIndexToActEdge = null;
//Arrays to GeoDominants
let geoArr = [];

//Functions for graph6 convert to array
//Make input string equals length 6
function brintToTheForm(what) {
    let length = what.length;
    if (length === 6) return what;
    if (length > 6) return what.substring(length - 6);
    while (length++ < 6) what = "0" + what;
    return what;
}

//Int to binary string
function dec2bin(dec) {
    return (dec >>> 0).toString(2);
  }
//String to binary string
function convertStringToBinaryString(str) {
    binStr = "";
    for (let i = 1; i < str.length; i++) {
        binStr = binStr.concat(brintToTheForm(dec2bin(str.charCodeAt(i) - 63)));
    }
    return binStr;
}

//Convert graph6 string to array
function convertStringToMatrix(string) {
    let size = string.charCodeAt(0) - 63;
    graphArrayLength = size;
    graph = new Array(graphArrayLength);
    for (let i = 0; i < graphArrayLength; i++) {
        graph[i] = (new Array(graphArrayLength));
    }
    let pos = 0;
    gString = convertStringToBinaryString(string);
    for (let i = 0; i < graphArrayLength; i++) {
        for (let j = 0; j < i; j++) {
            if (gString.charAt(pos++) === '1') {
                graph[i][j] = true;
				graph[j][i] = true;
            }
        }
    }
}


//Convert array to graph6 str
function convertMatrixToString(matrix) {
    let size = matrix.length;
    let binaryString = '';

    // Создаем битовую строку для рёбер графа
    for (let i = 0; i < size; i++) {
        for (let j = 0; j < i; j++) {
            if (matrix[i][j]) {
                binaryString += '1';
            } else {
                binaryString += '0';
            }
        }
    }

    // Конвертируем битовую строку в строку формата graph6
    let graph6String = String.fromCharCode(size + 63);
    let chunkSize = 6;
    for (let i = 0; i < binaryString.length; i += chunkSize) {
        let chunk = binaryString.substring(i, i + chunkSize);
        while (chunk.length < chunkSize) {
            chunk += '0';
        }
        let charCode = parseInt(chunk, 2) + 63;
        graph6String += String.fromCharCode(charCode);
    }

    return graph6String;
}

//Добавляет вершины в отдельный массив
function addVertexesToArray() {
    let midXCanvas = canvasWidth  / 2;
    let midYCanvas = canvasHeight / 2;
    let deltaAngle = Math.PI / graphArrayLength * 2;
    let angle = deltaAngle;
    for (let i = 0; i < graphArrayLength; i++) {
        let x = midXCanvas + canvasRadius * Math.cos(angle);
        let y = midYCanvas + canvasRadius * Math.sin(angle);
        vertexes.push( {x: x, y: y, radius: vertexRadius, style: defaultVertexStyle} );
        angle += deltaAngle;
    }
}

function clearVertexes() {
    graph = new Array(0);
    graphArrayLength = null;
    vertexes = [];
    currentVertexIndex = null;
}

function clearIndexVertexes() {
    firstVertexIndexToActEdge = null;
    secondVertexIndexToActEdge = null;
}

//Functions for actions on canvas
//Get Mouse position x,y
function getMousePos(canvas, evt) {
    var rect = canvas.getBoundingClientRect(), // abs. size of element
      scaleX = canvas.width / rect.width,    // relationship bitmap vs. element for x
      scaleY = canvas.height / rect.height;  // relationship bitmap vs. element for y
  
    return {
      x: (evt.clientX - rect.left) * scaleX,   // scale mouse coordinates after they have
      y: (evt.clientY - rect.top) * scaleY     // been adjusted to be relative to element
    }
}

//Is mouse in Circle
function isVertexContainsMouse(mouse_pos, vertex) {
    let dist = Math.sqrt(Math.pow(mouse_pos.x - vertex.x, 2) + Math.pow(mouse_pos.y - vertex.y, 2));
    return dist <= vertex.radius;
}

//Actions by mouse button
function getIndexVertexByMouse(ev) {
    let mouse_pos = getMousePos(canvas, ev);
    let index = 0;
    for (let vertex of vertexes) {
        if (vertex !== null && isVertexContainsMouse(mouse_pos, vertex)) {
            currentVertexIndex = index;
            console.log('vertex id = ' + currentVertexIndex);
            break;
        }
        index++;
    }
}

//Move vertex following mouse
function moveVertex(ev) {
    getIndexVertexByMouse(ev);
    if (currentVertexIndex === null) return;
    canvas.onmousemove = function(ev) {
        mouse_pos = getMousePos(canvas, ev);
        vertexes[currentVertexIndex].x = mouse_pos.x;
        vertexes[currentVertexIndex].y = mouse_pos.y;
        vertexes[currentVertexIndex].style = selectedVertexStyle;
        drawGraph();
    }
    canvas.onmouseup = function(ev) {
        if (currentVertexIndex !== null) vertexes[currentVertexIndex].style = defaultVertexStyle;
        canvas.onmousemove = null;
        currentVertexIndex = null;
        drawGraph();
        canvas.onmouseup = null;
        return;
    }
}

//Remove vertex
function deleteVertex(ev) {
    getIndexVertexByMouse(ev);
    if (currentVertexIndex === null) return;
    canvas.onmouseup = function(ev) {
        vertexes[currentVertexIndex] = null;
        for (let i = 0; i < graphArrayLength; i++) {
            graph[currentVertexIndex][i] = false;
        }
        currentVertexIndex = null;
        canvas.onmouseup = null;
        drawGraph();
    }
}

//Add vertex
function addVertex(ev) {
    let mousePos = getMousePos(canvas, ev);
    vertexes.push( {x: mousePos.x, y: mousePos.y, radius: vertexRadius, style: defaultVertexStyle} );
    graph.push(new Array(graphArrayLength));
    graphArrayLength++;
    for (let i = 0; i < graphArrayLength; i++) {
        graph[i].push(false);
    }
    drawGraph();
}

//Delete edge
function makeActionWithEdge(ev, bVal) {
    getIndexVertexByMouse(ev);
    if (currentVertexIndex === null) return;
    
    if (firstVertexIndexToActEdge === null && firstVertexIndexToActEdge != currentVertexIndex) {
        firstVertexIndexToActEdge = currentVertexIndex;
        vertexes[firstVertexIndexToActEdge].style = 'red';
        drawGraph();
        return;
    }

    if (secondVertexIndexToActEdge === null ) {
        secondVertexIndexToActEdge = currentVertexIndex;
    }

    vertexes[firstVertexIndexToActEdge].style = defaultVertexStyle;
    graph[firstVertexIndexToActEdge][secondVertexIndexToActEdge] = bVal;
    graph[secondVertexIndexToActEdge][firstVertexIndexToActEdge] = bVal;
        
    clearIndexVertexes();
        
    drawGraph();
    
}


//Draw section
//Draw circle on canvas
function drawCircle(vertex) {
    ctx.beginPath();
    ctx.strokeStyle = vertex.style;
    ctx.arc(vertex.x, vertex.y, vertex.radius, 0, 2 * Math.PI, false);
    ctx.stroke();
}

//Draw text in circle
function drawText(vertex, index) {
    if (vertex === null) return;
    ctx.font = 'italic 10px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = "middle";
    ctx.fillStyle = 'black';
    ctx.fillText(index, vertex.x, vertex.y);
}

//Draw text in vertexes
function drawTexts() {
    let index = 0;
    for (let vertex of vertexes) {
        drawText(vertex, index);
        index++;
    }
}

//Draw circle with text in
function drawVertex(vertex, index) {
    if (vertex === null) return;
    drawCircle(vertex);
}

//Draw all vertexes
function drawVertexes() {
    let index = 0;
    for (let vertex of vertexes) {
        drawVertex(vertex, index);
        index++;
    }
}

//Draw one line between two points 
function drawLine(first, second) {
    ctx.beginPath();
    ctx.strokeStyle = 'black';
    ctx.moveTo(first.x, first.y);
    ctx.lineTo(second.x, second.y);
    ctx.stroke();
}

//Draw one edge between two vertices
function drawEdge(first, second) {
    if (first === null || second === null) return;
    drawLine(first, second);
}

//Draw all edges
function drawEdges() {
    for (let i = 0; i < graphArrayLength; i++) {
        for (let j = 0; j < i; j++) {
            if (graph[i][j] === true) {
            drawEdge(vertexes[i], vertexes[j]);
        }
        }
    }
}

//Clear what inside vertex
function clearCircleInside() {
    let index = 0;
    for (let vertex of vertexes) {
        if (vertex === null) continue;
        ctx.beginPath();
        ctx.fillStyle = "white";
        ctx.arc(vertex.x, vertex.y, vertex.radius, 0, 2 * Math.PI, false);
        ctx.fill();
        index++;
    }
}


//Draw Graph on canvas
function drawGraph() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawEdges();
    clearCircleInside();
    drawVertexes();
    drawTexts();
}


//bind functions to actions
input.onkeyup = async function(ev) {
        clearVertexes();
        convertStringToMatrix(input.value);
        addVertexesToArray();
        drawGraph();
        //await initGeoSets("http://213.183.44.69:8000/api?g6=" + convertMatrixToString(graph));
}


canvas.onmousedown = function(ev) {
    ev.preventDefault();
    if (ev.button === 0) {
        //Перетаскивание
        moveVertex(ev);
    } else if (ev.button === 1) {
        //Удаление
        if (isWeDrawingEdgesRightNow) {
            makeActionWithEdge(ev, false);
        } else {
            deleteVertex(ev);
        }
    } else {
        //Добавление
        if (isWeDrawingEdgesRightNow) {
            makeActionWithEdge(ev, true);
        } else {
            addVertex(ev);
        }
       
    }
}

canvas.addEventListener('contextmenu', function(ev) {
    ev.preventDefault();
}, false);

//left - false - vertexes
//right - true - edges
inputCheckbox.onchange = function(ev) {
    isWeDrawingEdgesRightNow = inputCheckbox.checked;
    clearIndexVertexes();
}

async function httpGet(url) {
    const response = await fetch(url);
    textFile = await response.arrayBuffer();
    string = new TextDecoder().decode(textFile);
    return string;
}

async function initGeoSets(url) {
    console.log(url);
    const result = await httpGet(url);
    const helper = result.split(';').map(str => str.split(' ').map(Number));
    console.log(result.split(';'));
    geoArr = helper.map(arr => arr.sort((a, b) => a - b));
    for (let i = 0 ; i < 6; i++) {
        if (result.split(';')[i] == '') geoArr[i] = [];
    }
    writeGeoSetsValues();
}

function writeGeoSetsValues() {
    pForSets.innerHTML = "Число геодоминирования: " + geoArr[0].length + "<br>" +
                         "Число связного геодоминирования: " + geoArr[1].length + "<br>" +
                         "Число независимого геодоминирования: " + geoArr[2].length + "<br>" +
                         "Число доминирования: " + geoArr[3].length + "<br>" +
                         "Число связного доминирования: " + geoArr[4].length + "<br>" +
                         "Число независимого доминирования: " + geoArr[5].length + "<br>";
}

function clearVertexStyles() {
    for (let vertex of vertexes) {
        vertex.style = defaultVertexStyle;
    }
}

function drawSets(inputVal) {
    clearVertexStyles();
    let index = 0;
    for (let i = 0 ; i < graphArrayLength && index < geoArr[inputVal].length; i++) {
        if (i === geoArr[inputVal][index]) {
            index++;
            vertexes[i].style = 'red';
        }
    }
    drawGraph();
}

radio1.onchange = async function(ev) {
    await initGeoSets(apiUrl + convertMatrixToString(graph));
    drawSets(0);
}

radio2.onchange = async function(ev) {
    await initGeoSets(apiUrl + convertMatrixToString(graph));
    drawSets(1);
}

radio3.onchange = async function(ev) {
    await initGeoSets(apiUrl + convertMatrixToString(graph));
    drawSets(2);
}

radio4.onchange = async function(ev) {
    await initGeoSets(apiUrl + convertMatrixToString(graph));
    drawSets(3);
}

radio5.onchange = async function(ev) {
    await initGeoSets(apiUrl + convertMatrixToString(graph));
    drawSets(4);
}

radio6.onchange = async function(ev) {
    await initGeoSets(apiUrl + convertMatrixToString(graph));
    drawSets(5);
}


//пропадают цвета после отпуска вершины
