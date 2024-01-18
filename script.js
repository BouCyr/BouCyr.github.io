
var current = 0;
var timeout;


var exoHistory=[];
var found = window.localStorage.getItem("exoHistory");
if(found){
    exoHistory = JSON.parse(found);
}


function checkSum(answer, inputId){

    if(timeout){
        window.clearTimeout(timeout);
    }
    var input = document.getElementById(inputId);
    var resultGiven = Number.parseInt(input.value);

    if(resultGiven === answer){
        input.classList.add("ok");
        input.disabled = true;

        exoHistory[exoHistory.length-1].done=true;
        window.localStorage.setItem("exoHistory", JSON.stringify(exoHistory));

        nextExo(input);
    }else{
        timeout = window.setTimeout(
            () => {input.value="";},
            5000
        );
        return false;
    }
}

function nextExo(prev){
    var exo = {};
    exo.done=false;
    var roll = Math.random();
    if(roll > 0.65){
        exo.type="missing"
        var left = Math.floor(Math.random()*20);
        var right = 1+ Math.floor(Math.random()*9);
        var sum = left+right;

        exo.left = left;
        exo.sum = sum;
    }else if(roll > 0){
        exo.type="sum"
        var left = Math.floor(Math.random()*30);
        var right = 1+ Math.floor(Math.random()*29);
        if(left > 10) {
            right = 1+Math.floor(Math.random()*9);
        }
        exo.left=left;
        exo.right=right
    }

    exoHistory.push(exo);
    window.localStorage.setItem("exoHistory", JSON.stringify(exoHistory));

    displayExo(exo);
}


function given(value){
    var span = document.createElement("span");
    span.classList.add("given");
    span.appendChild(document.createTextNode(value));
    return span;
}
function operand(value){
    var span = document.createElement("span");
    span.appendChild(document.createTextNode(value));
    return span;
}
function question(id, answer, done=false){
    var input = document.createElement("input");
    //<input placeholder = "?" type="tel" maxLength="2" pattern="[0-9]*" class="question" id="exo1" oninput="checkSum(4,8,'exo1');"/>
    input.placeholder="?";
    input.type="tel";
    input.maxLength=2;
    input.pattern="[0-9]*";
    input.classList.add("question");
    current++;
    input.id=id;
    if(done){
        input.classList.add("ok");
        input.disabled = true;
        input.value = answer;
    }else{
        input.oninput = function(){ checkSum(answer,input.id);};
    }
    return input;
}
function displayExo(exo){

    if(exo.type === "sum"){
        var p = document.createElement("p");
        var left = given(exo.left);
        var add = operand("+");
        var right = given(exo.right);
        var eq = operand("=");
        var input = question("exo"+current,
            exo.right+exo.left,
            exo.done);


        p.appendChild(left);
        p.appendChild(add);
        p.appendChild(right);
        p.appendChild(eq);
        p.appendChild(input);
    }else if(exo.type==="missing"){
        var p = document.createElement("p");
        var left = given(exo.left);
        var add = operand("+");
        var input = question("exo"+current,
            exo.sum-exo.left,
            exo.done);
        var eq = operand("=");
        var sum = given(exo.sum);


        p.appendChild(left);
        p.appendChild(add);
        p.appendChild(input);
        p.appendChild(eq);
        p.appendChild(sum);
    }

    document.getElementById("MAIN").appendChild(p);
    input.focus();
    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' })
}

exoHistory.forEach(exo => displayExo(exo));

