let client = null;
let isConnected = false;

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

function showAttachment(url, user) {
    let attachmentItem = document.createElement('p');
    let now = new Date();
    let formattedTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')} ${String(now.getDate()).padStart(2, '0')}.${String(now.getMonth() + 1).padStart(2, '0')}.${now.getFullYear()}`;

    let link = document.createElement('a');
    link.href = url;
    link.target = "_blank";
    let urlToDisplay = url.toString().replace(/\/uploads\//, '').replace(/\(\d+\)/g, '');
    link.textContent = `Załączono plik: ${urlToDisplay}`;
    link.classList.add("attachment-link");

    attachmentItem.textContent = `${user} (${formattedTime}) : `;
    attachmentItem.classList.add("attachment-item");
    attachmentItem.appendChild(link);

    let attachmentsBox = document.getElementById('attachments-box');
    attachmentsBox.appendChild(attachmentItem);
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
                return response.text();
            })
            .then(filePath => {
                showAttachment(filePath, username);
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

document.getElementById('messageToSend').addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendMessage();
    }
});
