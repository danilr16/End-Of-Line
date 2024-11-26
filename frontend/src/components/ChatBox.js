import {  useEffect, useState, useRef } from "react";
import request from "../util/request";
import "../static/css/components/chatBox.css"
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";


export default function ChatBox({gameCode,user,jwt}){
    const [input, setInput] = useState('');
    const [chat, setChat] = useState([]);
    const [client, setClient] = useState(null);

    useEffect(() => {
        async function fetchChat() {
            if (!jwt) return; 
            const response = await request(`/api/v1/games/${gameCode}/chat`, 'GET', null, jwt);
            console.log("Fetched messages:", response.resContent);
            setChat(response.resContent);  // Actualizar el chat con los mensajes previos
        }
        fetchChat();
    }, [jwt]);

    useEffect(() => {
        const sock = new SockJS("http://localhost:8080/ws");
        const stompClient = new Client({
            webSocketFactory: () => sock,
            connectHeaders: {
                Authorization: `Bearer ${jwt}`, // Agrega el token JWT aquí
            },
            onConnect: () => {
                console.log("Connected to WebSocket");
                stompClient.subscribe("/topic/chat", (msg) => {
                    const receivedMessage = JSON.parse(msg.body);
                    setChat((prevChat) => [...prevChat, receivedMessage].slice(-MAX_MESSAGES));
                });
            },
            onStompError: (frame) => {
                console.error("STOMP error:", frame.headers["message"]);
                console.error("Details:", frame.body);
            },
        });
    
        stompClient.activate();
        setClient(stompClient);
    
        return () => stompClient.deactivate();
    }, [jwt]);
    

    const chatEndRef = useRef(null);

    const MAX_MESSAGES = 75; 

    useEffect(() => {
        if(chatEndRef.current) {
            chatEndRef.current.scrollTop = chatEndRef.current.scrollHeight;
        }
    }, [chat]) 
    


    const handleSendMessage = async () => {
        if (input.trim() === '') {
            return; //Para que no se envíen mensajes vacíos
        }

        if (client && input.trim()) {
            client.publish({
            destination: "/app/chat",
            body: JSON.stringify({username:user.userName,jwt:jwt, messageString:input,gameCode:gameCode}),
            });
            console.log({username:user.userName,jwt:jwt, messageString:input,gameCode:gameCode});
        }
        
        setInput('');
    }

    const handlePressKey = (e) => {
        if (e.key === 'Enter') {
            handleSendMessage();
        }
    }

    return (
        <div className="chat-container">
                    <div className="chat">
                        

                        <div className="message-container" ref={chatEndRef}>
                            <span style={{ color: "grey"}}>Welcome to the chat! </span>
                            {chat && chat.map((chatMessage, index) => (
                                <div key={index} className="chat-message">
                                    [{chatMessage.userName}]: <span className="message-content">{chatMessage.messageString}</span>
                                </div>
                            ))}
                        </div>
                        <input className="message-input"
                            type="text"
                            placeholder="Send your message..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={handlePressKey}
                            maxLength={500}
                        />
                    </div>
                </div>
    );

}