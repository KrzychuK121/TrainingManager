//var odliczanie = setTimeout("function(){  }", 1000);

/*
Legenda
> właściwości
-> metody

-=-=-=-=-=-=-=-=-=-
Potrzebne dane
-=-=-=-=-=-=-=-=-=-

-------------------
Trening:
>nazwa treningu
>tablica ćwiczeń
>ilość ćwiczeń = wielkość tablicy ćwiczeń
>ilość powtorzeń wszystkich ćwiczeń

->wypisanie treningu w postaci listy rozwijanej
->wyświetlenie panelu opcji treningu (w zależności od tego czy jest na czas czy na ilość wykonań będzie zwiększać ilość wykonanych)
->start treningu

-------------------

-------------------
Ćwiczenie:
>nazwa ćwiczenia
>opis ćwiczenia
>tryb = wykonywane przez x czasu / wykonywana ilość powtórzeń
>ilość czasu/powtórzeń

->
-------------------
*/

function StartBlock(obj){
    //Rozwinięcie listy i ustawienie koloru guzika (nagłówka listy) na kolor żółty, znak, że teraz robimy ten trening
    if(obj.trainingNumber < obj.secExcTab.length){
        document.getElementById("b" + obj.trainingNumber).style.backgroundColor = "#FFD700";
        document.getElementById("b" + obj.trainingNumber).setAttribute("class", "accordion-button naglowek-listy");
        document.getElementById("b" + obj.trainingNumber).setAttribute("aria-expanded", "true");
        document.getElementById("id" + obj.trainingNumber).setAttribute("class", "accordion-collapse collapse show");
        //ustawiamy panel kontrolny w zależności od typu treningu, który teraz będzie się wykonywać
        document.getElementById("buttons").innerHTML = obj.secExcTab[obj.trainingNumber].AddOptions();
        
        switch(obj.secExcTab[obj.trainingNumber].mode) {
            case 0: //timer
                document.getElementById("opt1").onclick = function () {
                    obj.PauseOrResume();
                };
                document.getElementById("opt2").onclick = function(){
                    obj.Stop();
                }
                
                obj.RepeatExc();
                break;
            case 1: //amount
                document.getElementById("opt1").onclick = function() {
                    obj.RepeatExc();
                }
                break;
        }
        
        document.getElementById("opt3").onclick = function () {
            obj.Skip();
        }
    } else{
        document.getElementById("buttons").innerHTML = '<button class="col-md-6 opt" id="opt0">START</button>';
        //czyszczenie guzików
        document.getElementById("opt0").onclick = (
            async function(){
                await DoTraining(obj.trainingId);
            }
        );
        document.getElementById("opt1").onclick = null;
        document.getElementById("opt2").onclick = null;
        document.getElementById("opt3").onclick = null;
        
    }
}


async function DoTraining(trainingId){
    const train = new Training(trainingId, 1);
    await train.init();
    //console.log(train);
    train.ShowList();
    StartBlock(train);
}

class Training {
    trainingNumber = null;
    trainingId = null;
    secExcTab = null;

    timer = 0;

    constructor(trainingId, trainingNumber){
        this.trainingNumber = trainingNumber;
        this.trainingId = trainingId;
    }

    async init(){
        this.secExcTab = await this.getSecExcTab(this.trainingId);
    }

    async getSecExcTab(trainingId){
        const secExcTab = [
            new Excersise("", "", 0, 0, 0)
        ]

        const response = await fetch(`/training/api/${trainingId}`);
        if(!response.ok)
            throw new Error("Coś poszło nie tak!");

        const training = await response.json();
        let counter = 0;

        training.exercises.forEach(
            exercise => {
                let howManyReps = [0, 0];
                if(exercise.repetition === 0){
                    const date = new Date('1970-01-01T' + exercise.time);
                    howManyReps[0] = date.getHours();
                    howManyReps[1] = date.getMinutes();
                }else
                    howManyReps[0] = exercise.repetition;

                secExcTab.push(
                    new Excersise(
                        exercise.name,
                        exercise.description,
                        exercise.repetition === 0  ? 0 : 1,
                        howManyReps,
                        ++counter
                    )
                );
            }
        );

        return secExcTab;
    }

    SetTrainingNumber(trainingNumber){
        console.log(this);
        this.trainingNumber = trainingNumber;
    }

    Skip(){
        //console.log(that);
        //ustawianie niewykonania ćwiczenia
        //console.log(that.trainingNumber);
        document.getElementById("b" + this.trainingNumber).style.backgroundColor = "#FF4655";
        document.getElementById("b" + this.trainingNumber).setAttribute("class", "accordion-button naglowek-listy collapsed");
        document.getElementById("b" + this.trainingNumber).setAttribute("aria-expanded", "false");
        document.getElementById("id" + this.trainingNumber).setAttribute("class", "accordion-collapse collapse");
        if(this.timer != 0)
            clearTimeout(this.timer);
        //startowanie kolejnego treningu na liście
        this.trainingNumber++;
        //console.log(this.trainingNumber);
        StartBlock(this);
    }
    
    PauseOrResume(){
        switch(document.getElementById("opt1").innerText){
            case 'WSTRZYMAJ':
                document.getElementById("opt1").innerHTML = 'START';
                clearTimeout(this.timer);
                break;
            case 'START':
                document.getElementById("opt1").innerHTML = 'WSTRZYMAJ';
                this.timer = setTimeout(this.RepeatExc, 1000);
                break;
        }
    }
    
    Stop(){
        clearTimeout(this.timer);
        this.secExcTab[this.trainingNumber].tempAmount[0] = this.secExcTab[this.trainingNumber].amount[0];
        this.secExcTab[this.trainingNumber].tempAmount[1] = this.secExcTab[this.trainingNumber].amount[1];
        StartBlock(this);
    }
    
    ShowList(){
        let panel = this.secExcTab[0].AddControlPanel();
        for(let i = 1; i < this.secExcTab.length; i++){
            panel += this.secExcTab[i].AddList();
        }
        document.getElementById("listaCwiczen").innerHTML =  panel;
    }
    
    //funkcja, która w pętli powtarza ćwiczenia na czas i/lub sprawdza
    RepeatExc(){
        console.log("RepeatExc this: ");
        console.log(this);
        switch(this.secExcTab[this.trainingNumber].mode){
            case 0: //timer
                if(this.secExcTab[this.trainingNumber].tempAmount[0] == 0 && this.secExcTab[this.trainingNumber].tempAmount[1] == 0){
                    this.setAttributes();
                    var zeroMin = ""; //zero ustawiane dla minut
                    var zeroSec = ""; //zero ustawiane dla sekund
                    if(this.secExcTab[this.trainingNumber].amount[0] < 10)
                        zeroMin = "0";
                    if(this.secExcTab[this.trainingNumber].amount[1] < 10)
                        zeroSec = "0";
                    var timeSum = zeroMin + this.secExcTab[this.trainingNumber].amount[0] + ":" + zeroSec + this.secExcTab[this.trainingNumber].amount[1];
                    document.getElementById("b" + this.secExcTab[this.trainingNumber].which).innerHTML = this.secExcTab[this.trainingNumber].name +': '+ timeSum + this.secExcTab[this.trainingNumber].getHowMany();
                    clearTimeout(this.timer);
                    //startowanie kolejnego treningu na liście
                    this.trainingNumber++;
                    StartBlock(this);
                }else{
                    this.secExcTab[this.trainingNumber].DoExcersise();

                    let that = this;
                    this.timer = setTimeout(
                        function(){
                            that.RepeatExc();
                        },
                        1000
                    );
                }
                break;
            case 1: //amount
                if(this.secExcTab[this.trainingNumber].tempAmount[0] != 1)
                   this.secExcTab[this.trainingNumber].DoExcersise();
                else{
                    this.setAttributes();
                    document.getElementById("b" + this.secExcTab[this.trainingNumber].which).innerHTML = this.secExcTab[this.trainingNumber].name +': '+ this.secExcTab[this.trainingNumber].amount[0] + this.secExcTab[this.trainingNumber].getHowMany();
                    
                    //startowanie kolejnego treningu na liście
                    this.trainingNumber++;
                    StartBlock(this);
                }
                break;
        }
        
    }

    setAttributes(){
        document.getElementById("b" + this.trainingNumber).style.backgroundColor = "#9EF01A";
        document.getElementById("b" + this.trainingNumber).setAttribute("class", "accordion-button naglowek-listy collapsed");
        document.getElementById("b" + this.trainingNumber).setAttribute("aria-expanded", "false");
        document.getElementById("id" + this.trainingNumber).setAttribute("class", "accordion-collapse collapse");
    }
    
}

class Excersise {
    name = null;
    desc = null;
    mode = null; //0 - timer | 1 - amount
    //ilość czasu/powtórzeń
    which = null; //ktory to trening z kolei | which == 0 -> AddControlPanel (panel z opcjami typu start/stop etc)
    
    //0 - minutes/how many repeats | 1 - secounds
    amount = [];
    tempAmount = [];

    constructor(name, desc, mode, amount, which) {
        this.name = name;
        this.desc = desc;
        this.mode = mode;
        this.which = which;

        this.amount = new Array(amount.length);
        this.tempAmount = new Array(amount.length);

        for(let i = 0; i < amount.length; i++){
            this.amount[i] = amount[i];
            this.tempAmount[i] = amount[i];
        }
    }

    getHowMany(){
        let toReturn = null;
        switch(this.mode){
            case 0:
                toReturn = "minut";
                break;
            case 1:
                toReturn = "serii";
                break;
        }

        return toReturn;
    }
    
    //wykonanie ćwiczenia
    DoExcersise(){
        switch(this.mode){
            case 0://timer
                if(this.tempAmount[1] == 0){
                    this.tempAmount[0]--; 
                    this.tempAmount[1] = 59;
                }else
                    this.tempAmount[1]--;
                var zeroMin = ""; //zero ustawiane dla minut
                var zeroSec = ""; //zero ustawiane dla sekund
                if(this.tempAmount[0] < 10)
                    zeroMin = "0";
                if(this.tempAmount[1] < 10)
                    zeroSec = "0";
                var timeSum = zeroMin + this.tempAmount[0] + ":" + zeroSec + this.tempAmount[1];
                document.getElementById("b" + this.which).innerHTML = this.name +': '+ timeSum + this.getHowMany();
                break;
            case 1://amount
                this.tempAmount[0]--;
                document.getElementById("b" + this.which).innerHTML = this.name +': '+ this.tempAmount[0] + this.getHowMany();
                break;
        }

    }  
        
    //dodaje guziki do panelu kontrolnego w zależności od trybu
    AddOptions(){
        if(this.mode === 0)
            return '<button class="col-md-6 col-lg-3 opt" id="opt1">WSTRZYMAJ</button><button class="col-md-6 col-lg-3 opt" id="opt2">STOP</button><button class="col-md-6 col-lg-3 opt" id="opt3">POMIŃ</button>';
        else
            return '<button class="col-md-6 col-lg-3 opt" id="opt1">NASTEPNE POWTORZENIE</button><button class="col-md-6 col-lg-3 opt" id="opt3">POMIŃ</button>';
    }

    AddControlPanel(){
        return `
            <div class="accordion-item cialo-listy">
                <h2 class="accordion-header h2" id="ha0"></h2>
                <div 
                    class="accordion-button collapsed naglowek-listy" 
                    data-bs-target="#id0" 
                    aria-expanded="false" 
                    aria-controls="id0"
                >
                    <div id="buttons" class="row justify-content-center w-100"></div>
                </div>
            </div>
            <div id="id0" class="accordion-collapse collapse" aria-labelledby="ha0" data-bs-parent="#listaCwiczen">
                <div class="accordion-body"></div>
            </div>
        </div>`;
    }

    //W zależności od przebiegu zwraca albo opcje kontroli treningu albo listę ćwiczeń
    AddList(){
        let tempAmount = 0;
        if(this.mode === 0){   //oba w przypadku kiedy
            let zeroMin = ""; //zero ustawiane dla minut
            let zeroSec = ""; //zero ustawiane dla sekund
            if(this.amount[0] < 10)
                zeroMin = "0";
            if(this.amount[1] < 10)
                zeroSec = "0";
                tempAmount = zeroMin + this.amount[0] + ":" + zeroSec + this.amount[1];
        }else
            tempAmount = this.amount[0];

        return `
            <div class="accordion-item cialo-listy">
                <h2 class="accordion-header h2" id="ha${this.which}">
                    <button 
                        id="b${this.which}" 
                        class="accordion-button collapsed naglowek-listy" 
                        type="button" 
                        data-bs-toggle="collapse" 
                        data-bs-target="#id${this.which}" 
                        aria-expanded="false" 
                        aria-controls="id${this.which}"
                    >
                        ${this.name}: ${tempAmount} ${this.getHowMany()} 
                    </button>
                </h2>
                <div 
                    id="id${this.which}" 
                    class="accordion-collapse collapse" 
                    aria-labelledby="ha${this.which}" 
                    data-bs-parent="#listaCwiczen"
                >
                    <div class="accordion-body">${this.desc}</div>
                </div>
            </div>`;
    }
}