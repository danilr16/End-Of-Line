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
            <DropdownMenu >
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/rules")} >Perfil</DropdownItem>
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/games")} >Partidas</DropdownItem>
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/rules")} >Estadísticas</DropdownItem>
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/rules")} >Amigos</DropdownItem>
            {isAdmin?<DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/users")} >Usuarios</DropdownItem>:<></>}
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/logout")} >Logout</DropdownItem>
            </DropdownMenu>
        </Dropdown>
        </div>
    );
}
