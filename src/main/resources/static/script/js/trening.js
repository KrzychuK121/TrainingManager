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

function StartBlock(obj) {
    //Rozwinięcie listy i ustawienie koloru guzika (nagłówka listy) na kolor żółty, znak, że teraz robimy ten trening
    if (obj.trainingNumber < obj.secExcTab.length) {
        let currExercise = obj.secExcTab[obj.trainingNumber];
        currExercise.tempRounds--;

        document.getElementById("b" + obj.trainingNumber).style.backgroundColor = "#FFD700";
        document.getElementById("b" + obj.trainingNumber).setAttribute("class", "accordion-button naglowek-listy");
        document.getElementById("b" + obj.trainingNumber).setAttribute("aria-expanded", "true");
        document.getElementById("id" + obj.trainingNumber).setAttribute("class", "accordion-collapse collapse show");
        //ustawiamy panel kontrolny w zależności od typu treningu, który teraz będzie się wykonywać
        document.getElementById("buttons").innerHTML = currExercise.addOptions();

        switch (currExercise.mode) {
            case 0: //timer
                document.getElementById("opt1").onclick = function () {
                    obj.pauseOrResume();
                };
                document.getElementById("opt2").onclick = function () {
                    obj.stop();
                }

                obj.repeatExc();
                break;
            case 1: //amount
                document.getElementById("opt1").onclick = function () {
                    obj.repeatExc();
                }
                break;
        }

        document.getElementById("opt3").onclick = function () {
            obj.skip();
        }
    } else {
        document.getElementById("buttons").innerHTML = '<button class="col-md-6 opt" id="opt0">START</button>';
        //czyszczenie guzików
        document.getElementById("opt0").onclick = (
            async function () {
                await DoTraining(obj.trainingId);
            }
        );
        document.getElementById("opt1").onclick = null;
        document.getElementById("opt2").onclick = null;
        document.getElementById("opt3").onclick = null;

    }
}


async function DoTraining(trainingId) {
    const train = new Training(trainingId, 1);
    train.init().then(
        () => {
            // TODO: Repair this so when the exception occurs, next methods wont  do anything
            //console.log(train);
            train.showList();
            StartBlock(train);
        }
    ).catch(
        (error) => {
            console.log(error);
        }
    );


}

class Training {
    trainingNumber = null;
    trainingId = null;
    secExcTab = null;

    timer = 0;

    constructor(trainingId, trainingNumber) {
        this.trainingNumber = trainingNumber;
        this.trainingId = trainingId;
    }

    async init() {
        this.secExcTab = await this.getSecExcTab(this.trainingId);
    }

    async getSecExcTab(trainingId) {
        const secExcTab = [
            new Excersise("", "", 0, 0, 0, 0)
        ]

        const response = await fetch(
            `/api/training/${trainingId}`,
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );
        if (!response.ok)
            throw new Error("Coś poszło nie tak!");

        const training = await response.json();
        let counter = 0;

        training.exercises.forEach(
            exercise => {
                let howManyReps = [0, 0];
                if (exercise.repetition === 0) {
                    const date = new Date('1970-01-01T' + exercise.time);
                    howManyReps[0] = date.getMinutes();
                    howManyReps[1] = date.getSeconds();
                } else
                    howManyReps[0] = exercise.repetition;

                secExcTab.push(
                    new Excersise(
                        exercise.name,
                        exercise.description,
                        exercise.repetition === 0 ? 0 : 1,
                        exercise.rounds,
                        howManyReps,
                        ++counter
                    )
                );
            }
        );

        return secExcTab;
    }

    skip() {
        //console.log(that);
        //ustawianie niewykonania ćwiczenia
        //console.log(that.trainingNumber);
        document.getElementById("b" + this.trainingNumber).style.backgroundColor = "#FF4655";
        document.getElementById("b" + this.trainingNumber).setAttribute("class", "accordion-button naglowek-listy collapsed");
        document.getElementById("b" + this.trainingNumber).setAttribute("aria-expanded", "false");
        document.getElementById("id" + this.trainingNumber).setAttribute("class", "accordion-collapse collapse");
        if (this.timer != 0)
            clearTimeout(this.timer);
        //startowanie kolejnego treningu na liście
        this.trainingNumber++;
        //console.log(this.trainingNumber);
        StartBlock(this);
    }

    pauseOrResume() {
        switch (document.getElementById("opt1").innerText) {
            case 'WSTRZYMAJ':
                document.getElementById("opt1").innerHTML = 'START';
                clearTimeout(this.timer);
                break;
            case 'START':
                document.getElementById("opt1").innerHTML = 'WSTRZYMAJ';
                let that = this;
                this.setTimer(that);
                break;
        }
    }

    stop() {
        clearTimeout(this.timer);
        this.secExcTab[this.trainingNumber].tempAmount[0] = this.secExcTab[this.trainingNumber].amount[0];
        this.secExcTab[this.trainingNumber].tempAmount[1] = this.secExcTab[this.trainingNumber].amount[1];
        StartBlock(this);
    }

    showList() {
        let panel = this.secExcTab[0].addControlPanel();
        for (let i = 1; i < this.secExcTab.length; i++) {
            panel += this.secExcTab[i].addList();
        }
        document.getElementById("listaCwiczen").innerHTML = panel;
    }

    //funkcja, która w pętli powtarza ćwiczenia na czas i/lub sprawdza
    repeatExc() {
        let currentExercise = this.secExcTab[this.trainingNumber];
        switch (currentExercise.mode) {
            case 0: //timer
                if (
                    currentExercise.tempAmount[0] === 0 &&
                    currentExercise.tempAmount[1] === 0 &&
                    currentExercise.tempRounds === 0
                ) {
                    this.setAttributes();
                    var zeroMin = ""; //zero ustawiane dla minut
                    var zeroSec = ""; //zero ustawiane dla sekund
                    if (currentExercise.amount[0] < 10)
                        zeroMin = "0";
                    if (currentExercise.amount[1] < 10)
                        zeroSec = "0";
                    var timeSum = zeroMin + currentExercise.amount[0] + ":" + zeroSec + currentExercise.amount[1];
                    document.getElementById(
                        "b" + currentExercise.which
                    ).innerHTML = currentExercise.getExerciseCountSummary(currentExercise.rounds, timeSum);
                    clearTimeout(this.timer);
                    //startowanie kolejnego treningu na liście
                    this.trainingNumber++;
                    StartBlock(this);
                } else {
                    currentExercise.doExcersise();

                    let that = this;
                    this.setTimer(that);
                }
                break;
            case 1: //amount

                if (currentExercise.tempAmount[0] !== 1 || currentExercise.tempRounds !== 0)
                    currentExercise.doExcersise();
                else {
                    this.setAttributes();
                    document.getElementById(
                        "b" + currentExercise.which
                    ).innerHTML = currentExercise.getExerciseCountSummary(
                        currentExercise.rounds,
                        currentExercise.amount[0]
                    );

                    //startowanie kolejnego treningu na liście
                    this.trainingNumber++;
                    StartBlock(this);
                }
                break;
        }

    }

    setTimer(that) {
        this.timer = setTimeout(
            function () {
                that.repeatExc();
            },
            1000
        );
    }

    setAttributes() {
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

    //ilość serii
    rounds = null;
    tempRounds = null;
    //0 - minutes/how many repeats | 1 - secounds
    amount = [];
    tempAmount = [];

    constructor(name, desc, mode, rounds, amount, which) {
        this.name = name;
        this.desc = desc;
        this.mode = mode;
        this.which = which;

        this.rounds = rounds;
        this.tempRounds = rounds;
        this.amount = new Array(amount.length);
        this.tempAmount = new Array(amount.length);

        for (let i = 0; i < amount.length; i++) {
            this.amount[i] = amount[i];
            this.tempAmount[i] = amount[i];
        }
    }

    getHowMany() {
        let toReturn = null;
        switch (this.mode) {
            case 0:
                toReturn = "minut";
                break;
            case 1:
                toReturn = "powtórzeń";
                break;
        }

        return toReturn;
    }

    getExerciseCountSummary(roundsToDisplay, counter) {
        return this.name + ': ' + roundsToDisplay + ' serii, ' + counter + this.getHowMany();
    }

    //wykonanie ćwiczenia
    doExcersise() {
        switch (this.mode) {
            case 0://timer
                if (
                    this.tempAmount[0] === 0 &&
                    this.tempAmount[1] === 0 &&
                    this.tempRounds !== 0
                ) {
                    this.tempAmount[0] = this.amount[0];
                    this.tempAmount[1] = this.amount[1];
                    this.tempRounds--;
                }

                if (this.tempAmount[1] === 0) {
                    this.tempAmount[0]--;
                    this.tempAmount[1] = 59;
                } else
                    this.tempAmount[1]--;
                var zeroMin = ""; //zero ustawiane dla minut
                var zeroSec = ""; //zero ustawiane dla sekund
                if (this.tempAmount[0] < 10)
                    zeroMin = "0";
                if (this.tempAmount[1] < 10)
                    zeroSec = "0";
                var timeSum = zeroMin + this.tempAmount[0] + ":" + zeroSec + this.tempAmount[1];
                document.getElementById("b" + this.which).innerHTML = this.getExerciseCountSummary(
                    this.tempRounds, timeSum
                );
                break;
            case 1://amount
                if (
                    this.tempAmount[0] === 0 &&
                    this.tempRounds !== 0
                ) {
                    this.tempAmount[0] = this.amount[0];
                    this.tempRounds--;
                }
                this.tempAmount[0]--;
                document.getElementById("b" + this.which).innerHTML = this.getExerciseCountSummary(
                    this.tempRounds, this.tempAmount[0]
                );
                break;
        }

    }

    //dodaje guziki do panelu kontrolnego w zależności od trybu
    addOptions() {
        if (this.mode === 0)
            return '<button class="col-md-6 col-lg-3 opt" id="opt1">WSTRZYMAJ</button><button class="col-md-6 col-lg-3 opt" id="opt2">STOP</button><button class="col-md-6 col-lg-3 opt" id="opt3">POMIŃ</button>';
        else
            return '<button class="col-md-6 col-lg-3 opt" id="opt1">NASTEPNE POWTORZENIE</button><button class="col-md-6 col-lg-3 opt" id="opt3">POMIŃ</button>';
    }

    addControlPanel() {
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
    addList() {
        let tempAmount = 0;
        if (this.mode === 0) {   //oba w przypadku kiedy
            let zeroMin = ""; //zero ustawiane dla minut
            let zeroSec = ""; //zero ustawiane dla sekund
            if (this.amount[0] < 10)
                zeroMin = "0";
            if (this.amount[1] < 10)
                zeroSec = "0";
            tempAmount = zeroMin + this.amount[0] + ":" + zeroSec + this.amount[1];
        } else
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
                        ${this.getExerciseCountSummary(this.rounds, tempAmount)}
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