(async function (){
    const trainingsResponse = await fetch("/training/api");
    const weekdaysResponse = await fetch("/weekdays");
    const allTrainings = await trainingsResponse.json();
    const weekdays = await weekdaysResponse.json();

    for(const weekday of weekdays){
        const inputText = document.getElementById("search" + weekday);
        const select = document.getElementById("training"+ weekday);

        initSelect(allTrainings, select);

        inputText.addEventListener(
            'input', async (e) =>{
                e.preventDefault();

                while(select.firstChild)
                    select.firstChild.remove();
                createOption("--Dzień wolny--", -1, true, select);

                const inputValue = e.currentTarget.value.toLowerCase();

                if(inputValue.trim() === ""){
                    initSelect(allTrainings, select);
                    return;
                }

                allTrainings.forEach(
                    training => {
                        const title = training.title.toLowerCase();

                        if(title.includes(inputValue))
                            createOption(`id: ${training.id} tytuł: ${training.title}`, training.id, false, select);
                    }
                );
            }
        );
    }
})();

function createOption(text, value, selected, parent){
    const newOption = document.createElement("option");
    newOption.value = value;
    newOption.selected = selected;
    newOption.innerHTML = text;
    parent.appendChild(newOption);
}

/**
 * Creates default top few trainings in select list.
 * Invoked while search bar is empty
 */
function initSelect(trainings, parent, howManyRows = 20){
    for(let i = 0; i < Math.min(trainings.length, howManyRows); i++){
        const training = trainings[i];
        createOption(`id: ${training.id} tytuł: ${training.title}`, training.id, false, parent);
    }
}