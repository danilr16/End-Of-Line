import React, { useState, useEffect,useRef } from 'react';
import { Navbar, NavbarBrand, NavLink, NavItem, Nav,  NavbarToggler, Collapse } from 'reactstrap';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { Link } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";
import NavBarDropdown from './components/NavBarDropdown';
import { FaBell } from "react-icons/fa";
import { useColors } from './ColorContext';
import { useAlert } from './AlertContext';
import "./static/css/home/home.css";
import request from './util/request';
import NotificationPanel from './components/NotificationPanel';


function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);
    const { colors, updateColors } = useColors();
    const {alertMessage,updateAlert} = useAlert();
    const [activeAlert,setActiveAlert] = useState(false);
    const [showNotifications,setShowNotification] = useState(false);
    const [notifications,setNotifications] = useState([]);
    const [client, setClient] = useState(null);
    const notPanelRef = useRef(null);

    const toggleNavbar = () => setCollapsed(!collapsed);

    useEffect(() => { //Close NotificationPanel when clicking outisde it
        const handleClickOutside = (event) => {
        if (notPanelRef.current && !notPanelRef.current.contains(event.target)) {
            setShowNotification(false);
        }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
        document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [notPanelRef]);

    useEffect(() => {
        async function fetchChat() {
            if (!jwt || !username) return; 
            const response = await request(`/api/v1/notifications/user/${username}`, 'GET', null, jwt);
            console.log("Fetched messages:", response.resContent);
            setNotifications(Array.isArray(response.resContent)?response.resContent:[]);  // Actualizar el chat con los mensajes previos
        }
        fetchChat();
    }, [jwt,username]);

    useEffect(() => { //Connection to WebSocket
        const sock = new SockJS("http://localhost:8080/ws");
        const stompClient = new Client({
            webSocketFactory: () => sock,
            connectHeaders: {
                Authorization: `Bearer ${jwt}`,
            },
            onConnect: () => {
                console.log("Connected to WebSocket");
                stompClient.subscribe(`/topic/notifications/${username}`, (msg) => {
                    const receivedNotification = JSON.parse(msg.body);
                    setNotifications((prevNotifications) => [...prevNotifications, receivedNotification]);
                    updateAlert("Notification received")
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
    }, [jwt,username]);

    useEffect(() => {
        console.log("reahced")
        if (alertMessage !== "") {
            setActiveAlert(true);
            const timer = setTimeout(() => {setActiveAlert(false);updateAlert("");}, 5000);
            
            return () => clearTimeout(timer); // Limpiar el temporizador cuando el componente se desmonte o el efecto se vuelva a ejecutar
        }
    }, [alertMessage]);

    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
            setUsername(jwt_decode(jwt).sub);
        }
    }, [jwt])

    let adminLinks = <></>;
    let ownerLinks = <></>;
    let userLinks = <></>;
    let userLogout = <></>;
    let publicLinks = <></>;
    let publicLinks2 = <>
        <NavItem>
            <NavLink style={{ color: "#1A1207" }} id="rules" tag={Link} to="/rules">Rules</NavLink>
        </NavItem>
    </>

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>                    
                    
                    
                </>
            )
        }        
    })

    if (!jwt) {
        publicLinks = (
            <>
                <NavItem>
                    <NavLink style={{ color: "#1A1207" }} id="register" tag={Link} to="/register">Register</NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "#1A1207" }} id="login" tag={Link} to="/login">Login</NavLink>
                </NavItem>
            </>
        )
    } else {
        userLinks = (
            <>
                <NavItem>
                    <NavLink style={{ color: "#1A1207" }} tag={Link} to="/dashboard">Dashboard</NavLink>
                </NavItem>
            </>
        )
        userLogout = (
            <>
            <NavItem>
                <NavBarDropdown roles = {roles} username = {username}/>
            </NavItem>
            </>
        )

    }

    return (
        <div>
            <Navbar expand="md" /* dark color="dark" */className="navbar" style={{"backgroundColor":colors.normal}}>
                <NavbarBrand href="/">
                    {/* <img alt="logo" src="/logo1-recortado.png" style={{ height: 40, width: 40 }} /> */}
                    END OF LINE
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {publicLinks2}
                        {adminLinks}
                        {/* {userLinks}
                        {adminLinks}
                        {ownerLinks} */} 
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        <FaBell onClick={()=>{setShowNotification(!showNotifications)}} style={{ marginRight: '20px',top: '6px',position: 'relative', fontSize: '1.5rem' , color:"#1A1207", cursor: 'pointer'}} />
                        
                        {publicLinks}
                        {userLogout} 
                    </Nav>
                </Collapse>
            </Navbar>
            {showNotifications && <NotificationPanel ref={notPanelRef} notifications={notifications} setNotifications = {setNotifications} jwt={jwt}/>}
            {activeAlert && <div className='custom-alert'>{alertMessage}</div>}
        </div>
    );
}

export default AppNavbar;