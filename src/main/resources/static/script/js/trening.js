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
        
        switch(obj.secExcTab[obj.trainingNumber].mode){
            case 0: //timer
                document.getElementById("opt1").onclick = obj.PauseOrResume;
                document.getElementById("opt2").onclick = obj.Stop;
                
                obj.RepeatExc();
                break;
            case 1: //amount
                document.getElementById("opt1").onclick = obj.RepeatExc;
                break;
        }
        
        document.getElementById("opt3").onclick = obj.Skip;
    } else{
        document.getElementById("buttons").innerHTML = '<button class="col-md-6 opt" id="opt0">START</button>';
        //czyszczenie guzików
        document.getElementById("opt0").onclick = DoTraining;
        document.getElementById("opt1").onclick = null;
        document.getElementById("opt2").onclick = null;
        document.getElementById("opt3").onclick = null;
        
    }
}


function DoTraining(){
    var train = new Training(1);
    console.log(train);
    train.ShowList();
    StartBlock(train);
}

function Training(trainingNumber) {
    this.trainingNumber = trainingNumber;
    this.timer = 0;
    var that = this;
    
    /*[komplet1 = [nazwa, opis, tryb, ilość], komplet2 = [...], ...]*/
    var excTab = [ 
    ["", "", 0, 0], 
        
    ["Zabijanie celów w kółku", "Stój pośrodku botów ustawionych na tryb treningu. Obracaj się dookoła, robiąc jak największe ruchy i zabijaj headshotem kolejne boty, trenując tym samym celność.", 0, [10, 0] ], 
    
    ["Flicki", "Stój blisko botów ustawionych na tryb treningu. Chodź po mapie, skupiając uwagę na jednym bocie. Po chwili przeflickuj szybko na innego bota, którego widzisz kątem oka, trenując tym samym reakcję na nagłych przeciwników. Dla utrudnienia warto chodzić dookoła jednego celu, starając się utrzymać celownik cały czas na jego głowie (przykleić się celownikiem do jego głowy niczym magnes). Pomaga to trenować celność przy ruchliwych celach.", 0, [10, 0] ],
    
    ["Kontrola serii", "Ustaw skończoność amunicji. Stój blisko botów ustawionych na tryb treningu. Spróbuj zabić 3 boty za pomocą jednej serii, trenując tym samym kontrolę fullauto. Zalecane nie celowanie w głowę, tak aby jak najwięcej pocisków musiało zostać wystrzelonych by pokonać cel. Dla utrudnienia można ustawić się trochę dalej od botów. Im dalej znajduje się cel tym większy rozrzut i tym ciężej go kontrolować.", 0, [10, 0] ],
    
    ["Strzelnica - średni", "Uruchom średnią trudność 30 botów, które pojawią się na ekranie jeden po 2m. Spróbuj zabić jak najwięcej z nich za pomocą strzału w głowę.", 1, [10, 0] ],
    
    ["Strzelnica - trudny", "Uruchom trudny trych 30 botów, które pojawią się na ekranie jeden po 2m. Spróbuj zabić jak najwięcej z nich za pomocą strzału w głowę.", 1, [10, 0] ],
    
    ["Deathmatch", "Rozegraj 5 deathmatchy trenując tym samym Twoje umiejętności strzeleckie w praktyce, konforntując je z realnymi przeciwnikami. Tym samym ćwiczysz pozycjonowanie celownika we właściwym miejscu, dzięki czemu będziesz automatycznie trzymał celownik na poziomie głowy przeciwnika.", 1, [5, 0] ] ];
    
    /*var secExcTab = [
        new Excersise("", "", 0, 0),//panel kontrolny
        
        new Excersise("Zabijanie celów w kółku", "Stój pośrodku botów ustawionych na tryb treningu. Obracaj się dookoła, robiąc jak największe ruchy i zabijaj headshotem kolejne boty, trenując tym samym celność.", 0, 10),
        
        new Excersise("Flicki", "Stój blisko botów ustawionych na tryb treningu. Chodź po mapie, skupiając uwagę na jednym bocie. Po chwili przeflickuj szybko na innego bota, którego widzisz kątem oka, trenując tym samym reakcję na nagłych przeciwników. Dla utrudnienia warto chodzić dookoła jednego celu, starając się utrzymać celownik cały czas na jego głowie (przykleić się celownikiem do jego głowy niczym magnes). Pomaga to trenować celność przy ruchliwych celach.", 0, 10),
        
        new Excersise("Kontrola serii", "Ustaw skończoność amunicji. Stój blisko botów ustawionych na tryb treningu. Spróbuj zabić 3 boty za pomocą jednej serii, trenując tym samym kontrolę fullauto. Zalecane nie celowanie w głowę, tak aby jak najwięcej pocisków musiało zostać wystrzelonych by pokonać cel. Dla utrudnienia można ustawić się trochę dalej od botów. Im dalej znajduje się cel tym większy rozrzut i tym ciężej go kontrolować.", 0, 10),
        
        new Excersise("Strzelnica - średni", "Uruchom średnią trudność 30 botów, które pojawią się na ekranie jeden po 2m. Spróbuj zabić jak najwięcej z nich za pomocą strzału w głowę.", 1, 10),
        
        new Excersise("Strzelnica - trudny", "Uruchom trudny trych 30 botów, które pojawią się na ekranie jeden po 2m. Spróbuj zabić jak najwięcej z nich za pomocą strzału w głowę.", 1, 10),
        
        new Excersise("Deathmatch", "Rozegraj 5 deathmatchy trenując tym samym Twoje umiejętności strzeleckie w praktyce, konforntując je z realnymi przeciwnikami. Tym samym ćwiczysz pozycjonowanie celownika we właściwym miejscu, dzięki czemu będziesz automatycznie trzymał celownik na poziomie głowy przeciwnika.", 1, 5)]*/
    
    secExcTab = new Array();
    
    for(let i = 0; i < excTab.length; i++){
        secExcTab.push(new Excersise(excTab[i][0], excTab[i][1], excTab[i][2], excTab[i][3], i));
    }
    
    this.secExcTab = secExcTab;
    
    
    this.SetTrainingNumber = function(trainingNumber){
        console.log(this);
        this.trainingNumber = trainingNumber;
    }
    
    Training.prototype.Skip = function(){
        //console.log(that);
        //ustawianie niewykonania ćwiczenia
        //console.log(that.trainingNumber);
        document.getElementById("b" + that.trainingNumber).style.backgroundColor = "#FF4655";
        document.getElementById("b" + that.trainingNumber).setAttribute("class", "accordion-button naglowek-listy collapsed");
        document.getElementById("b" + that.trainingNumber).setAttribute("aria-expanded", "false");
        document.getElementById("id" + that.trainingNumber).setAttribute("class", "accordion-collapse collapse");
        if(that.timer != 0)
            clearTimeout(that.timer);
        //startowanie kolejnego treningu na liście
        that.trainingNumber++;
        //console.log(that.trainingNumber);
        StartBlock(that);
    }
    
    Training.prototype.PauseOrResume = function(){
        switch(document.getElementById("opt1").innerText){
            case 'WSTRZYMAJ':
                document.getElementById("opt1").innerHTML = 'START';
                clearTimeout(that.timer);
                break;
            case 'START':
                document.getElementById("opt1").innerHTML = 'WSTRZYMAJ';
                that.timer = setTimeout(that.RepeatExc, 1000);
                break;
        }
    }
    
    Training.prototype.Stop = function(){
        clearTimeout(that.timer);
        that.secExcTab[that.trainingNumber].tempAmount[0] = that.secExcTab[that.trainingNumber].amount[0];
        that.secExcTab[that.trainingNumber].tempAmount[1] = that.secExcTab[that.trainingNumber].amount[1];
        StartBlock(that);
    }
    
    Training.prototype.ShowList = function(){
        var panel = secExcTab[0].AddControlPanel();
        for(let i = 1; i < secExcTab.length; i++){
            panel += secExcTab[i].AddList();
        }
        document.getElementById("listaCwiczen").innerHTML =  panel;
    }
    
    //funkcja, która w pętli powtarza ćwiczenia na czas i/lub sprawdza
    Training.prototype.RepeatExc = function(){
        switch(that.secExcTab[that.trainingNumber].mode){
            case 0: //timer
                if(that.secExcTab[that.trainingNumber].tempAmount[0] == 0 && that.secExcTab[that.trainingNumber].tempAmount[1] == 0){
                    document.getElementById("b" + that.trainingNumber).style.backgroundColor = "#9EF01A";
                    document.getElementById("b" + that.trainingNumber).setAttribute("class", "accordion-button naglowek-listy collapsed");
                    document.getElementById("b" + that.trainingNumber).setAttribute("aria-expanded", "false");
                    document.getElementById("id" + that.trainingNumber).setAttribute("class", "accordion-collapse collapse");
                    var zeroMin = ""; //zero ustawiane dla minut
                    var zeroSec = ""; //zero ustawiane dla sekund
                    if(that.secExcTab[that.trainingNumber].amount[0] < 10)
                        zeroMin = "0";
                    if(that.secExcTab[that.trainingNumber].amount[1] < 10)
                        zeroSec = "0";
                    var timeSum = zeroMin + that.secExcTab[that.trainingNumber].amount[0] + ":" + zeroSec + that.secExcTab[that.trainingNumber].amount[1];
                    document.getElementById("b" + that.secExcTab[that.trainingNumber].which).innerHTML = that.secExcTab[that.trainingNumber].name +': '+ timeSum + that.secExcTab[that.trainingNumber].getHowMany();
                    clearTimeout(that.timer);
                    //startowanie kolejnego treningu na liście
                    that.trainingNumber++;
                    StartBlock(that);
                }else{
                    that.secExcTab[that.trainingNumber].DoExcersise();
                    that.timer = setTimeout(that.RepeatExc, 1000);
                }
                break;
            case 1: //amount
                if(that.secExcTab[that.trainingNumber].tempAmount[0] != 1)
                   that.secExcTab[that.trainingNumber].DoExcersise();
                else{
                    document.getElementById("b" + that.trainingNumber).style.backgroundColor = "#9EF01A";
                    document.getElementById("b" + that.trainingNumber).setAttribute("class", "accordion-button naglowek-listy collapsed");
                    document.getElementById("b" + that.trainingNumber).setAttribute("aria-expanded", "false");
                    document.getElementById("id" + that.trainingNumber).setAttribute("class", "accordion-collapse collapse");
                    document.getElementById("b" + that.secExcTab[that.trainingNumber].which).innerHTML = that.secExcTab[that.trainingNumber].name +': '+ that.secExcTab[that.trainingNumber].amount[0] + that.secExcTab[that.trainingNumber].getHowMany();
                    
                    //startowanie kolejnego treningu na liście
                    that.trainingNumber++;
                    StartBlock(that);
                }
                break;
        }
        
    }
    
}

function Excersise(name, desc, mode, amount, which){
    this.name = name;
    this.desc = desc;
    this.mode = mode; //0 - timer | 1 - amount
    //ilość czasu/powtórzeń
    this.which = which; //ktory to trening z kolei | which == 0 -> AddControlPanel (panel z opcjami typu start/stop etc)
    var that = this;
    
    //0 - minutes/how many repeats | 1 - secounds
    this.amount = new Array(amount.length);
    this.tempAmount = new Array(amount.length);
    
    for(let i = 0; i < amount.length; i++){
        this.amount[i] = amount[i];
        this.tempAmount[i] = amount[i];
    }
    
    Excersise.prototype.getHowMany = function(){
        switch(this.mode){
            case 0:
                return " minut";
                break;
            case 1:
                return " serii";
                break;
        }
    }
    
    //wykonanie ćwiczenia
    Excersise.prototype.DoExcersise = function(){
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
    Excersise.prototype.AddOptions = function(){
        if(this.mode == 0)
            return '<button class="col-md-6 col-lg-3 opt" id="opt1">WSTRZYMAJ</button><button class="col-md-6 col-lg-3 opt" id="opt2">STOP</button><button class="col-md-6 col-lg-3 opt" id="opt3">POMIŃ</button>';
        else
            return '<button class="col-md-6 col-lg-3 opt" id="opt1">NASTEPNE POWTORZENIE</button><button class="col-md-6 col-lg-3 opt" id="opt3">POMIŃ</button>';
    }

    Excersise.prototype.AddControlPanel = function(){
        return '<div class="accordion-item cialo-listy"><h2 class="accordion-header h2" id="ha0"></h2><div class="accordion-button collapsed naglowek-listy" data-bs-target="#id0" aria-expanded="false" aria-controls="id0"><div id="buttons" class="row justify-content-center w-100"></div></div></div><div id="id0" class="accordion-collapse collapse" aria-labelledby="ha0" data-bs-parent="#listaCwiczen"><div class="accordion-body"></div></div></div>';
    }

    //W zależności od przebiegu zwraca albo opcje kontroli treningu albo listę ćwiczeń
    Excersise.prototype.AddList = function(){
        var tempAmount = 0;
        if(this.mode == 0){   //oba w przypadku kiedy
            var zeroMin = ""; //zero ustawiane dla minut
            var zeroSec = ""; //zero ustawiane dla sekund
            if(this.amount[0] < 10)
                zeroMin = "0";
            if(this.amount[1] < 10)
                zeroSec = "0";
                tempAmount = zeroMin + this.amount[0] + ":" + zeroSec + this.amount[1];
        }else
            tempAmount = this.amount[0];

        return '<div class="accordion-item cialo-listy"><h2 class="accordion-header h2" id="ha' + this.which +'"><button id="b' + this.which + '" class="accordion-button collapsed naglowek-listy" type="button" data-bs-toggle="collapse" data-bs-target="#id' + this.which + '" aria-expanded="false" aria-controls="id' + this.which + '">'+ this.name +': '+ tempAmount + this.getHowMany() + '</button></h2><div id="id'+ this.which +'" class="accordion-collapse collapse" aria-labelledby="ha'+ this.which +'" data-bs-parent="#listaCwiczen"><div class="accordion-body">'+ this.desc +'</div></div></div>'
    }
}