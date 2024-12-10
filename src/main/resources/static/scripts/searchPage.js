const PageSizeButton = document.getElementById("pageSizeButton");
class QuweryParams {

    constructor(Page = 0, PageSize = 10, ListCategory = "All", Search = null, SortBy = null, SortOrder = null, IconId = null ) {
        this.Page = Page;
        this.PageSize = PageSize;
        this.ListCategory = ListCategory;
        this.Search = Search;
        this.SortBy = SortBy;
        this.SortOrder = SortOrder
        this.IconId = IconId;
    }

    generateParamsObject() {
        const params = {};
        for (const [key, value] of Object.entries(this)) {
            if (value !== null && key !== 'IconId') {
                params[key] = value;
            }
        }
        return params;

    }
    SetNewCategory(category){
        this.Page = 0;
        this.PageSize = 10;
        this.ListCategory = category;
        this.Search = null;
        this.SortBy = null;


        if (this.IconId !== null){
            if (this.SortOrder === "Asc"){
                document.getElementById(this.IconId).classList.remove("fa-arrow-down-wide-short");
            }else{
                document.getElementById(this.IconId).classList.remove("fa-arrow-up-short-wide");
            }
        }
        this.IconId = null;
        this.SortOrder = null;
        makeApiCall();
    }
    SetSearchWord(SearchWard){
        this.Search = SearchWard;
        makeApiCall();
    }
    SetNewSortRole(iconId, SortBy){
        if (this.IconId === null){
            this.IconId = iconId;
            this.SortBy = SortBy;
            this.SortOrder = "Asc";
            document.getElementById(this.IconId).classList.add("fa-arrow-down-wide-short");
        }else{

            if (this.IconId === iconId){
                if (this.SortOrder === "Asc"){
                    this.SortOrder = "Desc";
                    document.getElementById(this.IconId).classList.remove("fa-arrow-down-wide-short");
                    document.getElementById(this.IconId).classList.add("fa-arrow-up-short-wide");
                }else{
                    document.getElementById(this.IconId).classList.remove("fa-arrow-up-short-wide");
                    this.IconId = null;
                    this.SortBy = null;
                    this.SortOrder = null;
                }
            }else{
                if (this.SortOrder === "Asc"){
                    document.getElementById(this.IconId).classList.remove("fa-arrow-down-wide-short");
                }else{
                    document.getElementById(this.IconId).classList.remove("fa-arrow-up-short-wide");
                }
                this.SortOrder = "Asc";
                this.SortBy = SortBy;
                this.IconId = iconId;
                document.getElementById(this.IconId).classList.add("fa-arrow-down-wide-short");
            }
        }
        makeApiCall();
    }
    SetPage(pageNum){
        this.Page = pageNum - 1
        makeApiCall();
    }
    SetPageBack(){
        if (this.Page !== 0){
            this.Page --;
            makeApiCall();
        }
    }
    SetPageForward(){
        this.Page ++;
        makeApiCall();
    }
    SetPageSize(newPageSize){
        this.PageSize = newPageSize;
        PageSizeButton.innerHTML = newPageSize;
        makeApiCall();
    }

}
class ReportTime {
    constructor(openDateString, remainingTimeInSeconds ,firstRespond) {

        this.openDateString = openDateString;

        if (remainingTimeInSeconds < 0){
            this.remainingTimeInSeconds = 0;
        }else{
            this.remainingTimeInSeconds = remainingTimeInSeconds;
        }


        this.progress = this.calculateProgress();

        this.remainingTime = this.calculateRemainingTime();

        this.color = this.calculateColor();

        this.toTimeText = this.generateText(firstRespond);

    }

    calculateProgress() {

        const [day, month, year, time] = this.openDateString.match(/(\d{2})-(\d{2})-(\d{4}) (\d{2}:\d{2})/).slice(1);

        const openDate = new Date(`${year}-${month}-${day}T${time}`);
        const endDate = new Date(openDate.getTime() + this.remainingTimeInSeconds * 1000);
        const currentDate = new Date();

        const totalTime = endDate - openDate;
        const elapsedTime = currentDate - openDate;

        let progress = Math.min(Math.max((elapsedTime / totalTime) * 100, 1), 100);

        return Math.round(progress);
    }

    calculateRemainingTime() {
        if (this.remainingTimeInSeconds <= 0) {
            return "Czas minął";
        }

        const days = Math.floor(this.remainingTimeInSeconds / (24 * 60 * 60));
        const hours = Math.floor((this.remainingTimeInSeconds % (24 * 60 * 60)) / 3600);
        const minutes = Math.floor((this.remainingTimeInSeconds % 3600) / 60);

        let remainingTimeString = '';
        if (days > 0) remainingTimeString += `${days} Dni `;
        if (hours > 0) remainingTimeString += `${hours} Godz. `;
        if (minutes > 0) remainingTimeString += `${minutes} Min.`;

        return remainingTimeString.trim() || "0 Min.";
    }

    calculateColor() {
        if (this.progress >= 75) {
            return 'bg-danger';
        } else if (this.progress >= 50) {
            return 'bg-warning';
        } else if (this.progress >= 25) {
            return 'bg-success';
        } else {
            return 'bg-primary';
        }
    }
    generateText(firstRespond){
        if (firstRespond){
            return "Czas na odpowiedź: ";
        }else{
            return "Czas do rozwiązania: ";
        }
    }
}


//variables
const SearchText = document.getElementById("SearchTextInput");
const SearchButton = document.getElementById("SearchButton");
const dropdownList = document.getElementById("tableClass");
const MainList = document.getElementById("mainList");
const Placeholders = document.getElementById("placeholders");

var searchCount = document.getElementById("searchCount");

//Dropdown info
const dropPendingCount = document.getElementById("PendingCount");
const dropUnderReviewCount = document.getElementById("UnderReviewCount");
const dropCompleteCount = document.getElementById("CompleteCount");
const dropUnAssignedCount = document.getElementById("UnAssignedCount");

//Class
const quweryParam = new QuweryParams();

//Modal
const modalInfo = document.getElementById("modalInfo")
const modal = new bootstrap.Modal(document.getElementById('AlertModal'));


// fa-arrow-up-short-wide - ar-Up DESC
// fa-arrow-down-wide-short -ar-down ASC
//actions

//List Head
document.getElementById("pageBody").onload = firstload;
document.getElementById("Title").onclick = function (){quweryParam.SetNewSortRole("TitleIcon", "Title");}
document.getElementById("Description").onclick = function (){quweryParam.SetNewSortRole("DescriptionIcon", "Description");}
document.getElementById("Category").onclick = function (){quweryParam.SetNewSortRole("CategoryIcon", "Category");}
document.getElementById("Status").onclick = function (){quweryParam.SetNewSortRole("StatusIcon", "Status");}
document.getElementById("AddedDate").onclick = function (){quweryParam.SetNewSortRole("DateAddedIcon", "DateAdded");}
document.getElementById("AssignedEmployee").onclick = function (){quweryParam.SetNewSortRole("AssignedEmployeeIcon", "AssignedEmployee");}
document.getElementById("LastMessage").onclick = function (){quweryParam.SetNewSortRole("LastMessageIcon", "LastMessage");}
document.getElementById("Company").onclick = function (){quweryParam.SetNewSortRole("CompanyIcon", "Company");}

//Dropdown
document.getElementById("dropdownAll").onclick = function () {DropDownChange("All", "Wszystkie");}
document.getElementById("dropdownPending").onclick = function () {DropDownChange("Pending", "Oczekujące");}
document.getElementById("dropdownUnderReview").onclick = function () {DropDownChange("UnderReview", "W trakcie");}
document.getElementById("dropdownComplete").onclick = function () {DropDownChange("Completed", "Zakończone");}
document.getElementById("dropdownUnAssigned").onclick = function () {DropDownChange("NotAssigned", "Nie przydzielone");}
//Dropdown pageSize
document.getElementById("pageSize5").onclick = function () {quweryParam.SetPageSize(5);}
document.getElementById("pageSize10").onclick = function () {quweryParam.SetPageSize(10);}
document.getElementById("pageSize15").onclick = function () {quweryParam.SetPageSize(15);}
document.getElementById("pageSize20").onclick = function () {quweryParam.SetPageSize(20);}
document.getElementById("pageSize25").onclick = function () {quweryParam.SetPageSize(25);}

//pagination
const page1 = document.getElementById("page1");
const page2 = document.getElementById("page2");
const page3 = document.getElementById("page3");
const pageForward = document.getElementById("forwardPage");
const pageBack = document.getElementById("backPage");

page1.onclick = function () {quweryParam.SetPage(page1.innerHTML);}
page2.onclick = function () {quweryParam.SetPage(page2.innerHTML);}
page3.onclick = function () {quweryParam.SetPage(page3.innerHTML);}
pageForward.onclick = function () {quweryParam.SetPageForward();}
pageBack.onclick = function () {quweryParam.SetPageBack();}

//6 possible param
//Page - 0>
//PageSize
//ListCategory --> All, Pending, UnderReview, Completed ,NotAssigned{admins}
//Search --> Searched word
//SortBy --> Title, Description, Category, Status, DateAdded, AssignedEmployee, LastMessage, Company
//SortOrder --> Asc / Desc

SearchText.addEventListener("keyup", (event) =>{
    if (event.key === "Enter"){
        SearchButton.click();
    }
});

SearchButton.addEventListener("click", function () {
    if (SearchText.value === ''){
        quweryParam.SetSearchWord(null)
    }else{
        quweryParam.SetSearchWord(SearchText.value)
    }
})

function firstload(){

    modifyUrl(quweryParam.generateParamsObject());
    makeApiCall();
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

    modifyUrl(quweryParam.generateParamsObject());
    //show placeholders, del rest data
    MainList.innerHTML = "";
    Placeholders.style.display = "block";


    const currentUrl = window.location.href;
    const cUrl = new URL(currentUrl);

    try{
        console.log(cUrl.origin + "/api/reports" + cUrl.search);
        const response = await fetch(cUrl.origin + "/api/reports" + cUrl.search, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`Błąd HTTP! Status: ${response.status}`);
        }

        const data = await response.json();
        await sleep(1000);

        //feach json
        feachJson(data);

    }catch (error) {
        modalInfo.innerHTML = error;
        modal.show();
        Placeholders.classList.add("invisible")
    }

    //hide placeholders
    Placeholders.style.display = "none";
}

function feachJson(data) {
    const showTime = data.showTimetoClose;

    //set dropdown info
    dropPendingCount.innerHTML = data.totalPending;
    dropUnderReviewCount.innerHTML = data.totalUnderReview;
    dropCompleteCount.innerHTML = data.totalCompleted;
    dropUnAssignedCount.innerHTML = data.totalUnAssigned;

    searchCount.innerHTML = data.element.totalElements;

    //list body
    const content = data.element.content;

    preparePagination(data.element.number + 1, data.element.totalPages);

    content.forEach((item, index) => {


        const reportTime = new ReportTime(item.dateAdded, item.timeToLeft, item.firstRespond);


        const newElement = document.createElement('div');
        newElement.className = "row m-0";
        newElement.innerHTML = `
            <div class="col col-md-1 text-center" style="width: 3%;"><Strong>${index + 1}</Strong></div>
            <div class="col col-md-2 col-sm-1" style="width: 14%;">${item.title}</div>
            <div class="col col-md-3 col-sm-1" style="width: 19%;">${item.description}</div>
            <div class="col col-1 text-center" style="width: 8%;">${item.category}</div>
            <div class="col col-1 text-center text-center" style="width: 6%;"><span class="badge ${setColor(item.status)}">${item.status}</span></div>
            <div class="col col-2 text-break text-center" style="width: 9%;">${item.dateAdded}</div>
            <div class="col col-2 text-break text-center" style="width: 15%;">${item.assignedEmployee}</div>
            <div class="col col-2 text-break text-center" style="width: 10%;">${item.lastMessage}</div>
            <div class="col col-1 text-break text-center" style="width: 9%;">${item.company}</div>
            <div class="col col-1 text-center" style="width: 7%;"><a href="/chat?reportId=${item.id}" class="btn btn-primary">Sprawdź</a></div>
            ${showTime ? `<div class="row m-0 mt-3">
            <div class="progress position-relative p-0">
            <div class="progress-bar progress-bar-striped ${reportTime.color}" style="z-index: 1; width: ${reportTime.progress}%;"></div>
            <span class="position-absolute w-100 text-center fw-bold " style="z-index: 2;">
            ${reportTime.toTimeText}${reportTime.remainingTime}</span></div>` : ''}
            </div>`;
            MainList.appendChild(newElement);
            const separator = document.createElement('hr');
            separator.classList.add("my-3");

            if (index !== content.length - 1){
                MainList.appendChild(separator);
            }


    });


}


function DropDownChange(listName, Fulname) {
    quweryParam.SetNewCategory(listName);
    dropdownList.innerHTML = Fulname;

}
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
function setColor(category) {
    if (category === "Oczekujące"){
        return "bg-warning";
    }else if (category === "W trakcie"){
        return "bg-success";
    }else if (category === "Zakończone"){
        return "bg-secondary";
    }else{
        return "bg-danger"
    }


}
function preparePagination(PageValue, TotalPages) {
    unshowPage();
    // if out of range
    debugger;
    if (PageValue <= TotalPages){

        if (TotalPages === 1){
            page1.style.display = "block";
            page1.classList.add("active");
        }else if (TotalPages === 2){
            page1.style.display = "block";
            page2.style.display = "block";
            page2.innerHTML = "2";
            if (PageValue === 1){
                page1.classList.add("active");
                pageForward.classList.remove("disabled");
            }else{
                page2.classList.add("active");
                pageBack.classList.remove("disabled");
            }
        }else if (TotalPages >= 3){

            if (PageValue === 1){
                debugger;
                pageForward.classList.remove("disabled");

                page1.innerHTML = "1";
                page2.innerHTML = "2";
                page3.innerHTML = "3";

                page1.style.display = "block";
                page2.style.display = "block";
                page3.style.display = "block";
                page1.classList.add("active");

            }else if (PageValue === TotalPages){
                debugger;
                pageBack.classList.remove("disabled");

                page1.style.display = "block";
                page2.style.display = "block";
                page3.style.display = "block";
                page3.classList.add("active");

                page1.innerHTML = PageValue - 2;
                page2.innerHTML = PageValue - 1;
                page3.innerHTML = PageValue;

            }else{
                debugger;
                pageForward.classList.remove("disabled");
                pageBack.classList.remove("disabled");

                page1.style.display = "block";
                page2.style.display = "block";
                page3.style.display = "block";
                page2.classList.add("active");

                page1.innerHTML = PageValue - 1;
                page2.innerHTML = PageValue;
                page3.innerHTML = PageValue + 1;
            }

        }


    }else{
        page1.style.display = "block";
    }
}
function unshowPage() {

    pageForward.classList.add("disabled");
    pageBack.classList.add("disabled");

    page1.style.display = "none";
    page2.style.display = "none";
    page3.style.display = "none";

    page1.classList.remove("active");
    page2.classList.remove("active");
    page3.classList.remove("active");

    page1.innerHTML = "1";
    page2.innerHTML = "1";
    page3.innerHTML = "1";
}