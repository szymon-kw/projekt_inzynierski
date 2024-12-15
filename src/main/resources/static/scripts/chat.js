let client = null;
let isConnected = false;

var popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
var popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))

function showMessage(value, user) {
    let newResponse = document.createElement('p');

    let now = new Date();
    let hours = String(now.getHours()).padStart(2, '0');
    let minutes = String(now.getMinutes()).padStart(2, '0');
    let day = String(now.getDate()).padStart(2, '0');
    let month = String(now.getMonth() + 1).padStart(2, '0');
    let year = now.getFullYear();
    let formattedTime = `${hours}:${minutes} ${day}.${month}.${year}`;

    newResponse.textContent = `${user} (${formattedTime}) : ${value}`;
    newResponse.classList.add("single-message-box");

    let response = document.getElementById('chat-box');
    response.appendChild(newResponse);
    scrollToBottom();
}

function showAttachment(attachment) {

    let attachmentItem = document.createElement('div');

    attachmentItem.className = 'card border m-3 hover-ring';

    attachmentItem.setAttribute('style', 'max-width: 11em');
    attachmentItem.setAttribute('data-bs-toggle', 'popover');
    attachmentItem.setAttribute('data-bs-trigger', 'hover focus');
    attachmentItem.setAttribute('data-bs-placement', 'bottom');
    attachmentItem.setAttribute('data-bs-html', 'true');
    attachmentItem.setAttribute('data-bs-title', attachment.fileName);
    attachmentItem.setAttribute('data-bs-content', `<b>Przesłał:</b><br> ${attachment.addingUser} <br> <b>data:</b> <br> ${formatDate(attachment.timestamp)}`);


    attachmentItem.innerHTML = `
        <div class="card-body p-0">
            <div class="w-100 text-center mt-4 mb-4 text-danger">
                <i class="fa-solid fa-4x ${attachment.fileIconClass}"></i>
            </div>
            <div class="row p-0 m-0">
                <div class="col col-8 p-0 m-0">
                    <h6 class="mb-0 text-truncate">${attachment.fileName}</h6>
                    <p><small class="mb-0">${attachment.fileSize}</small></p>
                </div>
                <div class="col col-1 mt-3">
                    <a href="${attachment.filePath}" target="_blank" class="show-file"><i class="fa-solid fa-2xl fa-share-from-square"></i></a>
                </div>
        </div>
    `;

    let attachmentsBox = document.getElementById('attachments-box');
    attachmentsBox.appendChild(attachmentItem);

    popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
    popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))

}

function sendAttachment() {
    const fileInput = document.getElementById('attachmentFile');
    const file = fileInput.files[0];
    if (file) {
        let formData = new FormData();
        formData.append("file", file);
        formData.append("reportId", reportId);

        fetch("/chat/upload", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        console.error("Szczegóły błędu serwera:", text);
                        throw new Error("Błąd przesyłania załącznika.");
                    });
                }
                return response.json();
            })
            .then(attachment => {
                showAttachment(attachment);
                fileInput.value = "";
            })
            .catch(error => console.error("Błąd przesyłania załącznika:", error));
    }
}

function connectt(reportIdParam) {
    if (reportIdParam === undefined) {
        return;
    }

    reportId = reportIdParam;

    if (isConnected) {
        return;
    }

    client = Stomp.over(new SockJS('http://localhost:8080/chat'));

    client.connect({}, function (frame) {
        isConnected = true;

        client.subscribe('/topic/messages', function (message) {
            let msg = JSON.parse(message.body);
            if (msg.reportId === reportId) {
                showMessage(msg.value, msg.user);
            }
        });
    }, function(error) {
        console.error("Bląd połączenia:", error);
    });
}

function sendMessage() {
    if (client && client.connected) {
        let messageToSend = document.getElementById('messageToSend').value.trim();

        if (messageToSend === "") {
            alert("Wiadomość nie może być pusta!!");
            return;
        }

        client.send("/app/chat", {}, JSON.stringify({
            'value': messageToSend,
            'user': username,
            'reportId': reportId
        }));
        document.getElementById('messageToSend').value = "";
    } else {
        console.error("Połaczenie nie zostało jeszcze nawiazane");
    }
}

function scrollToBottom() {
    const chatBox = document.getElementById('chat-box');
    chatBox.scrollTop = chatBox.scrollHeight;
}
function formatDate(timestamp) {

    const date = new Date(timestamp);


    const hours = String(date.getHours()).padStart(2, '0'); // HH
    const minutes = String(date.getMinutes()).padStart(2, '0'); // mm
    const day = String(date.getDate()).padStart(2, '0'); // dd
    const month = String(date.getMonth() + 1).padStart(2, '0'); // MM
    const year = date.getFullYear(); // yyyy


    return `${hours}:${minutes} ${day}.${month}.${year}`;
}

document.getElementById('messageToSend').addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendMessage();
    }
});
