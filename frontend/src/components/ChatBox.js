import useFetchState from "../util/useFetchState";
import {  useEffect, useState, useRef } from "react";
import request from "../util/request";
import "../static/css/components/chatBox.css"




export default function ChatBox({gameCode,user,jwt}){
    const [input, setInput] = useState('');

    const [chat, setChat] = useFetchState(
        [],
        `/api/v1/games/${gameCode}/chat`,
        jwt
    );

    const chatEndRef = useRef(null);

    const MAX_MESSAGES = 75; //Numero maximo de mensajes que se mostrarán en el chat

    useEffect(() => {
        if(chatEndRef.current) {
            chatEndRef.current.scrollTop = chatEndRef.current.scrollHeight;
        }
    }, [chat]) //Esto es para que cuando se envíe un mensaje es scroll se vaya para abajo automáticamente
    


    const handleSendMessage = async () => {
        if (input.trim() === '') {
            return; //Para que no se envíen mensajes vacíos
        }
        console.log(JSON.stringify({userName: user.username, messageString:input}))//Borrar

        const newChat = (await request(`/api/v1/games/${gameCode}/chat`,'PATCH',{userName:user.username, messageString:input},jwt)).resContent;

        try{
            setChat(newChat.slice(-MAX_MESSAGES))
        }
        catch(e){
            return;
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
                            {chat.map((chatMessage, index) => (
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