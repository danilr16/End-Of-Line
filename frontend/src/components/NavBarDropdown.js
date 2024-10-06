import React, { useState, } from 'react'
import {Dropdown, DropdownToggle, DropdownItem, DropdownMenu} from 'reactstrap'
import { useNavigate } from 'react-router-dom';
import "../static/css/home/home.css";

export default function NavBarDropdown(props){
    const [dropdown, setDropdown] = useState(false)
    const toggleDropdown = ()=>{setDropdown(!dropdown)}
    const roles = props.roles
    const username = props.username
    const isAdmin = roles.includes("ADMIN")
    const navigate = useNavigate()


    return(
        <div> 
        <Dropdown isOpen={dropdown} toggle={toggleDropdown}>
            <DropdownToggle>
                {username}
            </DropdownToggle>
            <DropdownMenu  >
            <DropdownItem onClick={()=>navigate("/rules")} >Perfil</DropdownItem>
            <DropdownItem onClick={()=>navigate("/rules")} >Partidas</DropdownItem>
            <DropdownItem onClick={()=>navigate("/rules")} >Estadísticas</DropdownItem>
            <DropdownItem onClick={()=>navigate("/rules")} >Amigos</DropdownItem>
            {isAdmin?<DropdownItem onClick={()=>navigate("/users")} >Usuarios</DropdownItem>:<></>}
            <DropdownItem onClick={()=>navigate("/logout")} >Logout</DropdownItem>
            </DropdownMenu>
        </Dropdown>
        </div>
    );
}
