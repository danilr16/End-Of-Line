import React, { useState, useEffect } from 'react';
import { Navbar, NavbarBrand, NavLink, NavItem, Nav, NavbarText, NavbarToggler, Collapse } from 'reactstrap';
import { Link } from 'react-router-dom';
import tokenService from './services/token.service';
import jwt_decode from "jwt-decode";
import NavBarDropdown from './components/NavBarDropdown';
import { FaBell } from "react-icons/fa";
import "./static/css/home/home.css";


function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const jwt = tokenService.getLocalAccessToken();
    const [collapsed, setCollapsed] = useState(true);

    const toggleNavbar = () => setCollapsed(!collapsed);

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
                    <NavItem>
                        <NavLink style={{ color: "#1A1207" }} tag={Link} to="/users">Users</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{color: "#1A1207"}} tag={Link} to="/games">Games</NavLink>
                    </NavItem>
                </>
            )
        }        
    })

    if (!jwt) {
        publicLinks = (
            <>
                {/* <NavItem>
                    <NavLink style={{ color: "white" }} id="docs" tag={Link} to="/docs">Docs</NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="plans" tag={Link} to="/plans">Pricing Plans</NavLink>
                </NavItem> */}
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
            <Navbar expand="md" /* dark color="dark" */className="navbar">
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
                        <FaBell style={{ marginRight: '20px',top: '6px',position: 'relative', color: 'white', fontSize: '1.5rem' , color:"#1A1207"}} />
                        {publicLinks}
                        {userLogout} 
                    </Nav>
                </Collapse>
            </Navbar>
        </div>
    );
}

export default AppNavbar;