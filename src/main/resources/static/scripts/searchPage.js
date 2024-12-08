
//variables



//actions
document.getElementById("pageBody").onload = firstload;








//6 possible param
//Page - 0>
//PageSize
//ListCategory
//Search --> Searched word
//SortBy --> Title, Description, Category, Status, DateAdded, AssignedEmployee, LastMessage, Company
//SortOrder --> Asc / Desc

function firstload(){

    const Parameters = {
        Page: '0',
        PageSize: '10',
        ListCategory: 'All'
    };

    modifyUrl(Parameters);

}
function modifyUrl(newParams) {
    const currentUrl = window.location.href;
    const url = new URL(currentUrl);

    url.search = '';

    for (const [key, value] of Object.entries(newParams)) {
        url.searchParams.set(key, value);
    }

    window.history.replaceState(null, '', url);
}
async function makeApiCall() {

    //show placeholders, del rest data



    const currentUrl = window.location.href;
    const cUrl = new URL(currentUrl);


    const response = await fetch(cUrl.origin + "/api/reports" + cUrl.search, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (!response.ok) {

    }

    const data = await response.json();






}