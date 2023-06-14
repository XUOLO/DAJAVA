'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');

let stompClient = null;
let username = null;

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/javatechie');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.register",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function send(event) {
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    const messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined the chat!';
    } else if (message.type === 'LEAVE') { // Thêm điều kiện xử lý khi người dùng rời khỏi phòng chat
        messageElement.classList.add('event-message');
        message.content = message.sender + ' has left the chat.'; // Hiển thị thông báo "name has left the chat"
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('i');
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('span');
        usernameElement.textContent = message.sender;
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('p');
    textElement.textContent = message.content;

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}
function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    const index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', send, true);


// Xử lý khi người dùng ấn nút Leave
document.getElementById('leave-btn').addEventListener('click', function(event) {
  event.preventDefault();

  // Gửi yêu cầu kết thúc phiên chat
  stompClient.send("/app/chat.leave", {}, JSON.stringify({ sender: username, type: 'LEAVE' }));

  // Đóng kết nối
  stompClient.disconnect();

  // Hiển thị thông báo người dùng đã rời khỏi cuộc trò chuyện
  const messageElement = document.createElement('li');
  messageElement.classList.add('event-message');
  messageElement.classList.add('leave-message');
  messageElement.textContent = username + ' has left the chat.';
  messageArea.appendChild(messageElement);

  // Ẩn phòng chat và hiển thị trang đăng nhập
  window.location.href = '/admin/home';

  // Reset biến username và stompClient
  username = null;
  stompClient = null;
});
